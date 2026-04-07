package sep2.shoppingcart.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public final class LocalizationService {
    private static final String SELECT_LOCALIZATION_SQL =
            "SELECT `key`, value FROM localization_strings WHERE language = ? ORDER BY id";

    private LocalizationService() {
    }

    public static Map<String, String> getLocalizedStrings(Locale locale) {
        String languageCode = DatabaseConnection.getLanguageCode(locale);
        Map<String, String> localized = fetchByLanguage(languageCode);
        if (localized.isEmpty() && !"en_US".equals(languageCode)) {
            localized = fetchByLanguage("en_US");
        }
        return localized;
    }

    private static Map<String, String> fetchByLanguage(String languageCode) {
        Map<String, String> localized = new LinkedHashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_LOCALIZATION_SQL)) {
            statement.setString(1, languageCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    localized.put(resultSet.getString("key"), resultSet.getString("value"));
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("Unable to load localization strings", ex);
        }
        return localized;
    }
}
