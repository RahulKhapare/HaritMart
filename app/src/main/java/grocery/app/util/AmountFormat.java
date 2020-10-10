package grocery.app.util;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountFormat {

    public static String getFormatedAmount(String amount){
        String value = "";
        try {
            int givenAmount  = Integer.parseInt(amount);
            value = NumberFormat.getNumberInstance(Locale.US).format(givenAmount);
        }catch (Exception e){
            value = amount + "";
        }
        return value;
    }
}
