package fdshow;

/*
 * FDCard's fdshow direct dependencies
 *   Card
 */

import java.io.Reader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A Card that can read itself from
 * and write itself to a FlashCards Deluxe file.
 */
class FDCard extends Card
{
  /**
   * The names of the fields as they come in the flashcard data file.
   */
  FieldNames fieldNames;

  /**
   * Construct a Card from the specified reader and field names.
   * The reader is positioned right before the fields of the
   * Card to be constructed.
   *
   * @param r      the reader from which to read the Card fields
   * @param fields the names of the fields that will be read in
   */
  FDCard(Reader r, FieldNames fields) {
    super(readCard(r,fields));
    fieldNames = fields;
  }

  /**
   * Returns a Card from the specified reader and field names.
   * The reader is positioned right before the fields of the
   * Card to be constructed.
   *
   * @param r      the reader from which to read the Card fields
   * @param fields the names of the fields that will be read in
   * @return the Card read
   */
  private static Card readCard(Reader r, FieldNames fields) {
    final var cardData = new HashMap<String,String>();
    Integer id = null;
    for(int i=0; i<fields.length(); i++) {
      cardData.put(fields.data[i],nextField(r));
    }
    final String notes = cardData.get("Notes");
    if (encodesId(notes)) {
      final String[] splits = notes.split(" : DO NOT MODIFY THIS LINE ",2);
      cardData.put("Notes",splits[1]);
      id = Integer.valueOf(splits[0]);
    }
    return new Card(cardData, id);
  }  

  /**
   * Returns the next field.
   *
   * Between calls, the Reader points at the start of a field,
   * just after any preceeding delimiters.
   *
   * Control characters that are part of the persistant storage control,
   * for example quotes at the end of a multiline field, are not included.
   *
   * FDCard's format:
   * If there are " (lone quotes) or line seperators in the field
   * then start the field and end the field with a lone quote,
   * replacing any lone quotes in the actual text with "" (repeated quotes).
   * Line seperators are a \r\n sequences.
   *
   * Internally (that is, not in a Flashcards Deluxe file),
   * quotes are not repeated and a system appropriate line seperator is used.
   *
   * @param r the source of the fields
   * @return the data associated with the next field
   */
  private static String nextField(Reader r)
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
        return accum.toString().replaceAll("\r\n",System.lineSeparator());
      } else {
        while( ch != '\t' && ch != '\r' && ch != -1 ) {
          accum.append((char)ch);
          ch = r.read();
        }
        if( ch=='\r' ) {
          r.read(); // soak up \n
        }
        return accum.toString();
      }
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
  }
  
  /**
   * Returns true if the specified String encodes a card ID
   *
   * @param note the string to check for an ID
   * @return true if the field encodes an ID
   */
  static private boolean encodesId(String note) {
    return note != null &&
           Pattern.compile("^-?[0-9]+ : DO NOT MODIFY THIS LINE ")
                  .matcher(note)
                  .find();
  }

  /**
   * Given a string in just a simple normal format,
   * returns a string suitable for writing into a Flashcards Deluxe data file.
   *
   * @param s the string to convert
   * @return the specified string, converted into Flashcards Deluxe format.
   */
  private static String canonicalField(String s)
  {
    class Fixer {

      private boolean needsFixing(String x) {
        return x.contains(System.lineSeparator()) || x.contains("\"");
      }

      private String fixQuotes(String x) {
        return x.replace("\"","\"\"");
      }

      private String fixNewLines(String x) {
        return x.replace(System.lineSeparator(),"\r\n");
      }

      private String quoteIt(String x) {
        return "\"" + x + "\"";
      }

      public String fix(String x) {
        return needsFixing(x) ? quoteIt(fixQuotes(fixNewLines(x))) : x;
      }
    }
    return new Fixer().fix(s);
  }

  /**
   * Returns the Card
   * as a string suitable for writing into a Flashcards Deluxe data file.
   * @return the card in a Flashcards Deluxe suitable format.
   */
  public String toString()
  {
    final Map<String,String> content = getData();
    final var sb = new StringBuilder();

    // If we don't have notes, can't save IDs!
    final boolean hasNotes = Pattern.compile("(.*\\t)?Notes(\\t.*)?")
                                    .matcher(fieldNames.toString())
                                    .find();
    
    for(int i=0; i<fieldNames.length(); i++) {
      if (getId() != null && !hasNotes) {
        throw new UnsupportedOperationException(
          "Can't save IDs in an FDFile with no 'Notes' fields."
          + System.lineSeparator()
          + "Fields are: "
          + fieldNames.toString().replaceAll("\t","\\\\t")
          );
      }

      if (fieldNames.data[i].equals("Notes") && getId() != null) {
        sb.append(
          canonicalField(
            getId().toString() + " : DO NOT MODIFY THIS LINE " +
            content.get(fieldNames.data[i])));
      } else {
        sb.append(canonicalField(content.get(fieldNames.data[i])));
      }
      if (i < fieldNames.length()-1) sb.append("\t");
    }
    return sb.toString();
  }
}
