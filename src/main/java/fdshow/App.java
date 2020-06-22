package fdshow;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;

/**
 * Maintains consistency between a flashcard program and a wiki.
 *
 * Usage: App flashcardFile wikiFile
 */
public class App 
{
    public static void main( String[] args )
    throws java.io.FileNotFoundException
    {
        FDFileData fileData = new FDFileData();
        fileData.loadFrom(new BufferedReader( new FileReader("All.txt") ) );
        fileData.saveTo(new FileOutputStream("AllRewrite.txt") );
    }
}
