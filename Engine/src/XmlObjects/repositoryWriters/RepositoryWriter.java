package XmlObjects.repositoryWriters;

import Objects.branch.Branch;
import Objects.Commit;
import Objects.Folder;
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
        MakeDirectoriesForRepositories();

        WriteAllBranches(i_NameHeadBranch);

        Folder.SpanDirectory(m_RepositoryToWrite.getActiveBranch().getPointedCommit().getRootFolder());
    }

    public void WriteAllBranches(String i_NameHeadBranch) throws ParseException, IOException
    {
        if(m_RepositoryToWrite.getAllBranches() == null)
            return;

        for (Branch currentBranch : m_RepositoryToWrite.getAllBranches())
        {
            Commit currentCommit = currentBranch.getPointedCommit();

            WriteCommitInFileSystem(currentCommit);

//            String pathForWritingBranch = m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches;

            CheckIfCurrentBranchIsHeadAndUpdateIfItDoes(currentBranch.getBranchName(), i_NameHeadBranch,
                    m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches);

            MagitFileUtils.WritingFileByPath(
                    m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches
                            + sf_Slash + currentBranch.getBranchName()
                    + Repository.sf_txtExtension, currentCommit.getSHA1());
        }
    }

    public void MakeDirectoriesForRepositories()
    {
        MagitFileUtils.CreateDirectory(m_RepositoryToWrite.getRepositoryPath().toString());
        Engine.CreateRepositoryDirectories(Paths.get(m_RepositoryToWrite.getRepositoryPath().toString()));
    }

    public void WriteCommitInFileSystem(Commit currentCommit) throws ParseException, IOException
    {
        if (!m_CommitsThatHaveBeenWritten.contains(currentCommit))
            writeCommitAndAllPrevCommits(currentCommit);
    }

    public void CheckIfCurrentBranchIsHeadAndUpdateIfItDoes(String i_CurrentBranchName, String i_NameHeadBranch,
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
