package grocery.app.util;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {

    public static boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private String capitalize(String capString){
        String returnValue = "";
        try {
            StringBuffer capBuffer = new StringBuffer();
            Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()){
                ((Matcher) capMatcher).appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }
            returnValue = capMatcher.appendTail(capBuffer).toString();
        }catch (Exception e){
            returnValue = capString;
        }
        return  returnValue;
    }
}
