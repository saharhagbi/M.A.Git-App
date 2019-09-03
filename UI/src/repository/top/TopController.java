package repository.top;

import Objects.Commit;
import common.MAGitResourceConstants;
import common.MAGitUtilities;
import common.StringConstants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    @FXML
    void Commit_OnClick(ActionEvent event)
    {
        Task commitTask = new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                try
                {
                    if (m_RepositoryController.IsFirstCommit())
                    {
                        doCommit();
                    } else
                    {
                        if (m_RepositoryController.ShowStatus().equals(StringConstants.NOTHING_TO_COMMIT_ON))
                        {

                           /* Platform.runLater(()->{

                            });*/
                            MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.INFORMATION, StringConstants.COMMIT, "Can't Execute Commit"
                                    , StringConstants.NOTHING_TO_COMMIT_ON);
                        } else
                        {
                            doCommit();

                        }

                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

                //we arrived here it mean that everything is ok and we need to update all window by new commit

                Commit lastCommit = m_RepositoryController.GetCurrentCommit();

                //m_RepositoryController.UpdateTableColumnAccordingToLastCommit();
                //m_RepositoryController.AddNodeCommitToTree();
                return null;
            }
        };

        // todo: bind task to ui
        Label test = new Label();
        test.textProperty().bind(commitTask.messageProperty());
        new Thread(commitTask).start();
       // commitTask.get
    }

    private void doCommit() throws Exception
    {
        String commitMessage = MAGitUtilities.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT);
        m_RepositoryController.CommitChanges(commitMessage);
    }
}




