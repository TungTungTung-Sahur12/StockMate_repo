package com.stockmate.stockmate_backend.userauthentication.repository;

import com.stockmate.stockmate_backend.shared.entity.Role;
import com.stockmate.stockmate_backend.shared.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}
