package collaboration;

import Objects.Branch;
import Objects.Commit;

public class RemoteBranch extends Branch
{
    public RemoteBranch(String i_BranchName, Commit i_CurrentCommit)
    {
        super(i_BranchName, i_CurrentCommit);
    }
}
