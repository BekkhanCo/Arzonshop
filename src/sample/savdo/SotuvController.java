package sample.savdo;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.*;
import sample.util.MyAutoCompletionBinding;
import sample.util.Util;
import sample.views.Stages;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SotuvController {
    @FXML
    private AnchorPane root;

    @FXML
    private JFXButton btn_list;


    @FXML
    private Label lblUsumma;

    @FXML
    private Label lbl_sana;

    @FXML
    private Label lbl_soat;

    @FXML
    private Label lbl_kassir;


    @FXML
    private JFXTextField txtKiritish;

    @FXML
    private JFXTextField txtMiqdor;

    @FXML
    private Label lblQoldiq;

    @FXML
    private Label lblNarx;

    @FXML
    private Label txtNomi;

    @FXML
    private Label txtTur;

    @FXML
    private JFXComboBox<String> combo_nomi;

    @FXML
    private TableView<Korzinka> table_Sotuv;

    @FXML
    private TableColumn<Korzinka, String> col_nomer;

    @FXML
    private TableColumn<Korzinka, String> col_nomi;

    @FXML
    private TableColumn<Korzinka, String> col_soni;

    @FXML
    private TableColumn<Korzinka, String> col_narxi;

    @FXML
    private TableColumn<Korzinka, String> col_summa;

    ObservableList<Product> finds = FXCollections.observableArrayList();
    ObservableList<String> groupStringList = FXCollections.observableArrayList();

    public static ObservableList<Korzinka> korzinkas = FXCollections.observableArrayList();

    FilteredList<String> stringFilteredList;

    public static DecimalFormat dc = new DecimalFormat("#########.###");
    DBAccessObject accessObject;
    Sotuv sotuv;
    Product productSelected = null;


    boolean isDonalik = true;
    public static boolean A = true;

    double USumma = 0;
    double skidka = 0;

    int a = 2;
    int orderNo = 1;

    public static User user;

    @FXML
    public void handle_back(ActionEvent event) {
        A = false;
        Stages.getLogin().show();
        Stages.closeStage(lbl_kassir);
    }

    @FXML
    public void hendle_Clear(ActionEvent event) {
        if (a == 2) {
            txtKiritish.clear();
            a = 1;
        }
        a++;
    }

    @FXML
    public void hendle_btnBesh(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "5");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnBir(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "1");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnNol(ActionEvent event) {
        if (a == 2) {
            String kirit = txtKiritish.getText();
            txtKiritish.setText(kirit + "0");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnOlti(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "6");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnSakkiz(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "8");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnToqqiz(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "9");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnTort(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "4");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnUch(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "3");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btnYetti(ActionEvent event) {
        if (a == 2) {

            txtKiritish.setText(txtKiritish.getText() + "7");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_btn_Ikki(ActionEvent event) {
        if (a == 2) {
            txtKiritish.setText(txtKiritish.getText() + "2");
            a = 1;
        }
        a++;
    }

    @FXML
    void hendle_find(ActionEvent event) {
        int c = 1;
        Find();
    }

    @FXML
    void hendle_CClear(ActionEvent event) {
        txtMiqdor.setText("");
        lblQoldiq.setText("");
        lblNarx.setText("");
        txtKiritish.setText("");
        txtNomi.setText("");
        txtTur.setText("");
        productSelected=null;
    }

    @FXML
    public void hendle_Add(ActionEvent event) {
        Add();
    }

    @FXML
    void hendle_remove(ActionEvent event) {
        boolean A = false;
        Korzinka mah = table_Sotuv.getSelectionModel().getSelectedItem();
        if (mah != null) {
            for (Korzinka modelKorzinka : korzinkas) {
                if (modelKorzinka.getName().equals(mah.getName())) {
                    A = true;
                }
            }
        }
        if (A) {
            USumma -= Util.decimalFormatterToDouble(mah.getUmumiySumma());
            lblUsumma.setText(USumma + skidka + "");
            korzinkas.remove(mah);
        }
        refreshTableSotuv();
    }

    @FXML
    public void hendle_Sell(ActionEvent event) {
        Sotuv("Наличные", 0 + "", lblUsumma.getText(), -1);
    }

    @FXML
    void handle_clean(ActionEvent event) {
        Clean();
    }

    @FXML
    void handle_plastik(ActionEvent event) {
        Plastik();
    }

    @FXML
    void handle_list(ActionEvent event) {
        list();
    }

    @FXML
    void initialize() {
        A = true;
        lbl_kassir.setText(user.get("uname"));
        accessObject = new DBAccessObject();
        table_Sotuv.setPlaceholder(new Label("Корзина пуста"));
        txtKiritish.setText("");
        korzinkas.clear();

        patok();
        initTableSotuv();
        initClock();
        Sana();
        refreshTableSotuv();
        Sort();
        keyboard();

        MyAutoCompletionBinding.bindAutoCompletion(combo_nomi.getEditor(), groupStringList);
    }

    private void refreshTableSotuv() {
        table_Sotuv.setItems(korzinkas);
    }

    private void initTableSotuv() {
        col_nomer.setCellValueFactory(new PropertyValueFactory<>("nomer"));
        col_nomi.setCellValueFactory(new PropertyValueFactory<>("name"));
        col_soni.setCellValueFactory(new PropertyValueFactory<>("soni"));
        col_narxi.setCellValueFactory(new PropertyValueFactory<>("narxi"));
        col_summa.setCellValueFactory(new PropertyValueFactory<>("umumiySumma"));
    }

    public final void Sort() {
        combo_nomi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (Product o : finds) {
                    if (o.get("nomi").equals(combo_nomi.getValue())) {
                        txtKiritish.setText(o.get("id"));
                        txtKiritish.requestFocus();
                    }
                }
            }
        });


    }

    private void initClock() {
        Timeline clock = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            lbl_soat.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private String Sana() {
        Date d = new Date();
        SimpleDateFormat dformat = new SimpleDateFormat("dd.MM.yyyy");
        lbl_sana.setText(dformat.format(d));
        return dformat.format(d);
    }

    private void Find() {
        productSelected = null;
        if (!txtKiritish.getText().trim().isEmpty() && checkNumberFormat(txtKiritish.getText())) {
            if (txtKiritish.getText().length() < 9) {
                for (Product product : finds) {
                    if (!txtKiritish.getText().trim().isEmpty()) {
                        if (Integer.parseInt(product.get("id")) == Integer.parseInt(txtKiritish.getText())) {
                            productSelected = product;

                            txtMiqdor.setText("id: " + product.getId() + "x");
                            lblQoldiq.setText(Util.doubleToString(product.get("soni")));
                            lblNarx.setText(product.get("sotish"));
                            txtNomi.setText(product.get("nomi"));
                            txtTur.setText(product.get("turi"));
                            txtKiritish.setText("");

                            isDonalik = !(product.get("type") != null && product.get("type").equals("kg"));
                            break;
                        }
                    }
                }
                if (productSelected == null) {
                    Util.alert("Предупреждение", "Там нет такого продукта идентификатора!!!");
                    CClear();
                }

            } else {
                for (Product product : finds) {
                    if (!txtKiritish.getText().trim().isEmpty() && product.get("shtrix") != null) {
                        StringTokenizer tokenizer = new StringTokenizer(product.get("shtrix"), ",");
                        while (tokenizer.hasMoreTokens()) {
                            if (tokenizer.nextToken().trim().contentEquals(txtKiritish.getText())) {
                                productSelected = product;

                                txtMiqdor.setText("id: " + product.getId() + "x");
                                lblQoldiq.setText(Util.doubleToString(product.get("soni")));
                                lblNarx.setText(product.get("sotish"));
                                txtNomi.setText(product.get("nomi"));
                                txtTur.setText(product.get("turi"));
                                txtKiritish.setText("");

                                isDonalik = !(product.get("type") != null && product.get("type").equals("kg"));
                                break;
                            }
                        }
                    }
                }
                if (productSelected == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(txtKiritish.getText() + " raqamli Shtrix Kodlik mahsulot yo'q!!!");
                    alert.setTitle("Ogoxlantirish");
                    alert.show();
                    CClear();
                }
            }
        }

    }

    private void Remove() {
        Korzinka mah = table_Sotuv.getSelectionModel().getSelectedItem();
        if (mah != null) {
            for (Korzinka modelKorzinka : korzinkas) {
                if (modelKorzinka.getName().equals(mah.getName())) {
                    USumma -= Util.decimalFormatterToDouble(mah.getUmumiySumma());
                    lblUsumma.setText(USumma + skidka + "");
                    korzinkas.remove(mah);
                    productSelected = null;
                    break;
                }
            }
        }
        refreshTableSotuv();
    }

    private void Add() {


        if (productSelected != null) {
            double soni = Util.decimalFormatterToDouble(txtKiritish.getText());
            if (soni != 0 || !txtKiritish.getText().trim().isEmpty()) {

                if (!isDonalik)
                    soni /= 1000;

                if (Util.decimalFormatterToDouble(lblQoldiq.getText()) >= soni) {

                    boolean isProductExist = false;
                    for (Korzinka korzinka : korzinkas) {
                        if (korzinka.getId() == productSelected.getId()) {
                            isProductExist = true;

                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setHeaderText("Bu mahsulot korzinkada bor!!!");
                            alert.setTitle("Ogoxlantirish");
                            alert.showAndWait();
                            break;
                        }
                    }

                    if (!isProductExist) {
                        System.out.println("chiqardi");
                        Korzinka modelKorzinka = new Korzinka();
                        modelKorzinka.setNomer(orderNo++);
                        modelKorzinka.setId(productSelected.getId());
                        modelKorzinka.setName(productSelected.get("nomi"));
                        modelKorzinka.setTuri(productSelected.get("turi"));
                        modelKorzinka.setNarxi(Util.decimalFormatterToDouble(lblNarx.getText()));
                        modelKorzinka.setSoni(soni);
                        modelKorzinka.setUmumiySumma(dc.format(soni * Util.decimalFormatterToDouble(lblNarx.getText())).replace(',', '.'));
                        modelKorzinka.setSana(Controller.Sana());
                        modelKorzinka.setFoyda(Util.decimalFormatterToDouble(productSelected.get("sotish")) - Util.decimalFormatterToDouble(productSelected.get("narx")));

                        USumma += soni * Util.decimalFormatterToDouble(lblNarx.getText());
                        lblUsumma.setText(Util.doubleToString(USumma + skidka + ""));

                        korzinkas.add(modelKorzinka);
                        refreshTableSotuv();
                    }

                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Yetarlicha mahsulot mavjud emas!!!");
                    alert.setTitle("Ogoxlantirish");
                    alert.showAndWait();
                }

                productSelected = null;
                txtTur.setText("..");
                txtKiritish.setText("");
                txtNomi.setText("..");
                txtMiqdor.setText("..");
                lblNarx.setText("..");
                lblQoldiq.setText("..");

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Mahsulot sonini kiriting!!!");
                alert.setTitle("Ogoxlantirish");
                alert.showAndWait();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Mahsulotni tanlanmagan!!!");
            alert.setTitle("Ogoxlantirish");
            alert.showAndWait();
        }
    }

    private void CClear() {
        txtMiqdor.setText("...");
        lblQoldiq.setText("...");
        lblNarx.setText("...");
        txtKiritish.setText("");
        txtNomi.setText("Товар ....");
        txtTur.setText("...");
    }

    private void Clean() {
        USumma = 0;
        lblUsumma.setText("");
        korzinkas.clear();
        refreshTableSotuv();
    }

    private void Plastik() {
        HBox hBox = new HBox();
        TextField tftPlast = new TextField();
        tftPlast.setPromptText("Summa");
        tftPlast.setPrefHeight(40);
        tftPlast.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
        tftPlast.setText(lblUsumma.getText());
        Label lbl = new Label(" UZS");
        hBox.getChildren().addAll(tftPlast, lbl);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setMargin(tftPlast, new Insets(0, 0, 0, 25));

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setGraphic(hBox);
        alert.setHeaderText("Plastik miqdori:  ");
        alert.setTitle("Plastik oyna");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get().equals(ButtonType.OK)) {
            if (!tftPlast.getText().isEmpty() && tftPlast.getText() != null) {
                if (tftPlast.getText().equals(lblUsumma.getText())) {
                    Sotuv("Пластик", tftPlast.getText(), "0", -1);
                } else {
                    double naqd = Util.decimalFormatterToDouble(lblUsumma.getText()) - Util.decimalFormatterToDouble(tftPlast.getText());
                    Sotuv("Наличные-Пластик", tftPlast.getText(), Util.doubleToString(String.valueOf(naqd)), -1);
                }
            } else {
                Alert ogoh = new Alert(Alert.AlertType.ERROR);
                ogoh.setTitle("Warnings");
                ogoh.setHeaderText("Plastik Summasini Kiriting!");
                ogoh.show();
            }
        }
    }

    private void keyboard() {

        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().isDigitKey() || ke.getCode().isLetterKey()) {
                    txtKiritish.setText(txtKiritish.getText().toUpperCase() + ke.getText());
                    ke.consume();
                }
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.BACK_SLASH) {
                    Skidka();
                    ke.consume();
                }
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ADD) {
                    Add();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SUBTRACT) {
                    Remove();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ---------------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.PAGE_DOWN) {
                    Plastik();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ---------------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.DELETE) {
                    list();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ---------------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.HOME) {
                    Clean();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ---------------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.PAGE_UP) {
                    Sotuv("Наличные", 0 + "", lblUsumma.getText(), -1);
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.SPACE) {
                    Find();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
//        ---------------
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.BACK_SPACE) {
                    if (txtKiritish.getText().length() > 0) {
                        txtKiritish.setText(txtKiritish.getText().substring(0, txtKiritish.getText().length() - 1));
                        ke.consume(); // <-- stops passing the event to next node
                    }
                }
            }
        });
        root.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    if (txtKiritish.getText().length() > 6) {
                        Find();
                        System.out.println(txtKiritish.getText());
                        ke.consume(); // <-- stops passing the event to next node
                    }
                }
            }
        });
//        ---------------


    }

    public void patok() {

        Thread thread = new Thread(() -> {
            while (A) {
                try {
                    String sql = "SELECT ombor.id,ombor.name nomi,ombor.soni,ombor.type,ombor.sotish,ombor.narx,tur.nomi turi,ombor.shtrix FROM ombor JOIN tur ON ombor.turi_id=tur.id";
                    finds = accessObject.getDataList(Product.class, sql);

                    groupStringList.clear();
                    for (Product product : finds) {
                        groupStringList.add(product.get("nomi"));
                    }
                    stringFilteredList = new FilteredList<>(groupStringList, p -> true);
                    combo_nomi.setItems(groupStringList);

                    Thread.sleep(5000);
                    System.out.println("tekshir");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void list() {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/savdo/list/list.fxml"));
        Pane pane = null;
        try {
            pane = pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setX(700);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setY(btn_list.getLayoutY());
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("List");
        stage.setResizable(false);
        stage.show();
    }

    void Skidka() {
        skidka = -Util.decimalFormatterToDouble(txtKiritish.getText());
        txtKiritish.clear();
        lblUsumma.setText(USumma + skidka + "");
    }

    private int Sotuv(String kassa, String card, String naqd, int klient) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        int chek_id = -1;
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH");

        if (sample.savdo.savdoAdmin.SotuvController.korzinkas != null) {
            if (sotuv == null) {
                sotuv = new Sotuv();
            }
            Chek check = new Chek();
            check.Set("check", kassa);
            check.Set("user_id", user.getId());
            check.Set("klient_id", klient);
            check.Set("naqd", Util.decimalFormatterToDouble(naqd));
            check.Set("plastik", Util.decimalFormatterToDouble(card));
            check.Set("summa", Util.decimalFormatterToDouble(String.valueOf(lblUsumma.getText()), 0));
            check.Set("sana", Controller.Sana());
            check.Set("soat", LocalDateTime.now().format(formatter));
            check.Set("skidka", Util.decimalFormatterToDouble("0", 0));
            chek_id = check.save();

            for (int i = 0; i < korzinkas.size(); i++) {
                DBAccessObject.InsertBuyurtma(korzinkas.get(i).getId(), korzinkas.get(i).getNarxi(), korzinkas.get(i).getSoni(),
                        Controller.Sana(), Integer.parseInt(LocalDateTime.now().format(format)), chek_id, korzinkas.get(i).getFoyda());
            }

            Map<String, String> data = new HashMap<>();
            data.put("firma", "UCH OG'A Savdo Markazi");
            data.put("id", String.valueOf(chek_id));
            data.put("kassir", lbl_kassir.getText());
            data.put("obsumma", lblUsumma.getText());

//            Stages.sotuvChekPrint(korzinkas, data);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("Успешные продажи!");
            alert.show();

            Clean();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alert.close();

        } else Util.alert("Предупреждение", "Информация неполная !!!");

        return chek_id;
    }

    private boolean checkNumberFormat(String num){
        for (char ch: num.toCharArray()){
            if (!Character.isDigit(ch)) {
                txtKiritish.clear();
                return false;
            }
        }
        return true;
    }

}
