package Objects.branch;

import collaboration.RemoteTrackingBranch;

public class BranchUtils
{
    public final static String RTB_STYLE = "#ff2121";
    public final static String RB_STYLE = "##1e90ff";


    public static boolean IsRemoteTrackingBranch(Branch branch)
    {
        return branch.getClass() == RemoteTrackingBranch.class;
    }
}
