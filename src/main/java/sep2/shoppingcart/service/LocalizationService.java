package sep2.shoppingcart.service;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public final class LocalizationService {
    private LocalizationService() {
    }

    public static Map<String, String> getLocalizedStrings(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("MessagesBundle", locale);
        Map<String, String> localized = new LinkedHashMap<>();
        for (String key : bundle.keySet()) {
            localized.put(key, bundle.getString(key));
        }
        return localized;
    }
}
