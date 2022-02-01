package sample.client;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.BaseModel;
import sample.models.Chek;
import sample.models.Klient;
import sample.models.Tulov;
import sample.util.*;
import sample.views.Stages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    private JFXTextField tft_search = new JFXTextField("");

    @FXML
    private JFXComboBox<String> combo_sana;

    @FXML
    private JFXComboBox<String> combo_klient;

    @FXML
    private TreeTableView<BaseModel> table_klient;

    @FXML
    private TreeTableColumn<BaseModel, String> col_id;

    @FXML
    private TreeTableColumn<BaseModel, String> col_klient;

    @FXML
    private TreeTableColumn<BaseModel, String> col_debit;

    @FXML
    private TreeTableColumn<BaseModel, String> col_kredit;

    @FXML
    private TreeTableColumn<BaseModel, String> col_summa;

    @FXML
    private TreeTableColumn<BaseModel, String> col_sana;

    @FXML
    private TreeTableColumn<BaseModel, String> col_coment;

    @FXML
    private TreeTableColumn<BaseModel, String> col_val;


//-----------------------------------

    @FXML
    private TableView<Klient> table_klientlar;

    @FXML
    private TableColumn<Klient, String> col_nomi;

    @FXML
    private TableColumn<Klient, String> col_adress;

    @FXML
    private TableColumn<Klient, String> col_tel;

    @FXML
    private TableColumn<Klient, String> col_koment;

    @FXML
    private TableColumn<Klient, String> col_qarz;

    @FXML
    private Label lbl_klient;

    @FXML
    private Label lbl_ostat;

    @FXML
    private JFXDatePicker beginDate;

    @FXML
    private JFXDatePicker endDate;

    int filterId = 2;
    DateTimeFormatter dt = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    TreeItem<BaseModel> root;
    FilteredList<Tulov> tovarFilteredList;
    SortedList<Tulov> sortedList;
    FilteredList<Klient> tovarFilteredList1;
    SortedList<Klient> sortedList1;
    ObservableList<Tulov> tulovs = FXCollections.observableArrayList();
    ObservableList<Chek> checks = FXCollections.observableArrayList();
    ObservableList<Klient> klients = FXCollections.observableArrayList();
    ObservableList<Klient> klientCombo = FXCollections.observableArrayList();
    ObservableList<String> groupStringList = FXCollections.observableArrayList();

    ObservableList<Chek> tovarExcel = FXCollections.observableArrayList();

    String sql = "", sana = "";
    DBAccessObject accessObject;
    public static String klientOl = "";

    @FXML
    void handle_addKlient(ActionEvent event) {
        Stage stage=Stages.getAddClient();
        stage.setOnCloseRequest(event1 -> {
            refreshTable(dt.format(beginDate.getValue()), dt.format(endDate.getValue()));
        });
        stage.show();
    }

    @FXML
    public void handle_back(ActionEvent event) {
        Stages.getHome().show();
        Stages.closeStage(event);
    }

    @FXML
    void handle_tulov(ActionEvent event) {
        Stage stage=Stages.getPayment();
        stage.setOnCloseRequest(event1 -> {
            Table();
            refreshTable(dt.format(beginDate.getValue()), dt.format(endDate.getValue()));
            klientOl = "";
        });
        stage.show();
    }


    @FXML
    void handle_sana(ActionEvent event) {
        refreshTable(dt.format(beginDate.getValue()), dt.format(endDate.getValue()));
    }


    @FXML
    void handle_excel(ActionEvent event) {
       exportToExcel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accessObject = new DBAccessObject();

        setData();
        refreshTable(dt.format(beginDate.getValue()), dt.format(endDate.getValue()));
    }

    void setData() {
        lbl_ostat.setText(DecimalFormat.decimalFormatter(Table()) + " UZS");
        klientOl = "";

        table_klient.setOnMouseClicked(event -> {
            klientOl = table_klient.getSelectionModel().getSelectedItem().getValue().get("fish");
        });

        beginDate.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        endDate.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        setCombo();
        initTable();
    }

    void setCombo() {
        klientCombo = new Klient().select().all();
        for (Klient klient : klientCombo) {
            groupStringList.add(klient.get("fish"));
        }
        combo_klient.setItems(groupStringList);
        MyAutoCompletionBinding.bindAutoCompletion(combo_klient.getEditor(), groupStringList);
        combo_klient.setOnAction(event -> {
            tft_search.setText(combo_klient.getValue());
        });
        combo_sana.setItems(FXCollections.observableArrayList("День", "Месяц", "Год", "Все"));
        combo_sana.setValue("Месяц");
        combo_sana.setOnAction(event -> {
            filterId = combo_sana.getSelectionModel().getSelectedIndex() + 1;
            refreshTable(dt.format(beginDate.getValue()), dt.format(endDate.getValue()));
        });
    }

    void initTable() {
        col_nomi.setCellValueFactory(new CallbackColumn<>("fish"));
        col_adress.setCellValueFactory(new CallbackColumn<>("adress"));
        col_koment.setCellValueFactory(new CallbackColumn<>("koment"));
        col_qarz.setCellValueFactory(new CallbackColumn<>("qarz"));
        col_sana.setCellValueFactory(new CallbackForTreeColumn<>("sana"));
        col_summa.setCellValueFactory(new CallbackForTreeColumn<>("summa"));
//        col_val.setCellValueFactory(new CallbackForTreeColumn<>("val"));
        col_id.setCellValueFactory(new CallbackForTreeColumn<>("id"));
        col_debit.setCellValueFactory(new CallbackForTreeColumn<>("debit"));
        col_kredit.setCellValueFactory(new CallbackForTreeColumn<>("kredit"));
        col_klient.setCellValueFactory(new CallbackForTreeColumn<>("fish"));
        col_coment.setCellValueFactory(new CallbackForTreeColumn<>("koment"));
        col_val.setCellValueFactory(new CallbackForTreeColumn<>("val"));
        setToolTip(col_nomi);
        setToolTip(col_adress);
        setToolTip(col_koment);
        setToolTip(col_qarz);
    }

    double Table() {
        sql = "SELECT klient.`id`, klient.`fish`, klient.`tel`, klient.`adress`, klient.`koment`, REPLACE(FORMAT(SUM(if(klient_balans.qarz is null,0,klient_balans.qarz))-SUM(if(klient_balans.summa_dol is null,0,klient_balans.summa_dol)),2),',','') qarz " +
                "FROM `klient` JOIN klient_balans ON klient.id=klient_balans.klient_id GROUP BY klient.id";
        klients = accessObject.getDataList(Klient.class, sql);
        tovarFilteredList1 = new FilteredList<>(klients);
        sortedList1 = new SortedList<>(tovarFilteredList1);
        table_klientlar.setItems(sortedList1);
        Sort();
        double summa = 0;
        for (Klient klient : sortedList1) {
            if (klient.get("qarz") != null) {
                summa += Util.decimalFormatterToDouble(klient.get("qarz"));
            }
        }
        lbl_klient.setText(klients.size() + "");
        return summa;
    }

    void refreshTable(String begin, String end) {
        switch (filterId) {
            case 1:
                sql = "SELECT klient_balans.`id`, `klient_id`,klient.fish, `cat_id`,tulovcat.nomi,`sana`, `soat`,\n" +
                        "if(`summa_dol` is null,'—',REPLACE(FORMAT(`summa_dol`,2),',','')) debit, `skidka`, REPLACE(FORMAT(`summa`,2),',','') summa,`val`, `kurs`,\n" +
                        "if(`qarz` is null,'—',REPLACE(FORMAT(`qarz`,2),',',''))  kredit, klient_balans.`chek_id`, klient_balans.`koment`,\n" +
                        "CONCAT(SUBSTRING(`sana`, 7,4),'-',SUBSTRING(`sana`,4,2),'-',SUBSTRING(`sana`, 1,2)) date \n" +
                        "FROM `klient_balans` JOIN klient ON klient_balans.klient_id=klient.id JOIN tulovcat ON klient_balans.cat_id=tulovcat.id \n" +
                        "WHERE `sana`>='" + begin + "'  \n" +
                        "                AND SUBSTRING(`sana`, 4, 2)>=SUBSTRING('" + begin + "', 4, 2) \n" +
                        "                AND SUBSTRING(`sana`, 7, 4)>=SUBSTRING('" + begin + "', 7, 4)\n" +
                        "                AND`sana`<='" + end + "'  \n" +
                        "                AND SUBSTRING(`sana`, 4, 2)<=SUBSTRING('" + end + "', 4, 2) \n" +
                        "                AND SUBSTRING(`sana`, 7, 4)<=SUBSTRING('" + end + "', 7, 4) ORDER BY klient_balans.id DESC";
                break;
            case 2:
                sql = "SELECT klient_balans.`id`, `klient_id`,klient.fish, `cat_id`,tulovcat.nomi,`sana`, `soat`,\n" +
                        "if(`summa_dol` is null,'—',REPLACE(FORMAT(`summa_dol`,2),',','')) debit, `skidka`, REPLACE(FORMAT(`summa`,2),',','') summa,`val`, `kurs`,\n" +
                        "if(`qarz` is null,'—',REPLACE(FORMAT(`qarz`,2),',',''))  kredit, klient_balans.`chek_id`, klient_balans.`koment`,\n" +
                        "CONCAT(SUBSTRING(`sana`, 7,4),'-',SUBSTRING(`sana`,4,2),'-',SUBSTRING(`sana`, 1,2)) date \n" +
                        "FROM `klient_balans` JOIN klient ON klient_balans.klient_id=klient.id JOIN tulovcat ON klient_balans.cat_id=tulovcat.id \n" +
                        "WHERE           SUBSTRING(`sana`, 4, 2)>=SUBSTRING('" + begin + "', 4, 2) \n" +
                        "                AND SUBSTRING(`sana`, 7, 4)>=SUBSTRING('" + begin + "', 7, 4)\n" +
                        "                AND SUBSTRING(`sana`, 4, 2)<=SUBSTRING('" + end + "', 4, 2) \n" +
                        "                AND SUBSTRING(`sana`, 7, 4)<=SUBSTRING('" + end + "', 7, 4) ORDER BY klient_balans.id DESC";
                break;
            case 3:
                sql = "SELECT klient_balans.`id`, `klient_id`,klient.fish, `cat_id`,tulovcat.nomi,`sana`, `soat`,\n" +
                        "if(`summa_dol` is null,'—',REPLACE(FORMAT(`summa_dol`,2),',','')) debit, `skidka`, REPLACE(FORMAT(`summa`,2),',','') summa,`val`, `kurs`,\n" +
                        "if(`qarz` is null,'—',REPLACE(FORMAT(`qarz`,2),',',''))  kredit, klient_balans.`chek_id`, klient_balans.`koment`,\n" +
                        "CONCAT(SUBSTRING(`sana`, 7,4),'-',SUBSTRING(`sana`,4,2),'-',SUBSTRING(`sana`, 1,2)) date \n" +
                        "FROM `klient_balans` JOIN klient ON klient_balans.klient_id=klient.id JOIN tulovcat ON klient_balans.cat_id=tulovcat.id \n" +
                        "WHERE         SUBSTRING(`sana`, 7, 4)>=SUBSTRING('" + begin + "', 7, 4)\n" +
                        "              AND SUBSTRING(`sana`, 7, 4)<=SUBSTRING('" + end + "', 7, 4) ORDER BY klient_balans.id DESC";
                break;
            case 4:
                sql = "SELECT klient_balans.`id`, `klient_id`,klient.fish, `cat_id`,tulovcat.nomi,`sana`, `soat`,\n" +
                        "                   if(`summa_dol` is null,'—',REPLACE(FORMAT(`summa_dol`,2),',','')) debit, `skidka`, REPLACE(FORMAT(`summa`,2),',','') summa,`val` , `kurs`,\n" +
                        "                   if(`qarz` is null,'—',REPLACE(FORMAT(`qarz`,2),',',''))  kredit, klient_balans.`chek_id`, klient_balans.`koment`,\n" +
                        "                   CONCAT(SUBSTRING(`sana`, 7,4),'-',SUBSTRING(`sana`,4,2),'-',SUBSTRING(`sana`, 1,2)) date  \n" +
                        "                   FROM `klient_balans` JOIN klient ON klient_balans.klient_id=klient.id JOIN tulovcat ON klient_balans.cat_id=tulovcat.id \n" +
                        "                   ORDER BY klient_balans.id DESC";
        }
        tulovs = accessObject.getDataList(Tulov.class, sql);
        tovarFilteredList = new FilteredList<>(tulovs);
        sortedList = new SortedList<>(tovarFilteredList);
        filter();
        createTable();
        Table();
    }

    void createTable() {
        tovarExcel.clear();
        root = new TreeItem<>();
        root.setExpanded(true);
        table_klient.setRoot(root);
        table_klient.setShowRoot(false);
        for (Tulov tovar : sortedList) {
            TreeItem<BaseModel> item = new TreeItem<>(tovar);
            root.getChildren().add(item);
            if (tovar.get("chek_id") != null) {
                sql = " SELECT sotuv.`id` nomer, `mah_id`,ombor.name fish, CONCAT(sotuv.`sotish`,'UZS x ',sotuv.soni,' =') debit ,REPLACE(FORMAT(sotuv.sotish* sotuv.soni,3),',','') kredit,sotuv.sana,`check`.`skidka`,`sotuv`.`sotish`,`sotuv`.`soni`" +
                        "FROM `sotuv` JOIN ombor ON sotuv.mah_id=ombor.id JOIN `check` ON sotuv.check_id=`check`.id WHERE check_id=" + tovar.get("chek_id") + " ORDER BY nomer DESC";
                checks = accessObject.getDataList(Chek.class, sql);
                for (Chek ostatTov : checks) {
                    tovarExcel.add(ostatTov);
                    TreeItem<BaseModel> treeItem = new TreeItem<>(ostatTov);
                    item.getChildren().add(treeItem);
                }
            }
        }
//        tft_summa.setText(Util.doubleToString(String.valueOf(summa)));
    }

    void filter() {
        tft_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String s, String t1) {
                String[] spl_keys = CyrillicToLatin.transliterate(t1.toLowerCase(Locale.ROOT)).split(" ");
                tovarFilteredList.setPredicate(Ftovar -> {
                    boolean ok = true;
                    int index = 0;
                    if (t1 == null || t1.isEmpty()) {
                        return true;
                    }
                    String item = CyrillicToLatin.transliterate(Ftovar.get("fish").toLowerCase());
                    for (String key : spl_keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }
                    if (String.valueOf(Ftovar.get("id")).contains(t1)) {
                        return true;
                    }
                    return ok;
                });
                createTable();
            }
        });
    }

    public final void Sort() {
        tft_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String[] keys = CyrillicToLatin.transliterate(t1.toLowerCase()).split(" ");
                tovarFilteredList1.setPredicate(student1 -> {
                    boolean ok = true;
                    int index = 0;
                    if (t1 == null || t1.isEmpty()) {
                        return true;
                    }

                    String item = CyrillicToLatin.transliterate(student1.get("id").toLowerCase());
                    for (String key : keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }

                    if (!ok) {
                        ok = true;
                        item = CyrillicToLatin.transliterate(student1.get("fish").toLowerCase());
                        for (String key : keys) {
                            index = item.indexOf(key);
                            if (index == -1) {
                                ok = false;
                                break;
                            }
                        }
                    }

                    return ok;
                });

                table_klientlar.setItems(sortedList1);
                double summa = 0;
                for (Klient klient : sortedList1) {
                    summa += Util.decimalFormatterToDouble(klient.get("qarz"));
                }
                lbl_ostat.setText(DecimalFormat.decimalFormatter(summa) + " UZS");
            }
        });


    }

    XSSFWorkbook workbook;
    XSSFSheet sheet;
    XSSFRow row;
    CellStyle yacheyka;
    CellStyle yacheykaBold;
    CellStyle regularCol;
    CellStyle boldCol;
    CellStyle boldColCenter;
    CellStyle senCol;
    CellStyle senColBold;
    DateTimeFormatter forma = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        public void exportToExcel() {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("List");
        row = sheet.createRow(0);
        yacheyka = workbook.createCellStyle();
        yacheykaBold = workbook.createCellStyle();
        regularCol =workbook.createCellStyle();
        boldCol =workbook.createCellStyle();
        boldColCenter =workbook.createCellStyle();
        senCol =workbook.createCellStyle();
        senColBold =workbook.createCellStyle();

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));
        sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 5));
//----------------------------------Sytles----------------------------------
        yacheyka.setAlignment(HorizontalAlignment.CENTER);
        yacheyka.setVerticalAlignment(VerticalAlignment.CENTER);
        yacheyka.setBorderBottom(BorderStyle.THIN);
        yacheyka.setBorderRight(BorderStyle.THIN);
        yacheyka.setBorderTop(BorderStyle.THIN);
        yacheyka.setBorderLeft(BorderStyle.THIN);

        yacheykaBold.setAlignment(HorizontalAlignment.CENTER);
        yacheykaBold.setVerticalAlignment(VerticalAlignment.CENTER);
        yacheykaBold.setBorderBottom(BorderStyle.THIN);
        yacheykaBold.setBorderRight(BorderStyle.THIN);
        yacheykaBold.setBorderTop(BorderStyle.THIN);
        yacheykaBold.setBorderLeft(BorderStyle.THIN);

        boldColCenter.setAlignment(HorizontalAlignment.CENTER);
        boldColCenter.setVerticalAlignment(VerticalAlignment.CENTER);

        Font fontTitle = sheet.getWorkbook().createFont();
        fontTitle.setFontName("Calibri");
        fontTitle.setFontHeightInPoints((short) 12);

        Font fontTitleBold = sheet.getWorkbook().createFont();
        fontTitleBold.setFontName("Calibri");
        fontTitleBold.setFontHeightInPoints((short) 12);
        fontTitleBold.setBold(true);

        Font fontSen = sheet.getWorkbook().createFont();
        fontSen.setFontName("Calibri");
        fontSen.setFontHeightInPoints((short) 16);

        Font fontSenBold = sheet.getWorkbook().createFont();
        fontSenBold.setFontName("Calibri");
        fontSenBold.setFontHeightInPoints((short) 16);

        senCol.setFont(fontSen);

        senColBold.setFont(fontSenBold);

        yacheyka.setFont(fontTitle);

        regularCol.setFont(fontTitle);

        boldCol.setFont(fontTitleBold);
        boldColCenter.setFont(fontTitleBold);
        yacheykaBold.setFont(fontTitleBold);
//---------------------------------------------------------------------
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);
        cell.setCellValue(forma.format(beginDate.getValue())+"-"+forma.format(endDate.getValue())+" Дата отчета");
        cell.setCellStyle(boldColCenter);

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("Клиент:");
        cell.setCellStyle(boldCol);
        cell = row.createCell(1);
        cell.setCellValue(combo_klient.getValue());
        cell.setCellStyle(regularCol);
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Задолженность клиентов:");
        cell.setCellStyle(boldCol);
        cell = row.createCell(1);
        cell.setCellValue(sortedList1.get(0).get("qarz")+" UZS");
        cell.setCellStyle(regularCol);

        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("---Продукты---");
        cell.setCellStyle(boldColCenter);
//------------------------------------------------

        int rowID = 6;

            for (String item: filter(tovarExcel)){
                rowID=createExcelCell(rowID,sorted(item,tovarExcel));
            }

//        row=sheet.createRow(rowID);
//        cell=row.createCell(0);
//        cell.setCellValue("To'lovlar");
//        cell.setCellStyle(boldColCenter);
//        sheet.addMergedRegion(new CellRangeAddress(rowID, rowID, 0, 2));
//        rowID++;
//        String[] columns ={"№","Summa","Sana"};
//        row=sheet.createRow(rowID);
//        rowID++;
//        int c=0;
//        for (String ustun:columns){
//            cell=row.createCell(c);
//            cell.setCellValue(ustun);
//            cell.setCellStyle(yacheykaBold);
//            c++;
//        }
//        c=1;
//        for (Qarz tulov:qarzs){
//            if (Double.valueOf(tulov.get("tulov"))>0){
//                row=sheet.createRow(rowID);
//                org.apache.poi.ss.usermodel.Cell cellNo=row.createCell(0);
//                org.apache.poi.ss.usermodel.Cell cellSum=row.createCell(1);
//                org.apache.poi.ss.usermodel.Cell cellSana=row.createCell(2);
//                cellNo.setCellStyle(yacheyka);
//                cellSana.setCellStyle(yacheyka);
//                cellSum.setCellStyle(yacheyka);
//
//                cellNo.setCellValue(c);
//                cellSum.setCellValue(tulov.get("tulov").replace('.',','));
//                cellSana.setCellValue(forma.format(LOCAL_DATE(tulov.get("sanasi"))));
//                c++;
//                rowID++;}
//        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);

        FileOutputStream outputStream;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel documents", ".xlsx"));
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                outputStream = new FileOutputStream(file + ".xlsx");
                workbook.write(outputStream);
                workbook.close();
                outputStream.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Данные переданы в Excel!");
                alert.show();

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                alert.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int createExcelCell(int rowID,ObservableList<Chek> mahsulots){
        row = sheet.createRow(rowID);
        org.apache.poi.ss.usermodel.Cell cell=row.createCell(0);
        cell=row.createCell(2);
        cell.setCellValue("Дата:");
        cell.setCellStyle(boldCol);
        Cell cellSana=row.createCell(3);
        cellSana.setCellValue(mahsulots.get(0).get("sana"));
        cellSana.setCellStyle(regularCol);

        String[] columns ={"Наименование товара","Кол-во ","Цена","Сумма"};
        row = sheet.createRow(rowID+1);
        for (int i = 0; i < columns.length; i++) {
            cell=row.createCell(i);
            cell.setCellStyle(yacheykaBold);
            cell.setCellValue(columns[i]);
        }
        rowID=rowID+2;
        double summa=0;
        for (Chek mahList : mahsulots) {
            row = sheet.createRow(rowID);
            Cell col_nomi = row.createCell(0);
            Cell col_soni = row.createCell(1);
            Cell col_narx = row.createCell(2);
            Cell col_summa = row.createCell(3);

            col_nomi.setCellStyle(yacheyka);
            col_soni.setCellStyle(yacheyka);
            col_narx.setCellStyle(yacheyka);
            col_summa.setCellStyle(yacheyka);
//------------------------------------------------------------
            col_nomi.setCellValue(mahList.get("fish"));
            col_soni.setCellValue(mahList.get("soni"));
            col_narx.setCellValue(Util.decimalFormatterToDouble(mahList.get("sotish"))+" UZS");
            col_summa.setCellValue(Util.decimalFormatterToDouble(mahList.get("kredit"))+" UZS");
            summa+=Util.decimalFormatterToDouble(mahList.get("kredit"));
            rowID++;
        }

        row=sheet.createRow(rowID);
        cell=row.createCell(2);
        cell.setCellValue("Общий:");
        cell.setCellStyle(boldColCenter);
        Cell cellSumma=row.createCell(3);
        cellSumma.setCellValue(Util.decimalFormatterToDouble(String.valueOf(summa))+" UZS");
        cellSumma.setCellStyle(boldColCenter);


        return rowID+2;
    }

    public List<String> filter(ObservableList<Chek> list){
        List<String> filtered=new ArrayList<>();
        filtered.add(list.get(0).get("sana"));

//------------------------------------------------------
        for (int j = 1; j < list.size(); j++) {
            if (!filtered.contains(list.get(j).get("sana"))){
                filtered.add(list.get(j).get("sana"));
            }}
        return filtered;
    }

    public ObservableList<Chek> sorted(String filter, ObservableList<Chek> list){
        System.out.println("sortlash boshlandi>");
        ObservableList<Chek> sorted=FXCollections.observableArrayList();
        for (int i = 0; i <list.size(); i++) {
            if (filter.contentEquals(list.get(i).get("sana"))){
                sorted.add(list.get(i));
            }
        }
        return sorted;
    }

    void setToolTip(TableColumn<Klient, String> nameCol) {

            table_klientlar.setOnMouseClicked(event -> {
                if(table_klientlar.getSelectionModel().getSelectedItem().get("fish")!=null){
            Alert showPhoneNum= new Alert(Alert.AlertType.INFORMATION);
            showPhoneNum.setTitle("Telefon raqam..");
            showPhoneNum.setContentText("\""+table_klientlar.getSelectionModel().getSelectedItem().get("fish")+"\"ning telefon raqami::");
            showPhoneNum.setHeaderText(table_klientlar.getSelectionModel().getSelectedItem().get("tel"));
            showPhoneNum.show();
            PauseTransition pt = new PauseTransition(Duration.seconds(10));
            pt.setOnFinished(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    showPhoneNum.close();
                }
            });
            pt.play();
                }
        });
         }

}