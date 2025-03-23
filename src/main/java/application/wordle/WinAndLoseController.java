package application.wordle;

import javafx.scene.control.Label;

import java.awt.*;
import java.io.IOException;

public class WinAndLoseController {

    public Label DetErRigtigtOrd;

    public void initialize() {
        DetErRigtigtOrd.setText(GameController.ordet);
    }

    public void PlayAgain() throws IOException {
        new SkiftScene("Home.fxml");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
    }

    public void ExitGame() {
        System.exit(0);
    }


}
