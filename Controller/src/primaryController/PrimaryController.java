package primaryController;

import Objects.Commit;
import System.Engine;
import XmlObjects.XMLMain;
import common.NumConstants;
import common.StringConstants;
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
        return m_Engine.getCurrentRepository().ThereAreNoCmmitsYet();
    }

    public void CommitChanges(String i_CommitMessage) throws Exception
    {
        m_Engine.CommitInCurrentRepository(i_CommitMessage);
    }

    public String ShowStatus() throws Exception
    {
        return m_Engine.ShowStatus();
    }

    public Commit GetCurrentCommit()
    {
        return m_Engine.getCurrentRepository().getActiveBranch().getCurrentCommit();
    }
}
