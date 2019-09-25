package repository;

import Objects.Commit;
import Objects.Item;
import System.FolderDifferences;
import System.MergeConflictsAndMergedItems;
import System.Repository;
import collaboration.LocalRepository;
import collaboration.RemoteBranch;
import common.Enums;
import common.MAGitUtils;
import common.constants.ResourceUtils;
import common.constants.StringConstants;
import javafx.collections.ObservableList;
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
import System.ConflictingItems;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RepositoryController {

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
    public void initialize() {
        m_TopController.SetRepositoryController(this);
        m_CenterController.SetRepositoryController(this);
        m_BottomController.SetRepositoryController(this);
        m_LeftController.SetRepositoryController(this);
        m_RightController.SetRepositoryController(this);
    }

    public Repository getCurrentRepository() {
        return m_MagitController.GetCurrentRepository();
    }

    public void initAllComponents() {
//        m_CurrentRepository = m_MagitController.GetCurrentRepository();
        m_TopController.InitAllComponentsInTop();
        m_LeftController.InitAllComponentsInLeft();
        m_RightController.InitAllComponentsInRight();
        m_CenterController.InitAllComponentsInCenter();
    }

    public void SetMagitController(MAGitController i_MagitController) {
        m_MagitController = i_MagitController;
    }

    public void SwitchScenes(String i_PathToStartingScene, Stage i_CurrentStage) throws IOException {
        m_MagitController.SwitchScenes(i_PathToStartingScene, i_CurrentStage);
    }

    public boolean IsFirstCommit() {
        return m_MagitController.IsFirstCommit();
    }

    public void CommitChanges(String i_CommitMessage) throws Exception {
        m_MagitController.CommitChanges(i_CommitMessage);
    }

    public FolderDifferences ShowStatus() throws Exception {
        return m_MagitController.ShowStatus();
    }

    /*public Commit GetCurrentCommit()
    {
        return m_CurrentRepository.getActiveBranch().getPointedCommit();
    }*/
    public ProgressBar GetProgressBar() {
        return m_BottomController.GetProgressBar();
    }

    public Label GetLabelBar() {
        return m_BottomController.GetLabelBar();
    }

    public void CreateNewBranch() throws Exception {
        m_MagitController.CreateNewBranch();
    }

    public void DeleteBranch(String i_BranchNameToErase) throws Exception {
        m_MagitController.DeleteBranch(i_BranchNameToErase);
    }

    public boolean RootFolderChanged() throws Exception {
        return m_MagitController.RootFolderChanged();
    }

    public void CheckOut(String i_BranchName) throws Exception {
        m_MagitController.CheckOut(i_BranchName);
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception {
        m_MagitController.ResetHeadBranch(i_Sha1OfCommit);

        m_RightController.ResetCommitsTree();
    }

    public void ShowDifferencesFiles(FolderDifferences i_FolderDifferences) {
        m_LeftController.ShowDifferencesFiles(i_FolderDifferences);
    }

    public void InitProgress(String i_Label) {
        m_BottomController.InitProgress(i_Label);
    }

    public void UpdateProgress() {
        m_BottomController.UpdateProgress();
    }

    public void NewCommitSelectedOnCenterTableView(Commit i_CommitToShow) {
        m_BottomController.ShowCommitInfo(i_CommitToShow);
    }

    public void showDeltaCommits(Commit i_Commit) {
        FolderDifferences folderDifferences = m_MagitController.ShowDeltaCommits(i_Commit);

        if (folderDifferences != null)
            m_LeftController.ShowDifferencesFiles(folderDifferences);

        else
            m_LeftController.ClearTableView();
    }

    public void UpdateCommitTable() {
        m_CenterController.InitObservCommitList();
        m_CenterController.loadCommitsInTableView();
    }

    public void UpdateCommitTree() {
        m_RightController.ResetCommitsTree();
    }

    public void UpdateCommitDetailsInBotoomAfterNodeClicked(Commit i_Commit) {
        showDeltaCommits(i_Commit);
        m_BottomController.ShowCommitInfo(i_Commit);
    }

    public void Fetch() throws Exception {
        m_MagitController.Fetch();
    }

    public void Pull() throws Exception {
        m_MagitController.Pull();
    }

    public void InformUserMessage(Alert.AlertType i_AlertType, String i_Title, String i_Header, String i_ContextText) {
        MAGitUtils.InformUserPopUpMessage(i_AlertType, i_Title, i_Header, i_ContextText);
    }

    public void SetUser(String newUserName) {
        m_MagitController.SetUser(newUserName);
    }

    public void Push() throws Exception {
        m_MagitController.Push();
    }

    public boolean IsLocalRepository() {
        return m_MagitController.IsLocalRepository();
    }

    public boolean IsHeadBranch(String branchName) {
        return m_MagitController.IsHeadBranch(branchName);
    }


    public void getBranchNameAndCommitSHA1AndCreateBranch() throws Exception {
        String newBranch = MAGitUtils.GetString("Enter the name of the new Branch", "Name", "New Branch");
        String SHA1Commit = MAGitUtils.GetString("Enter the SHA1 of the commit you want the branch will point",
                "SHA1:", "Commit SHA1");

        m_MagitController.CreateNewBranchToSystem(newBranch, SHA1Commit);
        m_TopController.updateBoardAfterCreatingNewBranch(newBranch);
    }

    public void getUserChoiceAndCreateBranch() throws Exception {
        String userChoice = MAGitUtils.GetUserChoice("Create Branch", "Choose which kind of Branch dwould you like to create",
                StringConstants.REMOTE_TRACKING_BRANCH, new String[]{StringConstants.REGULAR_BRANCH, StringConstants.REMOTE_TRACKING_BRANCH});

        Enums.BranchType type = userChoice.equals(StringConstants.REGULAR_BRANCH) ? Enums.BranchType.BRANCH : Enums.BranchType.REMOTE_TRACKING_BRANCH;

        if (type == Enums.BranchType.BRANCH)
            getBranchNameAndCommitSHA1AndCreateBranch();
        else
            createRTBranchInLocalRepository();
    }

    private void createRTBranchInLocalRepository() throws Exception {
        LocalRepository localRepository = (LocalRepository) getCurrentRepository();

        List<String> remoteBranchesNames = localRepository.getRemoteBranches().stream().map(branch -> branch.getBranchName()).collect(Collectors.toList());
        String[] remoteBranches = remoteBranchesNames.toArray(new String[0]);

        String remoteBranchName = MAGitUtils.GetUserChoice("Create Branch", "Choose remote Branch to track after:",
                null, remoteBranches);

        String[] temp = remoteBranchName.split(ResourceUtils.Slash);
        String branchName = temp[1];
        checkIfThereIsNotYetRTB(localRepository, branchName);

        RemoteBranch remoteBranch = localRepository.findRemoteBranchBranchByPredicate(remoteBranch1 -> remoteBranch1.getBranchName().equals(remoteBranchName));
        Commit pointedCommit = remoteBranch.getPointedCommit();

        m_MagitController.createRTB(pointedCommit, branchName);
        m_TopController.updateBoardAfterCreatingNewBranch(branchName);
    }

    private void checkIfThereIsNotYetRTB(LocalRepository localRepository, String branchName) throws Exception
    {
        boolean isExist = localRepository.findRemoteTrackingBranchByPredicate(remoteTrackingBranch ->
                remoteTrackingBranch.getBranchName().equals(branchName)) != null;

        if(isExist)
            throw new Exception("Remote Tracking Branch alreadt exist in that specific remote");
    }

    public void UpdateWindowAfterDeletingBranch(String i_branchNameToErase) {
        m_TopController.UpdateBoardAfterDeletingBranch(i_branchNameToErase);
        UpdateCommitTree();
    }

    public void UpdateWindowTreeAndTable() {
        UpdateCommitTable();
        UpdateCommitTree();
    }

    public void ClearTableView() {
        m_LeftController.ClearTableView();
    }

    public void SetConflictsForMergeInRepository(String i_selectedBranchNameToMerge) throws Exception {
        m_MagitController.SetConflictsForMergeInRepository(i_selectedBranchNameToMerge);
    }

    public boolean IsFastForwardCase() {
        return m_MagitController.IsFastForwardCase();
    }

    public boolean IsPulledAncestorOfPulling() {
        return this.m_MagitController.IsPulledAncestorOfPulling();
    }

    public ConflictingItems getConflictingItemsByName(String conflictingItemName)
    {
        return m_MagitController.getConflictingItemsByName(conflictingItemName);
    }

    public void CreateChosenBlobInWC(String blobText, ConflictingItems currentConflictingItem) throws IOException
    {
        m_MagitController.CreateChosenBlobInWC(blobText, currentConflictingItem);
    }
}
