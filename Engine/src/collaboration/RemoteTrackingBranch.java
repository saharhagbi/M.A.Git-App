package collaboration;

import Objects.Branch;
import Objects.Commit;

public class RemoteTrackingBranch extends Branch
{
    public RemoteTrackingBranch(String i_BranchName, Commit i_CurrentCommit)
    {
        super(i_BranchName, i_CurrentCommit);
    }
}
