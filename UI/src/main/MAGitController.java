package main;

import Objects.Commit;
import System.ConflictingItems;
import System.FolderDifferences;
import System.Repository;
import XmlObjects.MagitRepository;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import primaryController.PrimaryController;
import repository.RepositoryController;
import starting.StartingController;
import java.io.File;
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

    public void handleCurrentRepositoryAlreadyExist(MagitRepository i_XmlRepository) throws Exception
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
        BorderPane root = loader.load(fxml.openStream());

        boolean isRepositoryScene = setMagitController(loader);

        Scene scene = new Scene(root);


        i_PrimaryStage.setScene(scene);
        i_PrimaryStage.setHeight(root.getPrefHeight());
        i_PrimaryStage.setWidth(root.getPrefWidth());
        i_PrimaryStage.setMinWidth(root.getPrefWidth());
        i_PrimaryStage.setMinHeight(root.getPrefHeight() + 50);

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

    public void CreateNewBranch() throws Exception
    {
        m_PrimaryController.CreateNewBranch();
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

    public void Clone(File i_DirToClone, String i_RepositoryName, File i_DirOfRepo) throws Exception
    {
        m_PrimaryController.Clone(i_DirToClone, i_RepositoryName, i_DirOfRepo);
    }

    public void Fetch() throws Exception
    {
        m_PrimaryController.Fetch();
    }

    public void Pull() throws Exception
    {
        m_PrimaryController.Pull();
    }


    public void InformUserMessage(Alert.AlertType i_AlertType, String i_Title, String i_Header, String i_ContextText)
    {
        m_RepositoryController.InformUserMessage(i_AlertType, i_Title, i_Header, i_ContextText);
    }

    public void SetUser(String newUserName)
    {
        m_PrimaryController.SetUser(newUserName);
    }

    public void Push() throws Exception
    {
        m_PrimaryController.Push();
    }

    public boolean IsLocalRepository()
    {
        return m_PrimaryController.IsLocalRepository();
    }

    public boolean IsHeadBranch(String branchName)
    {
        return m_PrimaryController.IsHeadBranch(branchName);
    }

    public void getBranchNameAndCommitSHA1() throws Exception
    {
        m_RepositoryController.getBranchNameAndCommitSHA1AndCreateBranch();
    }

    public void CreateNewBranchToSystem(String newBranch, String sha1Commit) throws Exception
    {
        m_PrimaryController.CreateNewBranchToSystem(newBranch, sha1Commit);
    }

    public void getUserChoiceAndCreateBranch() throws Exception
    {
        m_RepositoryController.getUserChoiceAndCreateBranch();
    }

    public void createRTB(Commit commit, String branchName) throws IOException
    {
        m_PrimaryController.CreateRTB(commit, branchName);
    }

    public void UpdateWindowAfterDeletingBranch(String i_branchNameToErase)
    {
        m_RepositoryController.UpdateWindowAfterDeletingBranch(i_branchNameToErase);
    }

    public void UpdateWindowTreeAndTable()
    {
        m_RepositoryController.UpdateWindowTreeAndTable();
    }

    public void UpdateCommitTree()
    {
        m_RepositoryController.UpdateCommitTree();
    }

    public void SetConflictsForMergeInRepository(String i_selectedBranchNameToMerge) throws Exception
    {
        this.m_PrimaryController.SetConflictsForMergeInRepository(i_selectedBranchNameToMerge);
    }

    public boolean IsFastForwardCase()
    {
        return m_PrimaryController.IsFastForwardCase();
    }

    public boolean IsPulledAncestorOfPulling()
    {
        return this.m_PrimaryController.IsPulledAncestorOfPulling();
    }

    public ConflictingItems getConflictingItemsByName(String conflictingItemName)
    {
        return m_PrimaryController.getConflictingItemsByName(conflictingItemName);
    }

    public void CreateChosenBlobInWC(String blobText, ConflictingItems currentConflictingItem) throws IOException
    {
        m_PrimaryController.CreateChosenBlobInWC(blobText, currentConflictingItem);
    }
}
