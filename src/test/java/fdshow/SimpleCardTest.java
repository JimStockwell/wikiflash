package fdshow;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests the SimpleCard class
 */
public class SimpleCardTest
{
    /**
     * Constructs a SimpleCard and checks what was constructed.
     */
    @Test
    public void breaksUpInputCorrectly()
    throws java.io.FileNotFoundException
    {
        try {
          final String testInput = "Side 1:Side 2";
          final SimpleCard sc = new SimpleCard(testInput,3);
          final var m = sc.getData();
          assertEquals("Side 1", m.get("Text 1"));
          assertEquals("Side 2", m.get("Text 2"));
          assertEquals(2,m.size());
          assertEquals(Integer.valueOf(3),sc.getId());
        } catch (IllegalArgumentException e) {
          fail();
        }
    }
}
