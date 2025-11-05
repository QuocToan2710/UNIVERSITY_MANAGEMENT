package com.toan.university_management.service;

import com.nimbusds.jose.JOSEException;
import com.toan.university_management.dto.request.AuthenticationRequest;
import com.toan.university_management.dto.request.IntrospectRequest;
import com.toan.university_management.dto.request.LogoutRequest;
import com.toan.university_management.dto.request.RefreshRequest;
import com.toan.university_management.dto.response.AuthenticationResponse;
import com.toan.university_management.dto.response.IntrospectResponse;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    IntrospectResponse introspect(IntrospectRequest request) throws ParseException, JOSEException;
    void logout(LogoutRequest request) throws ParseException, JOSEException;
    AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException;

}
