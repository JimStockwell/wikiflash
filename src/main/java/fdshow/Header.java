package fdshow;

import java.io.Reader;
import java.util.LinkedList;

class Header {
  /**
   * The header data.
   * The Strings include the terminating \r\n of the flashcard file.
   */
  LinkedList<String> data;

  Header(Reader r)
  {
    data = new LinkedList<String>();
    while( hasNextLine(r) )
      data.add( nextLine(r) );
  }

  boolean hasNextLine( Reader r )
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

  String nextLine( Reader r )
  {
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
