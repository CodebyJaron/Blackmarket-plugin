package nl.bamischrijft.blackmarket.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class BalanceUtil {

    public static String convert(double d) {
        DecimalFormat f = ((DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("NL", "nl")));
        f.applyPattern("¤#,##0.00;¤ -#");
        String re = f.format(d);
        return re.endsWith(",00") ? re.substring(0, re.length()-3) : re;
    }

}
