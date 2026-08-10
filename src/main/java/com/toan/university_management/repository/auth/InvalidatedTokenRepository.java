package com.toan.university_management.repository.auth;

import com.toan.university_management.entity.auth.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {
}


