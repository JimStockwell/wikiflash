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
import java.util.Objects;
import java.util.function.ToIntFunction;
import java.util.function.Function;
import java.util.OptionalInt;
import java.util.stream.Collectors;

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
 */
class WikiData extends CardsHolder {

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

  WikiData(final WikiData other) {
      doc = other.doc.clone();
      assert doc.hasSameValue(other.doc);
  }

  /**
   * Makes a minimal HTML wiki file.
   */
  private void makeMinimalWiki() {
    final String document =
        "<!DOCTYPE html><html>"
      + "  <head></head>"
      + "  <body><new-cards-here></new-cards-here></body>"
      + "</html>";
    doc = Jsoup.parse(document);
  }

  /**
   * Loads the wiki file to work from.
   *
   * @param r The reader from which to get the wiki file.
   * @throws java.io.IOException if there is a problem loading from the reader.
   */
  @Override
  void loadFrom(final BufferedReader r)
  throws java.io.IOException {
    final var w = new StringWriter();
    r.transferTo(w);
    String input = w.toString();
    doc = Jsoup.parse(input);
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
   * @param file the destination to save the HTML file to.
   * @throws java.io.IOException if there is a problem writing to the Writer
   */
  @Override
  void saveTo(final File file)
  throws java.io.IOException {
    assert doc != null;
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        writer.write(doc.outerHtml());
    } catch (IOException e) {
        throw new java.io.IOException("Trying to write HTML wiki file", e);
    }
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
  private void updateCardInDoc(final Card newCard, final Element oldCard) {
    assert doc != null;
    if (newCard == null || oldCard == null) {
      throw new IllegalArgumentException("Arguments can't be null");
    }

    final var cardData = newCard.getData();

    oldCard.empty();

    for (String fname : Card.FIELD_NAMES_OF_INTEREST) {
      final String fdata = cardData.get(fname);
      appendCardField(fname, fdata, oldCard);
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
  private void appendCardField(
          final String fname,
          final String fdata,
          final Element cardNd) {
      if (fdata != null && !fdata.strip().isEmpty()
                        && !fname.equals("Statistics 1")) {
        final var fieldNd = makeAndAppendElement("field", cardNd);
        final var nameNd = makeAndAppendElement("name", fieldNd);
        final var valueNd = makeAndAppendElement("value", fieldNd);
        makeAndAppendText(fname, nameNd);
        makeAndAppendText(" : ", fieldNd);
        makeAndAppendText(fdata, valueNd);
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
  private void addCardToDoc(final Card c, final Element parent) {
    assert doc != null;
    if (c == null) {
      throw new IllegalArgumentException("Card can't be null");
    }
    if (parent == null) {
      throw new IllegalArgumentException("Parent can't be null");
    }
    if (c.getId() != null && contains(c.getId())) {
      throw new IllegalArgumentException("Can't add id '"
        + c.getId()
        + "' as it is already present.");
    }

    final var cardNd = makeAndAppendElement("card", parent);
    if (c.getId() != null) {
        cardNd.attr("id", c.getId().toString());
    }
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
  private Element makeAndAppendElement(
          final String name,
          final Element parent) {
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
  private void makeAndAppendText(final String text, final Element parent) {
    String[] lines = text.split(System.lineSeparator());

    parent.appendChild(new TextNode(lines[0]));
    for (int i = 1; i < lines.length; i++) {
      parent.appendChild(new Element("br"));
      parent.appendChild(new TextNode(lines[i]));
    }
  }

  // see superclass for javadoc
  @Override
  void updateCard(final Card c) {
    assert doc != null;
    if (c == null) {
        return;
    }

    final Integer id = c.getId();
    if (id == null) {
      throw new IllegalArgumentException("Can't update from an unIDed card.");
    }
    if (!contains(id)) {
      String msg =
        String.format("Can't update id '%d' as it is not present.", id);
      throw new IllegalArgumentException(msg);
    }

    Element toUpdate = doc.getElementById(id.toString());
    updateCardInDoc(c, toUpdate);
  }

  /**
   * Creates a Card from the card at the specified Element in the DOM.
   *
   * @param cardNode the DOM card node from which to create the Card
   * @return the created Card
   */
  private Card asCard(final Element cardNode) {
    if (cardNode == null) {
        return null;
    }

    Integer id = null;
    if (!cardNode.attr("id").equals("")) {
        id = Integer.valueOf(cardNode.attr("id"));
    }
    Elements fields = cardNode.select("field");
    final var fieldMap = new HashMap<String, String>();
    for (Element f : fields) {
      String name = null;
      String value = null;
      try {
        name = f.select("name").first().html();
        value = f.select("value").first().html();
      } catch (NullPointerException e) { // turn into a clearer exception
        throw new IllegalStateException(e);
      }
      fieldMap.put(name, value);
    }
    return new Card(fieldMap, id);
  }

  // see superclass for javadoc
  @Override
  Card getCard(final Integer id) {
    assert doc != null;
    Objects.requireNonNull(id, "id must not be null");

    final Element cardNode = doc.getElementById(String.valueOf(id));
    return asCard(cardNode);
  }

  /**
   * Adds the specified card to the wiki file.
   *
   * Places it as the last child under the new-cards-here tag.
   * If the ID is null, then the card added will have no ID.
   *
   * If the card is null, quietly does nothing.
   *
   * @param c The card to add
   */
  @Override
  void addCard(final Card c) {
    assert doc != null;

    if (c == null) {
        return;
    }

    final Integer id = c.getId();
    if (id != null && contains(id)) {
      String msg =
        String.format("Can't add id '%d' as it is already present.", id);
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
  ArrayList<Integer> getIds() {
    assert doc != null;

    final Function<Element, Integer> idFromCardElement =
      e -> e.attr("id").equals("") ? null : Integer.valueOf(e.attr("id"));

    return new ArrayList<Integer>(
                 doc.select("card").stream()
                                   .map(idFromCardElement)
                                   .collect(Collectors.toList()));
  }

  // see superclass for javadoc
  @Override
  void zapIds() {
    assert doc != null;
    doc.select("card").attr("id", null);
  }

  // see superclass for javadoc
  @Override
  List<Integer> markBlankIds() {

    assert doc != null;

    //
    // find "nextId", the Id from which to start numbering cards with blank ID
    //
    final ToIntFunction<Element> idAsInt =
        element -> {
            try {
                return Integer.valueOf(element.attr("id"));
            } catch (NumberFormatException nfe) {
                throw new RuntimeException(
                        "Illegal card id '" + element.attr("id") + "'", nfe);
            }
        };

    final OptionalInt oldMax = doc.select("card[id]")
                                  .stream()
                                  .mapToInt(idAsInt)
                                  .max();

    int nextId = oldMax.isPresent() ? oldMax.getAsInt() + 1
                                    : Integer.MIN_VALUE;

    //
    // Go through the cards that need IDing, and ID them.
    // Also, collect and return their newly assigned numbers.
    //
    final Elements haveNots = doc.select("card:not([id])");
    final var it = haveNots.iterator();
    final var collector = new LinkedList<Integer>();
    while (it.hasNext()) {
      collector.add(nextId);
      it.next().attr("id", String.valueOf(nextId++));
    }

    //
    // return the IDs that have been added
    //
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
    return cardElements.stream()
                       .map(e -> asCard(e))
                       .collect(Collectors.toList());
  }

  private void deleteCard(final Integer id) {
      doc.select("card[id=" + id + "]").remove();
  }

  @Override
  void deleteCards(final List<Integer> ids) {
        ids.forEach(i -> java.util.Objects.requireNonNull(i));
        ids.forEach(i -> deleteCard(i));
  }

  /**
   * Returns true if same class and same fields.
   * @param o the object to test for equality
   * @return true if same class and same fields
   */
  @Override
  public boolean equals(final Object o) {
      if (this == o) {
          return true;
      }
      if (o == null || getClass() != o.getClass()) {
          return false;
      }
      var cast = (WikiData) o;
      return doc.hasSameValue(cast.doc);
  }

  /**
   * Returns 0.
   *
   * This isn't ideal,
   * but the underlying classes just use Object's implementation.
   * We don't want that because then WikiData objects of equal value
   * will be considered different when hashCode is checked first.
   *
   * @return 0
   */
  @Override
  public int hashCode() {
    return 0;
  }
}
