package repository.right;

import Objects.Commit;
import javafx.scene.control.TreeTableView;
import repository.RepositoryController;
import repository.right.node.CommitNode;

public class RightController
{
    private RepositoryController m_RepositoryController;
//    private m_

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public void InitAllComponentsInRight()
    {
//        if (m_RepositoryController.GetCurrentCommit() != null)
            createCommitTree();
    }

    private void createCommitTree()
    {

    }
}
