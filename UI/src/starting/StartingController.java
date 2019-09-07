package starting;

import common.MAGitResourceConstants;
import common.MAGitUtilities;
import common.StringConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import main.MAGitController;

import java.io.File;
import java.io.IOException;

//import java.awt.*;

public class StartingController
{
    @FXML
    private Button m_NewRepositoryBtn;
    @FXML
    private Button m_LoadExistingRepositoryBtn;
    @FXML
    private Button m_LoadRepoFromXMLBtn;

    private MAGitController m_MagitController;


    @FXML
    public void CreateNewRepositry_OnClick()
    {
        File selectedDir = MAGitUtilities.GetDirectory(MAGitUtilities.GetStage(m_NewRepositoryBtn));
        //check if repository already exist, and if not

        try
        {
            String repositoryName = MAGitUtilities.GetString("Enter the name of your repository", "Name",
                    "Repository Name");
            m_MagitController.CreateNewRepositry(repositoryName, selectedDir.toPath());
            moveToRepositoryScene();
        } catch (Exception e)
        {
            //Todo:
            // handle exception in UI, pop up window
            // handle in case that repository already exist in location!! (popup window> need to move to repository stage?)
            MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", "Repository already exist",
                    "repository already exist in this location please choose another option");
            e.printStackTrace();
        }


    }

    @FXML
    public void LoadRepositoryFromXML_OnClick() throws Exception
    {

        File selectedFile = MAGitUtilities.GetFile(MAGitUtilities.GetStage(m_LoadRepoFromXMLBtn));
        //String pathToXML = MAGitUtilities.GetString("Enter yout path to XML file", "Path:", "XML file");

        m_MagitController.loadRepositoryFromXML(selectedFile.getAbsolutePath());

        moveToRepositoryScene();
    }

    @FXML
    public void LoadExistingRepository_OnClick()
    {
        try
        {
            String RepositoryName = MAGitUtilities.GetString("Enter your repository name.", "Name:", "Repository Name");
            File selecredDir = MAGitUtilities.GetDirectory(MAGitUtilities.GetStage(m_LoadExistingRepositoryBtn));
            //String RepositoryPath = MAGitUtilities.GetString("Enter your existing repository path", "Path:", "Repository Path");
            m_MagitController.PullAnExistingRepository(RepositoryName, selecredDir.getAbsolutePath());
        } catch (Exception e)
        {
            //TODO:
            // handling exception by proper message to user
            e.printStackTrace();
        }

        moveToRepositoryScene();
    }

    private void moveToRepositoryScene()
    {
        Stage currentStage = MAGitUtilities.GetStage(m_LoadExistingRepositoryBtn);

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

    public void HandleCurrentRepositoryAlreadyExist()
    {
        String headerText = String.format("Repository already exist in this location!"
                + System.lineSeparator()
                + "choose the repository you want to continue with:" + System.lineSeparator());

        String[] UserChoices = new String[]{StringConstants.XML_REPOSITORY,
                StringConstants.EXISTING_REPOSITORY};

        String title = String.format("Select A Repository");

        String defaultChoice = String.format(StringConstants.XML_REPOSITORY);

        String userChoice = MAGitUtilities.GetUserChoice(title, headerText, defaultChoice, UserChoices);

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
            e.printStackTrace();
        }
    }

    public void SetMagitController(MAGitController i_MaGitController)
    {
        m_MagitController = i_MaGitController;
    }
}
