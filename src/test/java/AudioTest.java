import com.auroraapi.models.Audio;

public class AudioTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting test...");

        System.out.println("recording...");
        Audio audio = Audio.record(2, 0);

        System.out.println("playing back...");
        audio.play();

        System.out.println("done...");
    }
}
