package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.views.Stages;

public class Main extends Application {

    @Override
    public void start(Stage stage){
        Stages.getLogin().show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
