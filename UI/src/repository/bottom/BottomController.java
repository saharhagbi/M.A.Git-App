package repository.bottom;

import Objects.Commit;
import Objects.Folder;
import Objects.Item;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import repository.RepositoryController;

public class BottomController
{
    private RepositoryController m_RepositoryController;

    @FXML
    private ProgressBar m_ProgressBar;
    @FXML
    private Label m_ProgressLabel;
    @FXML
    private Tab m_InfoTab;
    @FXML
    private Tab m_FileTreeTab;
    @FXML
    private GridPane m_InfoGridPane;
    @FXML
    private TreeView m_FileTreeView;


    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public ProgressBar GetProgressBar()
    {
        return m_ProgressBar;
    }

    public Label GetLabelBar()
    {
        return m_ProgressLabel;
    }

    public void InitProgress(String i_Label)
    {
        m_ProgressBar.setProgress(-1);
        m_ProgressLabel.setText(i_Label);
    }

    public void UpdateProgress()
    {
        m_ProgressBar.setProgress(1);
        m_ProgressLabel.setText("Progress");

    }

    public void ShowCommitInfo(Commit i_CommitToShow)
    {
        clearOldInfo();

        showCommitDetails(i_CommitToShow);

        createFileTree(i_CommitToShow.getRootFolder());
    }

    private void clearOldInfo()
    {
        m_InfoGridPane
                .getChildren()
                .removeIf(node ->
                        node.getClass() == Text.class);
    }

    private void showCommitDetails(Commit i_CommitToShow)
    {
        m_InfoGridPane.add(new Text(i_CommitToShow.getCommitMessage()), 1, 0);
        m_InfoGridPane.add(new Text(i_CommitToShow.getSHA1()), 1, 1);
        m_InfoGridPane.add(new Text(i_CommitToShow.getUserCreated().getUserName()), 1, 2);
        m_InfoGridPane.add(new Text(Item.getDateStringByFormat(i_CommitToShow.GetDate())), 1, 3);
        setPrevCommitSHA1InTableView(i_CommitToShow.GetPrevCommit(), 4);
        setPrevCommitSHA1InTableView(i_CommitToShow.GetSecondPrevCommit(), 5);
    }

    private void setPrevCommitSHA1InTableView(Commit i_CommitPrevCommit, int indexRow)
    {
        if (i_CommitPrevCommit != null)
            m_InfoGridPane.add(new Text(i_CommitPrevCommit.getSHA1()), 1, indexRow);
    }

    private void createFileTree(Folder i_Folder)
    {
        String fileName = m_RepositoryController.getCurrentRepository().getRepositoryPath().getFileName().toString();

        TreeItem<String> rootFolder = new TreeItem<String>(fileName);
        m_FileTreeView.setRoot(rootFolder);

        createFileTreeHelper(rootFolder, i_Folder);
    }

    private void createFileTreeHelper(TreeItem<String> i_RootFolder, Folder i_Folder)
    {
        for (Item item : i_Folder.getListOfItems())
        {
            if (item.getTypeOfFile() == Item.TypeOfFile.BLOB)
            {
                i_RootFolder.getChildren().add(new TreeItem<String>(item.getName()));
            } else//is folder
            {
                TreeItem<String> folderItem = new TreeItem<>(item.getName());
                Folder innerFolder = (Folder) item;
                createFileTreeHelper(folderItem, innerFolder);
                i_RootFolder.getChildren().add(folderItem);
            }
        }
    }
}
