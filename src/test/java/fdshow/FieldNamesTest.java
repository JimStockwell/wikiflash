package fdshow;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.io.BufferedReader;

/**
 * Tests the FieldNames class
 */
public class FieldNamesTest
{
  @Test
  public void makeAndCheck()
  {
    final String fieldNames =
      "Text 1\tText 2\tText 3\tText 4\tText 5\tNotes\r\n";
    final FieldNames fnObject = new FieldNames(
      new BufferedReader(
        new StringReader(
          fieldNames)));
    assertEquals(6,fnObject.length());
    assertEquals(fieldNames, fnObject.toString());
  }
}
