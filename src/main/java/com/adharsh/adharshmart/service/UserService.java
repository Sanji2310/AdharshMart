package com.adharsh.adharshmart.service;

import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Role;

/** F1 — registration and login business rules. */
public interface UserService {
    UserResponseDTO register(String name, String email, String password, Role role) throws ValidationException;

    UserResponseDTO login(String email, String password) throws ValidationException, UnauthorizedException;

    UserResponseDTO getById(Long id) throws com.adharsh.adharshmart.exception.NotFoundException;
}
