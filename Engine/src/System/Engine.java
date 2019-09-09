package System;

import Objects.Commit;
import Objects.Folder;
import XmlObjects.MagitRepository;
import XmlObjects.XMLMain;
import common.MagitFileUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Engine
{
    public static final String sf_NOTHING_TO_COMMIT_ON = "There Is Nothing To Commit On";
    private Repository m_CurrentRepository = null;
    private User m_User = new User("Administrator");
    private XMLMain m_XMLMain = new XMLMain();


    /*private void loadRepositoryFromXML(i_UserPath) throws Exception
    {
        boolean isXMLRepoExist;

        isXMLRepoExist = m_XMLMain.CheckXMLFile(Paths.get(i_UserPath));

        if (!isXMLRepoExist)
            setCurrentRepository(
                    m_XMLMain.ParseAndWriteXML(m_XMLMain.GetXmlRepository())
            );
        else
            handleCurrentRepositoryAlreadyExist(m_XMLMain.GetXmlRepository());
    }*/


    public static void CreateRepositoryDirectories(Path i_rootFolderPath)
    {
        Path objectsFolderPath, branchesFolderPath, tempFolderPath;
        Path magitFolderPath = Paths.get(i_rootFolderPath.toString() + "\\.magit");
        magitFolderPath.toFile().mkdir();

        objectsFolderPath = Paths.get(magitFolderPath.toString() + "\\Objects");
        objectsFolderPath.toFile().mkdir();

        branchesFolderPath = Paths.get(magitFolderPath.toString() + "\\Branches");
        branchesFolderPath.toFile().mkdir();

        tempFolderPath = Paths.get(magitFolderPath.toString() + "\\Temp");
        tempFolderPath.toFile().mkdir();
    }

    public static String ReadLineByLine(File i_file) throws Exception
    {
        try
        {
            String content = new String(Files.readAllBytes(Paths.get(i_file.getAbsolutePath())), StandardCharsets.UTF_8);
            return content;

        } catch (IOException e)
        {
            throw new Exception("Exception was occured, problem in reading file:" + i_file.getName());
        }
    }

    public void UpdateNewUserInSystem(String i_UserName)
    {
        if (m_User == null)
        {
            m_User = new User(i_UserName);
        } else
        {
            m_User.setUserName(i_UserName);
        }
    }

    public void CommitInCurrentRepository(String i_CommitMessage) throws Exception
    {
        m_CurrentRepository.CreateNewCommitAndUpdateActiveBranch(m_User, i_CommitMessage);
    }

    public void CreateNewLocalRepository(Path i_PathToRootFolderOfRepository, String i_RepositoryName) throws Exception
    {

        Boolean exists = false;
        Path magitFolderPath = Paths.get(i_PathToRootFolderOfRepository.toString() + "\\" + ".magit");
        //check if the repository already exists
        if (magitFolderPath.toFile().exists())
        {
            String existsMessage = "The Repository path you gave already exists" + System.lineSeparator() +
                    "Please choose a different path or select option number 4 with this path";
            throw new Exception(existsMessage);
        } else// making this folder a repository
        {
            createMagitFolderInRootFolder(i_PathToRootFolderOfRepository);
            Branch MasterBranch = new Branch("Master", null);
            m_CurrentRepository = new Repository(i_PathToRootFolderOfRepository, i_RepositoryName, MasterBranch);
        }

    }

    private void createMagitFolderInRootFolder(Path i_rootFolderPath) throws IOException
    {
        Path branchesFolderPath = Paths.get(i_rootFolderPath.toString() + Repository.sf_PathForBranches);
        CreateRepositoryDirectories(i_rootFolderPath);
        createHEADBranchAndAddressToMaster(branchesFolderPath);
        createEmptyMasterBranch(branchesFolderPath);
    }

    // this method creates the Master.txt branch in Branches folder
    private Path createEmptyMasterBranch(Path i_BranchFolderPath) throws IOException
    {
        File masterBranch = new File(i_BranchFolderPath.toString() + "\\Master.txt");
        masterBranch.createNewFile();
        return masterBranch.toPath();
    }

    private void createHEADBranchAndAddressToMaster(Path i_BranchFolderPath) throws IOException
    {
        Path HEADFilePath = Paths.get(i_BranchFolderPath.toString() + "\\HEAD.txt");
        Files.createFile(HEADFilePath);
        FileUtils.writeStringToFile(HEADFilePath.toFile(), "Master", "UTF-8", false);
    }

    public FolderDifferences ShowStatus() throws Exception
    {
        //1. create the currentWorkingCopy as Folder
        FolderDifferences differences = null;
        Folder wc = this.m_CurrentRepository.GetUpdatedWorkingCopy(this.m_User);
        if (this.m_CurrentRepository.getActiveBranch().getPointedCommit() == null)
            throw new Exception("Before show status, you need to commit first!");

        Folder lastCommitWc = this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder();

        if (!wc.getSHA1().equals(lastCommitWc.getSHA1()))
            differences = Folder.FinedDifferences(wc, lastCommitWc);


        return differences;
    }

    public void CheckOut(String i_BranchName) throws Exception
    {
        //1. set active branch to selectd branch
        this.m_CurrentRepository.replaceActiveBranch(i_BranchName);

        removeFilesFromWCAndSpanNewCommitInActiveBranch();
    }

    private void removeFilesFromWCAndSpanNewCommitInActiveBranch() throws IOException
    {
        //2. remove previous  files and folders
        Folder.RemoveFilesAndFoldersWithoutMagit(this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder().GetPath());
        //3. span rootFolder in WC
        Folder.SpanDirectory(this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder());
    }

    public void GetCommitHistoryInActiveBranch() throws IOException
    {
        Branch activeBranch = this.m_CurrentRepository.getActiveBranch();
        String commitHistoryOfActiveBranch = Branch.GetCommitHistory(activeBranch, m_CurrentRepository.GetObjectsFolderPath());
        System.out.println(commitHistoryOfActiveBranch);
    }

    public Repository GetCurrentRepository()
    {
        return m_CurrentRepository;
    }

    public void setCurrentRepository(Repository m_CurrentRepository)
    {
        this.m_CurrentRepository = m_CurrentRepository;
    }

    public void PullAnExistingRepository(String i_repositoryPathAsString, String i_NameOfRepository) throws Exception
    {
        Repository repository;
        Branch activeBranch;
        Path repositoryPath = Paths.get(i_repositoryPathAsString);

        if (!repositoryPath.toFile().exists())
        {
            throw new FileNotFoundException(repositoryPath.toString() + " does not exist - please make sure you are giving a correct path");
        }
        Path branchFolderPath = Paths.get(repositoryPath.toString() + "\\.magit\\Branches");

        // Path objectsFolderPath = Paths.get(repositoryPath.toString() + "\\.magit\\Objects");

        Path HEAD = Paths.get(branchFolderPath.toString() + "\\HEAD.txt");
        String activeBranchName = Engine.ReadLineByLine(HEAD.toFile());
        Path activeBranchPath = Paths.get(branchFolderPath.toString() + "\\" + activeBranchName + ".txt");

        activeBranch = Branch.createBranchInstanceFromExistBranch(activeBranchPath);


        List<Branch> allBranches = Branch.GetAllBranches(branchFolderPath);
        //  repository = new Repository(activeBranch, repositoryPath, i_NameOfRepository, allBranches);

        //  this.m_CurrentRepository = repository;
    }

    public String ShowAllCurrentCommitData()
    {
        return this.m_CurrentRepository.getActiveBranch().getPointedCommit().getAllFolderAndBlobsData();
    }

    public void CreateNewBranchToSystem(String i_NameOfNewBranch, String i_SHA1OfCommit) throws Exception
    {
        //check if commit exist
        checkIfSHA1CommitExist(i_SHA1OfCommit);
        //check if branch name already exist
        if (isBranchExist(i_NameOfNewBranch))
            throw new Exception("Error!" + System.lineSeparator() + "Branch name already exist." + System.lineSeparator());

        if (m_CurrentRepository.getActiveBranch().getPointedCommit() != null)
        {
            m_CurrentRepository.AddingNewBranchInRepository(i_NameOfNewBranch, i_SHA1OfCommit);
        } else
            throw new Exception("Error!" + System.lineSeparator() + "There are no commits yet" + System.lineSeparator());

    }

    private boolean isaCommitExistBySHA1(String i_SHA1OfCommit)
    {
        return m_CurrentRepository.GetAllCommitsSHA1ToCommit().containsKey(i_SHA1OfCommit);
    }

    private boolean isBranchExist(String i_NameOfNewBranch)
    {
        return m_CurrentRepository.getAllBranches()
                .stream()
                .anyMatch(branch ->
                        branch.getBranchName().equals(i_NameOfNewBranch));
    }

    public void DeleteBranchFromSystem(String i_BranchNameToErase) throws Exception
    {
        if (i_BranchNameToErase.equals(m_CurrentRepository.getActiveBranch().getBranchName()))
            throw new Exception("Error! Can not erase HEAD Branch");

        String pathBranch = m_CurrentRepository.getRepositoryPath().toString()
                + Repository.sf_PathForBranches
                + Repository.sf_Slash
                + i_BranchNameToErase
                + Repository.sf_txtExtension;

        File tempFileForCheckingExistence = new File(pathBranch);

        if (!tempFileForCheckingExistence.exists())
            throw new Exception("Error! Branch doesnt exist!");
        else
        {
            deleteBranch(tempFileForCheckingExistence, i_BranchNameToErase);
        }
    }

    private void deleteBranch(File i_TempFileForCheckingExistence, String i_BranchNameToErase)
    {
        //TODO:
        // SOLVE THIS METHOD
        i_TempFileForCheckingExistence.delete();
        m_CurrentRepository.getAllBranches().removeIf(branch -> branch.getBranchName().equals(i_BranchNameToErase));
    }

    public void RemoveTempFolder() throws IOException
    {
        Path tempFolder = this.m_CurrentRepository.GetTempFolderPath();
        if (tempFolder.toFile().exists())
        {
            FileUtils.deleteDirectory(tempFolder.toFile());
        }
    }

    public boolean CheckIfRootFolderChanged() throws Exception
    {
        //1. get sha1 of root folder of last commit
        Folder rootFolderOfCommit = this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder();
        //2. get working copy
        Folder wc = this.m_CurrentRepository.GetUpdatedWorkingCopy(this.m_User);
        //3. compare both Sha1 - if equal then there are no changes
        if (wc.getSHA1().equals(rootFolderOfCommit.getSHA1()))
        {
            return false;
        } else
            return true;
    }

    public void ExecuteUserChoice(int i_RepoChoice, MagitRepository i_MagitRepository,
                                  XMLMain i_XmlMain) throws Exception
    {
        switch (i_RepoChoice)
        {
            case 1:
                Folder.DeleteDirectory(i_MagitRepository.getLocation());
                m_CurrentRepository = i_XmlMain.ParseAndWriteXML(i_MagitRepository);
                break;

            case 2:
                PullAnExistingRepository(i_MagitRepository.getLocation(), i_MagitRepository.getName());
                break;
        }
    }

    public void CheckExistenceCurrentRepository() throws Exception
    {
        if (m_CurrentRepository == null)
            throw new Exception("Can't execute this operation!" + System.lineSeparator() +
                    "Repository doesn't exist in System");
    }

    private void checkExistenceOfCommit() throws Exception
    {
        if (m_CurrentRepository.ThereAreNoCmmitsYet())
            throw new Exception("Can't execute this operation!" + System.lineSeparator() +
                    "There are no commits in systen yet");
    }

    public void CheckIfRepoAndCommitInSystem() throws Exception
    {
        CheckExistenceCurrentRepository();
        checkExistenceOfCommit();
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception
    {
        checkIfSHA1CommitExist(i_Sha1OfCommit);

        Commit commitRequested = m_CurrentRepository.GetAllCommitsSHA1ToCommit().get(i_Sha1OfCommit);

        String branchFilePath = m_CurrentRepository.getBranchesFolderPath().toString()
                + Repository.sf_Slash
                + m_CurrentRepository.getActiveBranch().getBranchName()
                + Repository.sf_txtExtension;

        MagitFileUtils.OverwriteContentInFile(commitRequested.getSHA1(), branchFilePath);

        m_CurrentRepository.getActiveBranch().SetCurrentCommit(commitRequested);

        removeFilesFromWCAndSpanNewCommitInActiveBranch();

//        CheckOut(m_CurrentRepository.getActiveBranch().getBranchName());
    }

    private void checkIfSHA1CommitExist(String i_Sha1OfCommit) throws Exception
    {
        if (!isaCommitExistBySHA1(i_Sha1OfCommit))
        {
            throw new Exception("Error!" + System.lineSeparator() + "Commit doesn't exist." + System.lineSeparator());
        }
    }
}


