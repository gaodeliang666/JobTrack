package io.github.gaodeliang666.jobtrack.database;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

public class MySqlTestSafetyGuard implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        if (!Boolean.parseBoolean(environment.getProperty("jobtrack.mysql.tests", "false"))) {
            throw new IllegalStateException("MySQL integration tests require -Djobtrack.mysql.tests=true");
        }

        if (!Boolean.parseBoolean(environment.getProperty("spring.flyway.clean-disabled"))) {
            throw new IllegalStateException("MySQL integration tests require Flyway clean to remain disabled");
        }

        MySqlTestSafetyValidator.validate(
                environment.getProperty("spring.datasource.url"),
                environment.getProperty("spring.datasource.username"),
                environment.getProperty("spring.datasource.password"),
                environment.getProperty("jobtrack.current-user-id"));
    }
}
