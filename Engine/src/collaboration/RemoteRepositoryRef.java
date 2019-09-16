package collaboration;

import java.nio.file.Path;

public class RemoteRepositoryRef
{
    private String m_Name;
    private Path m_RepoPath;

    public String getName()
    {
        return m_Name;
    }

    public Path getRepoPath()
    {
        return m_RepoPath;
    }

    public RemoteRepositoryRef(String i_Name, Path i_RepoPath)
    {
        this.m_Name = i_Name;
        this.m_RepoPath = i_RepoPath;
    }
}
