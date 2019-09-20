package collaboration;

import Objects.Commit;
import Objects.Folder;
import Objects.branch.Branch;
import Objects.branch.BranchUtils;
import System.Engine;
import XmlObjects.repositoryWriters.RepositoryWriter;
import common.constants.ResourceUtils;

import java.io.IOException;
import java.text.ParseException;

public class Push
{
    private Fetch m_Fetcher;
    private Engine m_Engine;
    private LocalRepository m_LocalRepository;
    private Branch m_BranchToPushToInRepo;

    public Push(Engine engine, LocalRepository localRepository) throws Exception
    {
        m_LocalRepository = localRepository;
        m_Engine = engine;
        m_Fetcher = new Fetch(engine, localRepository);
    }


    public boolean isPossibleToPush() throws Exception
    {
        if (m_Engine.ShowStatus() != null)
            return false;

        //check if active branch is RTB
        if (!BranchUtils.IsRemoteTrackingBranch(m_LocalRepository.getActiveBranch()))
            return false;

        //headbranch in local is RTB
        RemoteTrackingBranch remoteTrackingBranchInLocal = (RemoteTrackingBranch) m_LocalRepository.getActiveBranch();
        String remoteBranchNameExpected = m_LocalRepository.getRemoteRepoRef().getName() + ResourceUtils.Slash +
                remoteTrackingBranchInLocal.getBranchName();

        RemoteBranch remoteBranchOfRTB = m_LocalRepository.findRemoteBranchBranchByPredicate(
                remoteBranch ->
                        remoteBranch.getBranchName().equals(remoteBranchNameExpected)
        );


        m_BranchToPushToInRepo = m_Fetcher.getRemoteRepositoryToFetchFrom().findBranchByPredicate(branch ->
                branch.getBranchName().equals(remoteTrackingBranchInLocal.getBranchName()));

        return remoteBranchOfRTB.getPointedCommit().AreTheCommitsTheSame(m_BranchToPushToInRepo.getPointedCommit());
    }

    public void Push() throws IOException, ParseException
    {
        RepositoryWriter repositoryWriter = new RepositoryWriter(m_Fetcher.getRemoteRepositoryToFetchFrom());


        //in function isPossibleToPush we check that headBranch Is rtb
        m_BranchToPushToInRepo.setPointedCommit(m_LocalRepository.getActiveBranch().getPointedCommit());

        repositoryWriter.WriteBranch(m_BranchToPushToInRepo);

        if (m_BranchToPushToInRepo.AreTheSameBranches(m_LocalRepository.getActiveBranch()))
        {
            Commit commitOfHeadBranchInRepo = m_BranchToPushToInRepo.getPointedCommit();

            Folder.RemoveFilesAndFoldersWithoutMagit(commitOfHeadBranchInRepo.getRootFolder().GetPath());
            Folder.SpanDirectory(m_BranchToPushToInRepo.getPointedCommit().getRootFolder());
        }
    }
}
