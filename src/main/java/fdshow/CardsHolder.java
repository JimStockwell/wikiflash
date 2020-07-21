package fdshow;

import java.util.List;
import java.util.ArrayList;

/*
 * CardsHolder's fdshow direct dependencies
 *   Card
 */

import java.util.Collection;

/**
 * This class represents what is common between a flashcard file
 * and a wiki file that contains, among other things, flashcards.
 */
abstract class CardsHolder {
  
  /**
   * Returns a list of IDs in the CardsHolder.
   * UnIDed cards ARE included, as null values.
   *
   * The type is ArrayList so that the method "RemoveAll" is definitely
   * supported.
   *
   * @return a list of the IDs in the CardsHolder.
   */
  abstract ArrayList<Integer> getIds();

  /**
   * Returns a count of IDed cards in the CardsHolder.
   * That is, the number of cards with ID not equal to null.
   *
   * @return the count of IDed cards in the CardsHolder
   */
  int getCountOfIds() {
    final ArrayList<Integer> list = getIds();
    list.removeIf( id -> id == null);
    return list.size();
  }

  /**
   * Returns true if the CardsHolder contains a card with the specified ID,
   * otherwise false.
   *
   * @param id  the ID to check
   * @return true if the CardsHolder contains the ID.
   */
  boolean contains(Integer id) {
    return getIds().contains(id);
  }

  /**
   * Gets the specified card.
   *
   * @param id The ID of the card to get.  Null is not a valid value.
   * @return The specified card if present, null otherwise.
   */
  abstract Card getCard(Integer id);

  /**
   * Put IDs on the unIDed cards, and return a list of those new IDs.
   *
   * @return the newly assigned card IDs
   */
  abstract List<Integer> markBlankIds();

  /**
   * Updates the specified card.
   * All card fields are replaced with this new card's fields.
   *
   * This card must have a valid ID that is already in the CardsHolder.
   *
   * @param c Provides both the card number to update and
   *          the fields to update it with.
   */
  abstract void updateCard(Card c);

  /**
   * Adds the specified card.
   *
   * If the card is null, quitely does nothing.
   *
   * @param c The card to add
   */
  abstract void addCard(Card c);

  // javadoc comments from superclass
  public String toString()
  throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException();
  }

  /**
   * Clears the ID from all cards.
   */
  abstract void zapIds();

  /**
   * Get a complete list of the cards in this CardsHolder.
   * The list is a new list, so modifying it will not modify the CardsHolder's
   * state.
   *
   * @return A complete list of the cards in this CardsHolder.
   */
  abstract List<Card> getCards();
}
