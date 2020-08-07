package fdshow;

//import static org.junit.Assert.*;
//import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

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

        Sync.markAndAddNewCards(flashCards,wiki);
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
        Sync.deleteExtraCards(flashCards,wiki);
        
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
        Sync.deleteExtraCards(flashCards,wiki);
        
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
        Sync.update(flashCards,wiki);
        
        // and confirm wiki now contains sc1 instead of sc2
        final CardsHolder expected = new WikiData();
        expected.addCard(sc1);
        assertEquals(expected, wiki);        
    }  
    
    @Test
    public void should_copy_zeroCards()
    {
        // set up to have both holders empty...
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        
        // Do the degenerate case "move"
        Sync.copyUnmatchedIdedCards(wiki,fc); // reverse of the usual order
        
        // Check that destination is still empty
        assertEquals(0,fc.getIds().size());
    }
    
    @Test
    public void should_copy_when_DestinationIsEmpty()
    {
        // set up to the destination empty, but IDed content in the source 
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        final Card sc1 = new Card(new SimpleCard("A:B",1));
        wiki.addCard(sc1);
        
        // Move the one card to the destination
        Sync.copyUnmatchedIdedCards(wiki,fc);
        
        // Check that destination added it
        var listFC =         fc.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        var listW  =       wiki.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        assertEquals(listW,listFC);

    }
    
    @Test
    public void should_ignoreNullsInTheSource_when_copying()
    {
        // Set up source to have only a null IDed card
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        final Card sc1 = new Card(new SimpleCard("A:B",null));
        wiki.addCard(sc1);
        
        // Expect nothing to be moved
        Sync.copyUnmatchedIdedCards(wiki,fc);

        assertEquals(0,fc.getCards().size());
    }
    
    @Test
    public void should_notCopy_when_allIdsAreMatched()
    {
        // set up one card each CardsHolder, same IDs, but different content
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        final Card sc1a = new Card(new SimpleCard("A:A",1));
        final Card sc1b = new Card(new SimpleCard("B:B",1));
        wiki.addCard(sc1a);
        fc.addCard(sc1b);
        
        // Make a move attempt
        Sync.copyUnmatchedIdedCards(wiki,fc);
        
        // Verify that nothing was moved, since there is nothing unmatched
        final CardsHolder target = new FDCards();
        target.addCard(sc1b);

        var listFC =         fc.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        var listTarget = target.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        assertEquals(listTarget,listFC);
    }
    
    @Test
    public void should_ignoreNullsInDestination_when_copying()
    {
        // put a null into the destination
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        final Card sc1a = new Card(new SimpleCard("A:A",1));
        final Card sc1b = new Card(new SimpleCard("B:B",1));
        final Card scNull = new Card(new SimpleCard("C:C",null));
        wiki.addCard(sc1a);
        fc.addCard(sc1b);
        fc.addCard(scNull);
        
        // Make a move attempt, the null shouldn't cause any extra excitement
        Sync.copyUnmatchedIdedCards(wiki,fc);
        
        // Verify that nothing was moved, since there is nothing unmatched
        final CardsHolder target = new FDCards();
        target.addCard(sc1b);
        target.addCard(scNull);

        var listFC =         fc.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        var listTarget = target.getCards()
                               .stream()
                               .map(c->new Card(c))
                               .collect(Collectors.toList());
        assertEquals(listTarget,listFC);
    }
    
    @Test
    public void should_countCardsCopied()
    {
        // set up to move one card and ignore a null card
        final CardsHolder wiki = new WikiData();
        final CardsHolder fc = new FDCards();
        final Card sc1 = new Card(new SimpleCard("A:A",1));
        final Card sc2 = new Card(new SimpleCard("B:B",2));
        final Card scNull = new Card(new SimpleCard("C:C",null));
        wiki.addCard(sc1);
        wiki.addCard(scNull);
        fc.addCard(sc2);
        
        // Make the move
        int count = Sync.copyUnmatchedIdedCards(wiki,fc);
        
        // Verify correct number was moved
        assertEquals(1,count);
    }
}
