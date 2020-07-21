package fdshow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

public class CardsHolderTest
{

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void getIds_Matches_Contains(CardsHolder x) {
    //
    // Initially they should agree there are no cards
    //
    assertEquals(0,x.getIds().size());
    assertTrue(!x.contains(42));

    //
    // If we add a card, they should both see it there
    // and nothing else there.
    //
    x.addCard(new SimpleCard("First front:back",42));
    assertEquals(1,x.getIds().size());
    assertEquals(42,x.getIds().get(0));
    assertTrue(x.contains(42));
    assertTrue(!x.contains(43));
  }

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_countCardsWithIds(CardsHolder x) {
    //
    // It should strip out nulls but not nonnulls.
    //
    assertEquals(0,x.getCountOfIds());

    x.addCard(new SimpleCard("First front:back",42));
    x.addCard(new SimpleCard("Second front:back",null));
    assertEquals(1,x.getCountOfIds());
  }

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_haveNoCards_when_constructedEmpty(CardsHolder x) {
    assertEquals(0,x.getCards().size());
  }

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_getCards_when_notEmpty(CardsHolder x) {
    // should return an ArrayList of the cards we add in
    Card sc1 = new Card(new SimpleCard("A:B",1));
    Card sc2 = new Card(new SimpleCard("C:D",2));
    var expected = new ArrayList<Card>(2);
    expected.add(sc1);
    expected.add(sc2);

    // make non-empty CardsHolder
    x.addCard(sc1);
    x.addCard(sc2);

    // test it
    assertEquals(expected, new ArrayList(x.getCards()));
  }

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_notModifyInteralState_when_getCardsResultModified(CardsHolder x) {
    // should return an ArrayList of the cards we add in
    Card sc1 = new Card(new SimpleCard("A:B",1));
    Card sc2 = new Card(new SimpleCard("C:D",2));
    var expected = new ArrayList<Card>(2);
    expected.add(sc1);
    expected.add(sc2);

    // make matching CardsHolder, and get the cards from it
    x.addCard(sc1);
    x.addCard(sc2);
    var gotCards = x.getCards();
    
    // then change the got cards!
    gotCards.remove(0);

    // make sure the CardsHolder still has both cards
    assertEquals(expected, new ArrayList(x.getCards()));
  }
  
  /**
   * Supply empty CardsHolder objects,
   * planning to do generic testing on each.
   * Ideally we supply one object for each subclass of consequence.
   *
   * @returns a stream of the empty Cardsholder objects.
   */
  private static Stream<CardsHolder> createCardsHolderSubclasses()
  {

    final WikiData wd = new WikiData();
    final FDCards fdc = new FDCards();

    return Stream.of(wd,fdc);
  }
}

