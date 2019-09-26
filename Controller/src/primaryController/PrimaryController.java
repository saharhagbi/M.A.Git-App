package primaryController;

import Objects.Blob;
import Objects.Commit;
import System.ConflictingItems;
import System.Engine;
import System.FolderDifferences;
import System.Repository;
import XmlObjects.XMLMain;
import collaboration.LocalRepository;
import collaboration.Push;
import collaboration.RemoteBranch;
import common.constants.NumConstants;
import common.constants.StringConstants;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import main.MAGitController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PrimaryController
{
    private Engine m_Engine;
    private MAGitController m_MagitController;
    private XMLMain m_XMLMain = new XMLMain();

    public PrimaryController(MAGitController i_MagitController)
    {
        this.m_MagitController = i_MagitController;
        this.m_Engine = new Engine();
    }

    public void CreateNewRepository(String i_RepoName, Path i_RepoPath) throws Exception
    {
        m_Engine.CreateNewRepository(i_RepoPath, i_RepoName);
    }

    public void loadRepositoryFromXML(String i_PathToXML) throws Exception
    {
        boolean isXMLRepoExist;

        isXMLRepoExist = m_XMLMain.CheckXMLFile(Paths.get(i_PathToXML));

        if (!isXMLRepoExist)
        {
            m_Engine.setCurrentRepository(
                    m_XMLMain.ParseAndWriteXML(m_XMLMain.GetXmlRepository()));
            //m_Engine.WriteRepositoryNameFileInMagitRepository();
            m_Engine.AssignFitRepository(m_XMLMain.GetXmlRepository(), m_XMLMain);
        } else
            m_MagitController.handleCurrentRepositoryAlreadyExist(m_XMLMain.GetXmlRepository());
    }

    public void ExecuteUserChoice(String i_selectedItem) throws Exception
    {
        int userChoice;

        userChoice = i_selectedItem.equals(StringConstants.XML_REPOSITORY) ? NumConstants.ONE : NumConstants.TWO;

        m_Engine.ExecuteUserChoice(userChoice, m_XMLMain.GetXmlRepository(), m_XMLMain);
    }


    public void PullAnExistingRepository(String i_RepositoryName, String i_RepositoryPath) throws Exception
    {
        m_Engine.PullAnExistingRepository(i_RepositoryPath, i_RepositoryName);
    }

    public boolean IsFirstCommit()
    {
        return m_Engine.getCurrentRepository().ThereAreNoCmmitsYet();
    }

    public void CommitChanges(String i_CommitMessage, Commit prevSecondCommit) throws Exception
    {
        //m_Engine.CommitInCurrentRepository(i_CommitMessage);
        // Commit currentCommit = m_Engine.GetCurrentRepository().getActiveBranch().getCurrentCommit();
        m_Engine.CommitInCurrentRepository(i_CommitMessage, prevSecondCommit);
    }

    public FolderDifferences ShowStatus() throws Exception
    {
        return m_Engine.ShowStatus();
    }

    /*public Commit GetCurrentCommit()
    {
        return m_Engine.GetCurrentRepository().getActiveBranch().getCurrentCommit();
    }*/

    public Repository GetCurrentRepository()
    {
        return m_Engine.getCurrentRepository();
    }

    public void CreateNewBranch() throws Exception
    {

        if (m_Engine.IsLocalRepository())
            createBranchOnLocalRepository();
        else
            createReugularBranch();

    }

    private void createBranchOnLocalRepository() throws Exception
    {
        m_MagitController.getUserChoiceAndCreateBranch();
    }

    public void CreateNewBranchToSystem(String i_NewBranch, String i_SHA1Commit) throws Exception
    {
        m_Engine.CreateNewBranchToSystem(i_NewBranch, i_SHA1Commit);
    }


    private void createReugularBranch() throws Exception
    {
        m_MagitController.getBranchNameAndCommitSHA1();

    }

    public void DeleteBranch(String i_BranchNameToErase) throws Exception
    {
        if (IsLocalRepository())
            checkIfBranchIsRemoteBranch(i_BranchNameToErase);
        else
        {
            deleteBranchFromSystem(i_BranchNameToErase);
        }
    }

    public void deleteBranchFromSystem(String i_BranchNameToErase) throws Exception
    {
        m_Engine.DeleteBranchFromSystem(i_BranchNameToErase);
        m_MagitController.InformUserMessage(Alert.AlertType.INFORMATION, "Deleting Branch", "Deleting Branch",
                "Branch was deleted Successfully!");
        m_MagitController.UpdateWindowAfterDeletingBranch(i_BranchNameToErase);
    }

    private void checkIfBranchIsRemoteBranch(String i_branchNameToErase) throws Exception
    {
        LocalRepository localRepository = (LocalRepository) m_Engine.getCurrentRepository();

        RemoteBranch remoteBranch = localRepository.findRemoteBranchBranchByPredicate(remoteBranch1 ->
                remoteBranch1.getBranchName().equals(i_branchNameToErase));

        if (remoteBranch == null)
            deleteBranchFromSystem(i_branchNameToErase);
        else
            m_MagitController.InformUserMessage(Alert.AlertType.ERROR, "Delete Branch", "Remote Branch", "Can't delete RemoteBranch");
    }

    public boolean RootFolderChanged() throws Exception
    {
        return m_Engine.CheckIfRootFolderChanged();
    }

    public void CheckOut(String i_BranchName) throws Exception
    {
        m_Engine.CheckOut(i_BranchName);
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception
    {
        m_Engine.ResetHeadBranch(i_Sha1OfCommit);
    }

    public FolderDifferences ShowDeltaCommits(Commit i_Commit)
    {
        FolderDifferences folderDifferences = m_Engine.ShowDeltaCommits(i_Commit);

        if (folderDifferences == null)
            return null;

        return folderDifferences;
    }

    public void Clone(File i_DirToClone, String i_RepositoryName, File i_DirOfRepo) throws Exception
    {
        m_Engine.Clone(i_DirToClone, i_RepositoryName, i_DirOfRepo);
    }

    public void Fetch() throws Exception
    {
        m_Engine.Fetch();
    }

    public void Pull() throws Exception
    {
        if (m_Engine.ShowStatus() != null)
            m_MagitController.InformUserMessage(Alert.AlertType.ERROR, "Error!", "WC is Dirty",
                    "Can not execute Pull, there open changes in WC");
        else
        {
            m_Engine.Pull();
            m_MagitController.UpdateWindowTreeAndTable();
        }
    }

    public void SetUser(String newUserName)
    {
        m_Engine.UpdateNewUserInSystem(newUserName);
    }

    public void Push() throws Exception
    {
        Push pusher = new Push(m_Engine, (LocalRepository) m_Engine.getCurrentRepository());

        if (pusher.isPossibleToPush())
        {
            pusher.Push();
            m_MagitController.UpdateCommitTree();
        } else
            m_MagitController.InformUserMessage(Alert.AlertType.ERROR, "Error!", "Push Invalid!", "Can't push " +
                    "because one of the following reasons:" + System.lineSeparator() +
                    "1. There are open changes in Wc" + System.lineSeparator() +
                    "2. HeadBranch is not RTB" + System.lineSeparator() +
                    "3. There is nothing to push! RR and LR synchronized!");
    }

    public void SetConflictsForMergeInRepository(String i_PushingBranchName) throws Exception
    {
        m_Engine.SetConflictsForMergeInRepository(i_PushingBranchName);
    }

    public boolean IsLocalRepository()
    {
        return m_Engine.IsLocalRepository();
    }

    public boolean IsHeadBranch(String branchName)
    {
        return m_Engine.IsHeadBranch(branchName);
    }

    public void CreateRTB(Commit commit, String branchName) throws IOException
    {
        m_Engine.CreateRTB(commit, branchName);
    }

    public boolean IsFastForwardCase()
    {
        return m_Engine.GetConflictsForMerge().IsFastForwardCase();
    }

    public boolean IsPulledAncestorOfPulling()
    {
        return m_Engine.getCurrentRepository().getConflictsItemsAndNames().IsPulledAncestorOfPulling();
    }

    public ConflictingItems getConflictingItemsByName(String conflictingItemName)
    {
        return m_Engine.getConflictingItemsByName(conflictingItemName);
    }

    public void CreateChosenBlobInWC(String blobText, Blob chosenBlob) throws IOException
    {
/*if(blobText.isEmpty())
    m_MagitController.InformUserMessage("Error!");*/
        m_Engine.CreateChosenBlobInWC(blobText, chosenBlob);
    }

    public ObservableList<String> GetAllConflictsNames()
    {
        return m_Engine.getCurrentRepository().GetAllConflictsNames();
    }

    public void CreateCommitMerge(String commitMessage, String selectedBranchName) throws Exception
    {
        m_Engine.CreateCommitMerge(commitMessage, selectedBranchName);

        m_MagitController.UpdateWindowTreeAndTable();
    }

    public void FastForwardBranch(String selectedBranch) throws IOException
    {
        m_Engine.FastForwardBranch(selectedBranch);

        m_MagitController.UpdateWindowTreeAndTable();
    }

    public String GetExistingRepositoryName(File i_existingRepositoryFolder) throws IOException {
        return m_Engine.GetExistingRepositoryName(i_existingRepositoryFolder);
    }
}
