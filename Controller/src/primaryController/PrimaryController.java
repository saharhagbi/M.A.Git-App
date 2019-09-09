package primaryController;

import Objects.Commit;
import System.Engine;
import System.FolderDifferences;
import System.Repository;
import XmlObjects.XMLMain;
import common.constants.NumConstants;
import common.constants.StringConstants;
import main.MAGitController;

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
        m_Engine.CreateNewLocalRepository(i_RepoPath, i_RepoName);
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
        return m_Engine.GetCurrentRepository().ThereAreNoCmmitsYet();
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
        return m_Engine.GetCurrentRepository();
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
}
