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

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            //write to console or file
            StreamResult output = new StreamResult(w);

            //write data
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
    var factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();    

    doc = builder.parse(new InputSource(r));
  }
}
