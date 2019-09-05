package repository.left;

import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import repository.RepositoryController;

public class LeftController
{
    private RepositoryController m_RepositoryController;
    @FXML
    private TreeView m_FilesTreeView;

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public void InitAllComponentsInLeft()
    {
        TreeItem<String> dummyRoot = new TreeItem<>();
        TreeItem<String> newFiles = new TreeItem<>("New Files");
        TreeItem<String> deletedFiles = new TreeItem<>("Deleted Files");
        TreeItem<String> changedFiles = new TreeItem<>("Changed Files");

        m_FilesTreeView.setRoot(dummyRoot);
        dummyRoot.getChildren().addAll(newFiles, deletedFiles, changedFiles);

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
}
