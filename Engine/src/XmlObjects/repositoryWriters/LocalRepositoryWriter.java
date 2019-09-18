package XmlObjects.repositoryWriters;

import Objects.Commit;
import Objects.Folder;
import collaboration.LocalRepository;
import collaboration.RemoteBranch;
import collaboration.RemoteTrackingBranch;
import common.MagitFileUtils;
import common.constants.ResourceUtils;
import common.constants.StringConstants;

import java.io.IOException;
import java.text.ParseException;

import static System.Repository.sf_PathForBranches;
import static System.Repository.sf_txtExtension;
import static XmlObjects.XMLParser.sf_Slash;

public class LocalRepositoryWriter
{
    private LocalRepository m_RepositoryToWrite;
    private RepositoryWriter m_Writer;


    public LocalRepositoryWriter(LocalRepository i_Repository)
    {
        m_RepositoryToWrite = i_Repository;
        m_Writer = new RepositoryWriter(i_Repository);
    }

    public void WriteRepositoryToFileSystem(String i_BranchName) throws IOException, ParseException
    {
        m_Writer.MakeDirectoriesForRepositories();
        m_Writer.WriteAllBranches(i_BranchName);

        WriteAllRemoteTrackingBranches(i_BranchName);

        String pathForWritingRB = m_RepositoryToWrite.getRepositoryPath().toString()
                + sf_PathForBranches
                + sf_Slash + m_RepositoryToWrite.getRemoteRepoRef().getName();

        MagitFileUtils.CreateDirectory(pathForWritingRB);

        WriteAllRemoteBranches();

        //writing repository details

        String remoteRepositoryDetailsToWrite = m_RepositoryToWrite.getRemoteRepoRef().getName() + System.lineSeparator()
                + m_RepositoryToWrite.getRemoteRepoRef().getRepoPath();

        MagitFileUtils.WritingFileByPath(m_RepositoryToWrite.getRepositoryPath() +
                        ResourceUtils.AdditinalPathMagit + ResourceUtils.Slash + StringConstants.REPOSITORY_DETAILS + sf_txtExtension,
                remoteRepositoryDetailsToWrite);

        Folder.SpanDirectory(m_RepositoryToWrite.getActiveBranch().getPointedCommit().getRootFolder());
    }

    public void WriteAllRemoteBranches() throws IOException, ParseException
    {
        for (RemoteBranch remoteBranch : m_RepositoryToWrite.getRemoteBranches())
        {
            Commit currentCommit = remoteBranch.getPointedCommit();

            m_Writer.WriteCommitInFileSystem(currentCommit);

            MagitFileUtils.WritingFileByPath(m_RepositoryToWrite.getBranchesFolderPath()
                    + sf_Slash + remoteBranch.getBranchName() + sf_txtExtension, currentCommit.getSHA1());
        }
    }

    private void WriteAllRemoteTrackingBranches(String i_BranchName) throws IOException, ParseException
    {
        if (m_RepositoryToWrite.getRemoteTrackingBranches() == null)
            return;

        for (RemoteTrackingBranch remoteTrackingBranch : m_RepositoryToWrite.getRemoteTrackingBranches())
        {
            Commit currentCommit = remoteTrackingBranch.getPointedCommit();

            m_Writer.WriteCommitInFileSystem(currentCommit);

            m_Writer.CheckIfCurrentBranchIsHeadAndUpdateIfItDoes(remoteTrackingBranch.getBranchName(), i_BranchName,
                    m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches);

            MagitFileUtils.WritingFileByPath(
                    m_RepositoryToWrite.getRepositoryPath().toString() + sf_PathForBranches
                            + sf_Slash + remoteTrackingBranch.getBranchName()
                            + sf_txtExtension,
                    currentCommit.getSHA1() + System.lineSeparator() + StringConstants.TRUE);
        }
    }
}
