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

/**
 * Maintains consistency between a flashcard program and a wiki.
 *
 * Usage: App flashcardFile wikiFile
 */
public class App 
{
    public static void main( String[] args )
    throws java.io.FileNotFoundException,
           java.io.IOException,
           javax.xml.parsers.ParserConfigurationException,
           javax.xml.transform.TransformerConfigurationException,
           javax.xml.transform.TransformerException,
           org.xml.sax.SAXException
    {
        if(args.length != 2)
          throw new Error("Usage: App flashcardFile wikiFile");

        //
        // Load the flashcard data
        //
        File fcFile=new File(args[0]);
        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );

        //
        // Load the wiki data
        //
        File wFile=new File(args[1]);
        WikiData wData = new WikiData();
        wData.loadFrom(new BufferedReader( new FileReader( wFile ) ) );

        //
        // Add any unmarked flashcards to the wiki file
        // (And mark them in the flashcard file and the wiki file)
        //
        wData.addNewCardsFrom(fcData);

        //
        // Save the flashcard data
        //
        boolean renamed = fcFile.renameTo(new File(args[0] + ".bak"));
        if( !renamed )
          throw new Error("Could not rename flashcard file to backup");
        fcData.saveTo(new FileOutputStream( args[0] ) );

        //
        // Save the wiki data
        //
        renamed = wFile.renameTo(new File(args[1] + ".bak"));
        if( !renamed )
          throw new Error("Could not rename wiki file to backup");
        try {
          wData.saveTo(new FileWriter( wFile ));
        } catch (java.io.IOException e) {
          throw new Error("IO Exception writing wiki file: "+wFile.getName(),e);
        }
    }
}
