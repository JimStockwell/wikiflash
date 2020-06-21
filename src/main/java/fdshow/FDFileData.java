package fdshow;

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

