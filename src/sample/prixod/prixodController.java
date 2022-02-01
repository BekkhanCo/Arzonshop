package sample.prixod;

import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.Category;
import sample.models.Delete;
import sample.models.Prixod;
import sample.models.Product;
import sample.util.CallbackColumn;
import sample.util.CyrillicToLatin;
import sample.util.MyAutoCompletionBinding;
import sample.util.Util;
import sample.vendor.Report;
import sample.views.Stages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class prixodController {

    @FXML
    private JFXTextField tft_search;

    @FXML
    private ComboBox<Category> combo_tur;

    @FXML
    private TextField tft_summa;

    @FXML
    private Label lbl_soat;

    @FXML
    private TableView<Prixod> table_prixod;

    @FXML
    private TableColumn<Prixod, String> col_id;

    @FXML
    private TableColumn<Prixod, String> col_nomi;

    @FXML
    private TableColumn<Prixod, String> col_tur;

    @FXML
    private TableColumn<Prixod, String> col_soni;

    @FXML
    private TableColumn<Prixod, String> col_kelish;

    @FXML
    private TableColumn<Prixod, String> col_sotish;

    @FXML
    private TableColumn<Prixod, String> col_summa;

    @FXML
    private TableColumn<Prixod, String> col_sana;

    @FXML
    private DatePicker data_sana;

    @FXML
    private JFXComboBox<String> combo_nomi;

    @FXML
    private TextField tft_soni;

    @FXML
    private TextField tft_kelish;
    @FXML
    private TextField edt_nomi_id;
    @FXML
    private TextField tft_shtrix;

    @FXML
    private AnchorPane root;


    @FXML
    private TextField tft_sotish;
    private Report report;

    public static boolean A = true;

    ObservableList<Category> turs = FXCollections.observableArrayList();
    ObservableList<Prixod> prixods = FXCollections.observableArrayList();
    ObservableList<String> groupStringList = FXCollections.observableArrayList();
    FilteredList<String> stringFilteredList;
    ObservableList<Product> ombors = FXCollections.observableArrayList();

    FilteredList<Prixod> filteredList;
    SortedList<Prixod> sortedList;
    String sql = "";

    DBAccessObject accessObject = new DBAccessObject();
    Prixod prixod;


    @FXML
    void handle_clean(ActionEvent event) {
        tft_soni.clear();
        tft_kelish.clear();
        tft_sotish.clear();
        edt_nomi_id.clear();
        prixod = null;
    }

    @FXML
    void handle_delete(ActionEvent event) {
        Prixod prixod = table_prixod.getSelectionModel().getSelectedItem();
        Delete.delete(prixod, prixod.get("id"));
        refreshTable(Controller.Sana());

    }


    DecimalFormat dc = new DecimalFormat("###,###,###.##");
    SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
    DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    Map<String, Object> maps;

    @FXML
    public void handle_prixod(ActionEvent event) {
        prixod();
    }

    @FXML
    void handle_export(ActionEvent event) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Prixod List");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        XSSFRow row = sheet.createRow(0);
        Cell cell = row.createCell(0);

        cell.setCellValue("Список приходов");

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);

        Font fontTitle = sheet.getWorkbook().createFont();
        fontTitle.setFontName("Arial");
        fontTitle.setFontHeightInPoints((short) 18);
        titleStyle.setFont(fontTitle);
        cell.setCellStyle(titleStyle);
        cell = row.createCell(7);
        cell.setCellStyle(titleStyle);

        Date d = new Date();
        row = sheet.createRow(1);
        Cell cell0 = row.createCell(0);
        cell0.setCellValue("Дата выпуска:");
        Cell cell1 = row.createCell(1);
        cell1.setCellValue(dformat.format(d));

        CellStyle titleStyle1 = workbook.createCellStyle();
        titleStyle1.setAlignment(HorizontalAlignment.RIGHT);
        titleStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        Font fontTitle1 = sheet.getWorkbook().createFont();
        fontTitle1.setFontName("Arial");
        fontTitle1.setFontHeightInPoints((short) 14);
        titleStyle1.setFont(fontTitle1);
        cell1.setCellStyle(titleStyle1);

        int colID = 0;
        row = sheet.createRow(2);
        for (TableColumn column : table_prixod.getColumns()) {

            Cell cellTitle = row.createCell(colID);

            cellTitle.setCellValue(column.getText());

            colID++;
        }

        int rowID = 3;

        for (Prixod prixod : table_prixod.getItems()) {
            row = sheet.createRow(rowID);
            Cell col_id = row.createCell(0);
            Cell col_nomi = row.createCell(1);
            Cell col_tur = row.createCell(2);
            Cell col_soni = row.createCell(3);
            Cell col_kelish = row.createCell(4);
            Cell col_sotish = row.createCell(5);
            Cell col_summa = row.createCell(6);
            Cell col_sana = row.createCell(7);
//------------------------------------------------------------
            col_id.setCellValue(prixod.get("id"));
            col_nomi.setCellValue(prixod.get("name"));
            col_tur.setCellValue(prixod.get("nomi"));
            col_soni.setCellValue(prixod.get("soni"));
            col_kelish.setCellValue(prixod.get("narx"));
            col_sotish.setCellValue(prixod.get("sotish"));
            col_summa.setCellValue(prixod.get("usumma"));
            col_sana.setCellValue(prixod.get("sana"));
            rowID++;
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);

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
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handle_id(ActionEvent event) {

    }

    @FXML
    void handle_back(ActionEvent event) {
        A = false;
        Stages.getHome().show();
        Stages.closeStage(lbl_soat);
    }

    @FXML
    void handle_combo_tur(ActionEvent event) {
        tft_search.setText(combo_tur.getValue().get("nomi"));
    }

    @FXML
    void handle_Plist(ActionEvent event) {
        Stages.getImportListPanel().show();
        Stages.closeStage(lbl_soat);
    }


    @FXML
    void handle_PRINT(ActionEvent event) {
        Prixod prixod = table_prixod.getSelectionModel().getSelectedItem();
        if (prixod != null) {
            shtrixPrint(prixod);
        }
    }


    @FXML
    void initialize() {
        A = true;
        combo_tur.setStyle("-fx-font: 20px \"Montserrat\";");
        combo_nomi.setStyle("-fx-font: 20px \"Arial Unicode MS\";");
        table_prixod.setPlaceholder(new Label("Нет доступной информации!"));
        data_sana.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        Controller.initClock(lbl_soat);
        initTable();

        accessObject = new DBAccessObject();
        turs = new Category().select().all();
        combo_tur.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category group, boolean b) {
                        super.updateItem(group, b);
                        if (!b) {
                            setText(group.get("nomi"));
                        }
                    }
                };
            }
        });
        combo_tur.setButtonCell(combo_tur.getCellFactory().call(null));
        combo_tur.setItems(turs);

        //////////////////////////////////////////////////////////////

        ombors = new Product().select().all();
        groupStringList.clear();
        for (Product ombor : ombors) {
            groupStringList.add(ombor.get("name"));
        }
        stringFilteredList = new FilteredList<>(groupStringList, p -> true);
        combo_nomi.setItems(groupStringList);
        MyAutoCompletionBinding.bindAutoCompletion(combo_nomi.getEditor(), groupStringList);
        refreshTable(Controller.Sana());

        patok();

    }

    private void refreshTable(String sana) {
        sql = "select g.id, g.m_id,o.name,o.type, t.nomi, g.soni ,g.kelish,o.shtrix,g.sotish, g.sana,g.soni*g.sotish usumma " +
                "from prixod g join ombor as o ON g.m_id=o.id JOIN tur t ON o.turi_id=t.id " +
                "WHERE g.sana LIKE '%" + sana + "%' order by g.id desc";
        prixods = accessObject.getDataList(Prixod.class, sql);
        filteredList = new FilteredList<>(prixods);
        sortedList = new SortedList<>(filteredList);
        table_prixod.setItems(sortedList);
        sortedList.comparatorProperty().bind(table_prixod.comparatorProperty());
        Sort();
        double summa = 0;
        for (Prixod prixod : sortedList) {
            summa += Util.decimalFormatterToDouble(prixod.get("usumma"));
        }
        tft_summa.setText(dc.format(summa) + " UZS");
    }

    private void initTable() {
        col_id.setCellValueFactory(new CallbackColumn<>("id"));
        col_nomi.setCellValueFactory(new CallbackColumn<>("name"));
        col_tur.setCellValueFactory(new CallbackColumn<>("nomi"));
        col_soni.setCellValueFactory(new CallbackColumn<>("soni"));
        col_kelish.setCellValueFactory(new CallbackColumn<>("kelish"));
        col_sotish.setCellValueFactory(new CallbackColumn<>("sotish"));
        col_sana.setCellValueFactory(new CallbackColumn<>("sana"));
        col_summa.setCellValueFactory(new CallbackColumn<>("usumma"));
    }

    public final void Sort() {
        tft_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                String[] keys = CyrillicToLatin.transliterate(t1.toLowerCase()).split(" ");
                filteredList.setPredicate(student1 -> {
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
                    String item1 = CyrillicToLatin.transliterate(student1.get("soni").toLowerCase());
                    for (String key : keys) {
                        index = item1.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }

                    if (!ok) {
                        ok = true;
                        item = CyrillicToLatin.transliterate(student1.get("name").toLowerCase());
                        for (String key : keys) {
                            index = item.indexOf(key);
                            if (index == -1) {
                                ok = false;
                                break;
                            }
                        }
                    }

                    if (!ok) {
                        ok = true;
                        item = CyrillicToLatin.transliterate(student1.get("turi").toLowerCase());
                        for (String key : keys) {
                            index = item.indexOf(key);
                            if (index == -1) {
                                ok = false;
                                break;
                            }
                        }
                    }


                    if (!ok) {
                        ok = true;
                        item = CyrillicToLatin.transliterate(student1.get("sana").toLowerCase());
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

                table_prixod.setItems(sortedList);
                int summa = 0;
                for (Prixod prixod : sortedList) {
                    summa += Integer.parseInt(prixod.get("usumma"));
                }
                tft_summa.setText(dc.format(summa) + " UZS");
            }
        });

        combo_nomi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (Product o : ombors) {
                    if (o.get("name").equals(combo_nomi.getValue())) {
                        edt_nomi_id.setText(o.get("id"));
                        tft_kelish.setText(o.get("narx"));
                        tft_sotish.setText(o.get("sotish"));
                        tft_shtrix.setText(o.get("shtrix"));
                        tft_soni.requestFocus();
                    }
                }

            }
        });

        edt_nomi_id.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (Product o : ombors) {
                    if (edt_nomi_id.getText().length() < 7) {
                        if (o.get("id").equals(edt_nomi_id.getText())) {
                            combo_nomi.setValue(o.get("name"));
                            tft_kelish.setText(o.get("narx"));
                            tft_sotish.setText(o.get("sotish"));
                            tft_shtrix.setText(o.get("shtrix"));
                            tft_soni.requestFocus();
                        }
                    }

                }
            }
        });

        tft_shtrix.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                for (Product o : ombors) {
                    if (o.get("shtrix") != null) {
                        StringTokenizer tokenizer = new StringTokenizer(o.get("shtrix"), ",");
                        while (tokenizer.hasMoreTokens()) {
                            if (tokenizer.nextToken().trim().equals(tft_shtrix.getText())) {
                                combo_nomi.setValue(o.get("name"));
                                tft_kelish.setText(o.get("narx"));
                                tft_sotish.setText(o.get("sotish"));
                                tft_shtrix.setText(new StringTokenizer(o.get("shtrix"), ",").nextToken());
                                tft_soni.requestFocus();
                            }
                        }
                    }
                }
            }
        });

        tft_soni.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    tft_kelish.requestFocus();
                }
            }
        });

        tft_kelish.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    tft_sotish.requestFocus();
                }
            }
        });

        tft_sotish.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    prixod();
                }
            }
        });
    }

    public void prixod() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (!edt_nomi_id.getText().isEmpty() && !tft_soni.getText().isEmpty()) {
            if (!tft_sotish.getText().isEmpty() && !tft_kelish.getText().isEmpty()) {
                if (prixod == null) {
                    prixod = new Prixod();
                }
                prixod.Set("m_id", edt_nomi_id.getText());
                prixod.Set("soni", tft_soni.getText());
                prixod.Set("kelish", tft_kelish.getText());
                prixod.Set("sotish", tft_sotish.getText());
                prixod.Set("sana", format.format(data_sana.getValue()));
                prixod.Set("soat", LocalDateTime.now().format(formatter));
                prixod.save();

//                shtrixPrint(tft_shtrix.getText(),1);

                tft_soni.clear();
                tft_kelish.clear();
                tft_sotish.clear();
                edt_nomi_id.clear();
                tft_shtrix.clear();
                prixod = null;

                refreshTable(Controller.Sana());

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("Практика успешно завершена!!");
                alert.show();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                alert.close();


            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText("Информация неполная!");
                alert.show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                alert.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("");
            alert.setHeaderText("Информация неполная!");
            alert.show();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alert.close();
        }
    }

    public void patok() {

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (A) {
                    try {
                        Thread.sleep(5000);
                        ombors = new Product().select().all();
                        System.out.println("prixod");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    public void shtrixPrint(Prixod prixod) {
        Alert ogoh = new Alert(Alert.AlertType.INFORMATION);
        ogoh.setTitle("Предупреждение");
        ogoh.setHeaderText("Пожалуйста, подождите!");
        ogoh.setContentText("Это публикуется ....");
        ogoh.show();
        PauseTransition pt = new PauseTransition(Duration.seconds(3));
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ogoh.close();
            }
        });
        pt.play();

        int soni = prixod.get("type").equals("Donalik") ? Integer.parseInt(prixod.get("soni")) : 1;

        List<Map<String, Object>> list = new ArrayList<>();
        maps = new HashMap<>();
        StringTokenizer tokenizer=new StringTokenizer(prixod.get("shtrix"),",");
        maps.put("shtrixkod",tokenizer.nextToken());
        maps.put("nomi", prixod.get("name"));
        maps.put("price",  dc.format(Double.valueOf(prixod.get("sotish")))+" so'm" );
        list.add(maps);

        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        report = new Report();
        report.reportShtrix(maps, dataSource, soni);
        report.showReport();
    }

}
