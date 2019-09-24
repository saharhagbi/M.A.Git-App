package starting;

import common.MAGitResourceConstants;
import common.MAGitUtils;
import common.constants.StringConstants;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import main.MAGitController;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

//import java.awt.*;

public class StartingController
{
    @FXML
    private Button m_NewRepositoryBtn;
    @FXML
    private Button m_LoadExistingRepositoryBtn;
    @FXML
    private Button m_LoadRepoFromXMLBtn;
    @FXML
    private Button m_CloneBtn;

    private MAGitController m_MagitController;
    @FXML
    private ProgressBar m_ProgressBar;
    @FXML
    private Label m_WelcomeLabel;

    @FXML
    public void CreateNewRepositry_OnClick()
    {
        //check if repository already exist, and if not

        try
        {
            File selectedDir = MAGitUtils.GetDirectory(MAGitUtils.GetStage(m_NewRepositoryBtn), "Select the location of your new Repository");
            String repositoryName = MAGitUtils.GetString("Enter the name of your repository", "Name",
                    "Repository Name");
            m_MagitController.CreateNewRepositry(repositoryName, selectedDir.toPath());
            moveToRepositoryScene();
        } catch (Exception exception)
        {
            //Todo:
            // handle exception in UI, pop up window
            // handle in case that repository already exist in location!! (popup window> need to move to repository stage?)
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", null, exception.getMessage());
        }
    }

    @FXML
    public void LoadRepositoryFromXML_OnClick() throws Exception
    {
        File selectedFile = MAGitUtils.GetFile(MAGitUtils.GetStage(m_LoadRepoFromXMLBtn));

        m_LoadExistingRepositoryBtn.getParent().setVisible(false);
      m_ProgressBar.setVisible(true);
        /*  m_ProgressBar.setProgress();
*/
        Task loadTask = new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                updateMessage("Loading...");


                m_MagitController.loadRepositoryFromXML(selectedFile.getAbsolutePath());

                updateProgress(1, 1);
                return null;
            }
        };
      //  m_WelcomeLabel.textProperty().bind(loadTask.messageProperty());
        bindTaskComponentsToUI(m_WelcomeLabel, m_ProgressBar, loadTask);
        new Thread(loadTask).start();
        loadTask.setOnSucceeded(event -> moveToRepositoryScene());
        // moveToRepositoryScene();
    }

    private void bindTaskComponentsToUI(Label i_LabelBar, ProgressBar i_ProgressBar, Task i_CommitTask)
    {
        i_LabelBar.textProperty().bind(i_CommitTask.messageProperty());
        i_ProgressBar.progressProperty().bind(i_CommitTask.progressProperty());
    }

    @FXML
    public void LoadExistingRepository_OnClick()
    {
        try
        {
            String RepositoryName = MAGitUtils.GetString("Enter your repository name.", "Name:", "Repository Name");
            File selecredDir = MAGitUtils.GetDirectory(MAGitUtils.GetStage(m_LoadExistingRepositoryBtn), "Select the repository folder");
            //String RepositoryPath = MAGitUtils.GetString("Enter your existing repository path", "Path:", "Repository Path");
            m_MagitController.PullAnExistingRepository(RepositoryName, selecredDir.getAbsolutePath());
        } catch (Exception exception)
        {
            //TODO:
            // handling exception by proper message to user
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", null, exception.getMessage());
        }

        moveToRepositoryScene();
    }

    private void moveToRepositoryScene()
    {
        Stage currentStage = MAGitUtils.GetStage(m_LoadExistingRepositoryBtn);

        try
        {

            m_MagitController.SwitchScenes(MAGitResourceConstants.REPOSITORY_SCENE, currentStage);
        } catch (IOException e)
        {
            //TODO:
            // handling exception by proper message to user
            e.printStackTrace();
        }
    }

    public void HandleCurrentRepositoryAlreadyExist() throws ExecutionException, InterruptedException
    {
        String headerText = String.format("Repository already exist in this location!"
                + System.lineSeparator()
                + "choose the repository you want to continue with:" + System.lineSeparator());

        String[] UserChoices = new String[]{StringConstants.XML_REPOSITORY,
                StringConstants.EXISTING_REPOSITORY};

        String title = String.format("Select A Repository");

        String defaultChoice = String.format(StringConstants.XML_REPOSITORY);

        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtils.GetUserChoice(title, headerText, defaultChoice, UserChoices)
        );

        Platform.runLater(futureTask);


        String userChoice = futureTask.get();

        try
        {
            m_MagitController.ExecuteUserChoice(userChoice);
        } catch (Exception e)
        {
            //TODO:
            // Handling proper message
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize()
    {
        try
        {
            m_MagitController = new MAGitController(this);
        } catch (IOException e)
        {
            //todo:
            // handle exception
            e.printStackTrace();
        }
    }

    public void SetMagitController(MAGitController i_MaGitController)
    {
        m_MagitController = i_MaGitController;
    }

    @FXML
    void Clone_OnClick(ActionEvent event)
    {

        try
        {
            File dirOfRepo = MAGitUtils.GetDirectory(MAGitUtils.GetStage(m_CloneBtn), "Select the folder of the repository you want to clone from: ");
            File dirToClone = MAGitUtils.GetDirectory(MAGitUtils.GetStage(m_CloneBtn), "Select the target folder: ");
            String repositoryName = MAGitUtils.GetString("Enter your repository name.", "Name:", "Repository Name");
            m_MagitController.Clone(dirToClone, repositoryName, dirOfRepo);

            moveToRepositoryScene();
        } catch (Exception e)
        {
            //todo:
            // handle proper message UI
            e.printStackTrace();
        }
    }


}
