package io.github.gaodeliang666.jobtrack.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.gaodeliang666.jobtrack.user.context.CurrentUserProvider;
import io.github.gaodeliang666.jobtrack.user.mapper.AppUserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrentUserServiceTests {

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private AppUserMapper appUserMapper;

    private CurrentUserService currentUserService;

    @BeforeEach
    void setUp() {
        currentUserService = new CurrentUserService(currentUserProvider, appUserMapper);
    }

    @Test
    void shouldPassProviderUserIdToMapperAndReturnSameId() {
        long currentUserId = 42L;
        when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
        when(appUserMapper.existsById(currentUserId)).thenReturn(true);

        long result = currentUserService.requireCurrentUserId();

        assertEquals(currentUserId, result);
        verify(currentUserProvider).currentUserId();
        verify(appUserMapper).existsById(currentUserId);
    }

    @Test
    void shouldRejectConfiguredUserThatDoesNotExist() {
        long currentUserId = 42L;
        when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
        when(appUserMapper.existsById(currentUserId)).thenReturn(false);

        assertThrows(IllegalStateException.class, currentUserService::requireCurrentUserId);
        verify(appUserMapper).existsById(currentUserId);
    }
}
