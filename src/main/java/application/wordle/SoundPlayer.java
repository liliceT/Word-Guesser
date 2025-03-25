package application.wordle;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {

    public SoundPlayer(String fileName) {

        try {
            File soundFile = new File("src/main/resources/audio/" + fileName + ".wav");
            AudioInputStream audioSteam = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioSteam);

            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }
}
