package io.github.gaodeliang666.jobtrack.database;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MySqlMetadataNumberConversionTests {

    @Test
    void shouldConvertIntegerMetadataValue() {
        assertEquals(6, MySqlIntegrationTests.nullableInteger(Integer.valueOf(6)));
    }

    @Test
    void shouldConvertLongMetadataValue() {
        assertEquals(6, MySqlIntegrationTests.nullableInteger(Long.valueOf(6L)));
    }

    @Test
    void shouldConvertOtherNumberImplementations() {
        assertEquals(6, MySqlIntegrationTests.nullableInteger(BigInteger.valueOf(6L)));
    }

    @Test
    void shouldKeepNullMetadataValue() {
        assertNull(MySqlIntegrationTests.nullableInteger(null));
    }

    @Test
    void shouldRejectNonNumericMetadataValue() {
        assertThrows(IllegalArgumentException.class,
                () -> MySqlIntegrationTests.nullableInteger("6"));
    }
}
