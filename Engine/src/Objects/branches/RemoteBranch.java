package Objects.branches;

import Objects.Commit;

public class RemoteBranch
{
    protected Commit m_PointedCommit;

    public RemoteBranch(Commit m_PointedCommit)
    {
        this.m_PointedCommit = m_PointedCommit;
    }

    public Commit getPointedCommit()
    {
        return m_PointedCommit;
    }

    public void setPointedCommit(Commit m_PointedCommit)
    {
        this.m_PointedCommit = m_PointedCommit;
    }
}
