package fdshow;

import java.io.BufferedReader;

/**
 * Represents the field names as provided by a Flashcards Deluxe data file.
 */
class FieldNames {
  /**
   * Contains the field names.
   */
  private String[] data;

  /**
   * Constructs a list of field names from the given array of field names.
   * @param names the names of the fields
   */
  FieldNames(final String[] names) {
      data = names.clone();
  }
  /**
   * Constructs a list of field names from the given BufferedReader.
   * The reader is expected to be positioned just after the header information.
   *
   * @param r the specified BufferedReader
   */
  FieldNames(final BufferedReader r) {
    // readLine will deal with \n\r correctly,
    // regardless of local settings, per contract.
    try {
      data = r.readLine().split("\t");
    } catch (java.io.IOException x) {
        throw new Error("Unexpected IOException", x);
    }
  }

  /**
   * Returns the field list in a format suitable
   * for writing into a Flashcards Deluxe export file.
   *
   * @return the field list
   */
  @Override
  public String toString() {
    var joiner = java.util.stream.Collectors.joining("\t", "", "\r\n");
    var result = java.util.Arrays.stream(data).collect(joiner);
    return result;
  }

  /**
   * Returns the length of the field list,
   * that is, the number of fields.
   *
   * @return the length of the field list
   */
  int length() {
    return data.length;
  }

  /**
   * Returns the field names as an array.
   * @return the array
   */
  String[] toArray() {
      return data.clone();
  }
}
