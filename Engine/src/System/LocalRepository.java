package System;

import Objects.Commit;
import Objects.branches.Branch;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class LocalRepository extends Repository
{

    public LocalRepository(Path i_RepositoryPath, String i_RepositoryName, Branch i_ActiveBranch)
    {
        super(i_RepositoryPath, i_RepositoryName, i_ActiveBranch);
    }

    public LocalRepository(Branch i_ActiveBranch, Path i_RepositoryPath, String i_RepositoryName, List<Branch> i_AllBranches,
                           Map<String, Commit> i_AllCommitsRepository)
    {
        super(i_ActiveBranch, i_RepositoryPath, i_RepositoryName, i_AllBranches, i_AllCommitsRepository);
    }
}
