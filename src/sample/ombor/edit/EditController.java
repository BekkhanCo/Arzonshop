package sample.ombor.edit;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import sample.dbAccess.DBAccessObject;
import sample.models.Product;
import sample.models.Category;
import sample.ombor.omborControler;
import sample.views.Stages;

public class EditController{

    @FXML
    public TextField tft_id;

    @FXML
    public JFXTextField tft_name;

    @FXML
    public JFXComboBox<Category> combo_tur;

    @FXML
    private TextField tft_shtrix_kod;

    @FXML
    public TextField tft_son;

    @FXML
    public TextField tft_kelish;

    @FXML
    public TextField tft_sotish;
    Product products;



    @FXML
    void handel_cancel(ActionEvent event) {
        Stages.closeWindowStage(event);
    }

    @FXML
    void handle_update(ActionEvent event) {

        if (!tft_name.getText().isEmpty()&&combo_tur.getValue().getId()!=null){
            tft_name.setStyle("-fx-border-color:none");
            combo_tur.setStyle("-fx-border-color:none");
            if (!tft_sotish.getText().isEmpty()&&!tft_kelish.getText().isEmpty()){
                tft_sotish.setStyle("-fx-border-color:blue");
                tft_kelish.setStyle("-fx-border-color:blue");
                products = new Product();
                products.Set("id", Integer.parseInt(tft_id.getText()));
                products.Set("name", tft_name.getText());
                products.Set("soni", tft_son.getText());
                products.Set("narx", tft_kelish.getText());
                products.Set("sotish", tft_sotish.getText());
                products.Set("shtrix", tft_shtrix_kod.getText());
                products.Set("turi_id", combo_tur.getValue().getId());
                products.save();


                Node node = (Node) event.getSource();
                Stage stage1 = (Stage) node.getScene().getWindow();
                Window window=stage1.getScene().getWindow();
                window.fireEvent(new WindowEvent(window,WindowEvent.WINDOW_CLOSE_REQUEST));
                stage1.close();
//
            }else {
                Alert alert=new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Sotish yoki Kelish narxlari kiritilmagan!");
                alert.setTitle("Ogoxlantirish");
                alert.showAndWait();
                tft_sotish.setStyle("-fx-border-color:red");
                tft_kelish.setStyle("-fx-border-color:red");
            }
        }else{
            Alert alert=new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nomi yoki Tur kiritilmagan!");
            alert.setTitle("Ogoxlantirish");
            alert.showAndWait();
            tft_name.setStyle("-fx-border-color:red");
            combo_tur.setStyle("-fx-border-color:red");
        }
    }

    @FXML
    void initialize() {
        combo_tur.setStyle("-fx-font: 20px \"Serif\";");
        DBAccessObject accessObject = new DBAccessObject();
        ObservableList<Category> turi = FXCollections.observableArrayList();
        String sql = "select id, nomi from tur";
        turi = accessObject.getDataList(Category.class, sql);

        combo_tur.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> groupListView) {
                return new ListCell<Category>() {
                    @Override
                    protected void updateItem(Category turs, boolean b) {
                        super.updateItem(turs, b);
                        setVisible(false);
                        if (!b) {
                            setText(turs.get("nomi"));
                            setVisible(true);
                        }
                    }
                };
            }
        });

        combo_tur.setButtonCell(combo_tur.getCellFactory().call(null));
        combo_tur.setItems(turi);
        try {

            tft_id.setText(omborControler.getProduct.get("id"));
            tft_name.setText(omborControler.getProduct.get("name"));
            tft_son.setText(omborControler.getProduct.get("soni"));
            tft_sotish.setText(omborControler.getProduct.get("sotish"));
            tft_kelish.setText(omborControler.getProduct.get("narx"));
            tft_shtrix_kod.setText(omborControler.getProduct.get("shtrix"));

            for (Category t : turi) {
                if (t.get("id").equals(omborControler.getProduct.get("tur_id"))){
                    combo_tur.setValue(t);
                }

            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



    }


}
