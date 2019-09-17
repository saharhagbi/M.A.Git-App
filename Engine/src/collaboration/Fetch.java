package collaboration;

import Objects.Branch;
import Objects.Commit;
import System.Engine;
import System.Repository;
import common.constants.NumConstants;

import java.util.List;
import java.util.Map;

public class Fetch
{
    private Engine m_Engine;
    private LocalRepository m_CurrentLocalRepository;
    private Map<String, Commit> m_AllCommitsInLocal;

    public Fetch(Engine i_Engine, LocalRepository i_CurrentLocalRepository)
    {
        m_Engine = i_Engine;
        m_CurrentLocalRepository = i_CurrentLocalRepository;
        m_AllCommitsInLocal = m_CurrentLocalRepository.getAllCommitsSHA1ToCommit();
    }

    public void Fetch() throws Exception
    {
        RemoteRepositoryRef remoteRepositoryRef = m_CurrentLocalRepository.getRemoteRepoRef();

        m_Engine.PullAnExistingRepository(remoteRepositoryRef.getRepoPath().toString(),
                remoteRepositoryRef.getName());

        Repository repository = m_Engine.getCurrentRepository();

        fetchAllObjects(repository);
    }

    private void fetchAllObjects(Repository i_Repository)
    {
        for (Branch branch : i_Repository.getAllBranches())
        {
            if (!isBranchExistInLocal(branch))
            {
                createCommitsInLocalAndConcatThem(branch.getPointedCommit());
                m_CurrentLocalRepository.addRemoteBranch(new RemoteBranch(branch.getBranchName(), branch.getPointedCommit()));
            }
            else
            {
                RemoteBranch remoteBranch = findRemoteBranch(m_CurrentLocalRepository.getRemoteBranches(), branch);

                //if the pointed commits are not the same
                if (!areTheCommitsTheSame(remoteBranch.getPointedCommit(), branch.getPointedCommit()))
                {
                    createCommitsInLocalAndConcatThem(branch.getPointedCommit());

                    remoteBranch.setPointedCommit(branch.getPointedCommit());
                }
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
        return i_RemoteBranches
                .stream()
                .filter(remoteBranch -> remoteBranch.getBranchName().equals(i_Branch.getBranchName()))
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
