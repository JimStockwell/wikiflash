package fdshow;

//
// fdshow classes that this class knows about:
//    CardsHolder
//

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Synchronizes data between a Flashcards Deluxe data file
 * and an HTML wiki like file.
 *
 * The cards in the wiki file can be rearranged and organized
 * as the user sees fit,
 * and have additional information put into the wiki file around the cards
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

  static final Logger logger = Logger.getLogger(Sync.class.getName());
  
  /**
   * Updates cards in to "to" that have ID matches in "from".
   * @param from CardsHolder to update matches from
   * @param to CardsHolder to update matches to
   * @return the number of matches updated
   */
  private static int update(CardsHolder from, CardsHolder to) {
    // get list to update
    final List<Integer> theUpdates =
            from.getIds()
                .stream()
                .filter(Objects::nonNull) // omit "new" cards
                .filter(id -> to.contains(id)) // omit "deleted" cards
                    // omit already equal cards
//                .filter(id -> !from.getCard(id).equalsAsCard(to.getCard(id)))
                .collect(Collectors.toList());
    // update the items on the list
    theUpdates.forEach(id -> to.updateCard(from.getCard(id)));
    // return the number of items updated
    return theUpdates.size();
  }
  
  /**
   * Synchronizes the CardsHolders in one direction only.
   * @param from the CardsHolder to use as the unchanging standard
   * @param to   the CardsHolder to change to bring into conformity
   */
  static void oneWay(CardsHolder from, CardsHolder to) {
    //
    // Process Deleted
    //
    
    // The cards that have been deleted in the source,
    // but not yet the destination,
    // are what we need to delete from the destination.
    final ArrayList<Integer> wip = to.getIds();
    wip.removeAll(from.getIds());
    to.deleteCards(wip);
    logger.log(Level.INFO, "{0} cards deleted", wip.size());
    //
    // Update modified cards
    //
    if (from.getNextId() != to.getNextId()) {
        String msg = "Next ID is "
                     + from.getNextId() 
                     + " for source, but " 
                     + to.getNextId() 
                     + " for destination";
        logger.severe(msg);
        throw new IllegalStateException(msg);
    }
    final int updateCount = update(from, to);
    logger.log(Level.INFO, "{0} cards updated", updateCount);
    //
    // Add new cards
    //
    assert from.getNextId() == to.getNextId();
    final var newFrom = from.markBlankIds();
    final var it = newFrom.iterator();
    logger.fine("Adding the following card IDs");
    while (it.hasNext()) {
      final Card c = from.getCard(it.next());
      logger.fine(() -> " "+c.getId());
      to.addCard(c);
    }
    logger.log(Level.INFO, "{0} cards added", newFrom.size());
    
  }
}
