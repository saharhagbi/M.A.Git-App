package collaboration;


import Objects.Branch;
import Objects.Commit;
import System.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

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

    public LocalRepository(Branch i_ActiveBranch, Path i_RepositoryPath, String i_RepositoryName, List<Branch> i_AllBranches, Map<String, Commit> i_AllCommitsRepository)
    {
        super(i_ActiveBranch, i_RepositoryPath, i_RepositoryName, i_AllBranches, i_AllCommitsRepository);
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
}
