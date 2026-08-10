package com.toan.university_management.service.auth;

import com.nimbusds.jose.JOSEException;
import com.toan.university_management.dto.request.auth.AuthenticationRequest;
import com.toan.university_management.dto.request.auth.IntrospectRequest;
import com.toan.university_management.dto.request.auth.LogoutRequest;
import com.toan.university_management.dto.request.auth.RefreshRequest;
import com.toan.university_management.dto.response.auth.AuthenticationResponse;
import com.toan.university_management.dto.response.auth.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request);
    void logout(LogoutRequest request);
    AuthenticationResponse refreshToken(RefreshRequest request);
}


