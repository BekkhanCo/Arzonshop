package sample.dbAccess;

import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public interface DBCallback {


    void handle(String sql, List<SimpleObjectProperty<Object>> sql_values);
}
