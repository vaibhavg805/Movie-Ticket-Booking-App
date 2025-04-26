package com.vaibhav.booking.system.repository;

import com.vaibhav.booking.system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    //Optional<User> findById(Long id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
