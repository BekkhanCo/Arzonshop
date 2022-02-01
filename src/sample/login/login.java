package sample.login;

import com.jfoenix.controls.JFXButton;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import sample.animation.shake;
import sample.dbAccess.DBAccessObject;
import sample.models.User;
import sample.savdo.SotuvController;
import sample.views.Stages;

public class login {

    @FXML
    private TextField tft_uname;

    @FXML
    private PasswordField tft_pass;

    @FXML
    private JFXButton btn_kirish;

    @FXML
    private JFXButton btn_false;

    @FXML
    private Label exit;

    DBAccessObject accessObject;

    @FXML
    void handle_kirish(ActionEvent event) {
        kirish();
    }

    @FXML
    void initialize() {
        exit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                exit.setTextFill(Paint.valueOf("#ff1509"));
            }
        });
        exit.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                exit.setTextFill(Paint.valueOf("#293a4c"));
            }
        });
        exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.exit(0);
            }
        });
        btn_kirish.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    kirish();
                }
            }
        });
        tft_pass.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    kirish();
                }
            }
        });
        tft_uname.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    tft_pass.requestFocus();
                }
            }
        });

        btn_kirish.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_kirish.setStyle("-fx-background-color: #293a4c;-fx-border-width:0;-fx-background-radius:30;");
                btn_kirish.setTextFill(Paint.valueOf("white"));
            }
        });
        btn_kirish.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                btn_kirish.setStyle("-fx-background-color: white;-fx-border-color:#293a4c;-fx-border-radius:30;");
                btn_kirish.setTextFill(Paint.valueOf("#293a4c"));
            }
        });
    }

    public void kirish() {
        accessObject = new DBAccessObject();
        if (!tft_pass.getText().isEmpty() &&
                tft_pass.getText() != null &&
                !tft_uname.getText().isEmpty()
                && tft_uname.getText() != null) {

            User user = accessObject.checkUser(tft_pass.getText().trim(), tft_uname.getText().trim());
            if (user != null) {
                SotuvController.user=user;
                sample.savdo.savdoAdmin.SotuvController.user=user;
                switch (Integer.parseInt(user.get("rate"))) {
                    case 1:
                        btn_false.setStyle("-fx-border-color:#05D13E ;-fx-border-radius:20; -fx-border-width: 3");
                        btn_false.setText("★‿★");
                        btn_false.setPrefWidth(65);
                        PauseTransition pt = new PauseTransition(Duration.seconds(1));
                        pt.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Stages.getHome().show();
                                Stages.closeStage(btn_false);
                            }
                        });
                        pt.play();

                        break;
                    case 2:
                        btn_false.setStyle("-fx-border-color:#05D13E ;-fx-border-radius:20; -fx-border-width: 3");
                        btn_false.setText("✔");
                        btn_false.setTextFill(Paint.valueOf("#085B23"));
                        btn_false.setFont(Font.font(15));
                        PauseTransition pt1 = new PauseTransition(Duration.seconds(1));
                        pt1.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                Stages.getSalePanel().show();
                                Stages.closeStage(btn_false);
                            }
                        });
                        pt1.play();
                        break;
                    default:
                        btn_false.setStyle("-fx-border-color:#EB1617;-fx-border-radius:20");
                        btn_false.setText("╯︿╰");
                        btn_false.setTextFill(Paint.valueOf("#2E002F"));
                        btn_false.setPrefWidth(65);
                        shake shake = new shake(btn_false);
                        shake.playAnim();
                        tft_uname.requestFocus();

                }
            }
        } else {
            btn_false.setStyle("-fx-border-color:#EB1617;-fx-border-radius:20");
            btn_false.setText("╯︿╰");
            btn_false.setTextFill(Paint.valueOf("#2E002F"));
            btn_false.setPrefWidth(65);
            shake shake = new shake(btn_false);
            shake.playAnim();
            tft_uname.requestFocus();
        }

    }

}
