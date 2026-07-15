package io.github.gaodeliang666.jobtrack.user.service;

import org.springframework.stereotype.Service;

import io.github.gaodeliang666.jobtrack.user.context.CurrentUserProvider;
import io.github.gaodeliang666.jobtrack.user.mapper.AppUserMapper;

@Service
public class CurrentUserService {

    private final CurrentUserProvider currentUserProvider;
    private final AppUserMapper appUserMapper;

    public CurrentUserService(CurrentUserProvider currentUserProvider, AppUserMapper appUserMapper) {
        this.currentUserProvider = currentUserProvider;
        this.appUserMapper = appUserMapper;
    }

    public long requireCurrentUserId() {
        long currentUserId = currentUserProvider.currentUserId();
        if (!appUserMapper.existsById(currentUserId)) {
            throw new IllegalStateException("Configured current user does not exist");
        }
        return currentUserId;
    }
}
