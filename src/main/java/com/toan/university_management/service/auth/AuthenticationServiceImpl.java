package com.toan.university_management.service.auth;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.toan.university_management.dto.request.auth.AuthenticationRequest;
import com.toan.university_management.dto.request.auth.IntrospectRequest;
import com.toan.university_management.dto.request.auth.LogoutRequest;
import com.toan.university_management.dto.request.auth.RefreshRequest;
import com.toan.university_management.dto.response.auth.AuthenticationResponse;
import com.toan.university_management.dto.response.auth.IntrospectResponse;
import com.toan.university_management.entity.auth.InvalidatedToken;
import com.toan.university_management.entity.identity.RolePermission;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.entity.identity.UserRole;
import com.toan.university_management.exception.AppException;
import com.toan.university_management.exception.ErrorCode;
import com.toan.university_management.repository.auth.InvalidatedTokenRepository;
import com.toan.university_management.repository.identity.RolePermissionRepository;
import com.toan.university_management.repository.identity.UserRepository;
import com.toan.university_management.repository.identity.UserRoleRepository;
import com.toan.university_management.service.token.TokenBlacklistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    UserRepository userRepository;
    UserRoleRepository userRoleRepository;
    RolePermissionRepository rolePermissionRepository;
    com.toan.university_management.repository.identity.RoleRepository roleRepository;
    com.toan.university_management.repository.identity.PermissionRepository permissionRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    TokenBlacklistService tokenBlacklistService;
    PasswordEncoder passwordEncoder;

    @NonFinal
    @Value("${jwt.signer-key}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank() ||
            request.getPassword() == null || request.getPassword().isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String reqUsername = request.getUsername().trim();
        User user = userRepository.findByUsername(reqUsername)
                .or(() -> userRepository.findByUsernameIgnoreCase(reqUsername))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);

            long expirationMillis = expiryTime != null ? expiryTime.getTime() - System.currentTimeMillis() : 0;
            tokenBlacklistService.blacklistToken(jit, expirationMillis);

        } catch (AppException | ParseException exception) {
            log.info("Token already expired or invalid");
        }
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshRequest request) {
        SignedJWT signedJWT = verifyToken(request.getToken(), true);

        String jit;
        String username;
        Date expiryTime;
        try {
            jit = signedJWT.getJWTClaimsSet().getJWTID();
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            username = signedJWT.getJWTClaimsSet().getSubject();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        long expirationMillis = expiryTime != null ? expiryTime.getTime() - System.currentTimeMillis() : 0;
        tokenBlacklistService.blacklistToken(jit, expirationMillis);

        var user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByUsernameIgnoreCase(username))
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private byte[] getSignerKeyBytes() {
        byte[] keyBytes = SIGNER_KEY.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (keyBytes.length < 64) {
            byte[] padded = new byte[64];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return padded;
        }
        return keyBytes;
    }

    private SignedJWT verifyToken(String token, Boolean isRefresh) {
        try {
            JWSVerifier verifier = new MACVerifier(getSignerKeyBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expityTime = (isRefresh)
                    ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                    .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                    : signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(verifier);
            if (!(verified && expityTime.after(new Date())))
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            if (tokenBlacklistService.isTokenBlacklisted(jti))
                throw new AppException(ErrorCode.UNAUTHENTICATED);

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        List<UserRole> userRoles = userRoleRepository.findByUserId(user.getId());
        if (!CollectionUtils.isEmpty(userRoles)) {
            java.util.Set<Long> roleIds = userRoles.stream()
                    .map(UserRole::getRoleId)
                    .collect(java.util.stream.Collectors.toSet());

            if (!roleIds.isEmpty()) {
                List<com.toan.university_management.entity.identity.Role> roles = roleRepository.findAllByIdIn(roleIds);
                for (com.toan.university_management.entity.identity.Role role : roles) {
                    if (role.getRoleCode() != null) {
                        stringJoiner.add(role.getRoleCode());
                        if (!role.getRoleCode().startsWith("ROLE_")) {
                            stringJoiner.add("ROLE_" + role.getRoleCode());
                        }
                    }
                    if (role.getName() != null) {
                        stringJoiner.add(role.getName());
                    }
                }
            }
        }

        if ("admin".equalsIgnoreCase(user.getUsername())) {
            stringJoiner.add("ROLE_ADMIN");
            stringJoiner.add("ADMIN");
        }

        return stringJoiner.toString().trim().replaceAll("\\s+", " ");
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("toan.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(getSignerKeyBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
}
