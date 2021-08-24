package hotels.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SparkServiceTest {

    private final SparkService sparkService = new SparkService();

    @Test
    void sparkSession() {
        assertDoesNotThrow(sparkService::sparkSession);
    }

    @Test
    void sparkSessionNotNull() {
        assertNotNull(sparkService.sparkSession());
    }

}