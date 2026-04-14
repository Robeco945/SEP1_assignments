package sep2.shoppingcart.service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LocalizationServiceTest {

    private static final String SELECT_LOCALIZATION_SQL =
            "SELECT `key`, value FROM localization_strings WHERE language = ? ORDER BY id";

    @Test
    void getLocalizedStringsReturnsValuesForRequestedLanguage() throws Exception {
        Locale locale = Locale.of("en", "US");
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(eq(SELECT_LOCALIZATION_SQL))).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString("key")).thenReturn("title");
        when(resultSet.getString("value")).thenReturn("Shopping Cart Calculator");

        Map<String, String> localized;
        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(() -> DatabaseConnection.getLanguageCode(locale)).thenReturn("en_US");
            dbMock.when(DatabaseConnection::getConnection).thenReturn(connection);

            localized = LocalizationService.getLocalizedStrings(locale);
        }

        assertEquals("Shopping Cart Calculator", localized.get("title"));
        verify(statement).setString(1, "en_US");
    }

    @Test
    void getLocalizedStringsFallsBackToEnglishWhenPrimaryLanguageIsMissing() throws Exception {
        Locale locale = Locale.of("fi", "FI");

        Connection primaryConnection = mock(Connection.class);
        PreparedStatement primaryStatement = mock(PreparedStatement.class);
        ResultSet primaryResultSet = mock(ResultSet.class);

        Connection fallbackConnection = mock(Connection.class);
        PreparedStatement fallbackStatement = mock(PreparedStatement.class);
        ResultSet fallbackResultSet = mock(ResultSet.class);

        when(primaryConnection.prepareStatement(eq(SELECT_LOCALIZATION_SQL))).thenReturn(primaryStatement);
        when(primaryStatement.executeQuery()).thenReturn(primaryResultSet);
        when(primaryResultSet.next()).thenReturn(false);

        when(fallbackConnection.prepareStatement(eq(SELECT_LOCALIZATION_SQL))).thenReturn(fallbackStatement);
        when(fallbackStatement.executeQuery()).thenReturn(fallbackResultSet);
        when(fallbackResultSet.next()).thenReturn(true, false);
        when(fallbackResultSet.getString("key")).thenReturn("btn_calculate_all");
        when(fallbackResultSet.getString("value")).thenReturn("Calculate Totals");

        Map<String, String> localized;
        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(() -> DatabaseConnection.getLanguageCode(locale)).thenReturn("fi_FI");
            dbMock.when(DatabaseConnection::getConnection).thenReturn(primaryConnection, fallbackConnection);

            localized = LocalizationService.getLocalizedStrings(locale);
        }

        assertEquals("Calculate Totals", localized.get("btn_calculate_all"));
        verify(primaryStatement).setString(1, "fi_FI");
        verify(fallbackStatement).setString(1, "en_US");
    }

    @Test
    void getLocalizedStringsWrapsSqlErrors() throws Exception {
        Locale locale = Locale.of("en", "US");

        try (MockedStatic<DatabaseConnection> dbMock = mockStatic(DatabaseConnection.class)) {
            dbMock.when(() -> DatabaseConnection.getLanguageCode(locale)).thenReturn("en_US");
            dbMock.when(DatabaseConnection::getConnection).thenThrow(new SQLException("database down"));

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> LocalizationService.getLocalizedStrings(locale));

            assertTrue(thrown.getMessage().contains("Unable to load localization strings"));
            assertNotNull(thrown.getCause());
        }
    }
}
