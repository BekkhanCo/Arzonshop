package sample.util;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.io.IOException;

public class CatButton {
    public static ToggleGroup group=new ToggleGroup();
    static boolean tek=true;
    public static class yacheyka extends TreeCell<String> {
        BorderPane trasnfer;

        public yacheyka(BorderPane trasnfer) {
            this.trasnfer = trasnfer;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
//            setDisclosureNode(new ImageView(new Image("/sample/views/Home/icons/box.png")));
            setCursor(Cursor.HAND);
            setHover(true);

            if (isEmpty()) {
                setGraphic(null);
                setText(null);
            } else {
                String [] bup=item.split("##");
                if (!bup[0].contentEquals("1")){
                    HBox cellBox = new HBox(10);
                    JFXButton button = new JFXButton();
                    button.setCursor(Cursor.HAND);
                    button.setPrefSize(240,40);

                    HBox hBox=new HBox();
                    hBox.setSpacing(20);
                    hBox.setPadding(new Insets(0, 0, 0, 10));
                    hBox.setPrefSize(200,37);

                    Label name =new Label(bup[0]);
                    name.setTextFill(Paint.valueOf("#969696"));
                    name.setFont(new Font("Montserrat",20));

                    ImageView image=new ImageView(new Image((bup[1])));
                    image.setFitHeight(25);
                    image.setFitWidth(25);
                    image.setPreserveRatio(true);
                    image.setSmooth(true);

                    hBox.getChildren().addAll(image,name);
                    button.setGraphic(hBox);

                    if (this.getTreeItem().isLeaf()) {
//                  label.prefHeightProperty().bind(checkBox.heightProperty());
                        name.setFont(new Font("Montserrat",15));
                        name.setTextFill(Paint.valueOf("#005fa1"));
                        setGraphic(cellBox);
                        setText(null);
                        button.setOnAction(event -> {
                            button.setStyle("-fx-base:rgb(132, 145, 47)");
                            Pane pane = null;
                            try {
                                pane= FXMLLoader.load(getClass().getResource(bup[2]));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            trasnfer.setCenter(pane);
                        } );

                    } else {
                        setGraphic(cellBox);
                        setText(null);
                        button.setOnAction(e ->{
                            if (tek){
                                this.getTreeItem().setExpanded(true);
                                tek=false;
                            }else {
                                this.getTreeItem().setExpanded(false);
                                tek=true;
                            }
                        });
                    }
                    cellBox.getChildren().addAll(button);
                }else {
                    setGraphic(null);
                    setText(null);
                }
            }
        }
    }

}
