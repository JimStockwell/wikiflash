package fdshow;

import java.util.HashMap;

/**
 * A simple to create minimal Card class.
 */
class SimpleCard extends Card {

  /**
   * Constructs a rather minimal style card.
   *
   * Fields are unnamed in the input,
   * and are separated by colons.
   *
   * @param text  The flash card text.
   * @param id    The card id.  A value of null means no ID.
   */
  SimpleCard(final String text, final Integer id) {
    super(cardFrom(text, id));
  }

  /**
   * Returns a rather minimal style card.
   *
   * Fields are unnamed in the input,
   * and are separated by colons.
   *
   * @param text  The flash card text.
   * @param id    The card id.  A value of null means no ID.
   * @return the minimal Card
   */
  private static Card cardFrom(final String text, final Integer id) {
    if (text == null) {
        throw new IllegalArgumentException("text cannot be null");
    }
    final var data = new HashMap<String, String>();
    final String[] split = text.split(":");
    for (int i = 0; i < split.length; i++) {
      data.put("Text " + (i + 1), split[i]);
    }
    return new Card(data, id);
  }

  /**
   * Constructs a rather minimal style card.
   *
   * Fields are unnamed in the input,
   * and are separated by colons.
   *
   * Card ID is set to null.
   *
   * @param text  The flash card text.
   */
  SimpleCard(final String text) {
    this(text, null);
  }

  /**
   * Returns a string representation of
   * the field names, field contents,
   * and, if present, the ID.
   */
  @Override
  public String toString() {
    final var fields = getData();
    final Integer id = getId();
    final String result =
      fields + ((id == null) ? "" : System.lineSeparator() + id.toString());
    return result;
  }
}
