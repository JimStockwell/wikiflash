package fdshow;

/*
 * App's fdshow direct dependencies
 *   FDFileData
 *   WikiData
 *   CardsHolder (the superclass of both FDFileData and WikiData)
 */

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.regex.Pattern;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Maintains consistency between a flashcard program and a wiki.
 */
@Command(name = "App",
          description = "Flashcard -> WikiFile synchronizer",
          mixinStandardHelpOptions = true,
          version = "pre-release")
public class App implements Callable<Integer> {

  /**
   * Adds logging ability.
   */
  static final Logger LOGGER = Logger.getLogger(App.class.getName());

  /**
   * File name of flashcard data file.
   */
  @Parameters(
    index = "0",
    description = "File to sync from")
  private File fromFile;

  /**
   * File name of wiki data file.
   */
  @Parameters(
    index = "1",
    description = "File to sync to")
  private File toFile;

  /**
   * True for readonly access.
   * That is, to report on but not save the results.
   */
  @Option(names = "-r", description = "read only")
  private static boolean readOnly;

  /**
   * True to ignore existing IDs in the flashcard file.
   * This will reset the flashcard file and reimport it to the wiki.
   * The program checks first that the wiki is empty of cards.
   */
  @Option(names = "-i",
    description = "ignore existing IDs (use with caution)")
  private static boolean ignoreExistingIds;

  /**
   * True to move unmatched card with IDs.
   * That is, copy those cards to the destination document.
   *
   * This isn't normally necessary,
   * since there should not be any IDed cards in the source
   * that aren't reflected in the destination.
   * That's the point of being an IDed card.
   *
   * This is sometimes useful if the cards somehow get out of sync.
   */
  @Option(names = "-m",
    description = "move unmatched IDed cards") // this is unusual
  private static boolean moveExtraCards;

  /**
   * True to take unIDed (so new) cards in the source,
   * and ID them
   * and add them to the destination.
   */
  @Option(names = "-a",
    description = "add unIDed 'from' cards")
  private static boolean addNewCards;

  /**
   * True to take unIDed cards in the destination
   * (so deleted from the source?)
   * and delete them from the destination.
   */
  @Option(names = "-d",
    description = "delete unmatched 'to' cards")
  private static boolean deleteCards;

  /**
   * True to updated cards in the desination
   * from the source, where the destination card ID
   * matches the source card ID.
   */
  @Option(names = "-u",
    description = "update matching cards")
  private static boolean updateCards;

  /**
   * Synchronizes the flashcard file to the wiki file.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }

  private CardsHolder cardsHolderOpener(final File file)
  throws FileNotFoundException, IOException {
    CardsHolder holder = null;
    if (file != null) {
        if (Pattern.compile("\\.html$")
                   .matcher(file.getName())
                   .find()) {
            holder = new WikiData();
        } else if (Pattern.compile("\\.txt$")
                   .matcher(file.getName())
                   .find()) {
            holder = new FDFileData();
        } else {
            throw new Error(
              file.getName() + " file name must end in .html or .txt");
        }
        holder.loadFrom(new BufferedReader(new FileReader(file)));
    }
    return holder;
  }
  /**
   * the workhorse of our application
   *
   * Receives control from picocli,
   * loads the flashcard and wiki files,
   * processes them,
   * and writes them back.
   */
  @Override
  public Integer call() throws Exception {
    //
    // Load the "from" data
    //
    CardsHolder fromData = cardsHolderOpener(fromFile);
    CardsHolder toData = cardsHolderOpener(toFile);

    //
    // Update fcData -> wData
    // Maybe both directions some time in the future
    //
    LOGGER.log(
      Level.INFO,
      "Updating from {0} to {1}",
      new Object[]{fromFile.getName(), toFile.getName()});
    if (ignoreExistingIds) {
      if (toData.getCountOfIds() != 0) {
        var msg = "Aborted: Can't ignore flashcard IDs.  "
          + "There are cards in the 'to' file.";
        LOGGER.severe(msg);
        System.err.println("Aborted");
        return -1;
      }
      LOGGER.warning("Ignoring (overwriting) source card IDs");
      fromData.zapIds();
    }

    //
    // This should not normally happen.
    // The user may do this as an attempt to recover lost/damaged cards.
    //
    if (moveExtraCards) {
        Sync.copyUnmatchedIdedCards(fromData, toData);
    }
    if (deleteCards) {
        Sync.deleteExtraCards(fromData, toData);
    }
    if (updateCards) {
        Sync.update(fromData, toData);
    }
    if (addNewCards) {
        Sync.markAndAddNewCards(fromData, toData);
    }
    //
    // Save the files (unless we are readonly)
    //
    if (!readOnly) {
      //
      // Save the flashcard data
      //
      boolean renamed = fromFile.renameTo(
        new File(fromFile.getPath() + ".bak"));
      if (!renamed) {
        throw new Error("Could not rename " + fromFile.getPath()
                                            + " file to backup");
      }
      fromData.saveTo(fromFile);

      //
      // Save the wiki data
      //
      renamed = toFile.renameTo(new File(toFile.getPath() + ".bak"));
      if (!renamed) {
        throw new Error("Could not rename " + toFile.getPath()
                                            + " file to backup");
      }
      toData.saveTo(toFile);
    }
    //
    // ...and done
    //
    return 0;
  }
}
