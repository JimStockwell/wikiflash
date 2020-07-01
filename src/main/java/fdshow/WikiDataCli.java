package fdshow;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command( name = "WikiData",
          description = "WikiData utility",
          mixinStandardHelpOptions = true,
          version = "pre-release")
class WikiDataCli implements Callable<Integer> {

  @Option(names = "-a", description = "add card text")
  String addCardText;

  @Option(names = "-n", description = "card number")
  Integer cardNumber;

  // perhaps also support commands extract and delete

  public static void main(String[] args) throws Exception {
    int exitCode = new CommandLine(new WikiDataCli()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    final var wd = new WikiData();
    final var in = new BufferedReader(new InputStreamReader(System.in));
    wd.loadFrom(in);

    if (addCardText!=null && cardNumber==null) { // zero is valid, just not here
      wd.addCard(new SimpleCard(addCardText,cardNumber));
    }
    System.out.println(wd.toString());
    return 0;
  }
}
