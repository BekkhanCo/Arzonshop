package sample.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import sample.models.BaseModel;

public class CallbackColumn<P extends BaseModel> implements Callback<TableColumn.CellDataFeatures<P,String>, ObservableValue<String>> {
    private String col_name="";

    public CallbackColumn(String col_name) {
        this.col_name = col_name;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<P,String> param){
        if (param.getValue()!=null){
            if (!col_name.trim().equals("id")&&!col_name.trim().equals("name")&&!col_name.trim().equals("shtrix")){
            try {
                double d = Double.parseDouble(param.getValue().get(this.col_name));
                 param.getTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill:#293a4c");

                return new SimpleStringProperty(DecimalFormat.decimalFormatter(d));
            } catch (Exception e) {
                String s=String.valueOf(param.getValue().get(this.col_name));
            }
            }
            if (col_name.trim().equals("sana")){
                param.getTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: green");
            }
            return new SimpleStringProperty(param.getValue().get(col_name));
        }
        return null;
    }
}
