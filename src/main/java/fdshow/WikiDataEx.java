package fdshow;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/*
 * An "exercisor" is needed rather than "commandizing" the WikiData class
 * directly, because interpretting on the commands is enough for a class.
 */
@Command( name = "WikiDataEx",
          description = "Exercises the WikiData class",
          mixinStandardHelpOptions = true,
          version = "pre-release")
class WikiDataEx implements Callable<Integer> {

  @Parameters(index = "0", description="HTML file to act on.")
  private java.io.File htmlFile;

  public static void main(String[] args) throws Exception {
    int exitCode = new CommandLine(new WikiDataEx()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    final var wd = new WikiData();
    wd.loadFrom(new BufferedReader(new FileReader(htmlFile)));
    System.out.print(wd.toString());
    return 0;
  }
}
