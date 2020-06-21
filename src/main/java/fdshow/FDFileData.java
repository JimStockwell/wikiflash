package fdshow;

import java.io.InputStream;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * We don't use InputStreams because they are byte oriented and we'd need
 * to wonder whether a '\t' or other character we care about was somehow
 * a byte in some 2+ byte character.
 *
 * We use a Reader here, presumably a FileReader obtained elsewhere.
 */
public class FDFileData
{
  Header header;
  FieldNames fieldNames;
  Cards cards;

  void loadFrom(BufferedReader r)
  {
    header = new Header(r);
    fieldNames = new FieldNames(r);
    cards = new Cards(r,fieldNames);
  }

  void saveTo(OutputStream outStream)
  {
    var pw = new java.io.PrintWriter(outStream);
    pw.print(header.toString());
    pw.print(fieldNames.toString());
    pw.print(cards.toString());
    pw.close();
  }

}

class Cards
{
  private List<Card> data;
  private FieldNames fields;
  private int numberOfFields;
  Cards(Reader r, FieldNames fn)
  {
    fields = fn;
    numberOfFields = fn.length();
    data = new LinkedList<Card>();
    while(hasNextCard(r)) {
      data.add(nextCard(r));
    }
  }

  public String toString()
  {
    var joiner = java.util.stream.Collectors.joining("\r\n");
    var result = data.stream().map(c -> c.toString()).collect(joiner);
    return result;
  }
  
  private boolean hasNextCard(Reader r){
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

  private Card nextCard(Reader r)
  {
    Card card = new Card(fields);
    card.loadFields(r);
    return card;
  }
}

/**
 * A card can have different fields or numbers of fields
 * from in the flashcard data file, but that will need
 * to get straightened out before storing them back into
 * the flashcard data file.
 *
 * Unlike Header, FieldNames, and Cards,
 * this class (Card) is not loaded by the constructor.
 */

class Card
{
  FieldNames fieldNames;
  /**
   * The data stored in the flashcard fields.
   * Control characters that are part of the persistant storage control,
   * for example quotes at the end of a multiline field, are not included.
   */
  List<String> data;
  Card(FieldNames fields) {
    data = new LinkedList<String>();
    fieldNames = fields;
  }
  void loadFields(Reader r) {
//    initIterator();
//    while(hasNextField(r)) {
//      data.add(nextField(r));
//    }
    for(int i=0; i<fieldNames.length(); i++)
      data.add(nextField(r));
/*   
    if(data.size() != fieldNames.length()) {
      System.err.println(
        "Field 1: " + data.get(0));
      System.err.println(
        "Card has wrong number of fields: " + 
        data.size() + 
        " vs " + 
        fieldNames.length()
      );
    }
*/
  }  

  /**
   * Indicates that nextField just ate the card ending \r\n.
   */
  boolean ateEOL;

  /**
   * Resets ateEOL before iterating through the fields.
   */
  private void initIterator()
  {
    ateEOL = false;
  }

  /**
   * Returns the next field.
   *
   * Between calls, the Reader points at the start of a field,
   * just after any preceeding delimiters.
   */
  private String nextField(Reader r)
  {
    try {
      var accum = new StringBuilder();
      int ch = r.read();
      boolean quotedText = ch=='"';
      if(quotedText) {
        ch = r.read(); // get rid of the leading quote
        while( ch!='"' || (ch=r.read())=='"' ) {
          // ch is either a regular character, or the second of two quotes.
          accum.append((char)ch);
          ch = r.read();
        }
        // a first quote was read, then a non-quote.
        assert ch=='\t' || ch=='\r' || ch==-1 : "Error in flashcard file";
        if( ch == '\r' ) {
          ch = r.read();
          assert ch=='\n' : "Return not followed by linefeed in flashcard file";
        }
      } else {
        while( ch != '\t' && ch != '\r' && ch != -1 ) {
          accum.append((char)ch);
          ch = r.read();
        }
        if( ch=='\r' ) {
          r.read(); // soak up \n
        }
      }
      return accum.toString();
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
  }

  private static String canonicalField(String s)
  {
    if(s.contains("\n") || s.contains("\""))
      // Quote the begining and end, and replace solo quotes with pairs.
      return "\"" + s.replace("\"","\"\"") + "\"";
    else
      return s;
  }

  public String toString()
  {
    var joiner = java.util.stream.Collectors.joining("\t");
    var result = data.stream().map(s->canonicalField(s)).collect(joiner);
    return result;
  }
}
