package sample.kassa.addUser;

import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.dbAccess.DBAccessObject;
import sample.models.Delete;
import sample.models.User;
import sample.util.CallbackColumn;
import sample.util.CyrillicToLatin;
import sample.views.Stages;

import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private JFXTextField tft_name;

    @FXML
    private JFXTextField tft_pass;


    @FXML
    private Button btn_save;

    @FXML
    private JFXTextField tft_search;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> col_no;

    @FXML
    private TableColumn<User, String> col_user;

    @FXML
    private TableColumn<User, String> col_pass;

    @FXML
    private TableColumn<User, String> col_rate;

    FilteredList<User> filteredList;
    SortedList<User> sortedList;
    User user;


    @FXML
    void handle_add(ActionEvent event) {
        if (user == null &&
                !tft_name.getText().isEmpty() &&
                !tft_pass.getText().isEmpty()) {
            user = new User();
            user.Set("uname", tft_name.getText());
            user.Set("pass", tft_pass.getText());
            user.Set("rate", 2);
            user.save();
        }
        clear();
    }

    @FXML
    void handle_back(ActionEvent event) {
        Stages.closeWindowStage(event);
    }

    @FXML
    void handle_delete(ActionEvent event) {
        User user = tableView.getSelectionModel().getSelectedItem();
        Delete.delete(user, user.get("id"));
        refreshTable();
    }

    @FXML
    void handle_edit(ActionEvent event) {
        user = tableView.getSelectionModel().getSelectedItem();
        if (user != null) {
            tft_name.setText(user.get("uname"));
            tft_pass.setText(user.get("pass"));
        }
    }

    @FXML
    void handle_save(ActionEvent event) {
        if (!tft_name.getText().isEmpty() &&
                !tft_pass.getText().isEmpty()) {
            User user = new User();
            user.Set("id", this.user.getId());
            user.Set("pass", tft_pass.getText());
            user.Set("uname", tft_name.getText());
            user.save();
            refreshTable();
            this.user = null;
            clear();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tableView.setPlaceholder(new Label("Ma'lumotlar mavjud emas!"));
        initTable();
        refreshTable();
        filter();
    }


    private void refreshTable() {
        filteredList = new FilteredList<>(new DBAccessObject().getDataList(User.class, "select * from users where rate!=1"));
        sortedList = new SortedList<>(filteredList);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
    }

    private void clear() {
        refreshTable();
        tft_pass.clear();
        tft_name.clear();
        user = null;
    }

    private void initTable() {
        col_no.setCellValueFactory(new CallbackColumn<>("id"));
        col_user.setCellValueFactory(new CallbackColumn<>("uname"));
        col_pass.setCellValueFactory(new CallbackColumn<>("pass"));
        col_rate.setCellValueFactory(new CallbackColumn<>("rate"));
    }

    private void filter() {
        tft_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String s, String t1) {
                String[] spl_keys = CyrillicToLatin.transliterate(t1.toLowerCase()).split(" ");
                filteredList.setPredicate(user -> {
                    boolean ok = true;
                    int index = 0;
                    if (t1 == null || t1.isEmpty()) {
                        return true;
                    }
                    String item = CyrillicToLatin.transliterate(user.get("uname").toLowerCase());
                    for (String key : spl_keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }
                    if (String.valueOf(user.get("id")).contains(t1)) {
                        return true;
                    }

                    return ok;
                });

                tableView.setItems(sortedList);

            }
        });
    }

}
