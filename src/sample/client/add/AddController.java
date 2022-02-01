package sample.client.add;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDatePicker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.Home.Controller;
import sample.models.Klient;
import sample.models.Tulov;
import sample.util.Util;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AddController implements Initializable {

    @FXML
    private TextField tft_nomi;

    @FXML
    private TextField tft_adress;

    @FXML
    private TextField tft_tel;

    @FXML
    private TextField tft_koment;

    @FXML
    private TextField tft_inn;

    @FXML
    private JFXCheckBox chb_tulov;

    @FXML
    private VBox vb_tulov;

    @FXML
    private TextField tft_kol;

    @FXML
    private JFXDatePicker dt_sana;

    @FXML
    void handle_cancel(ActionEvent event) {
    closeStage(event);
    }
    DateTimeFormatter format= DateTimeFormatter.ofPattern("dd.MM.yyyy");
    @FXML
    void handle_save(ActionEvent event) {
        int id=-1;
    if (checkData()){
        Klient klient=new Klient();
        klient.Set("fish",tft_nomi.getText());
        klient.Set("adress",tft_adress.getText());
        klient.Set("tel",tft_tel.getText());
        klient.Set("inn",tft_inn.getText());
        klient.Set("koment",tft_koment.getText());
        id=klient.save();
        if (chb_tulov.isSelected()){
            if (!tft_kol.getText().trim().isEmpty()){
                Tulov tulov=new Tulov();
                tulov.Set("klient_id",id);
                tulov.Set("cat_id",2);
                tulov.Set("sana",format.format(dt_sana.getValue()));
                tulov.Set("qarz", Util.decimalFormatterToDouble(tft_kol.getText()));
                tulov.Set("skidka", "0");
                tulov.Set("val", "$");
                tulov.Set("koment", "Начальный остаток задолженность");
                tulov.save();
                closeStage(event);
            }
        }else closeStage(event);
    }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        chb_tulov.setOnAction(event -> {
            vb_tulov.setDisable(true);
            if (chb_tulov.isSelected()){
                vb_tulov.setDisable(false);
            }
        });
        dt_sana.setValue(Controller.LOCAL_DATE(Controller.Sana()));
    }

    void closeStage(Event event){
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        Window window=stage1.getScene().getWindow();
        window.fireEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSE_REQUEST));
        stage1.close();
    }

    boolean checkData(){
        if (!tft_nomi.getText().trim().isEmpty()&&!tft_adress.getText().trim().isEmpty()){
            if (!tft_tel.getText().isEmpty()){
                return true;
            }
        }
        return false;
    }
}
