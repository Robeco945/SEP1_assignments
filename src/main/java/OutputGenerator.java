
import java.util.Locale;
import java.util.ResourceBundle;

public class OutputGenerator {
    private ResourceBundle resourceBundle;

    public OutputGenerator(Locale locale) {
        this.resourceBundle = ResourceBundle.getBundle("MessagesBundle", locale);
    }

    public String getMessage(String key) {
        return resourceBundle.getString(key);
    }
}