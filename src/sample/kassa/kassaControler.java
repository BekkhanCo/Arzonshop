package sample.kassa;

import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.*;
import sample.util.CallbackForTreeColumn;
import sample.util.Util;
import sample.views.Stages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class kassaControler {

    @FXML
    private Label lbl_kun_summa;

    @FXML
    private Text lbl_sana;

    @FXML
    private Label lbl_ostat;

    @FXML
    private Label lbl_soat;

    @FXML
    private DatePicker date_sana;

    @FXML
    private ComboBox<String> combo_sana;

    @FXML
    private ComboBox<User> combo_kassir;

    @FXML
    private ProgressIndicator pbarr;

    @FXML
    private JFXTreeTableView<BaseModel> table_kassa;

    private JFXTreeTableColumn<BaseModel, String> col_id = new JFXTreeTableColumn<>("id");

    private JFXTreeTableColumn<BaseModel, String> col_turi = new JFXTreeTableColumn<>("Кол-во");

    private JFXTreeTableColumn<BaseModel, String> col_kassir = new JFXTreeTableColumn<>("Кассир");

    private JFXTreeTableColumn<BaseModel, String> col_sana = new JFXTreeTableColumn<>("Дата");

    private JFXTreeTableColumn<BaseModel, String> col_soat = new JFXTreeTableColumn<>("Время");

    private JFXTreeTableColumn<BaseModel, String> col_Summa = new JFXTreeTableColumn<>("Сумма");

    private JFXTreeTableColumn<BaseModel, String> col_chegirma = new JFXTreeTableColumn<>("Скидка");

    private JFXTreeTableColumn<BaseModel, String> col_plast = new JFXTreeTableColumn<>("Пластик");

    private JFXTreeTableColumn<BaseModel, String> col_naqd = new JFXTreeTableColumn<>("Наличный");

    private JFXTreeTableColumn<BaseModel, String> col_tulandi = new JFXTreeTableColumn<>("Оплачено");


    ObservableList<Chek> checks = FXCollections.observableArrayList();
    ObservableList<ChekKar> checksSplit = FXCollections.observableArrayList();
    ObservableList<Sotuv> modelSotuvs = FXCollections.observableArrayList();
    ObservableList<String> comboData = FXCollections.observableArrayList();

    TreeItem<BaseModel> root;
    FilteredList<Chek> tovarFilteredList;
    SortedList<Chek> sortedList;
    DBAccessObject accessObject;
    String sql = "";

    double summaPlastik = 0;
    double summaNaqd = 0;

    private String kassirID = "";


    @FXML
    void handle_return(ActionEvent event) {

        BaseModel model = table_kassa.getSelectionModel().getSelectedItem().getValue();
        int enteryAccess = 0;

        do {
            enteryAccess = Stages.checkPassword(enteryAccess);
        } while (enteryAccess == 1);
//        if (enteryAccess == 2 ) {
        if (false ) {

            Chek chek = new Chek();
            chek.Set("user_id",model.get("user_id"));
            chek.Set("klient_id", -1 );
            chek.Set("check", "Возврат");
            chek.Set("naqd", -Util.decimalFormatterToDouble(model.get("summa")));
            chek.Set("plastik", Util.decimalFormatterToDouble("0"));
            chek.Set("summa", -Util.decimalFormatterToDouble(model.get("summa")));
            chek.Set("sana", Controller.Sana());
            chek.Set("soat", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            chek.Set("skidka", Util.decimalFormatterToDouble("0", 0));
            chek.save();

            Sotuv sotuv = new Sotuv();
            sotuv.Set("check_id", model.getId());
            sotuv.Set("deleted", LocalDateTime.now().toString());
            sotuv.save("check_id");
            refreshTable(Controller.Sana());


            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Успешно отменено!");
            alert.show();

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alert.close();
        }
    }

    @FXML
    void handle_returnList(ActionEvent event) {
        Stages.getReturnListPanel().show();
        Stages.closeStage(lbl_kun_summa);
    }

    @FXML
    void handle_cancelSale(ActionEvent event) {
        BaseModel model = table_kassa.getSelectionModel().getSelectedItem().getValue();
        int enteryAccess = 0;

        do {
            enteryAccess = Stages.checkPassword(enteryAccess);
        } while (enteryAccess == 1);
        if (enteryAccess == 2) {

            if (!(model.get("check")).equals("Возврат")) {

                Chek chek = new Chek();
                int id = model.getId();
                chek.Set("id", id);
                chek.Set("deleted", LocalDateTime.now().toString());
                chek.save();

                Sotuv sotuv = new Sotuv();
                sotuv.Set("check_id", id);
                sotuv.Set("deleted", LocalDateTime.now().toString());
                sotuv.save("check_id");
                refreshTable(Controller.Sana());


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успешно отменено!");
                alert.show();

                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                alert.close();
            }else Util.alert("Предупреждение","Возврат не может быть отменен");
        }

    }

    @FXML
    void handel_combo_sana(ActionEvent event) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = format.format(date_sana.getValue());
        refreshTable(SanaFormat(date));
    }

    @FXML
    void handle_back(ActionEvent event) {
        Stages.getHome().show();
        Stages.closeStage(event);
    }

    @FXML
    void handle_refresh(ActionEvent event) {
        pbarr.setVisible(true);
        PauseTransition pt = new PauseTransition(Duration.millis(800));
        pt.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                pbarr.setVisible(false);
            }
        });
        pt.play();
        kassirID="";
        combo_kassir.setValue(null);
        refreshTable(Controller.Sana());
    }

    @FXML
    void handle_sana(ActionEvent event) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date = format.format(date_sana.getValue());
        refreshTable(SanaFormat(date));
    }

    @FXML
    void handle_addUser(ActionEvent event) {
        Stages.getUserPanel().show();
    }

    @FXML
    public void initialize() {
        setData();
        initTable();
        refreshTable(Controller.Sana());
    }

    void setData() {
        accessObject = new DBAccessObject();
        Controller.initClock(lbl_soat);
        lbl_sana.setText(Controller.Sana());
        date_sana.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        pbarr.setVisible(false);
        table_kassa.setPlaceholder(new Label("Нет доступной информации!"));

        comboData.add("День");
        comboData.add("Месяц");
        comboData.add("Год");
        comboData.add("Все");
        combo_sana.setItems(comboData);

        combo_kassir.setItems(new User().all());
        combo_kassir.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User group, boolean b) {
                        super.updateItem(group, b);
                        if (!b) {
                            setText(group.get("uname"));
                        }
                    }
                };
            }
        });
        combo_kassir.setButtonCell(combo_kassir.getCellFactory().call(null));
        combo_kassir.setOnAction(event -> {
            User kassir=combo_kassir.getSelectionModel().getSelectedItem();
            kassirID = (kassir!=null?String.valueOf(kassir.getId()):"");
            DateTimeFormatter format = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            String date = format.format(date_sana.getValue());
            refreshTable(SanaFormat(date));
        });
    }

    public final String SanaFormat(String sana) {
        String substring = sana;
        if (combo_sana.getSelectionModel().getSelectedIndex() == 0) {
            substring = sana;
        }
        if (combo_sana.getSelectionModel().getSelectedIndex() == 1) {
            substring = sana.substring(3, sana.length());
            System.out.println(substring);
        }
        if (combo_sana.getSelectionModel().getSelectedIndex() == 2) {
            substring = sana.substring(6, sana.length());
            System.out.println(substring);
        }
        if (combo_sana.getSelectionModel().getSelectedIndex() == 3) {
            substring = ".";
        }
        return substring;
    }

    void initTable() {
        table_kassa.getColumns().setAll(col_id, col_sana, col_soat, col_kassir, col_turi, col_Summa, col_tulandi, col_naqd, col_plast, col_chegirma);
        col_id.setCellValueFactory(new CallbackForTreeColumn<>("id"));
        col_soat.setCellValueFactory(new CallbackForTreeColumn<>("soat"));
        col_sana.setCellValueFactory(new CallbackForTreeColumn<>("sana"));
        col_kassir.setCellValueFactory(new CallbackForTreeColumn<>("fish"));
        col_turi.setCellValueFactory(new CallbackForTreeColumn<>("check"));
        col_Summa.setCellValueFactory(new CallbackForTreeColumn<>("summa"));
        col_tulandi.setCellValueFactory(new CallbackForTreeColumn<>("tulandi"));
        col_naqd.setCellValueFactory(new CallbackForTreeColumn<>("naqd"));
        col_plast.setCellValueFactory(new CallbackForTreeColumn<>("plastik"));
        col_chegirma.setCellValueFactory(new CallbackForTreeColumn<>("skidka"));
    }

    void refreshTable(String date) {
        sql = "SELECT REPLACE(FORMAT(SUM(`check`.summa),2),',','') summa,\n" +
                "REPLACE(FORMAT((SUM(`check`.plastik+`check`.naqd)),2),',','') tulandi," +
                "REPLACE(FORMAT(SUM(`check`.`plastik`),2),',','') plastik" +
                ", REPLACE(FORMAT(SUM(`check`.`naqd`),2),',','') naqd" +
                ", REPLACE(FORMAT(SUM(`check`.`skidka`),2),',','') skidka," +
                " `check`.`sana`,\n" +
                "CONCAT(SUBSTRING(`check`.`sana`,7,4),'-',SUBSTRING(`check`.`sana`,4,2),'-',SUBSTRING(`check`.`sana`,1,2)) sanafil \n" +
                "FROM `check` JOIN users ON `check`.`user_id`=users.id \n" +
                "WHERE " + (kassirID.isEmpty() ? "" : "check.user_id=" + kassirID + " and") + " check.deleted is null and `check`.sana LIKE '%" + date + "%' GROUP BY `check`.`sana` ORDER BY sanafil DESC";
        checks = accessObject.getDataList(Chek.class, sql);
        tovarFilteredList = new FilteredList<>(checks);
        sortedList = new SortedList<>(tovarFilteredList);
        createTable();
        summaInsertedDate();
    }

    void createTable() {
        root = new TreeItem<>();
        root.setExpanded(true);
        table_kassa.setRoot(root);
        table_kassa.setShowRoot(false);

        for (Chek tovar : sortedList) {
            TreeItem<BaseModel> item = new TreeItem<>(tovar);
            if (sortedList.size() == 1) {
                item.setExpanded(true);
            }
            root.getChildren().add(item);
            sql = "SELECT `check`.`id`,users.uname fish,user_id, `check`,  REPLACE(FORMAT(summa,2),',','') summa," +
                    " REPLACE(FORMAT(plastik+naqd,2),',','') tulandi, \n" +
                    " REPLACE(FORMAT(`plastik`,2),',','') plastik ," +
                    " REPLACE(FORMAT(naqd,2),',','') naqd, REPLACE(FORMAT(skidka,2),',','') skidka," +
                    " `check`.`sana`, `check`.`soat`  \n" +
                    "FROM `check` JOIN sotuv ON `check`.`id`=sotuv.check_id JOIN users ON `check`.`user_id`=users.id " +
                    "WHERE " + (kassirID.isEmpty() ? "" : "check.user_id=" + kassirID + " and") + " check.deleted is null and `check`.sana LIKE '%" + tovar.get("sana") + "%'\n" +
                    "GROUP BY `check`.`id` order by `check`.`id` desc";
            checksSplit = accessObject.getDataList(ChekKar.class, sql);
            for (ChekKar tovar1 : checksSplit) {
                TreeItem<BaseModel> item1 = new TreeItem<>(tovar1);
                item.getChildren().add(item1);
                sql = "SELECT sotuv.`id`, sotuv.`mah_id`,ombor.name `check`, (sotuv.`sotish`*sotuv.`soni`) summa, sotuv.`soni` tulandi, sotuv.`sotish` naqd, `delta`  \n" +
                        "                        FROM `sotuv` JOIN ombor ON sotuv.mah_id=ombor.id WHERE sotuv.deleted is null and sotuv.check_id=" + tovar1.getId() + " ORDER by sotuv.id DESC";
                modelSotuvs = accessObject.getDataList(Sotuv.class, sql);
                for (Sotuv ostatTov : modelSotuvs) {
                    TreeItem<BaseModel> treeItem = new TreeItem<>(ostatTov);
                    item1.getChildren().add(treeItem);
                }
            }
        }

    }

    void summaInsertedDate() {
        summaNaqd = 0;
        summaPlastik = 0;
        for (Chek tovar : sortedList) {
            summaPlastik += Double.valueOf(tovar.get("plastik"));
            summaNaqd += Double.valueOf(tovar.get("naqd"));
        }
        lbl_kun_summa.setText(Util.doubleToString(String.valueOf(summaNaqd)) + " UZS");
        lbl_ostat.setText(Util.doubleToString(String.valueOf(summaPlastik)) + " UZS");
    }

}
