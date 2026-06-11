package com.example.badminton_management.repository;

import com.example.badminton_management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String PhoneNumber);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    @Query("SELECT u from User u JOIN FETCH u.role WHERE u.username = :username")
    Optional<User> findUsernameWithRole(@Param("username") String username);

}
