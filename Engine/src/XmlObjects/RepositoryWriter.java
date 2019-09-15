package XmlObjects;

import Objects.Commit;
import Objects.Folder;
import Objects.Branch;
import System.Engine;
import System.Repository;
import common.MagitFileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static System.Repository.WritingStringInAFile;
import static System.Repository.sf_PathForBranches;
import static XmlObjects.XMLParser.sf_Slash;

public class RepositoryWriter
{
    private final static String sf_AdditionalPathToHEAD = "\\HEAD";

    private Repository m_RepositoryToWrite;
    private List<Commit> m_CommitsThatHaveBeenWritten;

    public RepositoryWriter(Repository i_RepositoryToWrite)
    {
        this.m_RepositoryToWrite = i_RepositoryToWrite;
        this.m_CommitsThatHaveBeenWritten = new ArrayList<Commit>();
    }

    public void WriteRepositoryToFileSystem(String i_NameHeadBranch) throws IOException, ParseException
    {
       MagitFileUtils.CreateDirectory(m_RepositoryToWrite.getRepositoryPath().toString());
        Engine.CreateRepositoryDirectories(Paths.get(m_RepositoryToWrite.getRepositoryPath().toString()));

        for (Branch currentBranch : m_RepositoryToWrite.getAllBranches())
        {
            Commit currentCommit = currentBranch.getPointedCommit();

            if (!m_CommitsThatHaveBeenWritten.contains(currentCommit))
                writeCommitAndAllPrevCommits(currentCommit);

            String pathForWritingBranch = m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches;

            checkIfCurrentBranchIsHeadAndUpdateIfItDoes(currentBranch.getBranchName(), i_NameHeadBranch,
                    pathForWritingBranch);

            MagitFileUtils.WritingFileByPath(pathForWritingBranch + sf_Slash + currentBranch.getBranchName()
                    + Repository.sf_txtExtension, currentCommit.getSHA1());
        }
        Folder.SpanDirectory(m_RepositoryToWrite.getActiveBranch().getPointedCommit().getRootFolder());
    }

    private void checkIfCurrentBranchIsHeadAndUpdateIfItDoes(String i_CurrentBranchName, String i_NameHeadBranch,
                                                             String i_PathForWritingBranch) throws IOException
    {
        if (i_NameHeadBranch.equals(i_CurrentBranchName))
            MagitFileUtils.WritingFileByPath(i_PathForWritingBranch + sf_AdditionalPathToHEAD
                    + Repository.sf_txtExtension, i_NameHeadBranch);
    }

    private void writeCommitAndAllPrevCommits(Commit i_CurrentCommit) throws ParseException, IOException
    {
        Commit prevCommit = i_CurrentCommit.GetPrevCommit();
        if (prevCommit != null)
        /*String SHA1OfPrevCommit = i_CurrentCommit.GetPrevCommit().getSHA1();
        Commit prevCommit = m_MapSHA1ToCommit.get(SHA1OfPrevCommit);*/
        {
            if ((!m_CommitsThatHaveBeenWritten.contains(i_CurrentCommit)))
                writeCommitAndAllPrevCommits(prevCommit);
        }

        Commit secondPrevCommit = i_CurrentCommit.GetSecondPrevCommit();

        if (secondPrevCommit != null)
        /*String SHA1OfPrevCommit = i_CurrentCommit.GetPrevCommit().getSHA1();
        Commit prevCommit = m_MapSHA1ToCommit.get(SHA1OfPrevCommit);*/
        {
            if ((!m_CommitsThatHaveBeenWritten.contains(i_CurrentCommit)))
                writeCommitAndAllPrevCommits(secondPrevCommit);
        }
        putCommitInObjectsFile(i_CurrentCommit);

        m_CommitsThatHaveBeenWritten.add(i_CurrentCommit);
    }

    private void putCommitInObjectsFile(Commit i_CurrentCommit) throws ParseException, IOException
    {
        m_RepositoryToWrite.commitAllObjectsInAFolder(i_CurrentCommit.getRootFolder(), i_CurrentCommit.getUserCreated());

        Path pathForWritingWC = i_CurrentCommit.getRootFolder().WritingFolderAsATextFile();
        m_RepositoryToWrite.zipAndPutInObjectsFolder(pathForWritingWC.toFile(),
                i_CurrentCommit.getRootFolder().getSHA1());

        //putting Commit in objects
        String contentOfCommit = i_CurrentCommit.CreatingContentOfCommit();
        Path pathForWritingCommit = WritingStringInAFile(contentOfCommit, i_CurrentCommit.getSHA1());
        m_RepositoryToWrite.zipAndPutInObjectsFolder(pathForWritingCommit.toFile(), i_CurrentCommit.getSHA1());
    }
}
