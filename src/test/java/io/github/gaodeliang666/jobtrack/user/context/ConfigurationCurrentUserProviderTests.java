package io.github.gaodeliang666.jobtrack.user.context;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfigurationCurrentUserProviderTests {

    @Test
    void shouldReturnConfiguredPositiveUserIdWithoutRequestContext() {
        ConfigurationCurrentUserProvider provider = new ConfigurationCurrentUserProvider(42L);

        assertEquals(42L, provider.currentUserId());
    }

    @Test
    void providerContractShouldNotAcceptCallerSuppliedUserId() throws NoSuchMethodException {
        assertEquals(0, CurrentUserProvider.class.getMethod("currentUserId").getParameterCount());
    }

    @Test
    void shouldRejectZeroUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConfigurationCurrentUserProvider(0L));
    }

    @Test
    void shouldRejectNegativeUserId() {
        assertThrows(IllegalArgumentException.class,
                () -> new ConfigurationCurrentUserProvider(-1L));
    }
}
