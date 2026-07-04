package com.stockmate.stockmate_backend.repository;

import com.stockmate.stockmate_backend.entity.Role;
import com.stockmate.stockmate_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}