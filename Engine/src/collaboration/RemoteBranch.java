package collaboration;

import Objects.Branch;
import Objects.Commit;

import java.io.File;

public class RemoteBranch extends Branch
{
    public RemoteBranch(String i_BranchName, Commit i_CurrentCommit)
    {
        super(i_BranchName, i_CurrentCommit);
    }

    public static RemoteBranch createRemoteBranchFromBranch(Branch i_Branch, String i_CloneFromRepoName)
    {
        String remoteBranchName = i_CloneFromRepoName + File.separator + i_Branch.getBranchName();

        return new RemoteBranch(remoteBranchName, i_Branch.getPointedCommit());
    }
}
