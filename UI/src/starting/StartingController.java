package starting;

import common.MAGitResourceConstants;
import common.MAGitUtilities;
import common.StringConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
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
        DirectoryChooser DirChooser = new DirectoryChooser();
        File selectedDir = DirChooser.showDialog(null);

        if (selectedDir == null)
        {
            //TODO:
            // handle in case of cancelling
        }

        try
        {
            String repositoryName = MAGitUtilities.GetString("Enter the name of your repository", "Name",
                    "Repository Name");
            m_MagitController.CreateNewRepositry(repositoryName, selectedDir.toPath());
        } catch (Exception e)
        {
            //Todo:
            // handle exception in UI, pop up window
            // handle in case that repository already exist in location!! (popup window> need to move to repository stage?)
            e.printStackTrace();
        }

        moveToRepositoryScene();
    }

    @FXML
    public void LoadRepositoryFromXML_OnClick() throws Exception
    {
        String pathToXML = MAGitUtilities.GetString("Enter yout path to XML file", "Path:", "XML file");

        m_MagitController.loadRepositoryFromXML(pathToXML);

        moveToRepositoryScene();
    }

    @FXML
    public void LoadExistingRepository_OnClick()
    {
       /* String RepositoryPath = null;
        String RepositoryName = null;*/
        try
        {
            String RepositoryName = MAGitUtilities.GetString("Enter your repository name.", "Name:", "Repository Name");
            String RepositoryPath = MAGitUtilities.GetString("Enter your existing repository path", "Path:", "Repository Path");
            m_MagitController.PullAnExistingRepository(RepositoryName, RepositoryPath);
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

        String userChoice = MAGitUtilities.GetUsetChoice(title, headerText, defaultChoice, UserChoices);

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
