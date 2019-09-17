package collaboration;

import Objects.branch.Branch;
import Objects.Commit;
import System.Engine;
import System.Repository;
import XmlObjects.repositoryWriters.LocalRepositoryWriter;
import common.constants.NumConstants;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public class Fetch
{
    private Engine m_Engine;
    private LocalRepository m_CurrentLocalRepository;
    private Map<String, Commit> m_AllCommitsInLocal;
    private Repository m_RemoteRepositoryToFetchFrom;

    public Fetch(Engine i_Engine, LocalRepository i_CurrentLocalRepository) throws Exception
    {
        m_Engine = i_Engine;
        m_CurrentLocalRepository = i_CurrentLocalRepository;
        m_AllCommitsInLocal = m_CurrentLocalRepository.getAllCommitsSHA1ToCommit();
    /*}

    public void Fetch() throws Exception
    {*/
        RemoteRepositoryRef remoteRepositoryRef = m_CurrentLocalRepository.getRemoteRepoRef();

        m_Engine.PullAnExistingRepository(remoteRepositoryRef.getRepoPath().toString(),
                remoteRepositoryRef.getName());

        m_RemoteRepositoryToFetchFrom = m_Engine.getCurrentRepository();

//        fetchAllObjects(repository);
    }

    public Repository getRemoteRepositoryToFetchFrom()
    {
        return m_RemoteRepositoryToFetchFrom;
    }

    public void FetchAllObjects() throws IOException, ParseException
    {
        for (Branch branch : m_RemoteRepositoryToFetchFrom.getAllBranches())
        {
            FetchBranch(branch);
        }
        LocalRepositoryWriter writer = new LocalRepositoryWriter(m_CurrentLocalRepository);
        writer.WriteAllRemoteBranches();
    }

    public void FetchBranch(Branch i_Branch)
    {
        if (!isBranchExistInLocal(i_Branch))
        {
            createCommitsInLocalAndConcatThem(i_Branch.getPointedCommit());
            m_CurrentLocalRepository.addRemoteBranch(new RemoteBranch(i_Branch.getBranchName(), i_Branch.getPointedCommit()));
        } else
        {
            RemoteBranch remoteBranch = findRemoteBranch(m_CurrentLocalRepository.getRemoteBranches(), i_Branch);

            //if the pointed commits are not the same
            if (!areTheCommitsTheSame(remoteBranch.getPointedCommit(), i_Branch.getPointedCommit()))
            {
                createCommitsInLocalAndConcatThem(i_Branch.getPointedCommit());

                remoteBranch.setPointedCommit(i_Branch.getPointedCommit());
            }
        }
    }

    private void createCommitsInLocalAndConcatThem(Commit branchCommit)
    {
       /* if (areTheCommitsTheSame(remoteBranchCommit, branchCommit))
            return;*/

        if (branchCommit.ThereIsPrevCommit(NumConstants.ONE))
        {
            if (!m_AllCommitsInLocal.containsValue(branchCommit.GetPrevCommit()))
                createCommitsInLocalAndConcatThem(branchCommit.GetPrevCommit());

            Commit newCommitToLocal = new Commit(branchCommit);
            m_AllCommitsInLocal.put(newCommitToLocal.getSHA1(), newCommitToLocal);
        }

        if (branchCommit.ThereIsPrevCommit(NumConstants.TWO))
        {
            if (!m_AllCommitsInLocal.containsValue(branchCommit.GetSecondPrevCommit()))
                createCommitsInLocalAndConcatThem(branchCommit.GetSecondPrevCommit());

            Commit newCommitToLocal = new Commit(branchCommit);
            m_AllCommitsInLocal.put(newCommitToLocal.getSHA1(), newCommitToLocal);
        }
    }

    private boolean areTheCommitsTheSame(Commit remoteBranchCommit, Commit branchCommit)
    {
        return branchCommit.getSHA1().equals(remoteBranchCommit.getSHA1());
    }

    private RemoteBranch findRemoteBranch(List<RemoteBranch> i_RemoteBranches, Branch i_Branch)
    {
        String remoteBranchName = m_CurrentLocalRepository.getRemoteRepoRef().getName() + "/" + i_Branch;

        return i_RemoteBranches
                .stream()
                .filter(remoteBranch -> remoteBranch.getBranchName().equals(remoteBranchName))
                .findAny()
                .orElse(null);
    }

    private boolean isBranchExistInLocal(Branch i_Branch)
    {
        return m_CurrentLocalRepository.getRemoteBranches()
                .stream()
                .anyMatch(remoteBranch ->
                        remoteBranch.getBranchName().equals(i_Branch.getBranchName()));
    }
}
