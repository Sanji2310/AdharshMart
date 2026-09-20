package com.adharsh.adharshmart.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adharsh.adharshmart.model.Role;
import com.adharsh.adharshmart.model.User;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserDAOTest {

    private HikariDataSource dataSource;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        dataSource = TestDb.freshInMemory();
        userDAO = new UserDAOImpl(dataSource);
    }

    @AfterEach
    void tearDown() {
        dataSource.close();
    }

    @Test
    void createAssignsGeneratedId() throws Exception {
        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("buyer.test@example.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.BUYER);

        User created = userDAO.create(user);

        assertTrue(created.getId() > 0);
    }

    @Test
    void findByEmailReturnsCreatedUser() throws Exception {
        User user = new User();
        user.setName("Find Me");
        user.setEmail("findme@example.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.SELLER);
        userDAO.create(user);

        var found = userDAO.findByEmail("findme@example.com");

        assertTrue(found.isPresent());
        assertEquals("Find Me", found.get().getName());
        assertEquals(Role.SELLER, found.get().getRole());
    }

    @Test
    void existsByEmailReflectsState() throws Exception {
        assertFalse(userDAO.existsByEmail("nobody@example.com"));

        User user = new User();
        user.setName("Someone");
        user.setEmail("somebody@example.com");
        user.setPasswordHash("hashed");
        user.setRole(Role.BUYER);
        userDAO.create(user);

        assertTrue(userDAO.existsByEmail("somebody@example.com"));
    }

    @Test
    void emailUniqueConstraintIsEnforced() throws Exception {
        User first = new User();
        first.setName("First");
        first.setEmail("dup@example.com");
        first.setPasswordHash("hashed");
        first.setRole(Role.BUYER);
        userDAO.create(first);

        User second = new User();
        second.setName("Second");
        second.setEmail("dup@example.com");
        second.setPasswordHash("hashed");
        second.setRole(Role.BUYER);

        org.junit.jupiter.api.Assertions.assertThrows(java.sql.SQLException.class, () -> userDAO.create(second));
    }
}
