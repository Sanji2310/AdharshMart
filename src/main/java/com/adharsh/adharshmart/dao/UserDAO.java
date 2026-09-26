package com.adharsh.adharshmart.dao;

import com.adharsh.adharshmart.model.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Data access abstraction for the {@code users} table (DAO pattern). */
public interface UserDAO {
    User create(User user) throws SQLException;

    Optional<User> findById(Long id) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    boolean existsByEmail(String email) throws SQLException;

    List<User> findAll() throws SQLException;
}
