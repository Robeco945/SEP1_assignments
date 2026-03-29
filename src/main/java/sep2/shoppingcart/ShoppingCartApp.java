package sep2.shoppingcart;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;

public class ShoppingCartApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlUrl = getClass().getResource("/sep2/shoppingcart/shopping-cart-view.fxml");
        FXMLLoader loader = new FXMLLoader(fxmlUrl);

        VBox root = loader.load();
        Scene scene = new Scene(root, 520, 620);
        scene.getStylesheets().add(getClass().getResource("/sep2/shoppingcart/style.css").toExternalForm());

        primaryStage.setTitle("Roberto Caretto - Shopping Cart Calculator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
