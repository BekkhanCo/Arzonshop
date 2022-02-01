package sample.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class Util {

    public static Locale locale = new Locale("en", "UK");
    public static String pattern = "###,###,###.###";
    static final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
    public static DecimalFormat decimalFormatForDouble;

    static {
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(' ');
        decimalFormatForDouble = new DecimalFormat(pattern, symbols);
    }

    public static NumericChangeListenerDecimal setNumericTextFieldDecimal(TextField textField){
        return new NumericChangeListenerDecimal(textField, null);
    }

    public static double decimalFormatterToDouble(String value, double default_){
        if (value == null){
            return default_;
        }
        if (value.trim().isEmpty()){
            return default_;
        }
        try {
            return decimalFormatForDouble.parse(value).doubleValue();
        } catch (ParseException e) {
        }
        return default_;
    }

    public static Double decimalFormatterToDouble(String value){
        if (value == null){
            return null;
        }
        try {
            return decimalFormatForDouble.parse(value).doubleValue();
        } catch (ParseException e) {
        }
        return null;
    }
    public static String doubleToString(String value){
        return decimalFormatForDouble.format(decimalFormatterToDouble(value));
    }
    public static void alert(String header,String msg){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка...");
        alert.setHeaderText(header);
        alert.getDialogPane().setPrefWidth(400);
        alert.getDialogPane().setContent(new TextFlow(new Text(msg)));
        alert.show();
    }
}
