package fdshow;

//import static org.junit.Assert.*;
//import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Map;

public class FDFileDataTest 
{
    @Test
    public void getCardTest()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        var newIds = fcData.markBlankIds();
        assertEquals(Integer.MIN_VALUE,newIds.get(0));

        Card c = fcData.getCard(Integer.MIN_VALUE);
        assertTrue(c != null);

        Map<String,String> fields = c.getData();
        String text2 = fields.get("Text 2");
        String target = "The Shirky Principle:"
          + System.lineSeparator()
          + System.lineSeparator()
          + "“Institutions will try to preserve the problem "
          + "to which they are the solution.” — Clay Shirky";
        assertEquals(target, text2);
    }

    @Test
    public void markBlankIdsTest()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        var newIds = fcData.markBlankIds();
        assertEquals(FDData.RECORDS,newIds.size());
        assertEquals(Integer.MIN_VALUE+0,newIds.get(0).intValue());
        assertEquals(Integer.MIN_VALUE+1,newIds.get(1).intValue());
        assertEquals(Integer.MIN_VALUE+5,newIds.get(5).intValue());
    }
    
    @Test
    public void should_writeAndReadUnchanged_when_freshlyRead()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        
        var outStream = new java.io.ByteArrayOutputStream();
        fcData.saveTo(outStream);
        FDFileData fcData2 = new FDFileData();
        fcData2.loadFrom(
                new BufferedReader(
                        new StringReader(
                                outStream.toString())));
        assertEquals(
                new Card(fcData.getCards().get(0)),
                new Card(fcData2.getCards().get(0)));
    }
 
    @Test
    public void should_writeAndReadUnchanged_after_SetAndGetIds()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        FDFileData fcData1 = new FDFileData();
        fcData1.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        
        fcData1.markBlankIds();        
        var outStream = new java.io.ByteArrayOutputStream();
        fcData1.saveTo(outStream);
        FDFileData fcData2 = new FDFileData();
        fcData2.loadFrom(
                new BufferedReader(
                        new StringReader(
                                outStream.toString())));
        //
        // Should not have changed!
        //
        final Card c1 = new Card(fcData1.getCards().get(0));
        final Card c2 = new Card(fcData2.getCards().get(0));
        assertEquals(c1,c2);
    }
    
    @Test
    public void readAndWriteMakesNoChange()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = FDData.NAME;
        File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        
        File outFile=new File(baseName+".test");
        if(outFile.exists())
          outFile.delete();
        fcData.saveTo(new FileOutputStream(outFile));
        assertEquals(FDData.SIZE, outFile.length());

        var file1Contents = Files.readAllBytes(fcFile.toPath());
        var file2Contents = Files.readAllBytes(outFile.toPath());
        assertArrayEquals(file1Contents,file2Contents);

        if(outFile.exists())
          outFile.delete();
    }

    @Test
    public void zapIdsReallyZaps()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        final String baseName = FDData.NAME;
        final File fcFile=new File(baseName);
        assertEquals(FDData.SIZE, fcFile.length());

        final FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        final var newIds1 = fcData.markBlankIds();
        fcData.zapIds();
        final var newIds2 = fcData.markBlankIds();
        assertEquals(newIds1,newIds2);
    }
}
