public class TemperatureConverter {
    public static double fahrenheitToCelsius (double a){
        return((a - 32)*5);
    }
    public static double celsiusToFahrenheit (double a){
        return(a*9/5+32);
    }
    public static boolean isExtremeTemperature (double a){
        return (a < -40 || a > 50);
        }
    public static double kelvinToCelsius (double a){
        return(a - 273.15);
    }

