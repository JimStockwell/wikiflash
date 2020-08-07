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
   static int update(CardsHolder from, CardsHolder to) {
    // get list to update
    final List<Integer> theUpdates =
            from.getIds()
                .stream()
                .filter(Objects::nonNull) // omit "new" cards
                .filter(id -> to.contains(id)) // omit "deleted" cards
                .collect(Collectors.toList());
    // update the items on the list
    theUpdates.forEach(id -> to.updateCard(from.getCard(id)));
    // and report
    logger.log(Level.INFO, "{0} cards updated", theUpdates.size());

    // return the number of items updated
    return theUpdates.size();
  }
    /**
     * Deleted cards that are in the destination but not the source.
     * 
     * The thought here is that such cards came about by having
     * been deleted on the source, and that deletion needs to
     * be synced over to the destination.
     * 
     * @param base the CardsHolder used as a reference
     * @param update the CardsHolder to delete cards out of
     */
    static void deleteExtraCards(CardsHolder base, CardsHolder update) {
        final ArrayList<Integer> wip = update.getIds();
        wip.removeAll(base.getIds());
        update.deleteCards(wip);
        logger.log(Level.INFO, "{0} cards deleted", wip.size());        
    }
  /**
   * Moves unmatched cards, that have IDs, from 'from' to 'to'.
   * @param from the CardsHolder to move the cards from
   * @param to   the CardsHolder to move the cards to
   */
  static int copyUnmatchedIdedCards(CardsHolder from, CardsHolder to) {
        final List<Integer> toMove =
            from.getIds()
                .stream()
                .filter(Objects::nonNull)
                .filter(id -> !to.contains(id))
                .collect(Collectors.toList());
        toMove.forEach(id -> to.addCard(from.getCard(id))); 
        final int count = toMove.size();
        logger.info("Moved "
                + count
                + " unmatched but IDed cards from source to destination");
        return count;
  }
  
  static void markAndAddNewCards(CardsHolder from, CardsHolder to) {
    //
    // Get marked cards to copy
    //
    final var newFrom = from.markBlankIds();
    //
    // Copy them
    //
    final var it = newFrom.iterator();
    while (it.hasNext()) {
      final Card c = from.getCard(it.next());
      to.addCard(c);
    }
    //
    // Report on the results
    //
    logger.log(Level.INFO, "{0} cards added", newFrom.size());
  }
  
  /**
   * Synchronizes the CardsHolders in one direction only.
   * @param from the CardsHolder to use as the unchanging standard
   * @param to   the CardsHolder to change to bring into conformity
   */
  static void xxx(CardsHolder from, CardsHolder to) {
    //
    // Process Deleted
    //
    deleteExtraCards(from,to);

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
     //
    // Add new cards
    //
    markAndAddNewCards(from,to);
    
  }
}
