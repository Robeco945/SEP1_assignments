package convert;
import org.junit.jupiter.api.Test;

import convert.TemperatureConverter;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureConverterTest {
    TemperatureConverter converter = new TemperatureConverter();

    @Test
    void testConversions() {
        assertEquals(0.0, converter.fahrenheitToCelsius(32.0), 0.1);
        assertEquals(32.0, converter.celsiusToFahrenheit(0.0), 0.1);
        assertEquals(0.0, converter.kelvinToCelsius(273.15), 0.1);
    }

    @Test
    void testExtreme() {
        assertTrue(converter.isExtremeTemperature(-45));
        assertTrue(converter.isExtremeTemperature(55));
        assertFalse(converter.isExtremeTemperature(25));
    }
}