package application.wordle;

import java.awt.*;
import java.io.IOException;

public class HowToPlayController {


    public void BackToHome() throws IOException {
        new SkiftScene("Home.fxml");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
    }

}
