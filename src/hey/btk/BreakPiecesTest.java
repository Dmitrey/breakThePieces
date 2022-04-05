package hey.btk;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runners.JUnit4;
import java.util.Arrays;


public class BreakPiecesTest {
    @Test
    public void simpleTest() {
        String shape = String.join("\n", new String[] {"" +
                "+------------+",
                "|            |",
                "|            |",
                "|            |",
                "+------+-----+",
                "|      |     |",
                "|      |     |",
                "+------+-----+"});
        String expected[] = {String.join("\n", new String[] {
                "+------------+",
                "|            |",
                "|            |",
                "|            |",
                "+------------+"}),
                String.join("\n", new String[] {"" +
                        "+------+",
                        "|      |",
                        "|      |",
                        "+------+"}),
                String.join("\n", new String[] {"" +
                        "+-----+",
                        "|     |",
                        "|     |",
                        "+-----+"})};
        String actual[] = Main.process(shape);
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertEquals(expected, actual);
    }
}