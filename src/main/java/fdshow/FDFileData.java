package fdshow;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Flashcards Deluxe export data file.
 */
public class FDFileData extends FDCards
{
  /**
   * the file header
   */
  private Header header;

  /**
   * the field names (they follow the header in the file)
   */
  private FieldNames fieldNames;

  /**
   * Loads the Flashcards Deluxe export data file into this instance.
   *
   * @param r the BufferedReader to read the datafile from
   */
  public void loadFrom(BufferedReader r)
  {
    header = new Header(r);
    fieldNames = new FieldNames(r);
    super.loadFrom(r,fieldNames);
  }

  /**
   * Saves the file card data to the specified stream, and closes the stream.
   *
   * @param outStream the stream to save the file card data to
   */
  public void saveTo(OutputStream outStream)
  {
    var pw = new java.io.PrintWriter(outStream);
    pw.print(header.toString());
    pw.print(fieldNames.toString());
    pw.print(super.toString());
    pw.close();
  }

//  /**
//   * Returns a string representation of the data,
//   * in a format suitable for saving as a Flashcards Deluxe data file.
//   *
//   * @return the Flashcards Deluxe compatible data file string
//   */
//  public String toString() {
//    return new StringBuilder().
//      append(header.toString()).
//      append(fieldNames.toString()).
//      append(super.toString()).
//      toString();
//  }
}

