package fdshow;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;

public class App 
{
    public static void main( String[] args )
    throws java.io.FileNotFoundException
    {
        FDFileData fileData = new FDFileData();
        fileData.loadFrom(new BufferedReader(new FileReader("/Users/jimstockwell/Downloads/All.txt")));
        fileData.saveTo(new FileOutputStream("/Users/jimstockwell/fdshow/AllRewrite.txt"));
    }
}
