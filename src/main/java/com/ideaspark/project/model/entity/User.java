package com.ideaspark.project.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "account", nullable = false, length = 64)
    private String account;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "role", nullable = false, length = 16)
    private String role;

    @Column(name = "state", nullable = false, length = 16)
    private String state;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}

