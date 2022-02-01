package sample.setting;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.dbAccess.DBAccessObject;
import sample.models.User;

public class settingController {
    int id;
    String sql="";
    DBAccessObject accessObject;
    ObservableList<User> parols= FXCollections.observableArrayList();


    @FXML
    private JFXTextField tft_pass;

    @FXML
    private JFXTextField tft_newpass;

    @FXML
    private JFXTextField tft_smen;

    @FXML
    private Button btn_save;

    @FXML
    void handle_back(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        Window window=stage1.getScene().getWindow();
        window.fireEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSE_REQUEST));
        stage1.close();
    }

    @FXML
    void handle_save(ActionEvent event) {
        if (!tft_newpass.getText().isEmpty()){
            User parol=new User();
            parol.Set("id",id);
            parol.Set("pass",tft_newpass.getText());
//            parol.Set("SMEN",tft_smen.getText().substring(0,2));
            parol.save();

            Node node = (Node) event.getSource();
            Stage stage1 = (Stage) node.getScene().getWindow();
            Window window=stage1.getScene().getWindow();
            window.fireEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSE_REQUEST));
            stage1.close();
        }else{
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Yangi parol kiriting!");
            alert.setTitle("Ogoxlantirish");
            alert.showAndWait();
            tft_newpass.setStyle("-fx-border-color:red");
        }
    }



    @FXML
   public void initialize() {
        accessObject=new DBAccessObject();
        parols=new User().all();
        tft_pass.setText(parols.get(0).get("pass"));
        id=parols.get(0).getId();
    }

}
