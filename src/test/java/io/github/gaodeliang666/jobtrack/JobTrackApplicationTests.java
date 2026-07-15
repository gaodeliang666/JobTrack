package io.github.gaodeliang666.jobtrack;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import io.github.gaodeliang666.jobtrack.user.mapper.AppUserMapper;

@SpringBootTest
@ActiveProfiles("no-db-test")
class JobTrackApplicationTests {

    @MockitoBean
    private AppUserMapper appUserMapper;

    @Test
    void contextLoads() {
    }
}
