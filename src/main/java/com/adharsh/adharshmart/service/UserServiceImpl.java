package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dao.UserDAO;
import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.DataAccessException;
import com.adharsh.adharshmart.exception.NotFoundException;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.adharsh.adharshmart.util.PasswordUtil;
import com.adharsh.adharshmart.util.ValidationUtil;
import java.sql.SQLException;

/** Business rules for F1 — depends only on the {@link UserDAO} interface (SOLID). */
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserResponseDTO register(String name, String email, String password, Role role) throws ValidationException {
        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("name", "VALIDATION_ERROR", "Name is required");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("email", "VALIDATION_ERROR", "A valid email is required");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ValidationException("password", "VALIDATION_ERROR", "Password must be at least 8 characters");
        }
        if (role == null || role == Role.ADMIN) {
            throw new ValidationException("role", "VALIDATION_ERROR", "Role must be BUYER or SELLER");
        }
        try {
            if (userDAO.existsByEmail(email)) {
                throw new ValidationException("email", "EMAIL_TAKEN", "An account with this email already exists");
            }
            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hash(password));
            user.setRole(role);
            User created = userDAO.create(user);
            return UserResponseDTO.from(created);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to register user", e);
        }
    }

    @Override
    public UserResponseDTO login(String email, String password) throws ValidationException, UnauthorizedException {
        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            throw new ValidationException("email", "VALIDATION_ERROR", "Email and password are required");
        }
        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new UnauthorizedException("Invalid email or password", false));
            if (!PasswordUtil.matches(password, user.getPasswordHash())) {
                throw new UnauthorizedException("Invalid email or password", false);
            }
            return UserResponseDTO.from(user);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to authenticate user", e);
        }
    }

    @Override
    public UserResponseDTO getById(Long id) throws NotFoundException {
        try {
            User user = userDAO.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
            return UserResponseDTO.from(user);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to load user", e);
        }
    }
}
