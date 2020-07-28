package fdshow;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CardsHolderTest
{
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_getNextId_when_Empty(CardsHolder x) {
    assertEquals(Integer.MIN_VALUE,x.getNextId());
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_getNextId_when_IncludesNulls(CardsHolder x) {
    x.addCard(new SimpleCard("First front:back",0xABC));
    x.addCard(new SimpleCard("C:D",null));
    assertEquals(0xABC+1,x.getNextId());
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_getNextId_when_LowToHigh(CardsHolder x) {
    x.addCard(new SimpleCard("First front:back",42));
    x.addCard(new SimpleCard("C:D",0xABC));
    assertEquals(0xABC+1,x.getNextId());
  }

  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_getNextId_when_HighToLow(CardsHolder x) {
    x.addCard(new SimpleCard("First front:back",0xABC));
    x.addCard(new SimpleCard("C:D",42));
    assertEquals(0xABC+1,x.getNextId());
  }
  
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
    var actual = x.getCards()
                  .stream()
                  .map(c -> new Card(c))
                  .collect(Collectors.toCollection(ArrayList::new));
    assertEquals(expected, actual);
  }

  /**
   * When the results of GetCards is changed,
   * it does not change change the CardsHolder's state.
   * 
   * @param x the CardsHolder to test
   */
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
    var actual = x.getCards()
                  .stream()
                  .map(c -> new Card(c))
                  .collect(Collectors.toCollection(ArrayList::new));
    assertEquals(expected, actual);
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_deleteZeroCards(CardsHolder x) {
    x.deleteCards(new ArrayList<>());
    assertEquals(0, x.getCards().size());
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_throw_when_deleteCardsIncludesNulls(CardsHolder x) {
    assertThrows(
            NullPointerException.class, 
            () -> x.deleteCards(Arrays.asList(new Integer[] {null})));
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_throw_when_deleteCardsIsNull(CardsHolder x) {
    assertThrows(NullPointerException.class, () -> x.deleteCards(null));
  }
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_ignoreExtraDeleteRequests_when_deleting(CardsHolder x) {
    x.deleteCards(Arrays.asList(new Integer[] {42}));
    assertEquals(0, x.getCards().size());
  }  
  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_deleteOneCard_when_present(CardsHolder x) {
    // make a CardsHolder with #1 and #2 in it, then remove #1
    Card sc1 = new Card(new SimpleCard("A:B",1));
    Card sc2 = new Card(new SimpleCard("C:D",2));
    x.addCard(sc1);
    x.addCard(sc2);
    x.deleteCards(Arrays.asList(new Integer[] {1}));
    
    // confirm we have only #2 left
    var expected = new ArrayList<Card>(1);
    expected.add(sc2);
    var actual = x.getCards()
                  .stream()
                  .map(c -> new Card(c))
                  .collect(Collectors.toCollection(ArrayList::new));
    assertEquals(expected, actual);
  }

  
  @ParameterizedTest
  @MethodSource("createCardsHolderSubclasses")
  void should_ignoreNullCardsInCardsHolder_whenDeletingCards(CardsHolder x) {
    // make a CardsHolder that includes a null card
    Card sc1 = new Card(new SimpleCard("A:B",null));
    Card sc2 = new Card(new SimpleCard("C:D",2));
    Card sc3 = new Card(new SimpleCard("E:F",3));
    x.addCard(sc1);
    x.addCard(sc2);
    x.addCard(sc3);
    x.deleteCards(Arrays.asList(new Integer[] {2}));
    
    // confirm the null card, and the card not requested to be deleted, remain.
    var expected = new ArrayList<Card>(2);
    expected.add(sc1);
    expected.add(sc3);
    
//    var actual = new ArrayList<>(x.getCards());
    var actual = x.getCards()
                  .stream()
                  .map(c -> new Card(c))
                  .collect(Collectors.toCollection(ArrayList::new));
    assertEquals(expected, actual);
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

