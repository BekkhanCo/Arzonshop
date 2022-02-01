package sample.animation;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class shake {
    private TranslateTransition tt;

    public shake(Node node) {
        tt=new TranslateTransition(Duration.millis(100),node);
        tt.setFromX(0f);
        tt.setByX(10f);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);

    }

    public void playAnim(){
        tt.playFromStart();
    }

}
