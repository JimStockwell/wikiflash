package fdshow;

import java.io.BufferedReader;

/**
 * Represents the field names as provided by a Flashcards Deluxe data file
 */
class FieldNames
{
  /**
   * the list of field names
   */
  String[] data;

  /**
   * Constructs a list of field names from the given BufferedReader.
   * The reader is expected to be positioned just after the header information.
   *
   * @param r the specified BufferedReader
   */
  FieldNames(BufferedReader r)
  {
    // readLine will deal with \n\r correctly,
    // regardless of local settings, per contract.
    try {
      data = r.readLine().split("\t");
    } catch (java.io.IOException x) {throw new Error("Unexpected IOException");}
  }

  /**
   * Returns the field list in a format suitable
   * for writing into a Flashcards Deluxe export file.
   *
   * @return the field list
   */
  public String toString()
  {
    var joiner = java.util.stream.Collectors.joining("\t","","\r\n");
    var result = java.util.Arrays.stream(data).collect(joiner);
    return result;
  }

  /**
   * Returns the length of the field list,
   * that is, the number of fields.
   *
   * @return the length of the field list
   */
  int length()
  {
    return data.length;
  }
}
