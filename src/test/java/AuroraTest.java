import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.auroraapi.*;
import com.auroraapi.models.*;

import java.io.IOException;

public class AuroraTest {
    @Before
    public void initialize() {
        String appId = "<aurora-appId>";
        String appToken = "<aurora-appToken>";
        Aurora.init(appId, appToken);
    }

    @Test
    public void testTextInterpret() {
        try {
            String test1 = "what is the weather in los angeles";
            Text text = new Text(test1);

            assertEquals(test1, text.getText());

            Interpret interpret = Aurora.getInterpretation(text);

            assertEquals(interpret.getIntent(), "weather");
            assertEquals(interpret.getEntities().get("location"), "los angeles");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuroraException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testTextInterpretEmptyString() {
        try {
            String test1 = "";
            Text text = new Text(test1);
            assertEquals(test1, text.getText());
            Aurora.getInterpretation(text);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuroraException e) {
            assertNotNull(e);
            assertEquals(e.getMessage().substring(0, 50), "The request format and/or parameters were invalid.");
        }
    }

    @Test
    public void testTextInterpretMultipleEntities() {
        try {
            String test1 = "what is the weather in los angeles tomorrow";
            Text text = new Text(test1);

            assertEquals(test1, text.getText());

            Interpret interpret = Aurora.getInterpretation(text);

            assertEquals(interpret.getIntent(), "weather");
            assertEquals(interpret.getEntities().get("location"), "los angeles");
            assertEquals(interpret.getEntities().get("time"), "tomorrow");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuroraException e) {
            e.printStackTrace();
        }
    }
}
