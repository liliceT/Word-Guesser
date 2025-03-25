package application.wordle;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameController {

    public JsonReader JsonReader;
    public String JsonFilePath = "";
    public List<String> OrdListe;

    public StringBuilder OrdGæt;
    public List<String> GrønneBogstaver = new ArrayList<>();
    public List<String> GuleBogstaver = new ArrayList<>();

    public SplitPane MainPane;
    public SplitPane[] WordPane;
    public Label[][] BogstavBox;
    public Pane alfabetet;
    public Text errorText;

    public int BogstavSize = 75;
    public int længdeAfOrd = Main.længdeAfOrd;
    public int MainPaneWidth = BogstavSize * længdeAfOrd;

    public int AktivLinje = 0;
    public int AktivBogstavBox = 0;

    public Scene GameScene;

    public static String RigtigOrd = "";

    @FXML
    public void initialize() {
        loadOrd(længdeAfOrd);

        MainPane.getItems().clear();
        BogstavBox = new Label[6][længdeAfOrd];
        WordPane = new SplitPane[6];

        MainPane.setPrefSize(MainPaneWidth, BogstavSize * 6);
        MainPane.setLayoutY(25);
        MainPane.setLayoutX((550 - MainPane.getPrefWidth()) / 2);

        for (int i = 0; i < 6; i++) {

            WordPane[i] = new SplitPane();
            WordPane[i].setPrefSize(MainPaneWidth, BogstavSize);
            WordPane[i].setMaxSize(MainPaneWidth, BogstavSize);
            WordPane[i].setMinSize(MainPaneWidth, BogstavSize);
            WordPane[i].setLayoutY(BogstavSize * i);
            WordPane[i].setLayoutX(0);

            for (int j = 0; j < længdeAfOrd; j++) {
                BogstavBox[i][j] = new Label();
                BogstavBox[i][j].setPrefSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setMaxSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setMinSize(BogstavSize, BogstavSize);
                BogstavBox[i][j].setLayoutX(BogstavSize * j);
                BogstavBox[i][j].setLayoutY(0);
                BogstavBox[i][j].setAlignment(Pos.CENTER);
                BogstavBox[i][j].setStyle("-fx-text-fill: #222222; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                WordPane[i].getItems().add(BogstavBox[i][j]);
            }
            MainPane.getItems().add(WordPane[i]);
        }

        //Fordi KeyListener ikke virker i initialize, bruges platform.runLater
        //Så den sætter keyListener på scenen, efter den er blevet oprettet
        Platform.runLater(this::setupKeyListener);
    }

    public void setupKeyListener() {
        GameScene = MainPane.getScene();
        if (GameScene != null) {
            GameScene.setOnKeyPressed(this::FysiskKeyboard);
        }
    }

    public void loadOrd(int LængdeAfOrd) {

        switch (LængdeAfOrd) {
            case 3 -> JsonFilePath = "three-letter-words.json";
            case 4 -> JsonFilePath = "four-letter-words.json";
            case 5 -> JsonFilePath = "five-letter-words.json";
            case 6 -> JsonFilePath = "six-letter-words.json";
        }
        JsonReader = new JsonReader(JsonFilePath);
        RigtigOrd = JsonReader.getRandomWord();
    }

    //Tilsluttet alle taster på det virtuelle keyboard
    //Håndtere tasterne og indsætter deres værdier i deres respektive felt
    @FXML
    public void VirtueltKeyboard(MouseEvent event) {
        String LabelText = ((Label) event.getSource()).getText().toUpperCase();

        HåntereInput(LabelText);
    }

    private void FysiskKeyboard(KeyEvent event) {
        String keyCode = event.getCode().toString().toUpperCase();

        if (keyCode.equals("BACK_SPACE")) {
            keyCode = "DELETE";
        }

        HåntereInput(keyCode);
    }

    public void HåntereInput(String keyCode) {

        if (Objects.equals(keyCode, "ENTER")) {
            if (CheckOrdErEtOrd()) {
                try {
                    SammenlignOrd();
                    AktivLinje++;
                    AktivBogstavBox = 0;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                visError();
                new SoundPlayer("NotAWord");
            }

        } else if (Objects.equals(keyCode, "DELETE")) {
            if (AktivBogstavBox > 0) {
                AktivBogstavBox--;
            }
            BogstavBox[AktivLinje][AktivBogstavBox].setText("");

        } else if (keyCode.length() == 1 && keyCode.matches("[A-Z]")) {
            if (AktivBogstavBox <= længdeAfOrd - 1) {
                BogstavBox[AktivLinje][AktivBogstavBox].setText(keyCode);
                AktivBogstavBox++;
            }
        }
    }

    public void visError() {

        errorText.setVisible(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> errorText.setVisible(false));
        pause.play();

    }

    public boolean CheckOrdErEtOrd() {

        OrdGæt = new StringBuilder();

        for (int i = 0; i < længdeAfOrd; i++) {
            OrdGæt.append(BogstavBox[AktivLinje][i].getText());
        }

        JsonReader = new JsonReader(JsonFilePath);
        OrdListe = JsonReader.getJsonFile();

        return OrdListe.contains(OrdGæt.toString());
    }

    public void SammenlignOrd() throws IOException {

        if (OrdGæt.toString().equals(RigtigOrd)) {
            new SkiftScene("Win.fxml");
            new SoundPlayer("Win");
            Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
        } else if (AktivLinje == 5) {
            new SkiftScene("Lose.fxml");
            new SoundPlayer("GameOver");
            Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);
        }

        boolean[] letterFoundInRigtigOrd = new boolean[længdeAfOrd];
        String[] resultat = new String[længdeAfOrd];

        for (int i = 0; i < længdeAfOrd; i++) {

            if (RigtigOrd.charAt(i) == OrdGæt.charAt(i)) {
                letterFoundInRigtigOrd[i] = true;
                resultat[i] = "Grøn";

            } else if (RigtigOrd.contains(String.valueOf(OrdGæt.charAt(i)))) {

                for (int j = 0; j < længdeAfOrd; j++) {

                    if (RigtigOrd.charAt(j) == OrdGæt.charAt(i) && !letterFoundInRigtigOrd[j]) {
                        letterFoundInRigtigOrd[j] = true;
                        resultat[i] = "Gul";
                        break;

                    } else if (RigtigOrd.charAt(j) != OrdGæt.charAt(i)) {
                        resultat[i] = "Grå";
                    }
                }
            } else {
                resultat[i] = "Grå";
            }
        }

        for (int i = 0; i < længdeAfOrd; i++) {
            switch (resultat[i]) {
                case "Grøn" ->
                        BogstavBox[AktivLinje][i].setStyle("-fx-background-color:  #77dd76; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                case "Gul" ->
                        BogstavBox[AktivLinje][i].setStyle("-fx-background-color: #ebed74; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
                case "Grå" ->
                        BogstavBox[AktivLinje][i].setStyle("-fx-background-color: #787c7e; -fx-text-fill: #ffffff; -fx-font-family: System; -fx-font-weight: Normal; -fx-font-size: 50px;");
            }
        }

        FarveTaster();
    }

    public void FarveTaster() {
        for (int i = 0; i < 28; i++) {
            Label label = (Label) alfabetet.getChildren().get(i);

            if (GrønneBogstaver.contains(label.getText())) {
                label.setStyle("-fx-background-color: #77dd76; -fx-border-color: #000000;");

            } else if (GuleBogstaver.contains(label.getText())) {
                label.setStyle("-fx-background-color: #ebed74; -fx-border-color: #000000;");

            } else if (OrdGæt.toString().contains(label.getText())) {
                label.setStyle("-fx-background-color: #787c7e; -fx-border-color: #000000;");

            }
        }
    }

    public void giveUp() throws IOException {

        new SkiftScene("Lose.fxml");
        new SoundPlayer("Button");
        Main.stage.setX((double) Toolkit.getDefaultToolkit().getScreenSize().width / 2 - Main.stage.getWidth() / 2);

    }
}