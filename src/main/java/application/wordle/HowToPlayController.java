package application.wordle;

import java.awt.*;

public class HowToPlayController {


    public void BackToHome() {
        new SkiftScene("Home.fxml");
        new SoundPlayer("Button");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
    }

}
