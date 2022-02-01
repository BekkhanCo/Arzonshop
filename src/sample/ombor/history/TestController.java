package sample.ombor.history;


import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.dbAccess.DBAccessObject;
import sample.models.Sotuv;
import sample.models.Prixod;
import sample.ombor.omborControler;
import sample.util.CallbackColumn;
import sample.util.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TestController {
    @FXML
    private Label txt_id;
    @FXML
    private Label txtNomi;
    @FXML
    private TableColumn<Prixod, String> col_turi;

    @FXML
    private TableColumn<Prixod, String> col_Snarxi;

    @FXML
    private TableColumn<Prixod, String> col_Knarxi;

    @FXML
    private TableColumn<Prixod, String> col_soni;

    @FXML
    private TableColumn<Prixod, String> col_Usumma;

    @FXML
    private TableColumn<Prixod, String> col_sana;


    @FXML
    private TableView<Prixod> table_kirim;

    @FXML
    private TableView<Sotuv> table_sotuv;

    @FXML
    private TableColumn<Sotuv, String> col_SSturi;

    @FXML
    private TableColumn<Sotuv, String> col_SSnarxi;

    @FXML
    private TableColumn<Sotuv, String> col_SSsoni;

    @FXML
    private TableColumn<Sotuv, String> col_SSUsumma;

    @FXML
    private TableColumn<Sotuv, String> col_SSsana;

    @FXML
    private ComboBox<String> combo_tur_sana;

    @FXML
    private DatePicker data;

    @FXML
    private Label txt_Summa;

    @FXML
    private JFXButton btnSotuv;

    @FXML
    private JFXButton btnKirim;

    public static String id = "";
    public static String nomi = "";

    String sql = "";
    DBAccessObject dbAccessObject;
    ObservableList<Sotuv> modelSotuvs = FXCollections.observableArrayList();
    ObservableList<Prixod> modelKirims = FXCollections.observableArrayList();
    ObservableList<String> sortData = FXCollections.observableArrayList();
    DecimalFormat dc = new DecimalFormat("###,###,###,###.###");

    @FXML
    void handle_combo(ActionEvent event) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = format.format(data.getValue());
        refreshTableKirim(SanaFormat(date));
        refreshTableSotuv(SanaFormat(date));
    }

    @FXML
    void handle_sana(ActionEvent event) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = format.format(data.getValue());
        refreshTableKirim(SanaFormat(date));
        refreshTableSotuv(SanaFormat(date));
    }

    @FXML
    void initialize() {
        combo_tur_sana.setStyle("-fx-font: 20px \"Serif\";");
        Sana();
        table_kirim.setPlaceholder(new Label("Нет доступной информации!"));
        table_sotuv.setPlaceholder(new Label("Нет доступной информации!"));

        sortData.add("День");
        sortData.add("Месяц");
        sortData.add("Год");
        sortData.add("Все");
        combo_tur_sana.setItems(sortData);

        initTableSotuv();
        initTableKirim();

        refreshTableSotuv(".");
        getSumOfSotuv();

        btnSotuv.setOnAction(event -> {
            table_sotuv.setVisible(true);
            table_kirim.setVisible(false);
            refreshTableSotuv(".");
            getSumOfSotuv();
        });

        btnKirim.setOnAction(event -> {
            table_kirim.setVisible(true);
            table_sotuv.setVisible(false);
            refreshTableKirim(".");
            getSumOfKirim();
        });
    }

    private void refreshTableSotuv(String date) {
        dbAccessObject = new DBAccessObject();
        sql = "SELECT o.name nomi,t.nomi turi,s.sotish narxi,s.soni,s.sana ,s.sotish*s.soni usumma FROM sotuv s JOIN ombor o ON s.mah_id=o.id JOIN tur t ON t.id=o.turi_id WHERE s.mah_id=" + omborControler.product_id + " AND s.sana LIKE '%" + date + "%'";
        modelSotuvs = dbAccessObject.getDataList(Sotuv.class, sql);
        txt_id.setText(omborControler.product_id + "");
        txtNomi.setText(omborControler.product_name + "");
        table_sotuv.setItems(modelSotuvs);
    }

    private void initTableSotuv() {
        col_SSturi.setCellValueFactory(new CallbackColumn<>("turi"));
        col_SSnarxi.setCellValueFactory(new CallbackColumn<>("narxi"));
        col_SSsoni.setCellValueFactory(new CallbackColumn<>("soni"));
        col_SSUsumma.setCellValueFactory(new CallbackColumn<>("usumma"));
        col_SSsana.setCellValueFactory(new CallbackColumn<>("sana"));
    }

    private void refreshTableKirim(String date) {
        dbAccessObject = new DBAccessObject();
        sql = "SELECT t.nomi turi,p.sotish,p.kelish,p.soni,p.sana,p.sotish*p.soni usumma FROM prixod p JOIN ombor o ON p.m_id=o.id JOIN tur t ON o.turi_id=t.id where p.m_id=" + omborControler.product_id + " AND p.sana LIKE '%" + date + "%'";
        modelKirims = dbAccessObject.getDataList(Prixod.class, sql);
        table_kirim.setItems(modelKirims);
    }

    private void initTableKirim() {
        col_turi.setCellValueFactory(new CallbackColumn<>("turi"));
        col_Snarxi.setCellValueFactory(new CallbackColumn<>("sotish"));
        col_Knarxi.setCellValueFactory(new CallbackColumn<>("kelish"));
        col_soni.setCellValueFactory(new CallbackColumn<>("soni"));
        col_sana.setCellValueFactory(new CallbackColumn<>("sana"));
        col_Usumma.setCellValueFactory(new CallbackColumn<>("usumma"));

    }

    public final String Sana() {
        Date d = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat cformat = new SimpleDateFormat("yyyy-MM-dd");
        data.setValue(LOCAL_DATE(cformat.format(d)));
        return dformat.format(d);
    }

    public final String SanaFormat(String sana) {
        String substring = sana;
        if (combo_tur_sana.getSelectionModel().getSelectedIndex() == 0) {
            substring = sana;
        }
        if (combo_tur_sana.getSelectionModel().getSelectedIndex() == 1) {
            substring = sana.substring(3, sana.length());
            System.out.println(substring);
        }
        if (combo_tur_sana.getSelectionModel().getSelectedIndex() == 2) {
            substring = sana.substring(6, sana.length());
            System.out.println(substring);
        }
        if (combo_tur_sana.getSelectionModel().getSelectedIndex() == 3) {
            substring = ".";
        }
        return substring;
    }

    public static final LocalDate LOCAL_DATE(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

    public void getSumOfSotuv() {
        float summa = 0;
        for (Sotuv mod : modelSotuvs) {
            summa += Util.decimalFormatterToDouble(mod.get("narxi"), 0) * Util.decimalFormatterToDouble(mod.get("soni"), 0);
        }
        txt_Summa.setText(dc.format(summa));
    }

    public void getSumOfKirim() {
        float summa = 0;
        for (Prixod prixod : modelKirims) {
            summa += Util.decimalFormatterToDouble(prixod.get("sotish"), 0) * Util.decimalFormatterToDouble(prixod.get("soni"), 0);
        }
        txt_Summa.setText(dc.format(summa));
    }

}
