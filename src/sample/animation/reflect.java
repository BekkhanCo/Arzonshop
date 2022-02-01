package sample.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class reflect {
    FadeTransition fade = new FadeTransition();

    public reflect() {
    }

    public reflect(Node node,int count) {
        fade.setDuration(Duration.millis(1000));

        //setting the initial and the target opacity value for the transition
        fade.setFromValue(10);
        fade.setToValue(0.1);

        //setting cycle count for the Fade transition
        fade.setCycleCount(count);

        //the transition will set to be auto reversed by setting this to true
        fade.setAutoReverse(true);

        //setting Circle as the node onto which the transition will be applied
        fade.setNode(node);
    }
    public void playAnim(){
        fade.play();
    }

}
