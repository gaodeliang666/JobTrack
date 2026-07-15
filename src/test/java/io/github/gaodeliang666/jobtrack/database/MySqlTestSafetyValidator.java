package io.github.gaodeliang666.jobtrack.database;

import java.net.URI;
import java.util.regex.Pattern;

public final class MySqlTestSafetyValidator {

    private static final String JDBC_MYSQL_PREFIX = "jdbc:mysql://";
    private static final Pattern TEST_DATABASE_NAME = Pattern.compile("[A-Za-z0-9_]+_test");

    private MySqlTestSafetyValidator() {
    }

    public static void validate(String url, String username, String password, String currentUserId) {
        URI mysqlUri = parseMysqlUri(url);
        validateHost(mysqlUri.getHost());
        validateDatabaseName(mysqlUri.getPath());
        validateUsername(username);
        validatePassword(password);
        validateCurrentUserId(currentUserId);
    }

    private static URI parseMysqlUri(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("MySQL test URL is required");
        }
        if (!url.startsWith(JDBC_MYSQL_PREFIX)) {
            throw new IllegalArgumentException("MySQL test URL must use the jdbc:mysql protocol");
        }
        try {
            return URI.create(url.substring("jdbc:".length()));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("MySQL test URL is invalid", exception);
        }
    }

    private static void validateHost(String host) {
        if (!("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host))) {
            throw new IllegalArgumentException("MySQL tests are restricted to localhost");
        }
    }

    private static void validateDatabaseName(String path) {
        String databaseName = path == null || path.length() <= 1 ? "" : path.substring(1);
        if (!TEST_DATABASE_NAME.matcher(databaseName).matches()) {
            throw new IllegalArgumentException("MySQL test database name must end with _test");
        }
    }

    private static void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("MySQL test username is required");
        }
        if ("root".equalsIgnoreCase(username.trim())) {
            throw new IllegalArgumentException("MySQL integration tests must not use the root account");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("MySQL test password is required");
        }
    }

    private static void validateCurrentUserId(String currentUserId) {
        if (currentUserId == null || currentUserId.isBlank()) {
            throw new IllegalArgumentException("MySQL test current user ID is required");
        }
        try {
            if (Long.parseLong(currentUserId) <= 0) {
                throw new IllegalArgumentException("MySQL test current user ID must be positive");
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("MySQL test current user ID must be a positive integer", exception);
        }
    }
}
