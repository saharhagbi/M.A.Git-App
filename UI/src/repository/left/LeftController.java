package repository.left;

import Objects.Item;
import System.FolderDifferences;
import common.NumConstants;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import repository.RepositoryController;

import java.util.List;

public class LeftController
{
    @FXML
    TreeView<String> m_FilesTreeView;

    TreeItem<String> m_NewFiles;
    TreeItem<String> m_DeletedFiles;
    TreeItem<String> m_ChangedFiles;
    private RepositoryController m_RepositoryController;

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public void InitAllComponentsInLeft()
    {
        TreeItem<String> dummyRoot = new TreeItem<>();
        m_NewFiles = new TreeItem<>("New Files");
        m_DeletedFiles = new TreeItem<>("Deleted Files");
        m_ChangedFiles = new TreeItem<>("Changed Files");

        m_FilesTreeView.setRoot(dummyRoot);
        dummyRoot.getChildren().addAll(m_NewFiles, m_DeletedFiles, m_ChangedFiles);
        /*m_FilesTreeView.setRoot(m_NewFiles);
        m_FilesTreeView.setRoot(m_ChangedFiles);
        m_FilesTreeView.setRoot(m_DeletedFiles);
*/

        m_FilesTreeView.setShowRoot(false);
        /*

        m_FilesTreeView.setRoot(newFiles);
        newFiles.getChildren().addAll(nodeA, nodeB, nodeC);


        TreeItem<String> nodeA1 = new TreeItem<>("Node A");
        TreeItem<String> nodeB1= new TreeItem<>("Node B");
        TreeItem<String> nodeC1 = new TreeItem<>("Node C");

        m_FilesTreeView.setRoot(deletedFiles);
        deletedFiles.getChildren().addAll(nodeA1, nodeB1, nodeC1);

        TreeItem<String> nodeA2 = new TreeItem<>("Node A");
        TreeItem<String> nodeB2 = new TreeItem<>("Node B");
        TreeItem<String> nodeC2 = new TreeItem<>("Node C");

        m_FilesTreeView.setRoot(changedFiles);
        changedFiles.getChildren().addAll(nodeA2, nodeB2, nodeC2);*/
    }

    public void ShowDifferencesFiles(FolderDifferences i_FolderDifferences)
    {
        if (i_FolderDifferences.GetAddedItemList().size() != NumConstants.ZERO)
            addListToTreeItem(i_FolderDifferences.GetAddedItemList(), m_NewFiles);

        if (i_FolderDifferences.GetChangedItemList().size() != NumConstants.ZERO)
            addListToTreeItem(i_FolderDifferences.GetChangedItemList(), m_ChangedFiles);

        if (i_FolderDifferences.GetRemovedItemList().size() != NumConstants.ZERO)
            addListToTreeItem(i_FolderDifferences.GetRemovedItemList(), m_DeletedFiles);
    }

    private void addListToTreeItem(List<Item> i_ListToAdd, TreeItem<String> i_TreeItemRoot)
    {
        i_ListToAdd
                .stream()
                .map(item ->
                        item.GetPath().toString())
                .forEach(pathString ->
                        i_TreeItemRoot.getChildren().add(new TreeItem<>(pathString))
                );
    }
}
