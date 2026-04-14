package sep2.shoppingcart.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseConnectionTest {

    @Test
    void getLanguageCodeReturnsDefaultForNullLocale() {
        assertEquals("en_US", DatabaseConnection.getLanguageCode(null));
    }

    @Test
    void getLanguageCodeUsesLanguageAndCountryWhenAvailable() {
        assertEquals("ja_JP", DatabaseConnection.getLanguageCode(Locale.of("ja", "JP")));
    }

    @Test
    void getLanguageCodeDefaultsCountryToUsWhenMissing() {
        assertEquals("ar_US", DatabaseConnection.getLanguageCode(Locale.of("ar")));
    }

    @Test
    void buildUrlContainsExpectedJdbcParameters() throws Exception {
        Method buildUrlMethod = DatabaseConnection.class.getDeclaredMethod("buildUrl");
        buildUrlMethod.setAccessible(true);

        String url = (String) buildUrlMethod.invoke(null);

        assertTrue(url.startsWith("jdbc:mysql://"));
        assertTrue(url.contains("?useSSL=false"));
        assertTrue(url.contains("allowPublicKeyRetrieval=true"));
        assertTrue(url.contains("characterEncoding=UTF-8"));
    }
}
