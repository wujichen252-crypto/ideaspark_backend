package com.ideaspark.project.repository;

import com.ideaspark.project.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Page<User> findByUsernameContainingIgnoreCase(String username, Pageable pageable);

    boolean existsByEmail(String email);
}

