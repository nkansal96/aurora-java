import com.auroraapi.models.Audio;

public class AudioTest {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("starting test...");

        Audio audio = new Audio();

        System.out.println("recording...");
        audio.record(2, 0);

        System.out.println("finished recording...");
        Thread.sleep(1000);

        System.out.println("playing back...");
        audio.play();

        Thread.sleep(4000);

        System.out.println("done...");
    }
}
