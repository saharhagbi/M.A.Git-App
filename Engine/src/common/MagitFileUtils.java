package common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MagitFileUtils
{
    public static void OverwriteContentInFile(String i_Content, String i_PathOfFile) throws IOException
    {
        File fileToWrite = new File(i_PathOfFile);

        FileWriter fileWriter = new FileWriter(fileToWrite, false);

        fileWriter.write(i_Content);
        fileWriter.close();
    }
}
