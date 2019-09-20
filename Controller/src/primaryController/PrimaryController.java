package primaryController;

import Objects.Commit;
import Objects.branch.Branch;
import System.Engine;
import System.FolderDifferences;
import System.Repository;
import XmlObjects.XMLMain;
import collaboration.LocalRepository;
import collaboration.Push;
import common.constants.NumConstants;
import common.constants.StringConstants;
import javafx.scene.control.Alert;
import main.MAGitController;
import System.MergeConflictsAndMergedItems;
import java.io.File;
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
            m_Engine.setCurrentRepository(
                    m_XMLMain.ParseAndWriteXML(m_XMLMain.GetXmlRepository())
            );
        else
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

    public void CommitChanges(String i_CommitMessage) throws Exception
    {
        //m_Engine.CommitInCurrentRepository(i_CommitMessage);
        // Commit currentCommit = m_Engine.GetCurrentRepository().getActiveBranch().getCurrentCommit();
        m_Engine.CommitInCurrentRepository(i_CommitMessage);
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

    public void CreateNewBranch(String i_NewBranch, String i_SHA1Commit) throws Exception
    {
        m_Engine.CreateNewBranchToSystem(i_NewBranch, i_SHA1Commit);
    }

    public void DeleteBranch(String i_BranchNameToErase) throws Exception
    {
        m_Engine.DeleteBranchFromSystem(i_BranchNameToErase);
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
            m_Engine.Pull();
    }

    public void SetUset(String newUserName)
    {
        m_Engine.UpdateNewUserInSystem(newUserName);
    }

    public void Push() throws Exception
    {
        Push pusher = new Push(m_Engine, (LocalRepository) m_Engine.getCurrentRepository());

        if (pusher.isPossibleToPush())
            pusher.Push();
        else
            m_MagitController.InformUserMessage(Alert.AlertType.ERROR, "Error!", "Can't Push", "Can't push " +
                    "current head branch");
    }

    public MergeConflictsAndMergedItems GetConflictsForMerge(Branch i_PushingBranch) throws Exception {
        return m_Engine.getCurrentRepository().getActiveBranch().GetConflictsForMerge(i_PushingBranch,m_Engine.getCurrentRepository().getRepositoryPath());
    }
}
