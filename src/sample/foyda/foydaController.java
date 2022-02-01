package sample.foyda;

import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import sample.animation.reflect;
import sample.animation.rotate;
import sample.dbAccess.DBAccessObject;
import sample.models.Chek;
import sample.util.Util;
import sample.views.Stages;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class foydaController {

    @FXML
    private VBox root;

    @FXML
    private JFXDatePicker beginDate;

    @FXML
    private JFXDatePicker endDate;

    @FXML
    private ComboBox<String> combo1;

    @FXML
    private Label lbl_summa;

    @FXML
    private LineChart<Object, Object> linechart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private HBox hb1;

    @FXML
    private HBox root_kun;

    @FXML
    private Circle btn_circle;


    rotate rotate=new rotate();

    DecimalFormat dc=new DecimalFormat("###,###,###,###,###.##");
    DateTimeFormatter dt= DateTimeFormatter.ofPattern("dd.MM.yyyy");
    String sql;
    XYChart.Series series;
    ObservableList<XYChart.Data>data=FXCollections.observableArrayList();
    ObservableList<Chek> foydas= FXCollections.observableArrayList();
    ObservableList<String> nomlar= FXCollections.observableArrayList();
    DBAccessObject accessObject;

    @FXML
    void handle_back(ActionEvent event) {
        Stages.getHome().show();
        Stages.closeStage(lbl_summa);
    }

    @FXML
    void handle_sana(ActionEvent event) {
       combo();
    }

    @FXML
    void initialize(){
        root_kun.setStyle("-fx-background-color: linear-gradient(from 100% 100% to 100% 50%, #62869b,#294a5d);");
//        root_kun.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 50%, #294a5d 0%, #fff 25%,#62869b 100%);");
        accessObject=new DBAccessObject();
        combo1.setStyle("-fx-font: 20px \"Montserrat\";-fx-font-color:#294a5d");
        nomlar.add("День");
        nomlar.add("Месяц");
        nomlar.add("Год");
        combo1.setItems(nomlar);
        combo1.setValue("День");
        Sana();
        Dates(dt.format(beginDate.getValue()),dt.format(endDate.getValue()) );
        combo1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            combo();
            }
        });
    }

    public void Dates(String begin,String end) {
        double summa=0;
        root.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        series=new XYChart.Series();
        LineChart<Object,Object> linechart =new LineChart(xAxis,yAxis);

        series.setName("Ежедневный отчет");
        xAxis.setLabel("Даты");
        linechart.setTitle(begin+"-"+end+" Ежедневная валовая прибыль");
        if (begin.equals(end)){
            linechart.setTitle(begin+" Ежедневная валовая прибыль");
        }
//--------------------------------------------------------------
        sql = "SELECT SUM(sotuv.soni*sotuv.delta)-SUM(`check`.`skidka`) foyda, `check`.`sana`,\n" +
                "CONCAT(SUBSTRING(`check`.`Sana`, 7,4),'-',SUBSTRING(`check`.`Sana`,4,2),'-',SUBSTRING(`check`.`Sana`, 1,2)) date\n" +
                "FROM `check` JOIN sotuv ON `check`.`id`=sotuv.check_id \n" +
                "WHERE `check`.`Sana`>='"+begin+"'  \n" +
                "AND SUBSTRING(`check`.`Sana`, 4, 2)>=SUBSTRING('"+begin+"', 4, 2) \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)>=SUBSTRING('"+begin+"', 7, 4) \n" +
                "AND `check`.`Sana`<='"+end+"'  \n" +
                "AND SUBSTRING(`check`.`Sana`, 4, 2)<=SUBSTRING('"+end+"', 4, 2) \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)<=SUBSTRING('"+end+"', 7, 4) GROUP by date";
        foydas = accessObject.getDataList(Chek.class,sql);
//--------------------------------------------------------------
        data.removeAll();
        data.clear();

        for(Chek check:foydas){
            if(check.get("foyda")!=null){
                summa+= Util.decimalFormatterToDouble(check.get("foyda"));
                data.add(new XYChart.Data(check.get("sana"), Util.decimalFormatterToDouble(check.get("foyda"))));
            }
        }

        series.getData().addAll(data);
        lbl_summa.setText(dc.format(summa));

        Cycle();

        linechart.getData().addAll(series);
        series.getNode().setStyle("-fx-stroke: #F55B00; ");
        root.getChildren().addAll(linechart);
        tooltip(series);
    }

    public void Month(String begin,String end) {
        double summa=0;
        root.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        series=new XYChart.Series();
        LineChart<Object,Object> linechart =new LineChart(xAxis,yAxis);

        series.setName("Месячный отчет");
        xAxis.setLabel("Месяцы");
        linechart.setTitle(begin.substring(3,10)+"-"+end.substring(3,10)+" Ежемесячная валовая прибыль");
        if (begin.substring(3,10).equals(end.substring(3,10))){
            linechart.setTitle(begin.substring(3,10)+" Ежемесячная валовая прибыль");
        }
//--------------------------------------------------------------
        sql = "SELECT SUM(sotuv.soni*sotuv.delta)-SUM(`check`.`skidka`) foyda,\n" +
                "CONCAT(SUBSTRING(`check`.`Sana`, 7,4),'-',SUBSTRING(`check`.`Sana`,4,2),'-',SUBSTRING(`check`.`Sana`, 1,2)) date,\n" +
                "SUBSTRING(`check`.`Sana`, 4,7) oy,\n" +
                "IF(SUBSTRING(`check`.`Sana`, 4,2)=1,'Январь',IF(SUBSTRING(`check`.`Sana`, 4,2)=2,'Февраль',IF(SUBSTRING(`check`.`Sana`, 4,2)=3,'Март',IF(SUBSTRING(`check`.`Sana`, 4,2)=4,'Aпрель',IF(SUBSTRING(`check`.`Sana`, 4,2)=5,'Май',IF(SUBSTRING(`check`.`Sana`, 4,2)=6," +
                "'Июнь',IF(SUBSTRING(`check`.`Sana`, 4,2)=7,'Июль',IF(SUBSTRING(`check`.`Sana`, 4,2)=8,'Август',IF(SUBSTRING(`check`.`Sana`, 4,2)=9,'Сентябрь',IF(SUBSTRING(`check`.`Sana`, 4,2)=10,'Октябрь',IF(SUBSTRING(`check`.`Sana`, 4,2)=11,'Ноябрь','Декабрь'))))))))))) month\n" +
                "FROM `check` JOIN sotuv ON `check`.`id`=sotuv.check_id \n" +
                "AND SUBSTRING(`check`.`Sana`, 4, 2)>=SUBSTRING('"+begin+"', 4, 2) \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)>=SUBSTRING('"+begin+"', 7, 4) \n" +
                "AND SUBSTRING(`check`.`Sana`, 4, 2)<=SUBSTRING('"+end+"', 4, 2) \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)<=SUBSTRING('"+end+"', 7, 4) GROUP by oy ORDER BY date";
        foydas = accessObject.getDataList(Chek.class,sql);
//--------------------------------------------------------------
        data.removeAll();
        data.clear();

        for(Chek check:foydas){
            if(check.get("foyda")!=null){
                summa+=Double.valueOf(check.get("foyda"));
                data.add(new XYChart.Data(check.get("month"),Util.decimalFormatterToDouble(check.get("foyda"))));
            }
        }

        series.getData().addAll(data);
        lbl_summa.setText(dc.format(summa));

        Cycle();

        linechart.getData().addAll(series);
        series.getNode().setStyle("-fx-stroke: #F55B00; ");
        root.getChildren().addAll(linechart);
        tooltip(series);
    }

    public void Annual(String begin,String end) {
        double summa=0;
        root.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        series=new XYChart.Series();
        LineChart<Object,Object> linechart =new LineChart(xAxis,yAxis);

        series.setName("Годовой отчет");
        xAxis.setLabel("Годы");
        linechart.setTitle(begin.substring(6,10)+"-"+end.substring(6,10)+" Годовая валовая прибыль");
        if (begin.substring(6,10).equals(end.substring(6,10))){
            linechart.setTitle(begin.substring(6,10)+" Годовая валовая прибыль");
        }
//--------------------------------------------------------------
        sql = "SELECT SUM(sotuv.soni*sotuv.delta)-SUM(`check`.`skidka`) foyda,\n" +
                "SUBSTRING(`check`.`Sana`, 7,4) yil\n" +
                "FROM `check` JOIN sotuv ON `check`.`id`=sotuv.check_id \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)>=SUBSTRING('"+begin+"', 7, 4) \n" +
                "AND SUBSTRING(`check`.`Sana`, 7, 4)<=SUBSTRING('"+end+"', 7, 4) GROUP BY yil";
        foydas = accessObject.getDataList(Chek.class,sql);
//--------------------------------------------------------------
        data.removeAll();
        data.clear();

        for(Chek check:foydas){
            if(check.get("foyda")!=null){
                summa+=Double.valueOf(check.get("foyda"));
                data.add(new XYChart.Data(check.get("yil"),Util.decimalFormatterToDouble(check.get("foyda"))));
            }
        }

        series.getData().addAll(data);
        lbl_summa.setText(dc.format(summa));

        Cycle();

        linechart.getData().addAll(series);
        series.getNode().setStyle("-fx-stroke: #F55B00; ");
        root.getChildren().addAll(linechart);
        tooltip(series);
    }

    public final void Sana(){
        Date d=new Date();
        SimpleDateFormat cformat=new SimpleDateFormat("yyyy-MM-dd");
        beginDate.setValue(LOCAL_DATE(cformat.format(d)));
        endDate.setValue(LOCAL_DATE(cformat.format(d)));
    }

    public static final LocalDate LOCAL_DATE (String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate;
    }

    public void Cycle(){
        Stop[] stops = new Stop[] { new Stop(0, new Color(0,0.8,0.7,1)), new Stop(1, new Color(0,0.6,0.4,1))};
        LinearGradient linear = new LinearGradient(0, 0, 1, 0, true, CycleMethod.REPEAT, stops);
        btn_circle.setStroke(linear);
        rotate=new rotate(btn_circle);
        rotate.playAnim();
        reflect reflect=new reflect(btn_circle,4);
        reflect.playAnim();
        reflect=new reflect(lbl_summa,2);
        reflect.playAnim();
    }

    void combo(){
        switch (combo1.getSelectionModel().getSelectedIndex()){
            case 0: Dates(dt.format(beginDate.getValue()),dt.format(endDate.getValue()) );
                break;
            case 1:Month(dt.format(beginDate.getValue()),dt.format(endDate.getValue()) );
                break;
            case 2:Annual(dt.format(beginDate.getValue()),dt.format(endDate.getValue()) );
                break;
        }
    }
    void tooltip(XYChart.Series<Object,Object> s){
        root.getStylesheets().add(getClass().getResource("chart.css").toExternalForm());
            for (XYChart.Data<Object, Object> d : s.getData()) {
                Tooltip.install(d.getNode(), new Tooltip(
                            d.getXValue().toString() + "\n" +
                                "Summa : " + dc.format(d.getYValue())+" UZS"));

                //Adding class on hover
                d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));

                //Removing class on exit
                d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
            }

    }

}
