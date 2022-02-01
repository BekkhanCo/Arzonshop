package sample.savdo.list;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sample.dbAccess.DBAccessObject;
import sample.models.Sotuv;
import sample.models.Chek;
import sample.util.CallbackColumn;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class listController {

    @FXML
    private TableView<Sotuv> table_list;

    @FXML
    private TableColumn<Sotuv, String> col_id;

    @FXML
    private TableColumn<Sotuv, String> col_turi;

    @FXML
    private TableColumn<Sotuv, String> col_soni;

    @FXML
    private TableColumn<Sotuv, String> col_narx;

    @FXML
    private TableColumn<Sotuv, String> col_summa;

    @FXML
    private TableColumn<Sotuv, String> col_check;

    @FXML
    private TextField tft_cash;

    @FXML
    private TextField tft_card;


    @FXML
    public void handle_close() {
        Stage stage1 = (Stage) table_list.getScene().getWindow();
        stage1.close();
    }

    public void initialize(){
        table_list.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode() == KeyCode.ESCAPE) {
                    Stage stage1 = (Stage) table_list.getScene().getWindow();
                    stage1.close();
                    ke.consume(); // <-- stops passing the event to next node
                }
            }
        });
        table_list.setPlaceholder(new Label("Нет доступной информации!"));
        initTable();
        Table(Sana());
        Kassa(Sana());
        table_list.getSortOrder();
    }
    private void initTable() {
        col_id.setCellValueFactory(new CallbackColumn<>("id"));
        col_turi.setCellValueFactory(new CallbackColumn<>("name"));
        col_narx.setCellValueFactory(new CallbackColumn<>("sotish"));
        col_soni.setCellValueFactory(new CallbackColumn<>("soni"));
        col_summa.setCellValueFactory(new CallbackColumn<>("summa"));
        col_check.setCellValueFactory(new CallbackColumn<>("check_id"));
    }

    ObservableList<Sotuv>sotuvs= FXCollections.observableArrayList();
    ObservableList<Chek>kassa= FXCollections.observableArrayList();
    DBAccessObject accessObject;

    public final void Table(String date) {
        accessObject = new DBAccessObject();
        String sql = "select s.id, g.name, s.soni,s.sotish, t.nomi AS turi,s.sotish*s.soni as summa,s.sana,s.check_id,`soat`\n" +
                "from sotuv as s join ombor as g on s.mah_id=g.id JOIN tur AS t ON g.turi_id=t.id \n" +
                "where s.deleted is null and `sana`='"+date+"' order by id desc";
        sotuvs = accessObject.getDataList(Sotuv.class, sql);
        table_list.setItems(sotuvs);
    }

    public final String Sana(){
        Date d=new Date();
        SimpleDateFormat cformat=new SimpleDateFormat("dd.MM.yyyy");
        return cformat.format(d);
    }

    public void Kassa(String date){
        accessObject = new DBAccessObject();
        String sql = "SELECT REPLACE(format(IF(SUM(plastik) IS null,0,SUM(plastik)),2),',','')  plastik,  REPLACE(format(IF(SUM(naqd) IS null,0,SUM(naqd)),2),',','') naqd FROM `check` WHERE deleted is null and sana='"+date+"'";
        kassa = accessObject.getDataList(Chek.class, sql);
        DecimalFormat dc=new DecimalFormat("###,###,###,###.##");
        tft_card.setText(kassa.get(0).get("plastik"));
        tft_cash.setText(kassa.get(0).get("naqd"));
    }

    private String initClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH");
        Timeline clock = new Timeline(new KeyFrame(javafx.util.Duration.ZERO, e -> {
        }), new KeyFrame(javafx.util.Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        return LocalDateTime.now().format(formatter);
    }

}
