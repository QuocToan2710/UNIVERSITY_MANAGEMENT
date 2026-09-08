package com.toan.university_management.configuration;

import com.nimbusds.jwt.SignedJWT;
import com.toan.university_management.service.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final AuthenticationService authenticationService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    SignedJWT signedJWT = authenticationService.verifyToken(token, false);
                    String username = signedJWT.getJWTClaimsSet().getSubject();
                    String scope = (String) signedJWT.getJWTClaimsSet().getClaim("scope");

                    List<SimpleGrantedAuthority> authorities = StringUtils.hasText(scope)
                            ? Arrays.stream(scope.split(" "))
                                    .filter(StringUtils::hasText)
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList())
                            : Collections.emptyList();

                    Principal principal = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    accessor.setUser(principal);
                    log.info("WebSocket client authenticated successfully for user: {}", username);
                } catch (Exception e) {
                    log.warn("WebSocket token verification failed during connect: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}