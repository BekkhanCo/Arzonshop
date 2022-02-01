package sample.views;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import sample.dbAccess.DBAccessObject;
import sample.models.Korzinka;
import sample.vendor.Report;

import java.io.IOException;
import java.util.*;

public interface Stages {



    static Stage getLogin() {
        Stage stage = createStage("/sample/login/login.fxml", "Login");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setResizable(false);
        return stage;
    }


    static Stage getHome() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Stages.class.getResource("/sample/Home/sample.fxml"));
        Pane pane = null;
        try {
            pane = pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.getIcons().add(new Image("/sample/logos/logo.png"));
        stage.setTitle("HOME");
        stage.setResizable(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    static Stage getSalePanel() {
        Stage stage = createStage("/sample/savdo/sotuv.fxml", "Savdo");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        return stage;
    }

    static Stage getAdminSalePanel() {
        Stage stage = createStage("/sample/savdo/savdoAdmin/sotuv.fxml", "Savdo");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        return stage;
    }

    static Stage getStock() {
        Stage stage = createStage("/sample/ombor/ombor.fxml", "Ombor");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        return stage;
    }

    static Stage getImportPanel() {
        Stage stage = createStage("/sample/prixod/prixod.fxml", "Поставки");
        stage.setMaximized(true);
        return stage;
    }

    static Stage getIncomePanel() {
        Stage stage = createStage("/sample/foyda/foyda.fxml", "Валовая прибыль");
        stage.setMaximized(true);
        return stage;
    }

    static Stage getKassaPanel() {
        Stage stage = createStage("/sample/kassa/kassa.fxml", "Kassa");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMaximized(true);
        return stage;
    }

    static Stage getClientPanel() {
        Stage stage = createStage("/sample/client/client.fxml", "Контрагент ");
        stage.setMaximized(true);
        return stage;
    }

    static Stage getSettings() {
        Stage stage = createStage("/sample/setting/setting.fxml", "Settings");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    static Stage getAddProductPanel() {
        Stage stage = createStage("/sample/ombor/add/Add.fxml", "Add panel");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    static Stage getEditProductPanel() {
        Stage stage = createStage("/sample/ombor/edit/Edit.fxml", "Edit panel");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    static Stage getHistoryProductPanel() {
        Stage stage = createStage("/sample/ombor/history/test.fxml", "History panel");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    static Stage getCategoryPanel() {
        Stage stage = createStage("/sample/ombor/categ/categ.fxml", "Category panel");
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        return stage;
    }

    static Stage getImportListPanel() {
        Stage stage = createStage("/sample/prixod/List/List.fxml", "Список приходов");
        stage.setMaximized(true);
        stage.setResizable(true);
        return stage;
    }


    static Stage getUserPanel() {
        Stage stage = createStage("/sample/kassa/addUser/User.fxml", "User panel");
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    static Stage getPayment() {
        Stage stage = createStage("/sample/client/tulov/tulov.fxml", "Payment panel");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
    static Stage getAddClient() {
        Stage stage = createStage("/sample/client/add/add.fxml", "Client panel");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }
    static Stage getReturnListPanel() {
        Stage stage = createStage("/sample/kassa/returnProduct/Return.fxml", "Окно возврата");
        stage.setMaximized(true);
        stage.setResizable(false);
        stage.getScene().setFill(Color.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        return stage;
    }

    static Stage createStage(String path, String title) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Stages.class.getResource(path));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(e + "");
            alert.show();
            e.printStackTrace();
        }
        Scene scene = new Scene(pane);

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.getIcons().add(new Image("/sample/logos/logo.png"));
        stage.setTitle(title);
        return stage;
    }

    static void hideStage(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.hide();
    }

    static void closeStage(Node node) {
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
    }

    static void closeStage(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.hide();
    }

    static void closeWindowStage(Event event) {
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        Window window = stage1.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage1.close();
    }

    static int checkPassword(int wrong) {

        DBAccessObject dbAccessObject = new DBAccessObject();

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);

        dialog.setHeaderText("Пожалуйста введите ваш пароль!");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField password = new PasswordField();
        password.setPromptText("Password:");
        if (wrong == 1) {
            password.setStyle("-fx-border-color:red");
            password.setPromptText("Неправильный пароль!");
        }

        grid.add(new Label("Пароль:"), 0, 0);
        grid.add(password, 1, 0);
        dialog.getDialogPane().setContent(grid);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.get() == ButtonType.OK) {
            if (dbAccessObject.check(password.getText())) {
                return 2;
            } else {
                return 1;
            }
        } else {
            return 0;
        }
    }

    static void sotuvChekPrint(ObservableList<Korzinka> korzinkas,Map<String,String> data){
        Alert ogoh = new Alert(Alert.AlertType.INFORMATION);
        ogoh.setTitle("Предупреждение");
        ogoh.setHeaderText("Пожалуйста, подождите!");
        ogoh.setContentText("Это публикуется ....");
        ogoh.show();
        PauseTransition pt = new PauseTransition(Duration.seconds(1));
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ogoh.close();
            }
        });
        pt.play();
        List<Map<String, Object>> list=new ArrayList<>();
        Map<String, Object> maps=null;

        int nomer=1;
        for (Korzinka korzinka : korzinkas) {
            maps=new HashMap<>();
            maps.put("firma_nomi", data.get("firma"));
            maps.put("id", data.get("id"));
            maps.put("sana", korzinka.getSana());
            maps.put("kassir", data.get("kassir"));
            maps.put("nomer", nomer++);
            maps.put("tovar", (korzinka.getName())+"   "+korzinka.getSoni()+" x "+korzinka.getNarxi());
            maps.put("summa", String.valueOf(korzinka.getUmumiySumma()));
            maps.put("um_summa", data.get("obsumma"));
            maps.put("chegirma", "0");
            maps.put("tolov", data.get("obsumma"));
            maps.put("nasiya", "0");
            list.add(maps);
        }

        JRDataSource dataSource= new JRBeanCollectionDataSource(list);
        Report report=new Report();
        report.reportSotuv(maps, dataSource);
        report.showSotuvReport();
    }
}
