package fdshow;

import java.util.Map;
import java.util.HashMap;
import java.util.Objects;

//
// Card's fdshow direct dependencies
//   None.
//

/**
 * Contains the fields and optional ID of the flashcard/wiki Card.
 *
 * Notice that Card is immutable,
 * neither having methods that change it,
 * nor having outside references to its potentially mutable innards.
 * 
 */
class Card {
  /**
   * Card data, as a map from field to field content.
   */
  private final Map<String, String> dataByField;

  /**
   * Card ID.  Null means no ID assigned yet.
   */
  private final Integer id;

  /**
   * Creates a Card from the specified field names and data, and specified id.
   * The Map that specifies the fields may not be null, but the id may be.
   *
   * @param dataByField the fields and data
   * @param id          the id
   */
  Card(final Map<String, String> dataByField, final Integer id) {
    assert dataByField != null;
    this.dataByField = new HashMap<>(dataByField);
    this.id = id;
  }

  /**
   * Creates a Card as a duplicate of the given Card.
   *
   * @param c the card to construct this card from
   */
  Card(final Card c) {
    this(c.getData(), c.getId());
  }

  /**
   * Returns a new Map from field name to field contents.
   * @return the Map
   */
  Map<String, String> getData() {
    return new HashMap<>(dataByField);
  }

  /**
   * Gets the card's ID.
   * If the card does not have an ID, returns null.
   * @return the card's ID.
   */
  Integer getId() {
      return id;
  }

  /**
   * Gets a printable string.
   * It is normally a string representation of a number,
   * but if the ID is null, then some representation of that fact.
   *
   * @return a String representation of the ID.  Will not return null.
   */
  String getIdString() {
       return id != null ? id.toString() : "<NULL>";
  }

  /**
   * Returns true if same class and same fields.
   * @param o the object to test for equality
   * @return true if same class and same fields
   */
  @Override
  public boolean equals(final Object o) {
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
      if (this == o) {
          return true;
      }
      Card c = (Card) o;
      boolean idsSame = Objects.equals(id, c.getId()); // null is legitimate
      return idsSame && dataByField.equals(c.getData());
  }

  @Override
  public int hashCode() {
    return Objects.hash(dataByField, id);
  }

  /**
   * The standard field names.
   */
  static final String[] FIELD_NAMES_OF_INTEREST = new String[] {
      "Text 1", "Text 2", "Text 3", "Text 4", "Text 5",
      "Picture 1", "Picture 2", "Picture 3", "Picture 4", "Picture 5",
      "Sound 1", "Sound 2", "Category 1", "Statistics 1",
      "Notes", "Extra Info"};
}
