package fdshow;

//import static org.junit.Assert.*;
//import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Disabled;

public class SyncTest
{
    @Test
    public void should_syncNewCard()
    throws Exception
    {
        var flashCards = new FDCards();
        flashCards.addCard( new SimpleCard("front 1:back 1"));
        flashCards.addCard( new SimpleCard("front 2:back 2"));

        var wiki = new WikiData();

        Sync.oneWay(flashCards,wiki);
        var listFC = flashCards.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        var listW  =       wiki.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        assertEquals(listFC,listW);
    }

    @Test
    public void should_delete_zeroCards()
    {
        // set up to have flashCards and wikiData with the same data
        var flashCards = new FDCards();
        var wiki = new WikiData();
        Card sc1 = new Card(new SimpleCard("A:B",1));
        Card sc2 = new Card(new SimpleCard("C:D",2));
        flashCards.addCard(sc1);
        flashCards.addCard(sc2);
        wiki.addCard(sc1);
        wiki.addCard(sc2);
        var expected = new WikiData(wiki);
        
        // Sync flashcards to wiki...
        Sync.oneWay(flashCards,wiki);
        
        // and confirm wiki is unchanged
        boolean isEqual = expected.equals(wiki);
        assertEquals(expected, wiki);
    }    

    @Test
    public void should_delete_oneCard()
    {
        // set up to have an extra card, #2, in the destination CardsHolder
        var flashCards = new FDCards();
        var wiki = new WikiData();
        Card sc1 = new Card(new SimpleCard("A:B",1));
        Card sc2 = new Card(new SimpleCard("C:D",2));
        flashCards.addCard(sc1);
        wiki.addCard(sc1);
        wiki.addCard(sc2);
        
        // Sync flashcards to wiki...
        Sync.oneWay(flashCards,wiki);
        
        // and confirm wiki now only has the first card
        var expected = new WikiData();
        expected.addCard(sc1);
        assertEquals(expected, wiki);
    }
    
    @Test
    public void should_updated_oneCard()
    {
        // set up to have card sc1 in fashCards, but card sc2 in wiki
        // both with the same card IDs
        final CardsHolder flashCards = new FDCards();
        final CardsHolder wiki = new WikiData();
        final Card sc1 = new Card(new SimpleCard("A:B",1));
        final Card sc2 = new Card(new SimpleCard("C:D",1));
        flashCards.addCard(sc1);
        wiki.addCard(sc2);
        
        // Sync flashcards to wiki... 
        Sync.oneWay(flashCards,wiki);
        
        // and confirm wiki now contains sc1 instead of sc2
        final CardsHolder expected = new WikiData();
        expected.addCard(sc1);
        assertEquals(expected, wiki);        
    }  
}
