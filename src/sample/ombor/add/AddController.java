package sample.ombor.add;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.Prixod;
import sample.models.Product;
import sample.models.Category;
import sample.util.Util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AddController {

    @FXML
    private JFXTextField tft_name;

    @FXML
    private JFXComboBox<Category> combo_tur;

    @FXML
    private JFXComboBox<String> combo_type;

    @FXML
    private TextField tft_kelish;

    @FXML
    private TextField tft_sotish;

    @FXML
    private TextField tft_shtrix;

    @FXML
    private JFXCheckBox chb_auto;

    @FXML
    private JFXCheckBox chb_prixod;

    @FXML
    private VBox vb_edit;

    @FXML
    private TextField tft_kol;

    @FXML
    private JFXDatePicker dp_sana;

    @FXML
    private Label lbl_narx;

    @FXML
    private Label lbl_kol;

    @FXML
    private Label lbl_summa;

    @FXML
    private TabPane tabPane_shtrix;


    int rx = 0;
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @FXML
    void handle_clear(ActionEvent event) {

        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        stage1.hide();
    }

    @FXML
    void handle_insert(ActionEvent event) {
        Product product;
        if (!tft_name.getText().isEmpty() && combo_tur.getValue().getId() != null) {
            tft_name.setStyle("-fx-border-color:none");
            combo_tur.setStyle("-fx-border-color:none");
            if (!tft_sotish.getText().isEmpty() && !tft_kelish.getText().isEmpty()) {
                tft_sotish.setStyle("-fx-border-color:blue");
                tft_kelish.setStyle("-fx-border-color:blue");


                product = new Product();
                product.Set("name", tft_name.getText());
                product.Set("narx", Util.decimalFormatterToDouble(tft_kelish.getText()));
                product.Set("sotish", Util.decimalFormatterToDouble(tft_sotish.getText()));
                product.Set("turi_id", combo_tur.getValue().getId());
                product.Set("soni", "0");
                product.Set("type", combo_type.getValue());
                product.Set("sanasi", Controller.Sana());
                if (!chb_auto.isSelected()) {
                    product.Set("shtrix",getShtrixCodes());
                }

                rx = product.save();
                if (chb_auto.isSelected()) {
                    product = new Product();
                    product.Set("id", rx);
                    product.Set("shtrix", String.valueOf(270000000 + rx));
                    product.save();
                }
                if (chb_prixod.isSelected()) {
                    prixod(rx, event);
                } else {
                    closeStage(event);
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Sotish yoki Kelish narxlari kiritilmagan!");
                alert.setTitle("Ogoxlantirish");
                alert.showAndWait();
                tft_sotish.setStyle("-fx-border-color:red");
                tft_kelish.setStyle("-fx-border-color:red");
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nomi yoki Tur kiritilmagan!");
            alert.setTitle("Ogoxlantirish");
            alert.showAndWait();
            tft_name.setStyle("-fx-border-color:red");
            combo_tur.setStyle("-fx-border-color:red");
        }
    }

    String sql = "";
    DBAccessObject accessObject;
    ObservableList<Category> turi = FXCollections.observableArrayList();

    @FXML
    void initialize() {
        accessObject = new DBAccessObject();
        setData();
    }

    void setData() {
        sql = "select id, nomi from tur";
        turi = accessObject.getDataList(Category.class, sql);

        combo_tur.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> groupListView) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category turs, boolean b) {
                        super.updateItem(turs, b);
                        setVisible(false);
                        if (!b) {
                            setText(turs.get("nomi"));
                            setVisible(true);
                        }
                    }
                };
            }
        });

        combo_tur.setButtonCell(combo_tur.getCellFactory().call(null));
        combo_tur.setItems(turi);

        combo_type.setItems(FXCollections.observableArrayList(Arrays.asList("Donalik", "kg")));
        combo_type.setValue("Donalik");

        dp_sana.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        setdDecimal();
        additionAction();
    }

    void prixod(int id, Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (!tft_kol.getText().trim().isEmpty() && tft_kol.getText().trim() != null) {
            Prixod prixod = new Prixod();
            prixod.Set("m_id", id);
            prixod.Set("soni", Util.decimalFormatterToDouble(tft_kol.getText()));
            prixod.Set("kelish", Util.decimalFormatterToDouble(tft_kelish.getText()));
            prixod.Set("sotish", Util.decimalFormatterToDouble(tft_sotish.getText()));

            prixod.Set("sana", format.format(dp_sana.getValue()));
            prixod.Set("soat", LocalDateTime.now().format(formatter));
            prixod.save();
            closeStage(event);
        }
    }

    void setdDecimal() {
        Util.setNumericTextFieldDecimal(tft_kol);
        Util.setNumericTextFieldDecimal(tft_kelish);
        Util.setNumericTextFieldDecimal(tft_sotish);
    }

    void additionAction() {
        tft_kol.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                lbl_kol.setText(tft_kol.getText());
                lbl_narx.setText(tft_sotish.getText());
                lbl_summa.setText(Util.doubleToString(Util.decimalFormatterToDouble(tft_kol.getText()) * Util.decimalFormatterToDouble(tft_sotish.getText()) + ""));
            }
        });

        tft_shtrix.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) && !tft_shtrix.getText().isEmpty()){
                addTab(tft_shtrix.getText());
                tft_shtrix.clear();
            }
        });
//------------
        chb_auto.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (chb_auto.isSelected()) {
                    tft_shtrix.setDisable(true);
                    tabPane_shtrix.setDisable(true);
                } else {
                    tft_shtrix.setDisable(false);
                    tabPane_shtrix.setDisable(false);
                }
            }
        });
//-------------
        chb_prixod.setOnAction(event -> {
            vb_edit.setDisable(true);
            if (chb_prixod.isSelected()) {
                vb_edit.setDisable(false);
            }
        });
    }

    void closeStage(Event event) {
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        Window window = stage1.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        stage1.close();
    }

    private void addTab(String shtrix) {
        Tab tab = new Tab(shtrix);
        tab.setGraphic(null);
        tabPane_shtrix.getTabs().add(tab);
    }

    private String getShtrixCodes() {
        String result = "";
        ObservableList<Tab> tabs = tabPane_shtrix.getTabs();
        for (int i = 0; i <tabs.size(); i++) {
            result = result.concat(tabs.get(i).getText());
            if (i!=tabs.size()-1)
                result=result.concat(",");
        }
        return result;
    }


}
