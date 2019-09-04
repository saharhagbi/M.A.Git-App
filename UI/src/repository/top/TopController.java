package repository.top;

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
import java.util.concurrent.FutureTask;

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
                    updateMessage("Commit...");
                    if (m_RepositoryController.IsFirstCommit())
                    {
                        doCommit();
                    } else
                    {
                        if (m_RepositoryController.ShowStatus().equals(StringConstants.NOTHING_TO_COMMIT_ON))
                        {
                            Platform.runLater(() ->
                            {
                                MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.INFORMATION, StringConstants.COMMIT, "Can't Execute Commit"
                                        , StringConstants.NOTHING_TO_COMMIT_ON);
                            });
                        } else
                        {
                            doCommit();
                        }
                    }
                } catch (Exception commitException)
                {
                    //TODO:
                    // handle proper message if commit failed(look at exceptions from ui)
                    commitException.printStackTrace();
                }

                //we arrived here it mean that everything is ok and we need to update all window by new commit

                return null;
            }
        };

        // todo: bind task to ui
        bindCommitTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), commitTask);
        new Thread(commitTask).start();

      /*  commitTask.
                setOnSucceeded(() ->
                        m_RepositoryController.UpdateTableColumnAccordingToLastCommit(),
                m_RepositoryController.AddNodeCommitToTree());*/

        //function above if needed to execute to one function
    }

   /* private void onFinishCommit()
    {
        m_RepositoryController.UpdateTableColumnAccordingToLastCommit();
        m_RepositoryController.AddNodeCommitToTree();
    }*/


    //check if needed to do it in generic method in repository controller
    private void bindCommitTaskComponentsToUI(Label i_LabelBar, ProgressBar i_ProgressBar, Task i_CommitTask)
    {
        i_LabelBar.textProperty().bind(i_CommitTask.messageProperty());
        i_ProgressBar.progressProperty().bind(i_CommitTask.progressProperty());
    }

    private void doCommit() throws Exception
    {
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtilities.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT
                ));

        Platform.runLater(futureTask);

        String commitMessage = futureTask.get();

        //commitMessage = MAGitUtilities.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT);
        m_RepositoryController.CommitChanges(commitMessage);
    }
}




