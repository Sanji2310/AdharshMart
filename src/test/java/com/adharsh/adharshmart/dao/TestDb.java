package com.adharsh.adharshmart.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import org.h2.tools.RunScript;

/**
 * Test-only DataSource factory: a fresh embedded H2 instance per call, bootstrapped from the
 * repository's schema.sql, exactly as Section 9 specifies (jdbc:h2:mem:test;DB_CLOSE_DELAY=-1).
 */
public final class TestDb {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private TestDb() {
    }

    public static HikariDataSource freshInMemory() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl("jdbc:h2:mem:test" + COUNTER.incrementAndGet() + ";DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(4);
        HikariDataSource ds = new HikariDataSource(config);
        try (var conn = ds.getConnection(); Reader reader = Files.newBufferedReader(schemaPath())) {
            RunScript.execute(conn, reader);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to bootstrap test schema", e);
        }
        return ds;
    }

    private static Path schemaPath() {
        Path fromRoot = Path.of("schema.sql");
        if (Files.exists(fromRoot)) {
            return fromRoot;
        }
        return Path.of("../schema.sql");
    }
}
