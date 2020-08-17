package fdshow;

//import static org.junit.Assert.*;
//import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FDCardTest extends FDFileData
{
    
    @Test
    public void should_makeTheSameCard_when_givenFieldData()
    throws Exception
    {
        final var fields = new FieldNames(new String[] {"f1","f2"});
        final FDCard cRead = new FDCard(new StringReader("a\tb"), fields);
        final var cardData = new HashMap<String, String>();
        cardData.put("f1", "a");
        cardData.put("f2", "b");
        
        final FDCard cFromData = new FDCard(cardData, null, fields);
        assertEquals(cRead, cFromData);        
    }

    @Test
    public void should_makeTheSameCard_when_givenACard()
    throws Exception
    {
        final var fields = new FieldNames(new String[] {"f1","f2"});
        final FDCard cRead = new FDCard(new StringReader("a\tb"),fields);
        final FDCard cCopy = new FDCard(cRead, fields);
        assertEquals(cRead, cCopy);        
    }

    @Test
    public void should_respectEscapedQuotes_when_readingQuotedText()
    throws Exception
    {
        final var fields = new FieldNames(new String[] {"f1","f2"});
        //
        // we are loading into f2 a quoted string containing b""
        // and the two double quotes are an escape sequence representing
        // one double quote.  So --> b"
        //
        final FDCard card = new FDCard(new StringReader("a\t\"b\"\"\""),fields);
        assertEquals("b\"",card.getData().get("f2"));	
     }    
    

    @Test
    public void should_replaceCRLFwithLF_when_readingQuotedText()
    throws Exception
    {
        final var fields = new FieldNames(new String[] {"f1","f2"});
        final FDCard card = new FDCard(new StringReader("a\t\"b\r\n\""),fields);
        assertEquals("b" + System.lineSeparator(),card.getData().get("f2"));	
    }
    
    @Test
    public void writeAndReadShouldNotChangeData()
    throws Exception
    {
        // Load the flashcard file
        // and check correct number loaded
        final String baseName = FDData.NAME;
        final File fcFile = new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length()); // confirm no surprises
        final FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        assertEquals(FDData.RECORDS, fcData.getIds().size());

        // Mark the blank cards in it
        // and check correct number had been blank
        assertEquals(FDData.RECORDS, fcData.markBlankIds().size());

        // Get all IDs and make sure no blanks
        assertEquals(FDData.RECORDS,fcData.getCountOfIds());

        // Remark the blank cards.
        // and check that this doesn't pick up any more previously blank cards.
        assertEquals(0, fcData.markBlankIds().size());

        // Save the cards and reload them
        // Remark and make sure STILL no new blank ones.
        var savedVersion = new ByteArrayOutputStream();
        fcData.saveTo(savedVersion);
        final var fcDataReloaded = new FDFileData();
        fcDataReloaded.loadFrom(
          new BufferedReader(
            new StringReader(
              savedVersion.toString())));
        assertEquals(0, fcData.markBlankIds().size());
    }
  
    @Test
    public void should_useSystemStandardLineBreaks()
    throws java.io.FileNotFoundException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length()); // confirm no surprises

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        Card c = fcData.data.get(0);
        Map<String,String> fields = c.getData();
        String text2 = fields.get("Text 2");
        String target = "The Shirky Principle:"
          + System.lineSeparator()
          + System.lineSeparator()
          + "“Institutions will try to preserve the problem "
          + "to which they are the solution.” — Clay Shirky";
        
        assertEquals(target, text2);
    }
}
