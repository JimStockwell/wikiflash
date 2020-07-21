package fdshow;

//
// fdshow classes that this class knows about:
//    CardsHolder
//

import java.util.logging.Logger;

/**
 * Synchronizes data between a Flashcards Deluxe data file
 * and an HTML wiki like file.
 *
 * The cards in the wiki file can be rearranged and organized
 * as the user sees fit,
 * and have additonal information put into the wiki file around the cards
 * to give them context or build upon what they say.
 *
 * When the flash card file and the wiki file are out-of-sync,
 * a flash card may have been edited in one file, but not the other,
 * or a new card may have been added or an old card removed in one file
 * but not the other.
 * 
 * When the flash card file and the wiki file are in sync,
 * on the other hand, the cards match between the two files.
 *
 * <B>Actually, only addition is implemented at this point.</B>
 */
class Sync {

  static Logger logger = Logger.getLogger(Sync.class.getName());

  /**
   * Synchronizes the CardsHolders in one direction only.
   * @param from the CardsHolder to use as the unchanging standard
   * @param to   the CardsHolder to change to bring into conformity
   */
  static void oneWay(CardsHolder from, CardsHolder to) {
    //
    // Process Deleted
    //
    final var oldFrom = from.getIds();
    final var oldTo = to.getIds();
    final var deletedCards = oldTo.removeAll(oldFrom);
//    to.deleteCards(deletedCards);
//    logger.info("" + deletedCards.size() 
//                   + " cards deleted in " 
//                   + from.getName());
    //
    // Add new cards
    //
    final var newFrom = from.markBlankIds();
    final var it = newFrom.iterator();
    logger.fine("Adding the following card IDs");
    while (it.hasNext()) {
      final Card c = from.getCard(it.next());
      logger.fine(() -> " "+c.getId());
      to.addCard(c);
    }
    logger.info("Added " + newFrom.size() + " new cards.");
  }

  /**
   * Synchronizes the CardsHolders in both directions.
   *
   * @param first  the first of the two CardsHolders to synchronize.
   * @param second the second of the two CardsHolders to synchronize.
   */
  static void twoWay(CardsHolder first, CardsHolder second) {
    if (true) throw new UnsupportedOperationException();
    oneWay(first,second);
    oneWay(second,first);
  }
}
