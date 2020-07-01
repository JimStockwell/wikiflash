package fdshow;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.HashMap;

@Command( name = "card",
          description = "Card builder and querier",
          mixinStandardHelpOptions = true,
          version = "pre-release")
class CardCli implements Callable<Integer> {
  @Option(names = "-s", description = "'Simple card' text")
  String simpleCardText;

  @Option(names = "-n", description = "card number")
  Integer cardNumber;

  public static void main(String[] args) throws Exception {
    int exitCode = new CommandLine(new CardCli()).execute(args);
    System.exit(exitCode);
  }


  @Override
  public Integer call() throws Exception {

    Card card = null;

    if (simpleCardText != null) {
      card = new SimpleCard(simpleCardText,cardNumber);
    }

    if(card != null) {
      System.out.println(card.toString());
    }
    return 0;
  }
}

class SimpleCard extends Card {
  SimpleCard(String text, Integer id) {
    final var data = new HashMap<String,String>();
    final String[] split = text.split(":");
    for (int i=0; i<split.length; i++) {
      data.put("Text "+(i+1),split[i]);
    }
    setData(data);
    setId(id);
  }

  @Override
  public String toString() {
    final var fields = getData();
    final Integer id = getId();
    final String result = 
      fields + ((id == null) ? "" : System.lineSeparator() + id.toString());
    return result;
  }
}
