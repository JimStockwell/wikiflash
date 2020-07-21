package fdshow;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Tests the WikiData class
 */
public class WikiDataTest
{
    @Test
    public void markBlankIdsReallyMarks() {
      final var wd = new WikiData();

      wd.addCard(new SimpleCard("First front:back",null));
      wd.addCard(new SimpleCard("Second front:back",null));
      final var ids1 = wd.markBlankIds();
      assertEquals(Integer.valueOf(Integer.MIN_VALUE+0), ids1.get(0));
      assertEquals(Integer.valueOf(Integer.MIN_VALUE+1), ids1.get(1));
      assertEquals(2,ids1.size());
      assertEquals(
        "First front",
        wd.getCard(Integer.MIN_VALUE+0)
          .getData()
          .get("Text 1"));
      assertEquals(
        "Second front",
        wd.getCard(Integer.MIN_VALUE+1)
          .getData()
          .get("Text 1"));
      wd.addCard(new SimpleCard("Third front:back",null));
      final var ids2 = wd.markBlankIds();
      assertEquals(Integer.valueOf(Integer.MIN_VALUE+2), ids2.get(0));
      assertEquals(1,ids2.size());
      assertEquals(
        "Third front",
        wd.getCard(Integer.MIN_VALUE+2)
          .getData()
          .get("Text 1"));
      final var ids3 = wd.markBlankIds();
      assertEquals(0,ids3.size());
    }

    @Test
    public void updatesDontMoveCards()
    {
      final var wd = new WikiData();

      wd.addCard(new SimpleCard("First 1:First 2",1));
      wd.addCard(new SimpleCard("Second 1:Second 2",2));
      final var firstOrder = wd.getIds();

      wd.updateCard(new SimpleCard("Updated First 1:Updated First 2",1));
      wd.updateCard(new SimpleCard("Updated Second 1:Updated Second 2",2));
      final var secondOrder = wd.getIds();
      assertEquals(firstOrder,secondOrder);

      wd.updateCard(new SimpleCard("Updated First 1:Updated First 2",2));
      wd.updateCard(new SimpleCard("Updated Second 1:Updated Second 2",1));
      final var thirdOrder = wd.getIds();
      assertEquals(firstOrder,thirdOrder);

    }

    @Test
    public void canUpdate()
    {
      final var wd = new WikiData();

      wd.addCard(new SimpleCard("First 1:First 2",1));
      wd.addCard(new SimpleCard("Second 1:Second 2",2));
      assertEquals("First 1",wd.getCard(1).getData().get("Text 1"));
      assertEquals("Second 1",wd.getCard(2).getData().get("Text 1"));

      wd.updateCard(new SimpleCard("Updated First 1:Updated First 2",1));
      wd.updateCard(new SimpleCard("Updated Second 1:Updated Second 2",2));
      assertEquals("Updated First 1",wd.getCard(1).getData().get("Text 1"));
      assertEquals("Updated Second 1",wd.getCard(2).getData().get("Text 1"));
    }
         
    @Test
    public void addOverwritingDisallowed()
    {
        try {
          final String testInput = "Side 1:Side 2";
          final SimpleCard sc = new SimpleCard(testInput,3);
          final var wd = new WikiData();
          assertTrue(!wd.contains(3));
          wd.addCard(sc);
          assertTrue(wd.contains(3),wd.toString());
          IllegalArgumentException thrown = assertThrows(
            IllegalArgumentException.class,
            () -> wd.addCard(sc));
          assertEquals("Can't add id '3' as it is already present.",
            thrown.getMessage());
        } catch (Exception e) {
          e.printStackTrace();
          fail();
        }
    }
}
