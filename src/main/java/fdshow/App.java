package fdshow;

/*
 * App's fdshow direct dependencies
 *   FDFileData
 *   WikiData
 *   CardsHolder (the superclass of both FDFileData and WikiData)
 */

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.File;
import java.util.logging.Logger;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Maintains consistency between a flashcard program and a wiki.
 */
@Command( name = "App",
          description = "Flashcard -> WikiFile synchronizer",
          mixinStandardHelpOptions = true,
          version = "pre-release")
public class App implements Callable<Integer> {

  static Logger logger = Logger.getLogger(App.class.getName());

  /**
   * file name of flashcard data file
   */
  @Parameters(
    index = "0", 
    description = "Flashcards Deluxe file to sync from")
  private File fcFile;

  /**
   * file name of wiki data file
   */
  @Parameters(
    index = "1",
    description = "HTML wiki file to sync to")
  private File wFile;
  
  /**
   * True for readonly access.
   * That is, to report on but not save the results.
   */
  @Option( names = "-r", description = "read only" )
  private static boolean readOnly;

  /**
   * True to ignore existing IDs in the flashcard file.
   * This will reset the flashcard file and reimport it to the wiki.
   * The program checks first that the wiki is empty of cards.
   */
  @Option( names = "-i",
    description = "ignore existing IDs (use with caution)" )
  private static boolean ignoreExistingIds;

  /**
   * main entry point
   *
   * synchronizes the flashcard file to the wiki file
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
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
    // Load the flashcard data
    //
    FDFileData fcData = new FDFileData();
    fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );

    //
    // Load the wiki data
    //
    WikiData wData = new WikiData();
    wData.loadFrom(new BufferedReader( new FileReader( wFile ) ) );

    //
    // Update fcData -> wData
    // Maybe both directions some time in the future
    //
    logger.info("Updating from "
                    + fcFile.getName()
                    + " to "
                    + wFile.getName());
    if (ignoreExistingIds) {
      if (wData.getCountOfIds() != 0) {
        var msg = "Aborted: Can't ignore flashcard IDs.  "
          + "There are cards in the wiki file.";
        logger.severe(msg);
        System.err.println("Aborted");
        return -1;
      }
      logger.warning("Ignoring (overwriting) source card IDs");
      fcData.zapIds();
    }
    Sync.oneWay(fcData,wData);

    //
    // Save the files (unless we are readonly)
    //
    if (!readOnly) {
      //
      // Save the flashcard data
      //
      boolean renamed = fcFile.renameTo(new File(fcFile.getPath() + ".bak"));
      if( !renamed )
        throw new Error("Could not rename flashcard file to backup");
      var fos = new FileOutputStream(fcFile);
      fcData.saveTo(fos);
      fos.close();

      //
      // Save the wiki data
      //
      renamed = wFile.renameTo(new File(wFile.getPath() + ".bak"));
      if( !renamed )
        throw new Error("Could not rename wiki file to backup");
      try {
        var fw = new FileWriter(wFile);
        wData.saveTo(fw);
        fw.close();
      } catch (java.io.IOException e) {
        throw new Error(
          "IO Exception writing wiki file: "+wFile.getName(),e);
      }
    }
    //
    // ...and done
    //
    return 0;
  }
}
