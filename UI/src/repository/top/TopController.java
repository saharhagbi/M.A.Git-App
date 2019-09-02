package repository.top;

import common.MAGitResourceConstants;
import common.MAGitUtilities;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import repository.RepositoryController;

import java.io.IOException;

public class TopController
{
    private RepositoryController m_RepositoryController;

    @FXML
    private MenuBar m_MenuBar;
    @FXML
    private MenuItem m_SwitchRepoMenuItem;
    @FXML
    private MenuItem m_ExportToXmlMenuItem;
    @FXML
    private MenuItem m_SetUserNameMenuItem;
    @FXML
    private Control m_CommitBtn;


    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    @FXML
    void SwitchRepository_OnClick() throws IOException
    {
        Stage currentStage = MAGitUtilities.GetStage(m_CommitBtn);
        m_RepositoryController.SwitchScenes(MAGitResourceConstants.STARTING_SCENE, currentStage);
    }
}
