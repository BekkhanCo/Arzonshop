package sample.Home;

import com.jfoenix.controls.JFXButton;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import sample.prixod.prixodController;
import sample.views.Stages;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Controller {

    @FXML
    public Label lbl_soat;

    @FXML
    private Label lbl_sana;

    int a = 0;

    @FXML
    public void handle_back(ActionEvent event) {
        Stages.getLogin().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_ombor(ActionEvent event) {
        Stages.getStock().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_prixod(ActionEvent event) {
        Stages.getImportPanel().show();
        prixodController.A = true;
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void hendle_Sotuv(ActionEvent event) {
        Stages.getIncomePanel().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_kassa(ActionEvent event) {
        Stages.getKassaPanel().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_settings(ActionEvent event) {
        Stages.getAdminSalePanel().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_foyda(ActionEvent event) {
        Stages.getClientPanel().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_sozlama(ActionEvent event) {
        do {
            a = Stages.checkPassword(a);
        } while (a == 1);
        if (a == 2) {
            Stages.getSettings().show();
        }
    }

    @FXML
    public void initialize() {
        initClock(lbl_soat);
        lbl_sana.setText(Sana());
    }

    public static final String Sana() {
        Date d = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
        return dformat.format(d);
    }

    public static String initClock() {
        Label label = new Label();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            label.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        return label.getText();
    }

    public static String initClock(Label label) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            label.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        return label.getText();
    }

    public static final LocalDate LOCAL_DATE(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

}
