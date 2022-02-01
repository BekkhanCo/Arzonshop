package sample.util;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import sample.models.BaseModel;

public class CallbackForTreeColumn<P extends BaseModel> implements Callback<TreeTableColumn.CellDataFeatures<P, String>, ObservableValue<String>> {
    private String col_name = "";

    public CallbackForTreeColumn(String col_name) {
        this.col_name = col_name;
    }

    @Override
    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<P, String> prCellDataFeatures) {
        prCellDataFeatures.getTreeTableColumn().setStyle("-fx-alignment: CENTER;");

            if (prCellDataFeatures.getValue().getValue() != null) {
                if (prCellDataFeatures.getValue().getValue().get(this.col_name) == null){
                    return null;
                }
//                if (col_name.trim().equals("valyuta")) {
//                    if (prCellDataFeatures.getValue().getValue().get(this.col_name).trim().isEmpty()) {
//                        return new SimpleStringProperty(Valyuta.allWithoutEmpty());
//                    }
//                    else {
//                        return prCellDataFeatures.getValue().getValue().getProperty(col_name);
//                    }
//                }
                try {
                    double d = Double.parseDouble(prCellDataFeatures.getValue().getValue().get(this.col_name));
                    prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill:#293a4c;-fx-alignment: CENTER;");

                    if (col_name.trim().equals("tulandi")||col_name.trim().equals("debit")){
                        prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: green;-fx-alignment: CENTER;");
                    }
                    if (col_name.trim().equals("summa")){
                        prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: green;-fx-alignment: CENTER;");
                    }
                    if (col_name.trim().equals("kredit")){
                        prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: red;-fx-alignment: CENTER;");
                    }
                    return new SimpleStringProperty(DecimalFormat.decimalFormatter(d));
                } catch (Exception e) {
                    if (col_name.trim().equals("sana")){
                        prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: blue;");
                    }
                    if (col_name.trim().equals("tulandi")||col_name.trim().equals("debit")){
                        prCellDataFeatures.getTreeTableColumn().setStyle(" -fx-font-weight:bold;-fx-text-fill: green;-fx-alignment: CENTER;");
                    }
                    String s=String.valueOf(prCellDataFeatures.getValue().getValue().get(this.col_name));
                }

                return prCellDataFeatures.getValue().getValue().getProperty(this.col_name);
            }


        return null;
    }
}

