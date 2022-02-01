package sample.animation;

import javafx.animation.RotateTransition;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class rotate {
    RotateTransition rotate = new RotateTransition();

    public rotate() {
    }

    public rotate(Node node) {
        rotate.setAxis(Rotate.Z_AXIS);
        rotate.setByAngle(2500);
        rotate.setCycleCount(1);
        rotate.setDuration(Duration.millis(3000));
        rotate.setAutoReverse(true);
        rotate.setNode(node);
    }

    public void playAnim(){
        rotate.play();
    }
}
