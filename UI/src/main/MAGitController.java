package main;

import Objects.Commit;
import System.Repository;
import System.FolderDifferences;

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

    public void SwitchScenes(String i_PathToFXMLScene, Stage i_PrimaryStage) throws IOException
    {
        URL fxml = getClass().getResource(i_PathToFXMLScene);
        FXMLLoader loader = new FXMLLoader(fxml);
        Parent root = loader.load(fxml.openStream());

        boolean isRepositoryScene = setMagitController(loader);

        Scene scene = new Scene(root);

        i_PrimaryStage.setScene(scene);

        setComponentsIfRepositoryScene(i_PrimaryStage, isRepositoryScene);

        i_PrimaryStage.show();

    }

    private void setComponentsIfRepositoryScene(Stage i_PrimaryStage, boolean isRepositoryScene)
    {
        if (isRepositoryScene)
            m_RepositoryController.initAllComponents();
    }

    private boolean setMagitController(FXMLLoader i_Loader)
    {
        Object Controller;
        Controller = i_Loader.getController();

        if (Controller.getClass() == RepositoryController.class)
        {
            RepositoryController repositoryController = (RepositoryController) Controller;
            repositoryController.SetMagitController(this);
            m_RepositoryController = repositoryController;

            return true;

        } else
        {
            StartingController startingController = (StartingController) Controller;
            startingController.SetMagitController(this);
            m_StartingController = startingController;

            return false;
        }
    }

    public boolean IsFirstCommit()
    {
        return m_PrimaryController.IsFirstCommit();
    }

    public void CommitChanges(String i_CommitMessage) throws Exception
    {
        m_PrimaryController.CommitChanges(i_CommitMessage);
    }

    public FolderDifferences ShowStatus() throws Exception
    {
        return m_PrimaryController.ShowStatus();
    }

    public Repository GetCurrentRepository()
    {
        return m_PrimaryController.GetCurrentRepository();
    }

    public void CreateNewBranch(String i_NewBranch, String i_SHA1Commit) throws Exception
    {
        m_PrimaryController.CreateNewBranch(i_NewBranch, i_SHA1Commit);
    }

    public void DeleteBranch(String i_BranchNameToErase) throws Exception
    {
        m_PrimaryController.DeleteBranch(i_BranchNameToErase);
    }

    public boolean RootFolderChanged() throws Exception
    {
        return m_PrimaryController.RootFolderChanged();
    }

    public void CheckOut(String i_BranchName) throws Exception
    {
        m_PrimaryController.CheckOut(i_BranchName);
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception
    {
        m_PrimaryController.ResetHeadBranch(i_Sha1OfCommit);
    }

    public FolderDifferences ShowDeltaCommits(Commit i_Commit)
    {
        return m_PrimaryController.ShowDeltaCommits(i_Commit);
    }
}
