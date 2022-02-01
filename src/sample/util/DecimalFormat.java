package sample.util;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DecimalFormat {
    public static Locale locale = new Locale("en", "UK");
    public static String pattern = "###,##0.###";
    static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    private static final java.text.DecimalFormat decimalFormat;
    public static java.text.DecimalFormat decimalFormatForDouble;

    private static java.text.DecimalFormat format=new java.text.DecimalFormat("#,##0");

    static {
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        decimalFormat = new java.text.DecimalFormat(pattern, symbols);
        decimalFormatForDouble = new java.text.DecimalFormat(pattern, symbols);
    }
    public static String decimalFormatter(Double value){
        if (value == null){
            return "";
        }
        try {

            return decimalFormat.format(value);
        }catch (Exception e){

        }
        return "";
    }

    public static String doubleFormat(String value){
//        String data=value.replaceAll(String.valueOf((char)160),"").replaceAll(",",".");
        String data=value.replaceAll(" ","").replaceAll(String.valueOf((char)160),"").replaceAll(",",".");
        return data;
    }

    public static String  deimalFormatInt(double value) {
        return format.format(value);
    }
}
