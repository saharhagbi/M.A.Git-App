package repository;

import System.MergeConflictsAndMergedItems;
import Objects.Commit;
import Objects.branch.Branch;
import System.FolderDifferences;
import System.Repository;
import common.MAGitUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import main.MAGitController;
import repository.bottom.BottomController;
import repository.center.CenterController;
import repository.left.LeftController;
import repository.right.RightController;
import repository.top.TopController;

import java.io.IOException;

public class RepositoryController
{

    @FXML
    private GridPane m_Top;
    @FXML
    private TopController m_TopController;
    @FXML
    private AnchorPane m_Bottom;
    @FXML
    private BottomController m_BottomController;
    @FXML
    private SplitPane m_Center;
    @FXML
    private CenterController m_CenterController;
    @FXML
    private AnchorPane m_Right;
    @FXML
    private RightController m_RightController;
    @FXML
    private AnchorPane m_Left;
    @FXML
    private LeftController m_LeftController;
    private MAGitController m_MagitController;

//    private Repository m_CurrentRepository;

    @FXML
    public void initialize()
    {
        m_TopController.SetRepositoryController(this);
        m_CenterController.SetRepositoryController(this);
        m_BottomController.SetRepositoryController(this);
        m_LeftController.SetRepositoryController(this);
        m_RightController.SetRepositoryController(this);
    }

    public Repository getCurrentRepository()
    {
        return m_MagitController.GetCurrentRepository();
    }

    public void initAllComponents()
    {
//        m_CurrentRepository = m_MagitController.GetCurrentRepository();
        m_TopController.InitAllComponentsInTop();
        m_LeftController.InitAllComponentsInLeft();
        m_RightController.InitAllComponentsInRight();
        m_CenterController.InitAllComponentsInCenter();
    }

    public void SetMagitController(MAGitController i_MagitController)
    {
        m_MagitController = i_MagitController;
    }

    public void SwitchScenes(String i_PathToStartingScene, Stage i_CurrentStage) throws IOException
    {
        m_MagitController.SwitchScenes(i_PathToStartingScene, i_CurrentStage);
    }

    public boolean IsFirstCommit()
    {
        return m_MagitController.IsFirstCommit();
    }

    public void CommitChanges(String i_CommitMessage) throws Exception
    {
        m_MagitController.CommitChanges(i_CommitMessage);
    }

    public FolderDifferences ShowStatus() throws Exception
    {
        return m_MagitController.ShowStatus();
    }

    /*public Commit GetCurrentCommit()
    {
        return m_CurrentRepository.getActiveBranch().getPointedCommit();
    }*/
    public ProgressBar GetProgressBar()
    {
        return m_BottomController.GetProgressBar();
    }

    public Label GetLabelBar()
    {
        return m_BottomController.GetLabelBar();
    }

    public void CreateNewBranch(String i_NewBranch, String i_SHA1Commit) throws Exception
    {
        m_MagitController.CreateNewBranch(i_NewBranch, i_SHA1Commit);
    }

    public void DeleteBranch(String i_BranchNameToErase) throws Exception
    {
        m_MagitController.DeleteBranch(i_BranchNameToErase);
    }

    public boolean RootFolderChanged() throws Exception
    {
        return m_MagitController.RootFolderChanged();
    }

    public void CheckOut(String i_BranchName) throws Exception
    {
        m_MagitController.CheckOut(i_BranchName);
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception
    {
        m_MagitController.ResetHeadBranch(i_Sha1OfCommit);

        m_RightController.ResetCommitsTree();
    }

    public void ShowDifferencesFiles(FolderDifferences i_FolderDifferences)
    {
        m_LeftController.ShowDifferencesFiles(i_FolderDifferences);
    }

    public void InitProgress(String i_Label)
    {
        m_BottomController.InitProgress(i_Label);
    }

    public void UpdateProgress()
    {
        m_BottomController.UpdateProgress();
    }

    public void NewCommitSelectedOnCenterTableView(Commit i_CommitToShow)
    {
        m_BottomController.ShowCommitInfo(i_CommitToShow);
    }

    public void showDeltaCommits(Commit i_Commit)
    {
        FolderDifferences folderDifferences = m_MagitController.ShowDeltaCommits(i_Commit);

        if (folderDifferences != null)
            m_LeftController.ShowDifferencesFiles(folderDifferences);

        else
            m_LeftController.ClearTableView();
    }

    public void UpdateTableColumnAccordingToLastCommit()
    {
        Commit newLastCommit = getCurrentRepository().getActiveBranch().getPointedCommit();
        m_CenterController.AddCommitToObservList(newLastCommit);
    }

    public void UpdateCommitTree()
    {
        m_RightController.ResetCommitsTree();
    }

    public void UpdateCommitDetailsInBotoomAfterNodeClicked(Commit i_Commit)
    {
        showDeltaCommits(i_Commit);
        m_BottomController.ShowCommitInfo(i_Commit);
    }

    public void Fetch() throws Exception
    {
        m_MagitController.Fetch();
    }

    public void Pull() throws Exception
    {
        m_MagitController.Pull();
    }

    public void InformUserMessage(Alert.AlertType i_AlertType, String i_Title, String i_Header, String i_ContextText)
    {
        MAGitUtils.InformUserPopUpMessage(i_AlertType, i_Title, i_Header, i_ContextText);
    }

    public void SetUser(String newUserName)
    {
        m_MagitController.SetUser(newUserName);
    }

    public MergeConflictsAndMergedItems GetConflictsForMerge(Branch i_selectedBranchToMerge) throws Exception {
       return m_MagitController.GetConflictsForMerge(i_selectedBranchToMerge);
    }
}
