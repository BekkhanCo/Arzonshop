package sample.prixod.List;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sample.Home.Controller;
import sample.dbAccess.DBAccessObject;
import sample.models.PrixodKar;
import sample.models.BaseModel;
import sample.models.Prixod;
import sample.util.CallbackForTreeColumn;
import sample.util.CyrillicToLatin;
import sample.util.Util;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class listController {

    @FXML
    private Label lbl_soat;

    @FXML
    private JFXTextField tft_search;

    @FXML
    private ComboBox<String> combo_tur;

    @FXML
    private DatePicker date_sanasi;

    @FXML
    private TextField tft_summa;

    @FXML
    private JFXTreeTableView<BaseModel> table_prixod;

    private JFXTreeTableColumn<BaseModel, String> col_id=new JFXTreeTableColumn<>("id");

    private JFXTreeTableColumn<BaseModel, String> col_sana=new JFXTreeTableColumn<>("Дата");

    private JFXTreeTableColumn<BaseModel, String> col_soat=new JFXTreeTableColumn<>("Ценa");

    private JFXTreeTableColumn<BaseModel, String> col_Summa=new JFXTreeTableColumn<>("Сумма");

    private JFXTreeTableColumn<BaseModel, String> col_kol=new JFXTreeTableColumn<>("Кол-во");


    TreeItem<BaseModel> root;
    FilteredList<Prixod> tovarFilteredList;
    SortedList<Prixod> sortedList;
    ObservableList<Prixod> prixods= FXCollections.observableArrayList();
    ObservableList<PrixodKar> koriznas= FXCollections.observableArrayList();
    ObservableList<String> comboData= FXCollections.observableArrayList();
    String sql="", sana="";
    DBAccessObject accessObject;

    @FXML
    void handle_back(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/sample/prixod/prixod.fxml"));
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
        stage.setTitle("Home");
        stage.setResizable(false);
        stage.setMaximized(true);
        stage.show();
        Node node = (Node) event.getSource();
        Stage stage1 = (Stage) node.getScene().getWindow();
        stage1.close();
    }

    @FXML
    void handle_combo_tur(ActionEvent event) {
        DateTimeFormatter format= DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date=format.format(date_sanasi.getValue());
        tft_search.setText(SanaFormat(date));
    }

    @FXML
    void handle_delete(ActionEvent event) {

    }

    @FXML
    void handle_sana(ActionEvent event) {
        DateTimeFormatter format= DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String date=format.format(date_sanasi.getValue());
        tft_search.setText(SanaFormat(date));
    }
    @FXML
    void initialize() {
        accessObject=new DBAccessObject();
        table_prixod.setPlaceholder(new Label("Нет доступной информации!"));
        date_sanasi.setValue(Controller.LOCAL_DATE(Controller.Sana()));
        Controller.initClock(lbl_soat);
        refreshTable();
        initTable();

        comboData.add("День");
        comboData.add("Месяц");
        comboData.add("Год");
        comboData.add("Все");

        combo_tur.setItems(comboData);
        combo_tur.setValue("Месяц");
        refreshTable();
        tft_search.setText(SanaFormat(Controller.Sana()));
    }

    void initTable(){
        table_prixod.getColumns().setAll(col_id,col_sana,col_soat,col_kol,col_Summa);
        col_sana.setCellValueFactory(new CallbackForTreeColumn<>("sana"));
        col_id.setCellValueFactory(new CallbackForTreeColumn<>("id"));
        col_soat.setCellValueFactory(new CallbackForTreeColumn<>("narx"));
        col_kol.setCellValueFactory(new CallbackForTreeColumn<>("soni"));
        col_Summa.setCellValueFactory(new CallbackForTreeColumn<>("summa"));
    }

    void refreshTable(){
        sql = "SELECT prixod.id,prixod.sana,COUNT(prixod.id) soni,SUM(prixod.sotish*prixod.soni) summa " +
                "FROM `prixod` LEFT JOIN ombor ON prixod.m_id=ombor.id GROUP BY prixod.sana ORDER BY prixod.id DESC";
        prixods = accessObject.getDataList(Prixod.class, sql);
        tovarFilteredList = new FilteredList<>(prixods);
        sortedList = new SortedList<>(tovarFilteredList);
        filter();
        createTable();
    }

    void createTable(){
        root=new TreeItem<>();
        root.setExpanded(true);
        table_prixod.setRoot(root);
        table_prixod.setShowRoot(false);
        double summa=0;
        for (Prixod tovar : sortedList){
            summa+=Double.valueOf(tovar.get("summa"));
            TreeItem<BaseModel> item=new TreeItem<>(tovar);
            root.getChildren().add(item);
            sql = "SELECT ombor.name sana, prixod.soni, prixod.sotish narx, prixod.sotish*prixod.soni summa " +
                    "FROM prixod JOIN ombor ON prixod.m_id=ombor.id WHERE prixod.sana='"+tovar.get("sana")+"' ";
            koriznas = accessObject.getDataList(PrixodKar.class, sql);
            for (PrixodKar ostatTov:koriznas) {
                TreeItem<BaseModel>treeItem=new TreeItem<>(ostatTov);
                item.getChildren().add(treeItem);
            }
        }
        tft_summa.setText(Util.doubleToString(String.valueOf(summa)));
    }

    void filter(){
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
                    String item = CyrillicToLatin.transliterate(Ftovar.get("sana").toLowerCase());
                    for (String key : spl_keys) {
                        index = item.indexOf(key);
                        if (index == -1) {
                            ok = false;
                            break;
                        }
                    }
//                    if (!ok) {
//                        ok = true;
//                        item = CyrillicToLatin.transliterate(Ftovar.get("name").toLowerCase());
//                        for (String key : spl_keys) {
//                            index = item.indexOf(key);
//                            if (index == -1) {
//                                ok = false;
//                                break;
//                            }
//                        }
//                    }
                    if (String.valueOf(Ftovar.get("id")).contains(t1)) {
                        return true;
                    }
                    return ok;
                });
                createTable();
            }
        });
    }

    public final String  SanaFormat(String sana){
        String substring=sana;
        if(combo_tur.getSelectionModel().getSelectedIndex()==0){
            substring=sana;
        }
        if(combo_tur.getSelectionModel().getSelectedIndex()==1){
            substring = sana.substring(3, sana.length());
            System.out.println(substring);
        }
        if(combo_tur.getSelectionModel().getSelectedIndex()==2){
            substring = sana.substring(6, sana.length());
            System.out.println(substring);
        }
        if(combo_tur.getSelectionModel().getSelectedIndex()==3){
            substring=".";
        }
        return substring;
    }

}
