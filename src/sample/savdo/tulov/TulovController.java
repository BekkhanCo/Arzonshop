package sample.savdo.tulov;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.*;
import sample.savdo.savdoAdmin.SotuvController;
import sample.util.DecimalFormat;
import sample.util.MyAutoCompletionBinding;
import sample.util.Util;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class TulovController implements Initializable {

    @FXML
    private JFXComboBox<String> combo_mijoz;

    @FXML
    private JFXButton btn_mijoz_add;

    @FXML
    private Text lb_balans_sum;

    @FXML
    private JFXTextField edit_kurs;

    @FXML
    private JFXComboBox<String> combo_savdo_val;

    @FXML
    private Label lb_jami_summa;

    @FXML
    private Label lb_jami_summa_dol;

    @FXML
    private Label lb_chegirma;

    @FXML
    private Label lb_chegirma_dol;

    @FXML
    private Label lb_itog_summa;

    @FXML
    private Label lb_itog_summa_dol;

    @FXML
    private Label lb_tolov_summa;

    @FXML
    private Label lb_tolov_summa_dol;

    @FXML
    private Label lb_nasiya;

    @FXML
    private Label lb_nasiya_dol;

    @FXML
    private JFXComboBox<String> combo_tolov_val;

    @FXML
    private JFXTextField edit_summa;

    @FXML
    private JFXButton btn_tolov_add;

    @FXML
    private JFXButton btn_tolov_edit;

    @FXML
    private JFXButton btn_tolov_delete;

    @FXML
    private JFXButton btn_tolov_chegirma;

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

    @FXML
    private JFXComboBox<String> combo_tulov_usul;

    @FXML
    private CheckBox chb_print_check;

    @FXML
    private CheckBox chb_print;

    @FXML
    private JFXButton btn_sotish;
    double chegirma = 0, tolov = 0, itog = 0, nasiya = 0, summa = 0, dol_summa = 0, dol_itog = 0, dol_nasiya = 0, dol_chegirma = 0;

    public static double obSUmma = 0;
    public static double obSUmmaDol = 0;
    int no = 1;
    public static ObservableList<TulovKar> tulovKars = FXCollections.observableArrayList();
    ObservableList<String> klientList = FXCollections.observableArrayList();
    TulovKar tulovKar;

    @FXML
    void handle_Sotish(ActionEvent event) {
        double karta = 0;
        double naqd = 0;
        int klient_id = -1;
        int chek_id = -1;
        for (Klient klient : klients) {
            if (combo_mijoz.getValue().equals(klient.get("fish"))) {
                klient_id = klient.getId();
            }
        }

        for (TulovKar kar : tulovKars) {
            if (kar.getTulov().equals("Наличные")) {
                if (kar.getValyuta().equals("Сўм")) {
                    naqd += Util.decimalFormatterToDouble(String.valueOf(kar.getSumma()));
                } else naqd += Util.decimalFormatterToDouble(String.valueOf(kar.getSumma()));
            } else {
                if (kar.getValyuta().equals("Сўм")) {
                    karta += Util.decimalFormatterToDouble(String.valueOf(kar.getSumma()));
                } else karta += Util.decimalFormatterToDouble(String.valueOf(kar.getSumma()));
            }
        }

        if (klient_id > -1) {
            if (karta == 0) {
                chek_id = Sotuv("Наличные", karta + "", naqd + "", klient_id);
            } else if (naqd == 0) {
                chek_id = Sotuv("Пластик", karta + "", naqd + "", klient_id);
            } else chek_id = Sotuv("Наличные-Пластик", karta + "", naqd + "", klient_id);
        }

        if (chek_id > -1) {
            Tulov tulov = new Tulov();
            tulov.Set("klient_id", klient_id);
            tulov.Set("cat_id", 1);
            tulov.Set("sana", Controller.Sana());
            tulov.Set("qarz", (Util.decimalFormatterToDouble(String.valueOf(nasiya))));
            tulov.Set("skidka", Util.decimalFormatterToDouble(String.valueOf(chegirma)));
            tulov.Set("chek_id", chek_id);
            tulov.Set("val", "UZS");
            tulov.Set("koment", "Задолженность");
            tulov.save();
            System.out.println("qarzdorlik kiritildi");
            Node node = (Node) event.getSource();
            Stage stage1 = (Stage) node.getScene().getWindow();
            Window window = stage1.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            stage1.close();
        }
        tulovKars.clear();
    }

    @FXML
    void handle_mijoz_add(ActionEvent event) {

    }

    @FXML
    void handle_cansel(ActionEvent event) {
        closeStage(event);
    }

    @FXML
    void handle_tolov_Chegirma(ActionEvent event) {
        if (combo_tolov_val.getValue().equals("Сўм")) {
            double jami = Util.decimalFormatterToDouble(lb_jami_summa.getText(), 0);
            if (jami > 0 && !edit_summa.getText().isEmpty()) {
                double summa = Util.decimalFormatterToDouble(edit_summa.getText(), 0);
                dol_chegirma = summa / Util.decimalFormatterToDouble(edit_kurs.getText());
                chegirma = summa;
                setTolovSumma();
                edit_summa.clear();
            }
        } else {
            double jami = Util.decimalFormatterToDouble(lb_jami_summa_dol.getText(), 0);
            if (jami > 0 && !edit_summa.getText().isEmpty()) {
                double summa = Util.decimalFormatterToDouble(edit_summa.getText(), 0);
                dol_chegirma = summa;
                chegirma = summa * Util.decimalFormatterToDouble(edit_kurs.getText());
                lb_chegirma_dol.setText(DecimalFormat.decimalFormatter(summa));
                setTolovSumma();
                edit_summa.clear();
                lb_chegirma.setText("");
            }
        }
    }

    @FXML
    void handle_tolov_add(ActionEvent event) {
        if (!edit_summa.getText().isEmpty()) {
            if (tulovKar == null) {
                tulovKar = new TulovKar();
                tulovKar.setNo(no);
                tulovKar.setTulov(combo_tulov_usul.getValue());
                tulovKar.setSumma(Util.decimalFormatterToDouble(edit_summa.getText()));
                tulovKar.setValyuta(combo_tolov_val.getValue());
                no++;
                tulovKars.add(tulovKar);
                tableTolov.setItems(tulovKars);
                tableTolov.refresh();
                setTolovSumma();
                tulovKar = null;
            } else {
                tulovKar.setTulov(combo_tulov_usul.getValue());
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
    }

    void setData() {
        Util.setNumericTextFieldDecimal(edit_summa);
        Util.setNumericTextFieldDecimal(edit_kurs);
        comboSet();
        getKusr();
        lb_jami_summa.setText(obSUmma != 0 ? DecimalFormat.decimalFormatter(obSUmma) + " Сўм" : "");
        obSUmmaDol = obSUmma / Util.decimalFormatterToDouble(edit_kurs.getText());
        setTolovSumma();

        edit_kurs.setOnKeyReleased(keyEvent -> {
            setTolovSumma();
        });

    }

    ObservableList<Kurs> kurs = FXCollections.observableArrayList();

    private void getKusr() {
        sql = "SELECT kurs from `kurs` limit 1";
        kurs = accessObject.getDataList(Kurs.class, sql);
        if (kurs != null) {
            edit_kurs.setText(kurs.get(0).get("kurs"));
        } else {
            edit_kurs.setText("10600");
        }
        setKusr();
    }

    private void setKusr() {
        edit_kurs.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                sql = "Update `kurs` set kurs=" + Util.decimalFormatterToDouble(edit_kurs.getText(), 10600);
                accessObject.executeUpdate(sql);

            }
        });

    }

    void comboSet() {
        klients = new Klient().select().all();
        for (Klient klient : klients) {
            klientList.add(klient.get("fish"));
        }
        combo_mijoz.setButtonCell(combo_mijoz.getCellFactory().call(null));
        combo_mijoz.setItems(klientList);
        combo_mijoz.setValue(klientList.get(0));
        MyAutoCompletionBinding.bindAutoCompletion(combo_mijoz.getEditor(), klientList);

        combo_tulov_usul.setItems(FXCollections.observableArrayList("", "Наличные", "Пластик"));
        combo_savdo_val.setItems(FXCollections.observableArrayList("", "Сўм", "$"));
        combo_tolov_val.setItems(FXCollections.observableArrayList("Сўм", "$"));
        combo_savdo_val.setValue("Сўм");
        combo_tolov_val.setValue("Сўм");
        combo_tulov_usul.setValue("Наличные");

        combo_savdo_val.setOnAction(actionEvent -> {
            setTolovSumma();
        });
    }

    void initTable() {
        tableTolov.setPlaceholder(new Label(""));
        col_no.setCellValueFactory(new PropertyValueFactory("no"));
        col_tolov_kassa.setCellValueFactory(new PropertyValueFactory("tulov"));
        col_tolov_summa.setCellValueFactory(new PropertyValueFactory("summa"));
        col_tolov_val.setCellValueFactory(new PropertyValueFactory("valyuta"));
    }

    private void setTolovSumma() {
        tolov = 0;
        itog = 0;
        nasiya = 0;
        summa = 0;
        dol_summa = 0;
        dol_itog = 0;
        dol_nasiya = 0;
        double som = 0, dollar = 0;

        double kurs = Util.decimalFormatterToDouble(edit_kurs.getText(), 10600);

        for (TulovKar savdo : tableTolov.getItems()) {
            if (savdo.getValyuta().equals("Сўм"))
                som += savdo.getSumma();
            else dollar += savdo.getSumma();
        }


        summa = Util.decimalFormatterToDouble(lb_jami_summa.getText(), 0);
//        chegirma= Util.decimalFormatterToDouble(lb_chegirma.getText(),0);
//        itog= Double.parseDouble(DecimalFormat.doubleFormat(lb_itog_summa.getText()));

        dol_summa = Util.decimalFormatterToDouble(lb_jami_summa_dol.getText(), 0);
//        dol_chegirma= Util.decimalFormatterToDouble(lb_chegirma_dol.getText(),0);

        if (combo_savdo_val.getValue() == null || combo_savdo_val.getValue().equals("")) {
            itog = summa - chegirma;
            nasiya = itog - som;
            dol_itog = dol_summa - dol_chegirma;
            dol_nasiya = dol_itog - dollar;
            lb_itog_summa.setText(itog != 0 ? DecimalFormat.decimalFormatter(itog) + " Сўм" : "");
            lb_tolov_summa.setText(som != 0 ? DecimalFormat.decimalFormatter(som) + " Сўм" : "");
            lb_nasiya.setText(nasiya != 0 ? DecimalFormat.decimalFormatter(nasiya) + " Сўм" : "");

            lb_itog_summa_dol.setText(dol_itog != 0 ? DecimalFormat.decimalFormatter(dol_itog) + " $" : "");
            lb_tolov_summa_dol.setText(dollar != 0 ? DecimalFormat.decimalFormatter(dollar) + " $" : "");
            lb_nasiya_dol.setText(dol_nasiya != 0 ? DecimalFormat.decimalFormatter(dol_nasiya) + " $" : "");
        } else if (combo_savdo_val.getValue().equals("$")) {
            lb_chegirma.setText("");
            lb_chegirma_dol.setText(DecimalFormat.decimalFormatter(dol_chegirma) + " $");
            lb_jami_summa_dol.setText(obSUmmaDol != 0 ? DecimalFormat.decimalFormatter(obSUmmaDol) + " $" : "");
            lb_jami_summa.setText("");
            dol_itog = dol_summa + summa / kurs - dol_chegirma;
            tolov = dollar + som / kurs;
            dol_nasiya = dol_itog - tolov;
            lb_itog_summa.setText("");
            lb_tolov_summa.setText("");
            lb_nasiya.setText("");

            lb_itog_summa_dol.setText(DecimalFormat.decimalFormatter(dol_itog) + " $");
            lb_tolov_summa_dol.setText(DecimalFormat.decimalFormatter(tolov) + " $");
            lb_nasiya_dol.setText(DecimalFormat.decimalFormatter(dol_nasiya) + " $");
        } else {
            lb_chegirma.setText(DecimalFormat.decimalFormatter(chegirma) + " Сўм");
            lb_chegirma_dol.setText("");
            lb_jami_summa.setText(obSUmma != 0 ? DecimalFormat.decimalFormatter(obSUmma) + " Сўм" : "");
            lb_jami_summa_dol.setText("");
            itog = summa + dol_summa * kurs - dol_chegirma * kurs;
            dol_itog = summa / kurs + dol_summa - dol_chegirma;
            tolov = som + dollar * kurs;
            nasiya = itog - tolov;
            dol_nasiya = nasiya / kurs;
            lb_itog_summa.setText(itog != 0 ? DecimalFormat.decimalFormatter(itog) + " Сўм" : "");
            lb_tolov_summa.setText(som != 0 ? DecimalFormat.decimalFormatter(tolov) + " Сўм" : "");
            lb_nasiya.setText(nasiya != 0 ? DecimalFormat.decimalFormatter(nasiya) + " Сўм" : "");

            lb_itog_summa_dol.setText("");
            lb_tolov_summa_dol.setText("");
            lb_nasiya_dol.setText("");
        }
    }

    Sotuv sotuv;

    private int Sotuv(String kassa, String card, String naqd, int klient) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        int chek_id = -1;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH");

        if (SotuvController.korzinkas != null) {
            if (sotuv == null) {
                sotuv = new Sotuv();
            }
            Chek check = new Chek();
            check.Set("check", kassa);
            check.Set("user_id", 1);
            check.Set("klient_id", klient);
            check.Set("naqd", naqd);
            check.Set("plastik", card);
            check.Set("summa", Util.decimalFormatterToDouble(String.valueOf(obSUmma), 0));
            check.Set("sana", Controller.Sana());
            check.Set("soat", LocalDateTime.now().format(formatter));
            check.Set("skidka", Util.decimalFormatterToDouble(String.valueOf(chegirma), 0));
            chek_id = check.save();

            for (Korzinka korzinka : sample.savdo.savdoAdmin.SotuvController.korzinkas) {
                DBAccessObject.InsertBuyurtma(korzinka.getId(), korzinka.getNarxi(), korzinka.getSoni(),
                        Controller.Sana(), Integer.parseInt(LocalDateTime.now().format(format)), chek_id, korzinka.getFoyda());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Успешные продажи!");
            alert.show();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alert.close();

        } else Util.alert("Предупреждение", "Информация неполная !!!");

        return chek_id;
    }

    void closeStage(Event event) {
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        stage1.close();
    }


}
