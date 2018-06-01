import com.auroraapi.models.Audio;

import javax.sound.sampled.LineUnavailableException;

public class AudioTest {
    public static void main(String[] args) throws InterruptedException, LineUnavailableException {
        System.out.println("starting test...");

        System.out.println("recording...");
        Audio audio = Audio.timedRecord(2000);

        System.out.println("playing back...");
        audio.play();

        System.out.println("done...");
    }
}
