package sample.ombor;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sample.dbAccess.DBAccessObject;
import sample.models.Category;
import sample.models.Product;
import sample.util.CallbackColumn;
import sample.util.CyrillicToLatin;
import sample.util.Util;
import sample.vendor.Report;
import sample.views.Stages;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class omborControler {

    @FXML
    private Text txt_settings;

    @FXML
    private Label lbl_son;
    @FXML
    private Label lbl_ostat;
    @FXML
    private Label lbl_tur;
    @FXML
    private Label lbl_bezak;
    @FXML
    private Button btn_add;
    @FXML
    private Button btn_edit;
    @FXML
    private Button btn_delete;
    @FXML
    private JFXTextField tft_search;
    @FXML
    private TextField tft_summa;
    @FXML
    private ComboBox<Category> combo_tur;
    @FXML
    private TableView<Product> table_ombor;
    @FXML
    private TableColumn<Product, String> col_id;
    @FXML
    private TableColumn<Product, String> col_shtrix;
    @FXML
    private TableColumn<Product, String> col_nomi;
    @FXML
    private TableColumn<Product, String> col_soni;
    @FXML
    private TableColumn<Product, String> col_tur;
    @FXML
    private TableColumn<Product, String> col_summa;
    @FXML
    private TableColumn<Product, String> col_sotish;
    @FXML
    private TableColumn<Product, String> col_narx;
    @FXML
    private TableColumn<Product, String> col_type;

    public static Product getProduct;
    public static int product_id = -1;
    public static String product_name = "";

    DecimalFormat dc = new DecimalFormat("###,###,###,###,###.##");
    SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");

    ObservableList<Category> tur = FXCollections.observableArrayList();
    ObservableList<Product> products = FXCollections.observableArrayList();

    FilteredList<Product> filteredList;
    SortedList<Product> sortedList;
    String sql = "";
    Product product;
    DBAccessObject accessObject;

    @FXML
    void handle_add(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Хотите добавить новый продукт?");
        alert.setTitle("Предупреждение!");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            Stage stage = Stages.getAddProductPanel();
            stage.setOnCloseRequest(event1 -> {
                Tabler();
            });
            stage.show();
        }
    }

    @FXML
    void handle_delete(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Вы хотите удалить товар?");
        alert.setTitle("Предупреждение");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            product = table_ombor.getSelectionModel().getSelectedItem();
            try {
                if (product != null) {
                    sql = "delete from ombor where id=" + product.getId();
                    accessObject.executeUpdate(sql);
                    Tabler();
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setHeaderText("Товар удален?");
                    alert1.setTitle("Предупреждение");
                    alert1.show();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    alert1.close();
                }
            } catch (Exception e) {
                Alert alert1 = new Alert(Alert.AlertType.WARNING);
                alert1.setHeaderText("Не можете выключить продукт?");
                alert1.setTitle("Предупреждение");
                alert1.show();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                alert1.close();
            }
        }
    }

    @FXML
    void handle_edit(ActionEvent event) {
        Product pro = table_ombor.getSelectionModel().getSelectedItem();
        if (pro != null) {
            getProduct = pro;
            Stage stage = Stages.getEditProductPanel();
            stage.setOnCloseRequest(event1 -> {
                Tabler();
            });
            stage.show();
        }
    }

    @FXML
    void handle_history(ActionEvent event) {
        try {
            product_id = Integer.parseInt(table_ombor.getSelectionModel().getSelectedItem().get("id"));
            product_name = table_ombor.getSelectionModel().getSelectedItem().get("name");
        } catch (Exception e) {
        }
        if (product_id == -1) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Вы не выбрали ни одного товара !!!");
            alert.setTitle("Предупреждение");
            alert.showAndWait();
        } else {
            Stages.getHistoryProductPanel().show();
        }
    }

    @FXML
    void handel_categ(ActionEvent event) {
        Stages.getCategoryPanel().show();
    }

    @FXML
    void handel_structure(ActionEvent event) {

    }

    @FXML
    void handle_back(ActionEvent event) {
        Stages.getHome().show();
        Stages.closeStage(event);
    }

    @FXML
    void handel_combo_tur(ActionEvent event) {
        tft_search.setText(combo_tur.getValue().get("nomi"));
    }

    @FXML
    void handle_export(ActionEvent event) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Склад List");

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        XSSFRow row = sheet.createRow(0);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(0);

        cell.setCellValue("Список оставшихся товаров");

        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
        titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        titleStyle.setBorderBottom(BorderStyle.THIN);
        titleStyle.setBorderRight(BorderStyle.THIN);
        titleStyle.setBorderTop(BorderStyle.THIN);
        titleStyle.setBorderLeft(BorderStyle.THIN);

        Font fontTitle = sheet.getWorkbook().createFont();
        fontTitle.setFontName("Arial Rounded MT Bold");
        fontTitle.setFontHeightInPoints((short) 20);
        titleStyle.setFont(fontTitle);
        cell.setCellStyle(titleStyle);
        cell = row.createCell(6);
        cell.setCellStyle(titleStyle);

        Date d = new Date();
        row = sheet.createRow(1);
        org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
        cell0.setCellValue("Дата:");
        org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
        cell1.setCellValue(dformat.format(d));

        CellStyle titleStyle1 = workbook.createCellStyle();
        titleStyle1.setAlignment(HorizontalAlignment.RIGHT);
        titleStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        Font fontTitle1 = sheet.getWorkbook().createFont();
        fontTitle1.setFontName("Bahnschrift SemiBold SemiConden");
        fontTitle1.setFontHeightInPoints((short) 14);
        titleStyle1.setFont(fontTitle1);
        cell1.setCellStyle(titleStyle1);

        int colID = 0;
        row = sheet.createRow(2);
        for (TableColumn column : table_ombor.getColumns()) {

            org.apache.poi.ss.usermodel.Cell cellTitle = row.createCell(colID);

            cellTitle.setCellValue(column.getText());

            colID++;
        }

        int rowID = 3;

        for (Product prixod : table_ombor.getItems()) {
            row = sheet.createRow(rowID);
            org.apache.poi.ss.usermodel.Cell col_id = row.createCell(0);
            org.apache.poi.ss.usermodel.Cell col_shtrix = row.createCell(1);
            org.apache.poi.ss.usermodel.Cell col_nomi = row.createCell(2);
            org.apache.poi.ss.usermodel.Cell col_tur = row.createCell(3);
            org.apache.poi.ss.usermodel.Cell col_soni = row.createCell(4);
            org.apache.poi.ss.usermodel.Cell col_narx = row.createCell(5);
            org.apache.poi.ss.usermodel.Cell col_sotish = row.createCell(6);
            org.apache.poi.ss.usermodel.Cell col_summa = row.createCell(7);
//------------------------------------------------------------
            col_id.setCellValue(prixod.get("id"));
            if (prixod.get("shtrix") == null) {
                col_shtrix.setCellValue(" — ");
            } else col_shtrix.setCellValue(prixod.get("shtrix"));

            col_nomi.setCellValue(prixod.get("name"));
            col_tur.setCellValue(prixod.get("turi"));
            col_soni.setCellValue(prixod.get("soni"));
            col_narx.setCellValue(prixod.get("narx"));
            col_sotish.setCellValue(prixod.get("sotish"));
            col_summa.setCellValue(dc.format(Double.valueOf(prixod.get("summa"))));
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

    Map<String, Object> maps;

    @FXML
    void handle_print(ActionEvent event) {
        Product product=table_ombor.getSelectionModel().getSelectedItem();
        if (product != null && product.get("shtrix")!=null) {
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

            List<Map<String, Object>> list = new ArrayList<>();
            maps = new HashMap<>();
            StringTokenizer tokenizer=new StringTokenizer(product.get("shtrix"),",");
            maps.put("shtrixkod", tokenizer.nextToken().trim());
            maps.put("nomi", product.get("name"));
            maps.put("price", dc.format(Double.valueOf(product.get("sotish")))+" so'm");
            list.add(maps);

            JRDataSource dataSource = new JRBeanCollectionDataSource(list);
            Report report = new Report();
            report.reportShtrix(maps, dataSource, 1);
            report.showReport();
        }
    }

    @FXML
    void handel_ok(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/sklad/sklad.fxml"));
        Pane pane = null;
        try {
            pane = pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Sklad");
        stage.setMaximized(true);
        stage.setOnCloseRequest(event1 -> {
            Tabler();
        });
        stage.show();
    }

    @FXML
    public void initialize() {
        combo_tur.setStyle("-fx-font: 20px \"Serif\";");
        initTable();
        accessObject = new DBAccessObject();
        table_ombor.setPlaceholder(new Label("Ma'lumotlar mavjud emas!"));
        Tabler();
    }

    ObservableList<Category> turs = FXCollections.observableArrayList();

    public final void Tabler() {

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

//        ----------------------------------------------------------------
        sql = "select s.id, s.name, s.soni, s.type,s.narx,s.sotish, g.nomi turi, g.id tur_id,s.sotish*s.soni as summa,s.shtrix from ombor as s join tur as g on s.turi_id=g.id  order by s.id desc";
        products = accessObject.getDataList(Product.class, sql);
        filteredList = new FilteredList<>(products);
        sortedList = new SortedList<>(filteredList);
        table_ombor.setItems(sortedList);
        sortedList.comparatorProperty().bind(table_ombor.comparatorProperty());
//-------------------------------------------------------
        lbl_tur.setText(turs.size() + " видов");
        lbl_son.setText(products.size() + "");
        double summa = 0;
        double summa1 = 0;

        for (Product product1 : products) {
            summa += Util.decimalFormatterToDouble(product1.get("sotish")) * Util.decimalFormatterToDouble(product1.get("soni"));
            if (product1.get("turi").equals("Bezak")) {
                summa1 += Util.decimalFormatterToDouble(product1.get("sotish")) * Util.decimalFormatterToDouble(product1.get("soni"));
            }
        }
        lbl_ostat.setText(dc.format(summa) + " UZS");
//        lbl_bezak.setText(dc.format(summa1) + " UZS");
        Sort();
    }

    public void initTable() {
        col_id.setCellValueFactory(new CallbackColumn<>("id"));
        col_nomi.setCellValueFactory(new CallbackColumn<>("name"));
        col_shtrix.setCellValueFactory(new CallbackColumn<>("shtrix"));
        col_soni.setCellValueFactory(new CallbackColumn<>("soni"));
        col_type.setCellValueFactory(new CallbackColumn<>("type"));
        col_sotish.setCellValueFactory(new CallbackColumn<>("sotish"));
        col_narx.setCellValueFactory(new CallbackColumn<>("narx"));
        col_tur.setCellValueFactory(new CallbackColumn<>("turi"));
        col_summa.setCellValueFactory(new CallbackColumn<>("summa"));
    }

    public final void Sort() {
        tft_search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String s, String t1) {
                String[] spl_keys = CyrillicToLatin.transliterate(t1.toLowerCase(Locale.ROOT)).split(" ");
                filteredList.setPredicate(students1 -> {
                    boolean ok = true;
                    int index = 0;
                    if (t1 == null || t1.isEmpty()) {
                        return true;
                    }
                    String item = CyrillicToLatin.transliterate(students1.get("name").toLowerCase());
                    for (String key : spl_keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        ok = true;
                        item = CyrillicToLatin.transliterate(students1.get("turi").toLowerCase());
                        for (String key : spl_keys) {
                            index = item.indexOf(key);
                            if (index == -1) {
                                ok = false;
                                break;
                            }
                        }
                    }
                    if (String.valueOf(students1.get("id")).contains(t1)) {
                        return true;
                    }
                    return ok;
                });
                table_ombor.setItems(sortedList);
                double summa = 0;
                for (Product product2 : sortedList) {
                    summa += Util.decimalFormatterToDouble(product2.get("summa"));
                }
                tft_summa.setText(Util.decimalFormatForDouble.format(summa) + " UZS");
            }
        });
        txt_settings.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Tabler();
            }
        });
    }
}