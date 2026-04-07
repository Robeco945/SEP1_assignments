package sep2.shoppingcart.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;

public final class DatabaseConnection {
    private static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "shopping_cart_localization");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "root");

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(buildUrl(), DB_USER, DB_PASSWORD);
    }

    public static String getLanguageCode(Locale locale) {
        if (locale == null) {
            return "en_US";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (country == null || country.isBlank()) {
            return language + "_US";
        }
        return language + "_" + country;
    }

    private static String buildUrl() {
        return "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }
}