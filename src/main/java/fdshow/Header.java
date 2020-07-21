package fdshow;

import java.io.Reader;
import java.util.LinkedList;

/**
 * Represents the header in a Flashcards Deluxe data file.
 */
class Header {
  /**
   * The header data.
   * The Strings include the terminating \r\n of the flashcard file.
   */
  LinkedList<String> data;

  /**
   * Constructs the Header instance based on what is read in from the Reader.
   *
   * @param r provides the Header data
   */
  Header(Reader r)
  {
    data = new LinkedList<String>();
    while( hasNextLine(r) )
      data.add( nextLine(r) );
  }

  /**
   * Returns true if there is a header line yet to be read.
   *
   * @param r the Reader which may contain an additonal header line
   * @return true if there is a header line yet to be read
   */
  private boolean hasNextLine( Reader r )
  {
    boolean has;
    if(!r.markSupported()) throw new Error("Reader Mark not supported!");
    try {
      r.mark(10);
      has = (r.read() == '*' && r.read()=='\t');
      r.reset();
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
    return has;
  }

  /**
   * Returns the next header line
   *
   * @param r the Reader that contains the next header line
   * @return the next header line
   */
  String nextLine( Reader r )
  {
    assert hasNextLine(r);
    var accum = new StringBuilder();
    int ch;
    try {
      do {
        ch = r.read();
        if(ch==-1) throw new Error("Unexpected end of file");
        accum.append((char)ch);
      } while(ch != '\r');
      accum.append((char)r.read()); // Better be a \n
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
    return accum.toString();
  }

  /**
   * Returns a representation of the header suitable for
   * writing into a Flashcards Deluxe data file.
   */
  public String toString() {
    var accum = new StringBuilder();
    var it = data.iterator();
    while( it.hasNext() )
    {
      accum.append( it.next() );
    }
    return accum.toString();
  }
}
