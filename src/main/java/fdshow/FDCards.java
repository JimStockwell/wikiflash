package fdshow;

import java.io.StringReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.logging.Logger;

/**
 * Represents the collection of cards in a Flashcards Deluxe export file,
 * but does not include the header or field names.
 *
 * Some important responsibilities:
 * <UL>
 *  <LI> Load and save the cards section of a Flashcards Deluxe "export deck"
 *    data file.</LI>
 *  <LI>Provide access to the cards collection.</LI></UL>
 */
class FDCards extends CardsHolder
{
  static Logger logger = Logger.getLogger(FDCards.class.getName());

  /**
   * The actual cards themselves.
   */
  protected List<Card> data;

  /**
   * The names of the fields.
   */
  private FieldNames fields;

  @Override
  void saveTo(File file)
  throws java.io.IOException {
      throw new UnsupportedOperationException();
  }
  /**
   * Loads an FDCards collection
   * from the specified Flashcards Deluxe Reader.
   * The reader needs to be positioned at the begining
   * of the card collection data, that is,
   * after the header and the field names.
   * Field names are provided because it is helpful
   * in reading the correct number of fields.
   *
   * @param r       The Reader, positioned after the header and field list.
   * @param fields  The field list that describes the fields about to be read.
   *                Used for infering the number of fields to read.
   */
  void loadFrom(java.io.BufferedReader r, FieldNames fields)
  {
    this.fields = fields;
    data = new java.util.LinkedList<>();
    while(hasNextCard(r)) {
      data.add(nextCard(r));
    }
  }

  /**
   * loadFrom, without a fields list, is not supported.
   * @param r the Reader to load from
   */
  @Override
  void loadFrom(java.io.BufferedReader r) {
      throw new UnsupportedOperationException();
  }
  
  /**
   * Constructs an empty FDCards,
   * ready to read cards with the specified fields.
   *
   * @param fields the names of the fields to expect
   */
  FDCards( FieldNames fields ) {
    this.fields = fields;
    data = new java.util.LinkedList<Card>();
  }

  /**
   * Construct an empty FDCards.
   * It will typically be overwritten with 'loadFrom'
   * but is handy for testing otherwise.
   */
  FDCards() {
    this(
      new FieldNames(
        new BufferedReader(
          new StringReader(
            "Text 1\tText 2\tText 3\tText 4\tText 5\tNotes\r\n"))));
  }

  /**
   * Returns the FDCards data
   * in a format suitable for writing into a Flashcards Duluxe data file,
   * just after the header and field names sections.
   *
   * @return the formatted data
   */
  @Override
  public String toString()
  {
    assert data != null;
    var joiner = java.util.stream.Collectors.joining("\r\n");
    var result = data.stream().map(c -> c.toString()).collect(joiner);
    return result;
  }
 
  /**
   * Reads and returns the next Card from the Reader.
   *
   * @param r the source of the next card
   * @return the next Card
   */
  private Card nextCard(java.io.Reader r) {
    return new FDCard(r,fields);
  }

  /**
   * Returns true if there is a next Card to read.
   *
   * @param r the source of the possible next Card
   * @return true if there is in fact a next Card
   */
  private static boolean hasNextCard(java.io.Reader r){
    boolean has;
    try {
      r.mark(10);
      has = (r.read() != -1);
      r.reset();
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
    return has;
  }

  // See the superclass for javadoc
  @Override
  void addCard(Card c)
  {
    assert data != null;
    if (c == null) return;

    final Integer id = c.getId();
    if (id != null && contains(id)) {
      String msg =
        String.format("Can't add id '%d' as it is already present.",id);
      throw new IllegalArgumentException(msg);
    }

    data.add(new FDCard(c,fields));
  }

  // See the superclass for javadoc
  @Override
  void updateCard(Card c)
  {
    throw new UnsupportedOperationException();
  }

  // See the superclass for javadoc
  @Override
  Card getCard(Integer id)
  {
    assert data != null;
    if (id==null) {
        throw new IllegalArgumentException("Cannot get a 'null' IDed card");
    }

    for(final var iter = data.iterator(); iter.hasNext(); ) {
      Card c = iter.next();
      Integer cardId = c.getId();
      if (cardId.equals(id)) {
        return c;
      }
    }
    return null;
  }

  // See the superclass for javadoc
  @Override
  ArrayList<Integer> getIds()
  {
    assert data != null;

    return new ArrayList<Integer>(
                 data.stream()
                     .map(c -> c.getId())
                     .collect(Collectors.toList()));
  }
  
  // See the superclass for javadoc
  @Override
  void zapIds()
  {
    assert data != null;
    for (final var iter = data.listIterator(); iter.hasNext();) {
      final Card x = iter.next();
      iter.set(new FDCard(x.getData(),null,fields));
    }
  }
  
//  /**
//   * Determines the next ID to assign a null IDed Card.
//   * @return the next ID to assign
//   */
//  private int getNextId() {
//    OptionalInt maxFound = data.stream()
//                               .filter(c -> c.getId() != null)
//                               .mapToInt(c -> c.getId())
//                               .max();
//    return maxFound.isEmpty() ? Integer.MIN_VALUE : maxFound.getAsInt();
//  }

  /**
   * Sets the Cards with null ID to have an ID.
   * The ID starts at the specified ID and proceeds numerically upward.
   *
   * @param firstId the first ID to assign
   * @return the IDs assigned
   */
  private List<Integer> setAndGetIds(int firstId) {
    int nextId = firstId;
    for(final var iter = data.listIterator(); iter.hasNext(); ) {
      final Card oldCard = iter.next();
      if (oldCard.getId() == null) {
        Card newCard = new FDCard(oldCard.getData(),nextId++,fields);
        iter.set(newCard);
      }
    }
    List<Integer> assigned = IntStream.range(firstId,nextId)
                                      .boxed()
                                      .collect(Collectors.toList());
    return assigned;
  }

  // See the superclass for javadoc
  @Override
  List<Integer> markBlankIds()
  {
    assert data != null;

    int nextId = getNextId();
    return setAndGetIds(nextId);
  }

  @Override
  List<Card> getCards()
  {
    return new ArrayList<>(data);
  }
  
  @Override
  void deleteCards(List<Integer> ids) {
      ids.forEach(i -> java.util.Objects.requireNonNull(i));
      data.removeIf(c -> ids.contains(c.getId()));
  }
}
