package fdshow;

/* fdshow classes that FDCards knows about
 * CardsHolder
 * FDCard

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the collection of cards in a Flashcards Deluxe export file,
 * but does not represent the header or field names.
 *
 * Some important responsibilities:
 *  - Load and save a Flashcards Deluxe "export deck" data file.
 *  - Provide access to the cards collection.
 */

class FDCards extends CardsHolder
{

  /**
   * The names of the fields.
   */
  private FieldNames fields;

  /**
   * The quantity of fields.  Should agree with "fields" above.
   */
  private int numberOfFields;

  /**
   * Loads a FDCards collection
   * from the specified Flashcards Deluxe Reader.
   * The reader needs to be positioned at the begining
   * of the card collection data, that is,
   * after the header and the field names.
   * Field names are provided because it is helpful
   * in reading the correct number of fields.
   *
   * @param r   The Reader, positioned after the header and field list.
   * @param fn  The field list that describes the fields about to be read.
   *            Used for infering the number of fields to read.
   */
  void loadFrom(java.io.Reader r, FieldNames fn)
  {
    fields = fn;
    numberOfFields = fn.length();
    data = new java.util.LinkedList<Card>();
    while(hasNextCard(r)) {
      data.add(nextCard(r));
    }
  }

  public String toString()
  {
    if (data==null)
      throw new IllegalStateException("FDCards.loadFrom not called first.");
    var joiner = java.util.stream.Collectors.joining("\r\n");
    var result = data.stream().map(c -> c.toString()).collect(joiner);
    return result;
  }
  
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

  private Card nextCard(java.io.Reader r) {
    FDCard fdCard = new FDCard(fields);
    fdCard.loadFields(r);
    return fdCard;
  }
}
