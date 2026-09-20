package com.adharsh.adharshmart.listener;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.adharsh.adharshmart.util.AppConfig;
import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Owns the connection pool lifecycle (mandatory engineering rule #5 — Singleton pattern).
 * No {@code DriverManager.getConnection()} call exists anywhere outside this class.
 * Reads config.properties (JDBC URL, credentials) via {@link AppConfig}, falling back to
 * environment variables so the same .war runs unmodified across environments.
 */
@WebListener
public class DataSourceListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(DataSourceListener.class);
    private static HikariDataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.h2.Driver");
        config.setJdbcUrl(prop("db.url", "jdbc:h2:mem:adharshmart;DB_CLOSE_DELAY=-1"));
        config.setUsername(prop("db.username", "sa"));
        config.setPassword(prop("db.password", ""));
        config.setMaximumPoolSize(Integer.parseInt(prop("db.pool.size", "10")));
        config.setPoolName("adharshmart-pool");

        dataSource = new HikariDataSource(config);
        LOG.info("HikariCP pool initialized against {}", config.getJdbcUrl());

        runBootstrapScriptsIfNeeded();
        sce.getServletContext().setAttribute("dataSource", dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOG.info("HikariCP pool closed");
        }
    }

    public static HikariDataSource getDataSource() {
        return dataSource;
    }

    public static String prop(String key, String fallback) {
        return AppConfig.get(key, fallback);
    }

    /** Applies schema.sql/seed.sql on first boot against the embedded H2 instance for a zero-setup demo. */
    private void runBootstrapScriptsIfNeeded() {
        try (Connection conn = dataSource.getConnection()) {
            executeScriptIfPresent(conn, "schema.sql");
            executeScriptIfPresent(conn, "seed.sql");
        } catch (Exception e) {
            LOG.warn("Bootstrap schema/seed execution skipped: {}", e.getMessage());
        }
    }

    private void executeScriptIfPresent(Connection conn, String fileName) throws IOException {
        Path path = Path.of(fileName);
        try (Reader reader = Files.exists(path)
                ? Files.newBufferedReader(path)
                : openClasspathReader(fileName)) {
            if (reader == null) {
                return;
            }
            RunScript.execute(conn, reader);
            LOG.info("Executed {}", fileName);
        } catch (Exception e) {
            LOG.warn("{} execution failed: {}", fileName, e.getMessage());
        }
    }

    private Reader openClasspathReader(String fileName) throws IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream(fileName);
        return in == null ? null : new java.io.InputStreamReader(in);
    }
}
