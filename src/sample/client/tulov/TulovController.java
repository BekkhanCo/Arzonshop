package sample.client.tulov;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.Home.Controller;
import sample.client.ClientController;
import sample.dbAccess.DBAccessObject;
import sample.models.Klient;
import sample.models.Kurs;
import sample.models.Tulov;
import sample.models.TulovKar;
import sample.util.DecimalFormat;
import sample.util.MyAutoCompletionBinding;
import sample.util.Util;
import sample.views.Stages;

import java.net.URL;
import java.util.ResourceBundle;

public class TulovController implements Initializable {

    @FXML
    private JFXComboBox<String> combo_mijoz;

    @FXML
    private JFXTextField edit_kurs;

    @FXML
    private JFXComboBox<String> combo_savdo_val;

    @FXML
    private Label lb_jami_summa;

    @FXML
    private Label lb_jami_summa_dol;

    @FXML
    private Label lb_itog_summa;

    @FXML
    private Label lb_itog_summa_dol;

    @FXML
    private Label lb_tolov_summa;

    @FXML
    private Label lb_tolov_summa_dol;

    @FXML
    private JFXComboBox<String> combo_tolov_val;

    @FXML
    private JFXTextField edit_summa;

    @FXML
    private TableView<TulovKar> tableTolov;

    @FXML
    private TableColumn<TulovKar, String> col_no;

    @FXML
    private TableColumn<TulovKar, String> col_tolov_kassa;

    @FXML
    private TableColumn<TulovKar, String> col_tolov_summa;

    @FXML
    private TableColumn<TulovKar, String> col_tolov_val;


    public static double obSumma = 0;
    public static double obSummaDol = 0;
    int no = 1;
    public static ObservableList<TulovKar> tulovKars = FXCollections.observableArrayList();
    ObservableList<String> klientList = FXCollections.observableArrayList();
    TulovKar tulovKar;


    @FXML
    void handle_Sotish(ActionEvent event) {
        int klient_id = -1;
        for (Klient klient : klients) {
            if (combo_mijoz.getValue().equals(klient.get("fish"))) {
                klient_id = klient.getId();
            }
        }
        if (tulovKars != null) {
            for (TulovKar kar : tulovKars) {
                Tulov tulov = new Tulov();
                tulov.Set("klient_id", klient_id);
                tulov.Set("cat_id", 2);
                tulov.Set("sana", Controller.Sana());
                tulov.Set("summa", kar.getSumma());
                if (kar.getValyuta().equals("Сўм")) {
                    tulov.Set("summa_dol", kar.getSumma());
                    tulov.Set("val", "Сўм");
                } else {
                    tulov.Set("summa_dol", kar.getSumma() * Util.decimalFormatterToDouble(edit_kurs.getText()));
                    tulov.Set("val", "$");
                    tulov.Set("kurs", Util.decimalFormatterToDouble(edit_kurs.getText()));
                }
                tulov.Set("koment", "Оплата покупки");
                tulov.save();
            }
            Stages.closeStage(lb_itog_summa);
            tulovKars.clear();
        }

    }

    @FXML
    void handle_cansel(ActionEvent event) {
        Stages.closeStage(event);
    }

    @FXML
    void handle_tolov_add(ActionEvent event) {
        if (!edit_summa.getText().isEmpty()) {
            if (tulovKar == null) {
                tulovKar = new TulovKar();
                tulovKar.setNo(no);
                tulovKar.setSumma(Util.decimalFormatterToDouble(edit_summa.getText()));
                tulovKar.setValyuta(combo_tolov_val.getValue());
                no++;
                tulovKars.add(tulovKar);
                tableTolov.setItems(tulovKars);
                tableTolov.refresh();
                setTolovSumma();
                tulovKar = null;
            } else {
                tulovKar.setSumma(Util.decimalFormatterToDouble(edit_summa.getText()));
                tulovKar.setValyuta(combo_tolov_val.getValue());
                tableTolov.refresh();
                setTolovSumma();
                tulovKar = null;
            }

            edit_summa.clear();
        }
    }

    @FXML
    void handle_tolov_delete(ActionEvent event) {
        TulovKar kassaKirim = tableTolov.getSelectionModel().getSelectedItem();
        if (kassaKirim != null) {
            tulovKars.remove(kassaKirim);
            tableTolov.setItems(tulovKars);
            tableTolov.refresh();
            setTolovSumma();
        }
    }

    @FXML
    void handle_tolov_edit(ActionEvent event) {
        tulovKar = tableTolov.getSelectionModel().getSelectedItem();
        if (tulovKar != null) {
            edit_summa.setText(String.valueOf(tulovKar.getSumma()));
            combo_tolov_val.setValue(tulovKar.getValyuta());
        }
    }

    String sql = "";
    DBAccessObject accessObject;
    ObservableList<Klient> klients = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accessObject = new DBAccessObject();
        setData();
        initTable();
        setQarz();

    }

    void setData() {
        Util.setNumericTextFieldDecimal(edit_summa);
        Util.setNumericTextFieldDecimal(edit_kurs);
        getKusr();

        edit_kurs.setOnKeyReleased(keyEvent -> {
            setTolovSumma();
        });
        comboSet();
    }

    ObservableList<Kurs> kurs = FXCollections.observableArrayList();
    ObservableList<Tulov> klientqarzs = FXCollections.observableArrayList();

    private void getKusr() {
        sql = "SELECT kurs from `kurs` limit 1";
        kurs = accessObject.getDataList(Kurs.class, sql);
        if (kurs != null) {
            edit_kurs.setText(kurs.get(0).get("kurs"));
        } else {
            edit_kurs.setText("10600");
        }
    }

    void comboSet() {
        klients = new Klient().select().all();
        for (Klient klient : klients) {
            klientList.add(klient.get("fish"));
        }
        combo_mijoz.setButtonCell(combo_mijoz.getCellFactory().call(null));
        combo_mijoz.setItems(klientList);
        if (ClientController.klientOl.trim().isEmpty()) {
            combo_mijoz.setValue(klientList.get(0));
        } else {
            combo_mijoz.setValue(ClientController.klientOl);
        }

        MyAutoCompletionBinding.bindAutoCompletion(combo_mijoz.getEditor(), klientList);
        sql = "SELECT klient_balans.`id`, klient.fish,`summa_dol` tulov, `qarz` FROM `klient_balans` JOIN klient ON klient_balans.klient_id=klient.id ";
        klientqarzs = accessObject.getDataList(Tulov.class, sql);

        combo_mijoz.setOnAction(event -> {
            setQarz();
        });

        combo_savdo_val.setItems(FXCollections.observableArrayList("", "Сўм", "$"));
        combo_tolov_val.setItems(FXCollections.observableArrayList("Сўм", "$"));
        combo_savdo_val.setValue("Сўм");
        combo_tolov_val.setValue("Сўм");
        combo_savdo_val.setOnAction(actionEvent -> {
            setTolovSumma();
        });
    }

    void initTable() {
        tableTolov.setPlaceholder(new Label(""));
        col_no.setCellValueFactory(new PropertyValueFactory("no"));
        col_tolov_summa.setCellValueFactory(new PropertyValueFactory("summa"));
        col_tolov_val.setCellValueFactory(new PropertyValueFactory("valyuta"));
    }

    private void setTolovSumma() {
        double tolov = 0, itog = 0, summa = 0, dol_summa = 0, dol_itog = 0;
        double som = 0, dollar = 0;

        double kurs = Util.decimalFormatterToDouble(edit_kurs.getText(), 10600);

        for (TulovKar savdo : tableTolov.getItems()) {
            if (savdo.getValyuta().equals("Сўм"))
                som += savdo.getSumma();
            else dollar += savdo.getSumma();
        }

        summa = Util.decimalFormatterToDouble(lb_jami_summa.getText(), 0);

        dol_summa = Util.decimalFormatterToDouble(lb_jami_summa_dol.getText(), 0);

        if (combo_savdo_val.getValue().equals("$")) {
            lb_jami_summa_dol.setText(obSummaDol != 0 ? DecimalFormat.decimalFormatter(obSummaDol) + " $" : "");
            lb_jami_summa.setText("");
            tolov = dollar + som / kurs;
            dol_itog = dol_summa + summa / kurs-tolov;
            lb_itog_summa.setText("");
            lb_tolov_summa.setText("");

            lb_itog_summa_dol.setText(DecimalFormat.decimalFormatter(dol_itog) + " $");
            lb_tolov_summa_dol.setText(DecimalFormat.decimalFormatter(tolov) + " $");
        } else {
            lb_jami_summa.setText(obSumma != 0 ? DecimalFormat.decimalFormatter(obSumma) + " Сўм" : "");
            lb_jami_summa_dol.setText("");
            tolov = som + dollar * kurs;
            itog = summa + dol_summa * kurs-tolov;
            lb_itog_summa.setText(itog != 0 ? DecimalFormat.decimalFormatter(itog) + " Сўм" : "");
            lb_tolov_summa.setText(som != 0 ? DecimalFormat.decimalFormatter(tolov) + " Сўм" : "");

            lb_itog_summa_dol.setText("");
            lb_tolov_summa_dol.setText("");
        }
    }


    void setQarz() {
        double summaQarz = 0;
        for (Tulov qarz : klientqarzs) {
            if (qarz.get("fish").equals(combo_mijoz.getValue())) {
                if (qarz.get("qarz") != null) {
                    summaQarz += Util.decimalFormatterToDouble(qarz.get("qarz"));
                }
                if (qarz.get("tulov") != null) {
                    summaQarz -= Util.decimalFormatterToDouble(qarz.get("tulov"));
                }
            }
        }
        lb_jami_summa.setText(DecimalFormat.decimalFormatter(summaQarz));
        obSummaDol = summaQarz / Util.decimalFormatterToDouble(edit_kurs.getText());
        obSumma = summaQarz;
        setTolovSumma();
        edit_summa.requestFocus();
    }


}
