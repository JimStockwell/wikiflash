package fdshow;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Map;



public class FDCardTest extends FDFileData
{
    @Test
    public void shouldUseSystemStandardLineBreaks()
    throws java.io.FileNotFoundException
    {
        String baseName = "src/test/resources/All.txt";
        File fcFile=new File(baseName);
        assertEquals(2706L, fcFile.length()); // confirm no surprises

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        Card c = fcData.data.get(0);
        Map<String,String> fields = c.getData();
        String text2 = fields.get("Text 2");

        String targetRaw = "So whether you eat or drink or whatever you do," +
          " do it all for the glory of God.%n%n" + 
          "Some translation might say this: " +
          "Good - and make a card to nail imperfections%n" +
          "Otherwise: Bad";
        String target = String.format(targetRaw);
        
        assertEquals(target, text2);
    }
}
