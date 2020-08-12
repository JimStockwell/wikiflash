package fdshow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

/**
 * Represents a Flashcards Deluxe export data file.
 */
public class FDFileData extends FDCards {
  /**
   * The file header.
   */
  private Header header;

  /**
   * The field names (they follow the header in the file).
   */
  private FieldNames fieldNames;

  /**
   * Saves the CardsHolder to the indicated file, and closes the file.
   * @param file the file to save the CardsHolder to
   */
  @Override
  void saveTo(final File file)
  throws java.io.IOException {
      var fw = new FileWriter(file);
      try (java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
          pw.print(header.toString());
          pw.print(fieldNames.toString());
          pw.print(super.toString());
      }
  }

  /**
   * Loads the Flashcards Deluxe export data file into this instance.
   *
   * @param r the BufferedReader to read the datafile from
   */
  @Override
  public void loadFrom(final BufferedReader r) {
    header = new Header(r);
    fieldNames = new FieldNames(r);
    super.loadFrom(r, fieldNames);
  }

  /**
   * Saves the file card data to the specified stream, and closes the stream.
   *
   * @param outStream the stream to save the file card data to
   */
  public void saveTo(final OutputStream outStream) {
    var pw = new java.io.PrintWriter(outStream);
    pw.print(header.toString());
    pw.print(fieldNames.toString());
    pw.print(super.toString());
    pw.close();
  }
}
