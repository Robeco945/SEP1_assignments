
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField number1Field;
    @FXML private TextField number2Field;
    @FXML private Label resultLabel;

    @FXML
    private void onCalculateClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            double sum        = num1 + num2;
            double product    = num1 * num2;
            double difference = num1 - num2;
            Double quotient   = (num2 != 0) ? num1 / num2 : null;

            String divText = (quotient != null)
                    ? String.format("%.4f", quotient)
                    : "N/A (division by zero)";

            resultLabel.setText(String.format(
                    "Sum: %.4f%nProduct: %.4f%nSubtract: %.4f%nDivide: %s",
                    sum, product, difference, divText));

            // Save to DB
            ResultService.saveResult(num1, num2, sum, product, difference, quotient);

        } catch (NumberFormatException e) {
            resultLabel.setText("Please enter valid numbers!");
        }
    }
}