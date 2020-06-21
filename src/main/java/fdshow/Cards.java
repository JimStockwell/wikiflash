package fdshow;

import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

class Cards
{
  private List<Card> data;
  private FieldNames fields;
  private int numberOfFields;
  Cards(Reader r, FieldNames fn)
  {
    fields = fn;
    numberOfFields = fn.length();
    data = new LinkedList<Card>();
    while(hasNextCard(r)) {
      data.add(nextCard(r));
    }
  }

  public String toString()
  {
    var joiner = java.util.stream.Collectors.joining("\r\n");
    var result = data.stream().map(c -> c.toString()).collect(joiner);
    return result;
  }
  
  private boolean hasNextCard(Reader r){
    boolean has;
    try {
      r.mark(10);
      has = (r.read() != -1);
      r.reset();
    } catch (java.io.IOException x) {
      throw new Error("Unexpected IOException");
    }
    return has;
  }

  private Card nextCard(Reader r)
  {
    Card card = new Card(fields);
    card.loadFields(r);
    return card;
  }
}
