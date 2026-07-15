package io.github.gaodeliang666.jobtrack.user.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationCurrentUserProvider implements CurrentUserProvider {

    private final long currentUserId;

    public ConfigurationCurrentUserProvider(@Value("${jobtrack.current-user-id}") long currentUserId) {
        if (currentUserId <= 0) {
            throw new IllegalArgumentException("jobtrack.current-user-id must be greater than zero");
        }
        this.currentUserId = currentUserId;
    }

    @Override
    public long currentUserId() {
        return currentUserId;
    }
}
