package nl.bamischrijft.blackmarket.util;

public class TextUtil {

    public static String truncate(String input, int maxLength) {
        return input.length() > maxLength ? input.substring(0, maxLength-3) + "..." : input;
    }

}
