package fdshow;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command( name = "Util",
          description = "Card builder and queryier",
          mixinStandardHelpOptions = true,
          version = "pre-release",
          synopsisSubcommandLabel = "COMMAND",
          subcommands = {CardCli.class,WikiDataCli.class})
public class Util {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new Util()).execute(args);
    System.exit(exitCode);
  }
}
