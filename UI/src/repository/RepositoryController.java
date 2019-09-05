package repository;

import System.*;
import Objects.Commit;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
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

    private Repository m_CurrentRepository;

    @FXML
    public void initialize()
    {
        m_TopController.SetRepositoryController(this);
        m_CenterController.SetRepositoryController(this);
        m_BottomController.SetRepositoryController(this);
        m_LeftController.SetRepositoryController(this);
        m_RightController.SetRepositoryController(this);
    }

    public Repository GetCurrentRepository()
    {
        return m_CurrentRepository;
    }

    public void initAllComponents()
    {
        m_CurrentRepository = m_MagitController.GetCurrentRepository();
        m_TopController.InitAllComponentsInTop(m_CurrentRepository);
        m_LeftController.InitAllComponentsInLeft();
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

    public String ShowStatus() throws Exception
    {
        return m_MagitController.ShowStatus();
    }

    public Commit GetCurrentCommit()
    {
        return m_CurrentRepository.getActiveBranch().getCurrentCommit();
    }

    public ProgressBar GetProgressBar()
    {
        return m_BottomController.GetProgressBar();
    }

    public Label GetLabelBar()
    {
        return m_BottomController.GetLabelBar();
    }

    public void CreateNewBranch(String i_NewBranch) throws Exception
    {
        m_MagitController.CreateNewBranch(i_NewBranch);
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
}
