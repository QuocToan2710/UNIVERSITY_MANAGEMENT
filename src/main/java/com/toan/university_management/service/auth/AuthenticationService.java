package com.toan.university_management.service.auth;

import com.nimbusds.jose.JOSEException;
import com.toan.university_management.model.auth.AuthenticationRequest;
import com.toan.university_management.model.auth.IntrospectRequest;
import com.toan.university_management.model.auth.LogoutRequest;
import com.toan.university_management.model.auth.RefreshRequest;
import com.toan.university_management.model.auth.AuthenticationResponse;
import com.toan.university_management.model.auth.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request);
    AuthenticationResponse refreshToken(RefreshRequest request);
    void forgotPassword(com.toan.university_management.model.auth.ForgotPasswordRequest request);
    void resetPassword(com.toan.university_management.model.auth.ResetPasswordRequest request);
    com.nimbusds.jwt.SignedJWT verifyToken(String token, boolean isRefresh);
}


