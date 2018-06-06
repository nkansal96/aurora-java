import com.auroraapi.models.Audio;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class AudioTest {
    public static void main(String[] args) throws InterruptedException, LineUnavailableException, IOException {
        System.out.println("starting test...");

        System.out.println("recording...");
        Audio audio = Audio.timedRecord(2000);

        System.out.println("playing back...");
        audio.play();

        System.out.println("done...");
    }
}
