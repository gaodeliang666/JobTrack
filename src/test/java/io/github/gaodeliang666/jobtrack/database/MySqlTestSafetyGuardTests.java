package io.github.gaodeliang666.jobtrack.database;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySqlTestSafetyGuardTests {

    private static final String SAFE_URL = "jdbc:mysql://localhost:3306/jobtrack_test";
    private static final String SAFE_USERNAME = "jobtrack_test";
    private static final String SAFE_PASSWORD = "test-only-placeholder";
    private static final String SAFE_USER_ID = "1";

    @Test
    void shouldAcceptSafeEffectiveConfiguration() {
        assertDoesNotThrow(() -> initialize(safeEnvironment()));
    }

    @Test
    void shouldRejectRemoteEffectiveUrl() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("spring.datasource.url", "jdbc:mysql://database.example/jobtrack_test");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectEffectiveDatabaseWithoutTestSuffix() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("spring.datasource.url", "jdbc:mysql://localhost:3306/jobtrack");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectEffectiveRootUsername() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("spring.datasource.username", "root");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectMissingEffectivePassword() {
        MockEnvironment environment = safeEnvironmentWithout("spring.datasource.password");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectNonPositiveEffectiveCurrentUserId() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("jobtrack.current-user-id", "0");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectWhenFlywayCleanIsEnabled() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("spring.flyway.clean-disabled", "false");

        assertThrows(IllegalStateException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectRemoteEffectiveUrlEvenWhenRawTestUrlIsSafe() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("JOBTRACK_TEST_DB_URL", SAFE_URL)
                .withProperty("spring.datasource.url", "jdbc:mysql://database.example/jobtrack_test");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectEffectiveRootUsernameEvenWhenRawTestUsernameIsSafe() {
        MockEnvironment environment = safeEnvironment()
                .withProperty("JOBTRACK_TEST_DB_USERNAME", SAFE_USERNAME)
                .withProperty("spring.datasource.username", "root");

        assertThrows(IllegalArgumentException.class, () -> initialize(environment));
    }

    @Test
    void shouldRejectMissingFlywayCleanDisabledProperty() {
        MockEnvironment environment = safeEnvironmentWithout("spring.flyway.clean-disabled");

        assertThrows(IllegalStateException.class, () -> initialize(environment));
    }

    private MockEnvironment safeEnvironment() {
        return safeEnvironmentWithout(null);
    }

    private MockEnvironment safeEnvironmentWithout(String omittedProperty) {
        MockEnvironment environment = new MockEnvironment()
                .withProperty("jobtrack.mysql.tests", "true");
        addUnlessOmitted(environment, omittedProperty, "spring.datasource.url", SAFE_URL);
        addUnlessOmitted(environment, omittedProperty, "spring.datasource.username", SAFE_USERNAME);
        addUnlessOmitted(environment, omittedProperty, "spring.datasource.password", SAFE_PASSWORD);
        addUnlessOmitted(environment, omittedProperty, "jobtrack.current-user-id", SAFE_USER_ID);
        addUnlessOmitted(environment, omittedProperty, "spring.flyway.clean-disabled", "true");
        return environment;
    }

    private void addUnlessOmitted(MockEnvironment environment, String omittedProperty, String name, String value) {
        if (!name.equals(omittedProperty)) {
            environment.setProperty(name, value);
        }
    }

    private void initialize(MockEnvironment environment) {
        GenericApplicationContext applicationContext = new GenericApplicationContext();
        applicationContext.setEnvironment(environment);
        new MySqlTestSafetyGuard().initialize(applicationContext);
    }
}
