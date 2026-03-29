
import java.util.Locale;
import java.util.Scanner;
import sep2.shoppingcart.ShoppingCart;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Select language: 1. English, 2. Finnish, 3. Swedish, 4. Japanese");
        int choice = scanner.nextInt();
        
        OutputGenerator outGen;
        Locale selectedLocale;
        
        switch (choice) {
            case 2:
                selectedLocale = Locale.of("fi", "FI");
                break;
            case 3:
                selectedLocale = Locale.of("sv", "SE");
                break;
            case 4:
                selectedLocale = Locale.of("ja", "JP");
                break;
            default:
                selectedLocale = Locale.of("en", "US"); // Default language
        }
        outGen = new OutputGenerator(selectedLocale);
        ShoppingCart cart = new ShoppingCart();
        
        System.out.println(outGen.getMessage("prompt_num_items"));
        int numItems = scanner.nextInt();
        for (int i = 0; i < numItems; i++) {
            System.out.println(outGen.getMessage("prompt_price"));
            double price = scanner.nextDouble();
            System.out.println(outGen.getMessage("prompt_amount"));
            int amount = scanner.nextInt();
            cart.addItem(price, amount);
        }
        System.out.println(outGen.getMessage("prompt_total") + cart.getTotal());
        
        scanner.close(); 
    }
}
