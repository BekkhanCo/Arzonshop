package sample.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.ParseException;

public class NumericChangeListenerDecimal implements ChangeListener<String> {
    private TextField textField;
    private Double max;
    private Double min;
    private ChangeListener<String> changeListenerAfter;
    private ChangeListener<String> changeListenerBefore;
    private static final DecimalFormat decimalFormat;
    private boolean stop;
    static {
        decimalFormat = new DecimalFormat("###,###.######", Util.symbols);
    }

    public NumericChangeListenerDecimal(TextField textField) {

        this.textField = textField;
        this.textField.textProperty().addListener(this);

    }

    public NumericChangeListenerDecimal(TextField textField, Double max) {
        this.textField = textField;
        this.max = max;
        this.textField.textProperty().addListener(this);
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
        if (t1 == null){
            return;
        }
        if (isStop())
            return;

        if (changeListenerBefore != null){
            changeListenerBefore.changed(observableValue, s, textField.getText());
        }
        onBefore(observableValue, s, textField.getText());

        if (t1.isEmpty()){
            if (changeListenerAfter != null){
                changeListenerAfter.changed(observableValue, s, t1);
            }
            onAfter(observableValue, s, t1);
            return;
        }


        if (t1.endsWith(",")){
            textField.deleteText(t1.length() - 1, t1.length());
            textField.insertText(   textField.getText().length(), ".");
            if (changeListenerAfter != null){
                changeListenerAfter.changed(observableValue, s, t1);
            }
            onAfter(observableValue, s, t1);
            return;
        }

        if (t1.length() == 1 && t1.startsWith("-")){

            return;
        }
        int dot = 0;
        for (int i = 0; i < t1.length(); i++){
            if (t1.charAt(i) == '.'){
                dot++;
            }
        }
        String val = s;

        if (t1.endsWith(".") && dot == 1){
            if (changeListenerAfter != null){
                changeListenerAfter.changed(observableValue, s, t1);
            }
            onAfter(observableValue, s, t1);
            return;
        }
        if (t1.endsWith("0") && dot > 0 ){
            if (changeListenerAfter != null){
                changeListenerAfter.changed(observableValue, s, t1);
            }
            onAfter(observableValue, s, t1);
            return;
        }
        try {
            val = decimalFormatter(Double.parseDouble(t1));
        } catch (Exception e) {
            try {
                val = decimalFormatter(decimalFormat.parse(t1.trim()).doubleValue());
            } catch (ParseException parseException) {
                val = s;
            }
        }

        textField.textProperty().removeListener(this);
        textField.setText(val);
        if (max != null || min != null){
            try {
                Number number = decimalFormat.parse(val);
                if (max != null){
                    if (number.doubleValue() > max){
                        val = decimalFormatter(max);
                    }
                }
                if (min != null){
                    if (number.doubleValue() < min){
                        val = decimalFormatter(min);
                    }
                }
            } catch (ParseException e) {
            }
            try {
                textField.setText(val);
            }catch (Exception e){

            }
        }
        textField.textProperty().addListener(this);

        if (changeListenerAfter != null){
            changeListenerAfter.changed(observableValue, s, val);
        }
        onAfter(observableValue, s, val);
    }

    public void onAfter(ObservableValue<? extends String> observableValue, String s, String t1){

    }
    public void onBefore(ObservableValue<? extends String> observableValue, String s, String t1){

    }

    public void onAfter(ChangeListener<String> changeListener){
        this.changeListenerAfter = changeListener;
    }
    public void onBefore(ChangeListener<String> changeListener ){

        this.changeListenerBefore = changeListener;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
    public static String decimalFormatter(double value){
        return decimalFormat.format(value);
    }
}
