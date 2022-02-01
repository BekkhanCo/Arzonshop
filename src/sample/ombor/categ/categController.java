package sample.ombor.categ;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.dbAccess.DBAccessObject;
import sample.models.Delete;
import sample.models.Category;
import sample.util.CallbackColumn;
import sample.util.CyrillicToLatin;
import sample.views.Stages;

import java.util.Locale;

public class categController {

    @FXML
    private JFXTextField cat_nomi;

    @FXML
    private JFXTextField search;
    @FXML
    private Button btn_save;

    @FXML
    private TableView<Category> tableView;

    @FXML
    private TableColumn<Category, String> column_nomer;

    @FXML
    private TableColumn<Category, String> column_nomi;

    @FXML
    private TableColumn<Category, String> column_soni;
    ObservableList<Category> turList = FXCollections.observableArrayList();
    DBAccessObject accessObject;
    FilteredList<Category> filteredList;
    SortedList<Category> sortedList;
    String sql = "";
    Category turModel;


    @FXML
    void handle_add(ActionEvent event) {
        if (turModel == null && cat_nomi.getText() != null) {
            turModel = new Category();
            turModel.Set("nomi", cat_nomi.getText());
            turModel.save();
        }
        refreshTable();
        cat_nomi.clear();
        turModel = null;

    }
    @FXML
    void handle_save(ActionEvent event) {
        if (!cat_nomi.getText().isEmpty()){
            Category tur1 = new Category();
            tur1.Set("id", turModel.getId());
            tur1.Set("nomi", cat_nomi.getText());
            tur1.save();
            refreshTable();
            tur1=null;
            cat_nomi.clear();
        }
    }
    @FXML
    void handle_delete(ActionEvent event) {
        Category tur = tableView.getSelectionModel().getSelectedItem();
        Delete.delete(tur, tur.get("id"));
        refreshTable();
    }

    @FXML
    void handle_edit(ActionEvent event) {
        turModel = tableView.getSelectionModel().getSelectedItem();
        if (turModel != null) {
            cat_nomi.setText(turModel.get("nomi"));
        }
    }
    @FXML
    void handle_back(ActionEvent event) {
        Stages.closeWindowStage(event);
    }


    @FXML
    void initialize() {
        initTable();
        tableView.setPlaceholder(new Label("Ma'lumotlar mavjud emas!"));
        accessObject = new DBAccessObject();
        refreshTable();

        search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String s, String t1) {
                String[] spl_keys = CyrillicToLatin.transliterate(t1.toLowerCase(Locale.ROOT)).split(" ");
                filteredList.setPredicate(students1 -> {
                    boolean ok = true;
                    int index = 0;
                    if (t1 == null || t1.isEmpty()) {
                        return true;
                    }
                    String item = CyrillicToLatin.transliterate(students1.get("nomi").toLowerCase());
                    for (String key : spl_keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }
                    if (String.valueOf(students1.get("id")).contains(t1)) {
                        return true;
                    }

                    return ok;
                });
                tableView.setItems(sortedList);

            }
        });


    }


    private void refreshTable() {
        sql = "SELECT tur.id,tur.nomi,COUNT(ombor.id) soni FROM `tur`  LEFT JOIN `ombor` ON tur.id=ombor.turi_id GROUP BY tur.id";
        turList = accessObject.getDataList(Category.class, sql);
        filteredList = new FilteredList<>(turList);
        sortedList = new SortedList<>(filteredList);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());


    }

    private void initTable() {
        column_nomer.setCellValueFactory(new CallbackColumn<>("id"));
        column_nomi.setCellValueFactory(new CallbackColumn<>("nomi"));
        column_soni.setCellValueFactory(new CallbackColumn<>("soni"));
    }
}
