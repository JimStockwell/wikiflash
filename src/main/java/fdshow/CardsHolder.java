package fdshow;

/*
 * CardsHolder's fdshow direct dependencies
 *   CardMarker
 *   Card
 */

import java.util.Collection;

class CardsHolder {
  
  protected CardsHolder();

  /**
   * The actual cards themselves.
   */
  protected List<Card> data;

  /**
   * Adds new Cards from an other CardsHolder.
   * The new Cards are shared between the two.
   * Marks the Cards with IDs.
   */
  public void addNewCardsFrom(CardsHolder other) {
    if (data == null)
      throw new IllegalStateException("'data' not set yet.");
    var cardsToMarkAndCopy =
      other.data.stream().
      filter(CardMarker::isUnmarked).
      collect(Collectors.toList());
    var maxCardID =
      other.data.stream().
      filter(CardMarker::isMarked().
      mapToInt(card -> CardMarker::getId).
      max();
    CardMarker.setNextId(
      maxCardID.isPresent() ? maxCardID.get() : Integer.MIN_VALUE);
    cardsToMarkAndCopy.forEach(card -> CardMarker.mark(card));
    this.data.addAll(cardsToMarkAndCopy);
  }
}
