package com.adharsh.adharshmart.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.adharsh.adharshmart.dao.UserDAO;
import com.adharsh.adharshmart.dto.UserResponseDTO;
import com.adharsh.adharshmart.exception.UnauthorizedException;
import com.adharsh.adharshmart.exception.ValidationException;
import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.adharsh.adharshmart.util.PasswordUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    @Test
    void registerRejectsAdminRole() {
        assertThrows(ValidationException.class,
                () -> userService.register("Name", "a@b.com", "password123", Role.ADMIN));
    }

    @Test
    void registerRejectsShortPassword() {
        assertThrows(ValidationException.class,
                () -> userService.register("Name", "a@b.com", "short", Role.BUYER));
    }

    @Test
    void registerRejectsInvalidEmail() {
        assertThrows(ValidationException.class,
                () -> userService.register("Name", "not-an-email", "password123", Role.BUYER));
    }

    @Test
    void registerRejectsDuplicateEmail() throws Exception {
        when(userDAO.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> userService.register("Name", "taken@example.com", "password123", Role.BUYER));
    }

    @Test
    void registerHashesPasswordBeforePersisting() throws Exception {
        when(userDAO.existsByEmail(anyString())).thenReturn(false);
        when(userDAO.create(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            u.setCreatedAt(java.time.LocalDateTime.now());
            return u;
        });

        UserResponseDTO result = userService.register("Name", "new@example.com", "password123", Role.BUYER);

        assertEquals("new@example.com", result.getEmail());
        var captor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userDAO).create(captor.capture());
        assertNotEquals("password123", captor.getValue().getPasswordHash()); // never stored in plaintext
    }

    @Test
    void loginRejectsWrongPassword() throws Exception {
        User stored = new User();
        stored.setId(1L);
        stored.setEmail("user@example.com");
        stored.setPasswordHash(PasswordUtil.hash("correct-password"));
        stored.setRole(Role.BUYER);
        when(userDAO.findByEmail("user@example.com")).thenReturn(Optional.of(stored));

        assertThrows(UnauthorizedException.class, () -> userService.login("user@example.com", "wrong-password"));
    }

    @Test
    void loginSucceedsWithCorrectPassword() throws Exception {
        User stored = new User();
        stored.setId(1L);
        stored.setEmail("user@example.com");
        stored.setPasswordHash(PasswordUtil.hash("correct-password"));
        stored.setRole(Role.BUYER);
        stored.setName("User");
        when(userDAO.findByEmail("user@example.com")).thenReturn(Optional.of(stored));

        UserResponseDTO result = userService.login("user@example.com", "correct-password");

        assertEquals("user@example.com", result.getEmail());
    }
}
