package fdshow;

//
// fdshow classes that this class knows about:
//    CardsHolder
//    Card
//
import java.io.Writer;
import java.io.Reader;
import java.util.Map;
import java.util.LinkedList;

/**
 * Extends CardsHolder to allow reading and writing the held cards
 * to a Wiki like format.  (Just a text file for now.)
 *
 * Uses CardsHolder's "data" to read and write Cards to.
 */
class WikiData extends CardsHolder {

  WikiData() {
    data = new LinkedList<Card>();
  }

  private static void write(Writer w, Card c)
  throws java.io.IOException {
    //
    // It would be good to order the fields into some standard order.
    // This is the place for it, not in Card, since the standard order
    // would be that which is appropriate to a wiki, not necessarily
    // a flashcard file or something else.
    //
    var cardData = c.getData();
    String[] fieldNamesOfInterest = new String[] {
      "Text 1", "Text 2", "Text 3", "Text 4", "Text 5",
      "Picture 1", "Picture 2",
      "Sound 2", "Category 1", "Statistics 1",
      "Notes", "Extra"};
    //
    // This won't be very easy to read back in, but it'll do for now.
    //
    for (String fname : fieldNamesOfInterest) {
      String fdata = cardData.get(fname);
      w.append(fname).append("\"").append(fdata).append("\"\n");
    }
  }

  /**
   * Save the wiki file to the destination 'w'.
   */
  void saveTo(Writer w) 
  throws java.io.IOException {
    var it = data.iterator();
    while (it.hasNext()) {
      var card = it.next();
      write(w,card);
    }
  }

  /**
   * Does nothing for now.  Actually updating a wiki file isn't implemented yet.
   */
  void loadFrom(Reader r) {
  }
}
