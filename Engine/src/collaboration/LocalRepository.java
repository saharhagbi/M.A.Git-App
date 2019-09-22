package collaboration;


import Objects.Commit;
import Objects.branch.Branch;
import System.Repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class LocalRepository extends Repository
{
    private List<RemoteTrackingBranch> m_RemoteTrackingBranches;
    private List<RemoteBranch> m_RemoteBranches;
    private RemoteRepositoryRef m_RemoteRepoRef;


    public LocalRepository(Branch i_ActiveBranch, Path i_RepositoryPath, String i_RepositoryName, List<Branch> i_AllBranches, Map<String, Commit> i_AllCommitsRepository,
                           List<RemoteTrackingBranch> i_RemoteTrackingBranches, List<RemoteBranch> i_RemoteBranches, RemoteRepositoryRef i_RemoteRepoRef)
    {
        super(i_ActiveBranch, i_RepositoryPath, i_RepositoryName, i_AllBranches, i_AllCommitsRepository);
        this.m_RemoteTrackingBranches = i_RemoteTrackingBranches;
        this.m_RemoteBranches = i_RemoteBranches;
        this.m_RemoteRepoRef = i_RemoteRepoRef;
    }



    @Override
    public List<Branch> getAllBranches()
    {
        List<Branch> allBranches = new ArrayList<Branch>();

        if (m_Branches != null)
            allBranches.addAll(m_Branches);

        allBranches.addAll(m_RemoteBranches);
        allBranches.addAll(m_RemoteTrackingBranches);
        return allBranches;
    }

   public List<Branch> getRegularBranches()
   {
       return m_Branches;
   }

    public List<RemoteTrackingBranch> getRemoteTrackingBranches()
    {
        return m_RemoteTrackingBranches;
    }

    public List<RemoteBranch> getRemoteBranches()
    {
        return m_RemoteBranches;
    }

    public RemoteRepositoryRef getRemoteRepoRef()
    {
        return m_RemoteRepoRef;
    }

    public void addRemoteBranch(RemoteBranch remoteBranch)
    {
        this.m_RemoteBranches.add(remoteBranch);
    }

    public void FindAndSetActiveBranch(String activeBranchName)
    {
        Predicate<Branch> predicate = branch -> branch.getBranchName().equals(activeBranchName);

        //check if can replace two statements with statement below woth predicate
        m_ActiveBranch = findRemoteTrackingBranchByPredicate(remoteTrackingBranch ->
                remoteTrackingBranch.getBranchName().equals(activeBranchName));

        if (m_ActiveBranch == null)
        {
            m_ActiveBranch = findBranchByPredicate(predicate);
        }
    }

    public RemoteBranch findRemoteBranchBranchByPredicate(Predicate<RemoteBranch> predicate)
    {
        return m_RemoteBranches.stream().filter(branch ->
                predicate.test(branch)).findAny().orElse(null);
    }

    public RemoteTrackingBranch findRemoteTrackingBranchByPredicate(Predicate<RemoteTrackingBranch> predicate)
    {
        return m_RemoteTrackingBranches.stream().filter(remoteTrackingBranch ->
                predicate.test(remoteTrackingBranch)).findAny().orElse(null);
    }
}
