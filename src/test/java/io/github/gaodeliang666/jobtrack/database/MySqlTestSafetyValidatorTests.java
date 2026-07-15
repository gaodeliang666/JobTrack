package io.github.gaodeliang666.jobtrack.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySqlTestSafetyValidatorTests {

    private static final String SAFE_URL = "jdbc:mysql://localhost:3306/jobtrack_test";
    private static final String SAFE_USERNAME = "jobtrack_test";
    private static final String SAFE_PASSWORD = "test-only-placeholder";
    private static final String SAFE_USER_ID = "1";

    @Test
    void shouldAcceptSafeLocalTestConfiguration() {
        assertDoesNotThrow(() -> validate(SAFE_URL, SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectMissingUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(null, SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectNonMysqlJdbcProtocol() {
        assertThrows(IllegalArgumentException.class,
                () -> validate("jdbc:postgresql://localhost/jobtrack_test",
                        SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectNonLocalHost() {
        assertThrows(IllegalArgumentException.class,
                () -> validate("jdbc:mysql://database.example/jobtrack_test",
                        SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectDatabaseWithoutTestSuffix() {
        assertThrows(IllegalArgumentException.class,
                () -> validate("jdbc:mysql://localhost:3306/jobtrack",
                        SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectRootUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, "root", SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectMissingUsername() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, null, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectBlankPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, " ", SAFE_USER_ID));
    }

    @Test
    void shouldRejectNullPassword() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, null, SAFE_USER_ID));
    }

    @Test
    void shouldRejectMissingCurrentUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, SAFE_PASSWORD, null));
    }

    @Test
    void shouldRejectNonNumericCurrentUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, SAFE_PASSWORD, "not-a-number"));
    }

    @Test
    void shouldRejectMalformedMysqlUrl() {
        assertThrows(IllegalArgumentException.class,
                () -> validate("jdbc:mysql://[invalid/jobtrack_test",
                        SAFE_USERNAME, SAFE_PASSWORD, SAFE_USER_ID));
    }

    @Test
    void shouldRejectZeroCurrentUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, SAFE_PASSWORD, "0"));
    }

    @Test
    void shouldRejectNegativeCurrentUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> validate(SAFE_URL, SAFE_USERNAME, SAFE_PASSWORD, "-1"));
    }

    private void validate(String url, String username, String password, String currentUserId) {
        MySqlTestSafetyValidator.validate(url, username, password, currentUserId);
    }
}
