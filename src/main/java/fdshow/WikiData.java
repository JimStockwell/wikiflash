package fdshow;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.function.ToIntFunction;
import java.util.function.Function;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 * Extends CardsHolder to allow reading and writing the held cards
 * in a Wiki like format.  (Just a single HTML file for now.)
 *
 * Existing non-card data in the wiki file is not disturbed.
 *
 * The cards are read and written in the following general kind of structure.
 *
 * <pre>{@code
 * <card>
 *   <field><name>Text 1</name> : <value>Question</value></field>
 *   <field><name>Text 2</name> : <value>Answer</value></field>
 * </card>
 * }</pre>
 */
class WikiData extends CardsHolder {

  static Logger logger = Logger.getLogger(FDCards.class.getName());

  /**
   * Contains the wiki document that the class operates on.
   */
  private Document doc;

  /**
   * Constructs a minimal wiki,
   * having no cards, but having somewhere to put new cards.
   */
  WikiData() {
    makeMinimalWiki();
  }

  WikiData(WikiData other) {
      doc = other.doc.clone();
      assert doc.hasSameValue(other.doc);
  }
  /**
   * Converts the wiki data into a complete HTML string.
   *
   * @return An HTML string representing the wiki document.
   */
  @Override
  public String toString() {
    assert doc != null;
    return doc.outerHtml();
  }

  /**
   * Save the HTML file to the specified destination.
   * @param w the destination to save the HTML file to.
   * @throws java.io.IOException if there is a problem writing to the Writer
   */ 
 
  @Override
  void saveTo(File file) 
  throws java.io.IOException
  {
    assert doc != null;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write(doc.outerHtml());
    } catch (IOException e) {
        throw new java.io.IOException("Trying to write HTML wiki file",e);
    }
  }

  /**
   * Makes a minimal HTML wiki file
   */
  private void makeMinimalWiki() {
    final String document = 
      "<!DOCTYPE html><html>" +
      "  <head></head>" +
      "  <body><new-cards-here></new-cards-here></body>" +
      "</html>";
    doc = Jsoup.parse(document);
  }
    
  /**
   * Loads the wiki file to work from.
   *
   * @param r The reader from which to get the wiki file.
   * @throws java.io.IOException if there is a problem loading from the reader.
   */
  @Override
  void loadFrom(BufferedReader r)
  throws java.io.IOException
  {
    final var w = new StringWriter();
    r.transferTo(w);
    String input = w.toString();
    doc = Jsoup.parse(input);
  }
  
  /**
   * Updates a given card.
   * The old contents will be removed and new contents provided.
   * Only standard fields from the new card will be included.
   * 
   * @param newCard The new card to update with
   * @param oldCard The wiki document "card" node
   *                to be updated.
   */
  private void updateCardInDoc(Card newCard, Element oldCard)
  {
    assert doc != null;
    if (newCard == null)
      throw new IllegalArgumentException("newCard can't be null");
    if (oldCard  == null)
      throw new IllegalArgumentException("oldCard can't be null");

    final var cardData = newCard.getData();
    final Integer id = newCard.getId();

    oldCard.empty();
    final var cardNd = oldCard;

    for (String fname : Card.FIELD_NAMES_OF_INTEREST) {
      final String fdata = cardData.get(fname);
      appendCardField(fname, fdata, cardNd);
    }
  }

  /**
   * Append the specified card field name and data
   * below the specified card DOM element, as the last child.
   * 
   * @param fname The name of the field
   * @param fdata The data contained in the field
   * @param cardNd The card node in the DOM
   */
  private void appendCardField(String fname, String fdata, Element cardNd) {
      if (fdata != null && !fdata.strip().isEmpty()
                        && !fname.equals("Statistics 1")) {
        final var fieldNd = makeAndAppendElement("field",cardNd);
        final var nameNd = makeAndAppendElement("name",fieldNd);
        makeAndAppendText(fname,nameNd);
        makeAndAppendText(" : ",fieldNd);
        final var valueNd = makeAndAppendElement("value",fieldNd);
        makeAndAppendText(fdata,valueNd);
      }
  }
  
  /**
   * Adds the specified card to the document, under the specified parent.
   * Places it as the last sibling.
   *
   * HTML that is present in the flashcard field will be escaped
   * such that it will be <b>visible</b> in a browser, rather <b>functional</b>.
   * This may change in the future.
   *
   * @param c      The card to place.
   * @param parent The parent to place the card under, as the last child.
   */
  private void addCardToDoc(Card c, Element parent)
  {
    assert doc != null;
    if (c == null)
      throw new IllegalArgumentException("Card can't be null");
    if (parent == null)
      throw new IllegalArgumentException("Parent can't be null");
    if (c.getId() !=null && contains(c.getId()))
      throw new IllegalArgumentException("Can't add id '"
        + c.getId() 
        + "' as it is already present.");

    final var cardNd = makeAndAppendElement("card",parent);
    if (c.getId() != null) cardNd.attr("id",c.getId().toString());
    updateCardInDoc(c, cardNd);
  }

  /**
   * Creates the named element and makes it a child of the specified parent,
   * as the last child.
   *
   * @param name   the tag of the element to create
   * @param parent the element to use as the parent for the created element
   * @return the created Element
   */
  private Element makeAndAppendElement( String name, Element parent ) {
    final var element = doc.createElement(name);
    parent.appendChild(element);
    return element;
  }

  /**
   * Creates a text element and makes it a child of the specified parent.
   *
   * @param text   the text
   * @param parent the element to use as the parent for the created element
   */
  private void makeAndAppendText( String text, Element parent ) {
    String[] lines = text.split(System.lineSeparator());

    parent.appendChild(new TextNode(lines[0]));
    for (int i=1; i<lines.length; i++) {
      parent.appendChild(new Element("br"));
      parent.appendChild(new TextNode(lines[i]));
    }
  }

  // see superclass for javadoc
  @Override
  void updateCard(Card c)
  {
    assert doc != null;
    if (c == null) return;

    final Integer id = c.getId();
    if (id == null ) 
      throw new IllegalArgumentException("Can't update from an unIDed card.");
    if( !contains(id)) {
      String msg =
        String.format("Can't update id '%d' as it is not present.",id);
      throw new IllegalArgumentException(msg);
    }

    Element toUpdate = doc.getElementById(id.toString());
    updateCardInDoc(c,toUpdate);
  }
 
  /**
   * Creates a Card from the card at the specified Element in the DOM
   * 
   * @param cardNode the DOM card node from which to create the Card
   * @return the created Card
   */
  private Card asCard(Element cardNode) {
    if (cardNode == null) return null;

    Integer id = null;
    if (!cardNode.attr("id").equals("")) {
        id = Integer.valueOf(cardNode.attr("id"));
    }
    Elements fields = cardNode.select("field");
    final var fieldMap = new HashMap<String,String>();
    for (Element f : fields) {
      String name = null;
      String value = null;
      try {
        name = f.select("name").first().html();
        value = f.select("value").first().html();
      } catch ( NullPointerException e ) {
        throw new IllegalStateException(e);
      }
      fieldMap.put(name,value);
    }
    return new Card(fieldMap,id);      
  }
  
  // see superclass for javadoc
  @Override
  Card getCard(Integer id) {
    assert doc != null;

    final Element cardNode = doc.getElementById(String.valueOf(id));
    return asCard(cardNode);
  }

  /**
   * Adds the specified card to the wiki file.
   * Places it as the last child under the new-cards-here tag.
   *
   * If the card is null, quitely does nothing.
   *
   * @param c The card to add
   */
  @Override
  void addCard(Card c)
  {
    assert doc != null;
    if (c == null) return;

    final Integer id = c.getId();
    if (id != null && contains(id)) {
      String msg =
        String.format("Can't add id '%d' as it is already present.",id);
      throw new IllegalArgumentException(msg);
    }

    Elements nodes = doc.getElementsByTag("new-cards-here");
    if (nodes.size() == 0) {
      throw new IllegalStateException("There is no new-cards-here element");
    } else if (nodes.size() > 1) {
      throw new IllegalStateException("More than one new-card-here element");
    }
    addCardToDoc(c, nodes.get(0));
  }

  // see superclass for javadoc
  @Override
  ArrayList<Integer> getIds()
  {
    assert doc != null;

    final Function<Element,Integer> idFromCardElement =
      e -> e.attr("id") == "" ? null : Integer.valueOf(e.attr("id"));

    return new ArrayList<Integer> (
                 doc.select("card").stream()
                                   .map(idFromCardElement)
                                   .collect(Collectors.toList()));
  }

  // see superclass for javadoc
  @Override
  void zapIds()
  {
    assert doc != null;
    doc.select("card").attr("id",null);
  }

  // see superclass for javadoc
  @Override
  List<Integer> markBlankIds() {

    assert doc != null;

    //
    // find nextId, and cards that need IDing
    //
    final ToIntFunction<Element> idAsInt =
      e -> Integer.valueOf(e.attr("id"));
    final Elements haves = doc.select("card[id]");
    final Elements haveNots = doc.select("card:not([id])");
    final OptionalInt oldMax = haves.stream()
                                    .mapToInt(idAsInt)
                                    .max();

    int nextId = oldMax.isPresent() ? oldMax.getAsInt() + 1 
                                    : Integer.MIN_VALUE;

    //
    // Go through the cards that need IDing, and ID them.
    // Also, collect and return their newly assigned numbers.
    //
    final var it = haveNots.iterator();
    final var collector = new LinkedList<Integer>();
    while (it.hasNext()) {
      collector.add(nextId);
      it.next().attr("id", String.valueOf(nextId++));
    }

    return collector;
  }

  @Override
  List<Card> getCards() {
    assert doc != null;
    // get a list of card tagged elements
    Elements cardElements = doc.select("card");
    if (cardElements.isEmpty()) {
      return new ArrayList<>();
    }
    return cardElements.stream().map(e -> asCard(e)).collect(Collectors.toList());    
  }
  
  private void deleteCard(Integer id) {
      doc.select("card[id="+id+"]").remove();
  }
  
  @Override
  void deleteCards(List<Integer> ids) {
        ids.forEach(i -> java.util.Objects.requireNonNull(i));
        ids.forEach(i -> deleteCard(i));
  }
  
  /**
   * Returns true if same class and same fields
   * @param o the object to test for equality
   * @return true if same class and same fields
   */
  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
      var cast = (WikiData) o;
      return doc.hasSameValue(cast.doc);
  }

  @Override
  /**
   * Returns 0.
   * 
   * This isn't ideal, but underlying classes just use Object's implementation.
   * If we ever really want a real hashCode,
   * we could perhaps do doc.
   * 
   * @return 0
   */
  public int hashCode() {
    return 0;
  }
}
