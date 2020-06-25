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

/**
 * A Card, as read from, and writable to, a Flashcards Deluxe export file.
 *
 * Unlike Header, FieldNames, and Cards,
 * this class (Card) is not loaded by the constructor.
 */

class FDCard implements Card
{
  FieldNames fieldNames;

  /**
   * The data stored in the flashcard fields.
   * Control characters that are part of the persistant storage control,
   * for example quotes at the end of a multiline field, are not included.
   */
  List<String> data;

  FDCard(FieldNames fields) {
    data = new LinkedList<String>();
    fieldNames = fields;
  }

  void loadFields(Reader r) {
    for(int i=0; i<fieldNames.length(); i++)
      data.add(nextField(r));
  }  

  private boolean cardIsFullyLoaded() {
    return data.size()==fieldNames.length();
  }

  public String getField0() {
    if (!cardIsFullyLoaded())
      throw new IllegalStateException("The card is not fully loadeded yet.");
    return data.get(0);
  }

  public void setField0(String content) {
    if (!cardIsFullyLoaded())
      throw new IllegalStateException("The card is not fully loadeded yet.");
    data.set(0,content);
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
    var result = data.stream().map(FDCard::canonicalField).collect(joiner);
    return result;
  }
}

