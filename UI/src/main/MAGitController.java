package main;

import XmlObjects.MagitRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import primaryController.PrimaryController;
import repository.RepositoryController;
import starting.StartingController;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

public class MAGitController
{
    private PrimaryController m_PrimaryController;
    private StartingController m_StartingController;
    private RepositoryController m_RepositoryController;

    public MAGitController(StartingController i_StartingController) throws IOException
    {
        this.m_PrimaryController = new PrimaryController(this);
        this.m_StartingController = i_StartingController;

        /*URL repositoryFXML = getClass().getResource(MAGitResourceConstants.REPOSITORY_SCENE);
        FXMLLoader loader = new FXMLLoader(repositoryFXML);

        //init all members in FXMLLoader
        loader.load(repositoryFXML.openStream());

        m_RepositoryController = loader.getController();
        m_RepositoryController.SetAllControllers(this);*/
    }

    public void CreateNewRepositry(String i_RepoName, Path i_RepoPath) throws Exception
    {
        m_PrimaryController.CreateNewRepository(i_RepoName, i_RepoPath);
    }

    public void loadRepositoryFromXML(String i_PathToXML) throws Exception
    {
        m_PrimaryController.loadRepositoryFromXML(i_PathToXML);
    }

    public void handleCurrentRepositoryAlreadyExist(MagitRepository i_XmlRepository)
    {
        m_StartingController.HandleCurrentRepositoryAlreadyExist();
    }

    public void ExecuteUserChoice(String i_SelectedItem) throws Exception
    {
        m_PrimaryController.ExecuteUserChoice(i_SelectedItem);
    }

    public void PullAnExistingRepository(String i_RepositoryName, String i_RepositoryPath) throws Exception
    {
        m_PrimaryController.PullAnExistingRepository(i_RepositoryName, i_RepositoryPath);
    }

    public void SwitchScenes(String i_PathToSceneFXML, Stage i_CurrentStage) throws IOException
    {
        URL startingFXML = getClass().getResource(i_PathToSceneFXML);
        FXMLLoader loader = new FXMLLoader(startingFXML);
        Parent root = loader.load();

        setMagitController(loader);

        Scene scene = new Scene(root);
        i_CurrentStage.setScene(scene);
        i_CurrentStage.show();
    }

    private void setMagitController(FXMLLoader i_Loader)
    {
        Object Controller;
        Controller = i_Loader.getController();

        if(Controller.getClass() == RepositoryController.class)
        {
            RepositoryController repositoryController = (RepositoryController)Controller;
            repositoryController.SetMagitController(this);
        }
        else
        {
            StartingController startingController = (StartingController) Controller;
            startingController.SetMagitController(this);
        }
    }
}
