package fdshow;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.FileReader;
import java.io.FileOutputStream;

public class FDFileDataTest 
{
    @Test
    public void shouldAnswerWithTrue()
    throws java.io.FileNotFoundException, java.io.IOException
    {
        String baseName = "src/test/resources/All.txt";
        File fcFile=new File(baseName);
        assertEquals(fcFile.length(), 2706L);

        FDFileData fcData = new FDFileData();
        fcData.loadFrom(new BufferedReader( new FileReader( fcFile ) ) );
        
        File outFile=new File(baseName+".test");
        if(outFile.exists())
          outFile.delete();
        fcData.saveTo(new FileOutputStream(outFile));
        assertEquals(outFile.length(), 2706L);

        var file1Contents = Files.readAllBytes(fcFile.toPath());
        var file2Contents = Files.readAllBytes(outFile.toPath());
        assertArrayEquals(file1Contents,file2Contents);

        if(outFile.exists())
          outFile.delete();
    }
}
