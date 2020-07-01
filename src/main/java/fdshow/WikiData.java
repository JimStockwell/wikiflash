package fdshow;

//
// fdshow classes that this class knows about:
//    CardsHolder
//    Card
//
import java.io.Writer;
import java.io.Reader;
import java.util.Map;
import java.util.LinkedList;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Extends CardsHolder to allow reading and writing the held cards
 * to a Wiki like format.  (Just a text file for now.)
 *
 * Uses CardsHolder's "data" to read and write Cards to.
 */
class WikiData extends CardsHolder {

  Document doc;

  WikiData() {
    data = new LinkedList<Card>();
  }

  private static String duplicateQuotes(String s) {
    return s.replaceAll("\"","\"\"");
  }

  private static void write(Writer w, Card c)
  throws java.io.IOException {
    final var cardData = c.getData();

    final String[] fieldNamesOfInterest = new String[] {
      "Text 1", "Text 2", "Text 3", "Text 4", "Text 5",
      "Picture 1", "Picture 2",
      "Sound 2", "Category 1", "Statistics 1",
      "Notes", "Extra Info"};

    w.append("<FlashCard>"+System.lineSeparator());
    for (String fname : fieldNamesOfInterest) {
      final String fdata = cardData.get(fname);
      w.append("\"").append(duplicateQuotes(fdata)).append("\"\n");
    }
    w.append("</FlashCard>"+System.lineSeparator());
  }

  public String toString() {
    final var writer = new StringWriter();
    try {
      saveTo(writer);
    } catch (Exception e) {
      throw new Error(e);
    }
    return writer.toString();
  }

  /**
   * Save the wiki file to the destination 'w'.
   */
  void saveTo(Writer w) 
  throws java.io.IOException,
         javax.xml.transform.TransformerConfigurationException,
         javax.xml.transform.TransformerException
  {
/*
    var it = data.iterator();
    while (it.hasNext()) {
      var card = it.next();
      write(w,card);
    }
*/

    final TransformerFactory transformerFactory =
      TransformerFactory.newInstance();
    final Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    final DOMSource source = new DOMSource(doc);
    final StreamResult output = new StreamResult(w);
    transformer.transform(source, output);


  }

  /**
   * Does nothing for now.  Actually updating a wiki file isn't implemented yet.
   */
  void loadFrom(Reader r)
    throws  javax.xml.parsers.ParserConfigurationException,
            org.xml.sax.SAXException,
            java.io.IOException
    {
    final var factory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder builder = factory.newDocumentBuilder();    

    doc = builder.parse(new InputSource(r));
  }

  private void addCardToDoc(Card c, Node parent) {

    /*
     * <card>
     *   <field><name>Text 1</name> : <value>Question</value></field>
     *   <field><name>Text 2</name> : <value>Answer</value></field>
     * </card>
     *
     */
    final var cardData = c.getData();
    final String[] fieldNamesOfInterest = new String[] {
      "Text 1", "Text 2", "Text 3", "Text 4", "Text 5",
      "Picture 1", "Picture 2",
      "Sound 2", "Category 1", "Statistics 1",
      "Notes", "Extra Info"};
    var cardNd = makeAndAppendElement("card",parent);
    for (String fname : fieldNamesOfInterest) {
      final String fdata = cardData.get(fname);
      if (fdata != null) {
        var fieldNd = makeAndAppendElement("field",cardNd);
        var nameNd = makeAndAppendElement("name",fieldNd);
        makeAndAppendText(fname,nameNd);
        makeAndAppendText(" : ",fieldNd);
        var valueNd = makeAndAppendElement("value",fieldNd);
        makeAndAppendText(fdata,valueNd);
      }
    }
  }

  private Node makeAndAppendElement( String name, Node parent ) {
    final var element = doc.createElement(name);
    parent.appendChild(element);
    return element;
  }
  private Node makeAndAppendText( String name, Node parent ) {
    final var element = doc.createTextNode(name);
    parent.appendChild(element);
    return element;
  }


  void addCard(Card c)
  {
    if(c.getId()==null) {
      NodeList nodes = doc.getElementsByTagName("new-cards-here");
      if (nodes.getLength() == 0) {
        throw new IllegalStateException("There is no new-cards-here element");
      } else if (nodes.getLength() > 1) {
        throw new IllegalStateException("More than one new-card-here element");
      }
      addCardToDoc(c, nodes.item(0));
//      var textNode = doc.createTextNode(c.toString());
//      nodes.item(0).appendChild(textNode);
    } else {
      throw new Error("TODO: confirm card isn't already present");
    }
  }
}
