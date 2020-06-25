package fdshow;

/*
 * CardMarker's fdshow direct dependencies
 *   Card
 */

import java.util.regex.Pattern;

class ID {
  private int value;
  private boolean setYet = false;
  String getThenInc() {
    if (!setYet)
      throw new IllegalStateException("Tried to read before setting.");
    return String.valueOf(value++);
  }
  void set(int value) {
    this.value = value;
    this.setYet = true;
  }
  boolean isSet() {
    return this.setYet;
  }
}

/**
 * Markes a card with an ID number.
 *
 * It shouldn't matter what CardHolder is used to initialize the CardMarker,
 * as the two sets should be in sync.
 *
 * An ID, to the outside world, is an int.
 *
 * Internally, in the card, it is an HTML DIV tag, with an "id" attribute
 * assigning the ID.  The DIV section is typically empty, unless it contains
 * instructions to this program.
 */

class CardMarker {
  

  static private ID nextId = new ID();
  
  static void setNextId( int nextId ) {
    CardMarker.nextId.set( nextId );
  }

  static boolean isMarked( Card c ) {
    String firstSide = c.getField0();
    return firstSide.matches("^<DIV id=\"-?[0-9]+\">[A-Z]*</DIV>");
  }

  static boolean isUnmarked( Card c ) {
    return !isMarked(c);
  }

  /**
   * Mark the given card and update the "next ID".
   */
  static void mark(Card c) {
    if (isMarked(c))
      throw new IllegalArgumentException("Card is already marked.");
    if (!nextId.isSet())
      throw new IllegalStateException("Next ID has not been set yet.");
    String firstSide = c.getField0();
    String revisedFirstSide = new StringBuilder().
      append("<DIV id=\"").
      append(nextId.getThenInc()).
      append("\"></DIV>").
      append(firstSide).
      toString();
    c.setField0(revisedFirstSide);
  }

  static int getId( Card c ) {
    if (isUnmarked(c)) 
      throw new IllegalArgumentException("Card has no ID.");
    String idString = c.getField0().split("\"",2)[1];
    return Integer.parseInt(idString);
  }
}
