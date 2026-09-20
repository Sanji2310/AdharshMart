package com.adharsh.adharshmart.dto;

import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import java.time.LocalDateTime;

/** Public-facing user shape — deliberately excludes passwordHash (API contract rule #4). */
public final class UserResponseDTO {
    private final Long id;
    private final String name;
    private final String email;
    private final Role role;
    private final LocalDateTime createdAt;

    public UserResponseDTO(Long id, String name, String email, Role role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
