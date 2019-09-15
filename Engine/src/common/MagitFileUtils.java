package common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MagitFileUtils
{
    public static void OverwriteContentInFile(String i_Content, String i_PathOfFile) throws IOException
    {
        File fileToWrite = new File(i_PathOfFile);

        FileWriter fileWriter = new FileWriter(fileToWrite, false);

        fileWriter.write(i_Content);
        fileWriter.close();
    }

    public static void CreateDirectory(String i_PathToMakeDir)
    {
        File tempFileForMakingDir = new File(i_PathToMakeDir);

        if (!tempFileForMakingDir.exists())
            tempFileForMakingDir.mkdir();
    }

    public static void WritingFileByPath(String i_PathForWriting, String i_ContentTWrite) throws IOException
    {
        File newFileToWrite = new File(i_PathForWriting);

        Path fixedPathFile = Paths.get(i_PathForWriting);

        if (!newFileToWrite.exists())
            Files.createFile(fixedPathFile);

        org.apache.commons.io.FileUtils.writeStringToFile(fixedPathFile.toFile(), i_ContentTWrite, "UTF-8");
    }

    public static boolean IsMagitFolder(File file)
    {
        return file.getName().equals(".magit");
    }

}
