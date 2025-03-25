package application.wordle;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

import java.awt.*;

public class HomeController {

    @FXML
    public ChoiceBox<String> choiceBox;


    @FXML
    public void initialize() {

        choiceBox.getItems().addAll("3 Bogstaver", "4 Bogstaver", "5 Bogstaver", "6 Bogstaver");
        choiceBox.setValue("5 Bogstaver");

    }

    public void playGame() {

        String selectedValue = choiceBox.getValue();
        Main.længdeAfOrd = Character.getNumericValue(selectedValue.charAt(0));

        new SkiftScene("Game.fxml");
        new SoundPlayer("Button");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
    }

    public void exitGame() {
        new SoundPlayer("Button");
        System.exit(0);
    }

    public void howToPlay() {

        new SkiftScene("HowToPlay.fxml");
        new SoundPlayer("Button");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);

    }

}
