import com.auroraapi.models.Audio;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting test...");


        System.out.println("recording...");
        Audio audio = Audio.record(2, 0);

        System.out.println("finished recording...");

        System.out.println("playing back...");
        audio.play();

        try {
            audio.writeToFile("test.wav");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Thread.sleep(4000);

        System.out.println("done...");
    }
}
