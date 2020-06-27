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

/**
 * A Card, as read from, and writable to, a Flashcards Deluxe export file.
 *
 * Contains the fields the wiki user is interested in,
 * plus scheduling and other "INFO" data.
 *
 * Unlike Header, FieldNames, and Cards,
 * this class (Card) is not loaded by the constructor.
 */


class FDCard extends Card
{
  FieldNames fieldNames;

  FDCard(FieldNames fields) {
    super();
    fieldNames = fields;
  }

  /**
   *  Load the contents of each field from the Reader
   */
  void loadFields(Reader r) {
    var c = new HashMap<String,String>();
    for(int i=0; i<fieldNames.length(); i++) {
      c.put(fieldNames.data[i],nextField(r));
    }
    setData(c);
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

  public String toString()
  {
    Map<String,String> content = getData();
    var sb = new StringBuilder();
    
    for(int i=0; i<fieldNames.length(); i++) {
      sb.append(canonicalField(content.get(fieldNames.data[i])));
      if (i < fieldNames.length()-1) sb.append("\t");
    }
    return sb.toString();
  }
}

