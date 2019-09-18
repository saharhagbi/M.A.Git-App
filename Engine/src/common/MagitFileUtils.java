package common;

import common.constants.ResourceUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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

        FileUtils.writeStringToFile(fixedPathFile.toFile(), i_ContentTWrite, "UTF-8");
    }

    public static boolean IsMagitFolder(File file)
    {
        return file.getName().equals(".magit");
    }

    public static boolean IsFolderExist(Path i_BranchFolderPath)
    {
        File[] branches = getFilesInLocation(i_BranchFolderPath.toString());

        return Arrays.stream(branches).anyMatch(file ->
                file.isDirectory());
    }

    public static boolean IsRemoteRepositoryExistInLocation(String location)
    {
        //Path locationPath = Paths.get(location);
        File[] WC = getFilesInLocation(location);

        if (magitFileExist(WC))
        {
            location += ResourceUtils.AdditinalPathMagit;
            return IsFolderExist(Paths.get(location));
        }
        return false;
    }

    public static File[] getFilesInLocation(String location)
    {
        File repositoryFile = new File(location);
        return repositoryFile.listFiles();
    }

    private static boolean magitFileExist(File[] wc)
    {
        return Arrays.stream(wc).anyMatch(file ->
                IsMagitFolder(file));
    }
}
