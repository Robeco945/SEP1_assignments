package sep2.shoppingcart;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sep2.shoppingcart.service.CartItemData;
import sep2.shoppingcart.service.CartService;
import sep2.shoppingcart.service.DatabaseConnection;
import sep2.shoppingcart.service.LocalizationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingCartController {

    @FXML private VBox rootVBox;
    @FXML private Label lblTitle;
    @FXML private Label lblLanguage;
    @FXML private Button btnLanguage;
    @FXML private VBox languageOptionsBox;
    @FXML private Label lblNumItems;
    @FXML private TextField tfNumItems;
    @FXML private Button btnPrepareItems;
    @FXML private Label lblItemsHeader;
    @FXML private VBox itemsContainer;
    @FXML private Button btnCalculateAll;
    @FXML private Label lblResult;
    @FXML private Label lblLocalTime;

    private Locale currentLocale = Locale.of("en", "US");
    private Map<String, String> localizedStrings;
    private final Map<String, Locale> localeByName = new LinkedHashMap<>();
    private final List<ItemRow> itemRows = new ArrayList<>();
    private final ShoppingCart shoppingCart = new ShoppingCart();
    private final CartService cartService = new CartService();

    @FXML
    public void initialize() {
        localeByName.put("English", Locale.of("en", "US"));
        localeByName.put("Suomi", Locale.of("fi", "FI"));
        localeByName.put("Svenska", Locale.of("sv", "SE"));
        localeByName.put("日本語", Locale.of("ja", "JP"));
        localeByName.put("العربية", Locale.of("ar", "AR"));

        languageOptionsBox.setVisible(false);
        languageOptionsBox.setManaged(false);

        setLanguage(currentLocale);
        tfNumItems.textProperty().addListener((obs, oldVal, newVal) -> lblResult.setText(""));
    }

    @FXML
    public void onToggleLanguageOptions(ActionEvent e) {
        boolean show = !languageOptionsBox.isVisible();
        languageOptionsBox.setManaged(show);
        languageOptionsBox.setVisible(show);
    }

    @FXML
    public void onPrepareItemsClick(ActionEvent e) {
        try {
            int numItems = Integer.parseInt(tfNumItems.getText());
            if (numItems <= 0 || numItems > 50) {
                lblResult.setText(localizedStrings.getOrDefault("error_num_items", "Please enter a valid item count"));
                return;
            }
            buildItemRows(numItems);
            lblResult.setText("");
        } catch (NumberFormatException ex) {
            lblResult.setText(localizedStrings.getOrDefault("error_num_items", "Please enter a valid item count"));
        }
    }

    @FXML
    public void onCalculateAllClick(ActionEvent e) {
        if (itemRows.isEmpty()) {
            lblResult.setText(localizedStrings.getOrDefault("error_num_items", "Please enter a valid item count"));
            return;
        }

        double overallTotal = 0.0;
        int totalItems = 0;
        List<CartItemData> cartItems = new ArrayList<>();
        for (ItemRow row : itemRows) {
            try {
                double price = Double.parseDouble(row.priceField.getText());
                int quantity = Integer.parseInt(row.quantityField.getText());

                if (price <= 0 || quantity <= 0) {
                    lblResult.setText(localizedStrings.getOrDefault("error_invalid_input", "Please enter valid positive numbers"));
                    return;
                }

                double itemTotal = shoppingCart.calculateItemCost(price, quantity);
                overallTotal += itemTotal;
                totalItems += quantity;
                cartItems.add(new CartItemData(row.index, price, quantity, itemTotal));
                row.totalValueLabel.setText(String.format(localeForDisplay(currentLocale), "%.2f", itemTotal));
            } catch (NumberFormatException ex) {
                lblResult.setText(localizedStrings.getOrDefault("error_invalid_input", "Please enter valid positive numbers"));
                return;
            }
        }

        String totalMessage = String.format(
                localizedStrings.getOrDefault("overall_total", "Overall total: %.2f"),
                overallTotal
        );

        try {
            cartService.saveCart(
                    totalItems,
                    overallTotal,
                    DatabaseConnection.getLanguageCode(currentLocale),
                    cartItems
            );
            lblResult.setText(totalMessage);
        } catch (RuntimeException ex) {
            lblResult.setText(totalMessage + " | " + localizedStrings.getOrDefault(
                    "error_save_cart",
                    "Data was calculated but not saved"
            ));
        }
    }

    private void setLanguage(Locale locale) {
        currentLocale = locale;
        lblResult.setText("");

        localizedStrings = LocalizationService.getLocalizedStrings(locale);

        lblTitle.setText(localizedStrings.getOrDefault("title", "Shopping Cart Calculator"));
        lblLanguage.setText(localizedStrings.getOrDefault("label_language", "Language"));
        lblNumItems.setText(localizedStrings.getOrDefault("label_num_items", "Number of items"));
        tfNumItems.setPromptText(localizedStrings.getOrDefault("prompt_num_items_short", "e.g. 3"));
        btnPrepareItems.setText(localizedStrings.getOrDefault("btn_prepare_items", "Prepare Items"));
        lblItemsHeader.setText(localizedStrings.getOrDefault("label_item_inputs", "Item Inputs"));
        btnCalculateAll.setText(localizedStrings.getOrDefault("btn_calculate_all", "Calculate Totals"));

        relocalizeLanguageSelector();
        relocalizeItemRows();

        displayLocalTime(locale);
        applyTextDirection(locale);
    }

    private void relocalizeLanguageSelector() {
        localeByName.clear();
        localeByName.put(localizedStrings.getOrDefault("lang_en", "English"), Locale.of("en", "US"));
        localeByName.put(localizedStrings.getOrDefault("lang_fi", "Suomi"), Locale.of("fi", "FI"));
        localeByName.put(localizedStrings.getOrDefault("lang_sv", "Svenska"), Locale.of("sv", "SE"));
        localeByName.put(localizedStrings.getOrDefault("lang_ja", "日本語"), Locale.of("ja", "JP"));
        localeByName.put(localizedStrings.getOrDefault("lang_ar", "العربية"), Locale.of("ar", "AR"));

        languageOptionsBox.getChildren().clear();
        for (Map.Entry<String, Locale> entry : localeByName.entrySet()) {
            Locale optionLocale = entry.getValue();
            Button optionButton = new Button(entry.getKey());
            optionButton.getStyleClass().add("language-option");
            optionButton.setMaxWidth(Double.MAX_VALUE);
            optionButton.setOnAction(event -> {
                languageOptionsBox.setVisible(false);
                languageOptionsBox.setManaged(false);
                if (!optionLocale.equals(currentLocale)) {
                    setLanguage(optionLocale);
                }
            });
            languageOptionsBox.getChildren().add(optionButton);
        }

        for (Map.Entry<String, Locale> entry : localeByName.entrySet()) {
            if (entry.getValue().equals(currentLocale)) {
                btnLanguage.setText(entry.getKey());
                return;
            }
        }
        btnLanguage.setText(localizedStrings.getOrDefault("lang_en", "English"));
    }

    private void buildItemRows(int count) {
        itemsContainer.getChildren().clear();
        itemRows.clear();

        for (int i = 1; i <= count; i++) {
            Label itemLabel = new Label(String.format(
                    localizedStrings.getOrDefault("label_item", "Item %d"), i
            ));

            TextField priceField = new TextField();
            priceField.setPromptText(localizedStrings.getOrDefault("label_price", "Price"));
            priceField.getStyleClass().add("small-input");

            TextField quantityField = new TextField();
            quantityField.setPromptText(localizedStrings.getOrDefault("label_quantity", "Quantity"));
            quantityField.getStyleClass().add("small-input");

            Label totalLabel = new Label(localizedStrings.getOrDefault("label_item_total", "Total:"));
            Label totalValueLabel = new Label("0.00");
            totalValueLabel.getStyleClass().add("row-total");

            HBox row = new HBox(8.0, itemLabel, priceField, quantityField, totalLabel, totalValueLabel);
            row.getStyleClass().add("item-row");
            HBox.setHgrow(priceField, Priority.ALWAYS);
            HBox.setHgrow(quantityField, Priority.ALWAYS);

            ItemRow itemRow = new ItemRow(i, itemLabel, priceField, quantityField, totalLabel, totalValueLabel);
            itemRows.add(itemRow);
            itemsContainer.getChildren().add(row);
        }
    }

    private void relocalizeItemRows() {
        for (ItemRow row : itemRows) {
            row.itemLabel.setText(String.format(
                    localizedStrings.getOrDefault("label_item", "Item %d"), row.index
            ));
            row.priceField.setPromptText(localizedStrings.getOrDefault("label_price", "Price"));
            row.quantityField.setPromptText(localizedStrings.getOrDefault("label_quantity", "Quantity"));
            row.totalLabel.setText(localizedStrings.getOrDefault("label_item_total", "Total:"));
        }
    }

    private Locale localeForDisplay(Locale locale) {
        return locale == null ? Locale.US : locale;
    }

    private void applyTextDirection(Locale locale) {
        String lang = locale.getLanguage();
        boolean isRTL = lang.equals("fa")
                || lang.equals("ur")
                || lang.equals("ar")
                || lang.equals("he");

        Platform.runLater(() -> {
            if (rootVBox != null) {
                rootVBox.setNodeOrientation(
                        isRTL ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT
                );
            }

            String alignment = isRTL
                    ? "-fx-text-alignment: right; -fx-alignment: center-right;"
                    : "-fx-text-alignment: left; -fx-alignment: center-left;";
            tfNumItems.setStyle(alignment);
            for (ItemRow row : itemRows) {
                row.priceField.setStyle(alignment);
                row.quantityField.setStyle(alignment);
            }
        });
    }

    private void displayLocalTime(Locale locale) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                localizedStrings.getOrDefault("time_format", "HH:mm:ss")
        ).withLocale(locale);

        String timeStr = String.format(
                localizedStrings.getOrDefault("current_time", "Current Time: %s"),
                now.format(formatter)
        );
        lblLocalTime.setText(timeStr);
    }

    private static class ItemRow {
        private final int index;
        private final Label itemLabel;
        private final TextField priceField;
        private final TextField quantityField;
        private final Label totalLabel;
        private final Label totalValueLabel;

        private ItemRow(int index, Label itemLabel, TextField priceField,
                        TextField quantityField, Label totalLabel, Label totalValueLabel) {
            this.index = index;
            this.itemLabel = itemLabel;
            this.priceField = priceField;
            this.quantityField = quantityField;
            this.totalLabel = totalLabel;
            this.totalValueLabel = totalValueLabel;
        }
    }
}
