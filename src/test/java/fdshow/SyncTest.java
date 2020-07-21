package fdshow;

//import static org.junit.Assert.*;
//import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

public class SyncTest
{
    @Test
    public void movesCorrectNumberOfCards()
    throws Exception
    {
        var flashCards = new FDCards();
        flashCards.addCard( new SimpleCard("front 1:back 1"));
        flashCards.addCard( new SimpleCard("front 2:back 2"));

        var wiki = new WikiData();

        Sync.oneWay(flashCards,wiki);
        assertEquals(flashCards.getCards(),wiki.getCards());
    }
}
