//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package System;

import Objects.Commit;
import Objects.Folder;
import Objects.branch.Branch;
import XmlObjects.MagitRepository;
import XmlObjects.XMLMain;
import XmlObjects.repositoryWriters.LocalRepositoryWriter;
import collaboration.*;
import common.MagitFileUtils;
import common.constants.NumConstants;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;

import java.util.*;

import static javax.swing.text.html.HTML.Tag.HEAD;


public class Engine {
    public static final String sf_NOTHING_TO_COMMIT_ON = "There Is Nothing To Commit On";
    private Repository m_CurrentRepository = null;
    private User m_User = new User("Administrator");
    private XMLMain m_XMLMain = new XMLMain();
    private LocalRepository m_CurrentLocalRepository = null;

    public Engine() {
    }

    public static void CreateRepositoryDirectories(Path i_rootFolderPath) {
        Path magitFolderPath = Paths.get(i_rootFolderPath.toString() + "\\.magit");
        magitFolderPath.toFile().mkdir();
        Path objectsFolderPath = Paths.get(magitFolderPath.toString() + "\\Objects");
        objectsFolderPath.toFile().mkdir();
        Path branchesFolderPath = Paths.get(magitFolderPath.toString() + "\\Branches");
        branchesFolderPath.toFile().mkdir();
        Path tempFolderPath = Paths.get(magitFolderPath.toString() + "\\Temp");
        tempFolderPath.toFile().mkdir();
    }

    public static String ReadLineByLine(File i_file) throws Exception {
        try {
            String content = new String(Files.readAllBytes(Paths.get(i_file.getAbsolutePath())), StandardCharsets.UTF_8);
            return content;
        } catch (IOException var2) {
            throw new Exception("Exception was occured, problem in reading file:" + i_file.getName());
        }
    }

    public void UpdateNewUserInSystem(String i_UserName) {
        if (this.m_User == null) {
            this.m_User = new User(i_UserName);
        } else {
            this.m_User.setUserName(i_UserName);
        }

    }

    public void CommitInCurrentRepository(String i_CommitMessage) throws Exception {
        this.m_CurrentRepository.CreateNewCommitAndUpdateActiveBranch(this.m_User, i_CommitMessage);
    }

    public void CreateNewLocalRepository(Path i_PathToRootFolderOfRepository, String i_RepositoryName) throws Exception {
        Boolean exists = false;
        Path magitFolderPath = Paths.get(i_PathToRootFolderOfRepository.toString() + "\\.magit");
        if (magitFolderPath.toFile().exists()) {
            String existsMessage = "The Repository in the path you gave already exists" + System.lineSeparator();
            throw new Exception(existsMessage);
        } else {
            this.createMagitFolderInRootFolder(i_PathToRootFolderOfRepository);
            Branch MasterBranch = new Branch("Master", (Commit)null);
            this.m_CurrentRepository = new Repository(i_PathToRootFolderOfRepository, i_RepositoryName, MasterBranch);
        }
    }

    private void createMagitFolderInRootFolder(Path i_rootFolderPath) throws IOException {
        Path branchesFolderPath = Paths.get(i_rootFolderPath.toString() + "\\.magit\\Branches");
        CreateRepositoryDirectories(i_rootFolderPath);
        this.createHEADBranchAndAddressToMaster(branchesFolderPath);
        this.createEmptyMasterBranch(branchesFolderPath);
    }

    private Path createEmptyMasterBranch(Path i_BranchFolderPath) throws IOException {
        File masterBranch = new File(i_BranchFolderPath.toString() + "\\Master.txt");
        masterBranch.createNewFile();
        return masterBranch.toPath();
    }

    private void createHEADBranchAndAddressToMaster(Path i_BranchFolderPath) throws IOException {
        Path HEADFilePath = Paths.get(i_BranchFolderPath.toString() + "\\HEAD.txt");
        Files.createFile(HEADFilePath);
        FileUtils.writeStringToFile(HEADFilePath.toFile(), "Master", "UTF-8", false);
    }

    public FolderDifferences ShowStatus() throws Exception {
        FolderDifferences differences = null;
        Folder wc = this.m_CurrentRepository.GetUpdatedWorkingCopy(this.m_User);
        if (this.m_CurrentRepository.getActiveBranch().getPointedCommit() == null) {
            throw new Exception("Before show status, you need to commit first!");
        } else {
            Folder lastCommitWc = this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder();
            if (!wc.getSHA1().equals(lastCommitWc.getSHA1())) {
                differences = Folder.FinedDifferences(wc, lastCommitWc);
            }

            return differences;
        }
    }

    public void CheckOut(String i_BranchName) throws Exception {
        this.m_CurrentRepository.replaceActiveBranch(i_BranchName);
        this.removeFilesFromWCAndSpanNewCommitInActiveBranch();
    }

    private void removeFilesFromWCAndSpanNewCommitInActiveBranch() throws IOException {
        Folder.RemoveFilesAndFoldersWithoutMagit(this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder().GetPath());
        Folder.SpanDirectory(this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder());
    }

    public void GetCommitHistoryInActiveBranch() throws IOException {
        Branch activeBranch = this.m_CurrentRepository.getActiveBranch();
        String commitHistoryOfActiveBranch = Branch.GetCommitHistory(activeBranch, this.m_CurrentRepository.GetObjectsFolderPath());
        System.out.println(commitHistoryOfActiveBranch);
    }

    public Repository getCurrentRepository() {
        return this.m_CurrentRepository;
    }

    public void setCurrentRepository(Repository m_CurrentRepository) {
        this.m_CurrentRepository = m_CurrentRepository;
    }

    public void PullAnExistingRepository(String i_repositoryPathAsString, String i_NameOfRepository) throws Exception {
        Path repositoryPath = Paths.get(i_repositoryPathAsString);
        if (!repositoryPath.toFile().exists()) {
            throw new FileNotFoundException(repositoryPath.toString() + " does not exist - please make sure you are giving a correct path");
        } else {
            Path branchFolderPath = Paths.get(repositoryPath.toString() + "\\.magit\\Branches");
            Path HEAD = Paths.get(branchFolderPath.toString() + "\\HEAD.txt");
            String activeBranchName = ReadLineByLine(HEAD.toFile());
            Path activeBranchPath = Paths.get(branchFolderPath.toString() + "\\" + activeBranchName + ".txt");
            Branch activeBranch = Branch.createBranchInstanceFromExistBranch(activeBranchPath);
            List<Branch> allBranches = Branch.GetAllBranches(branchFolderPath);
            Map<String, Commit> allCommitsInRepositoryMap = Commit.GetMapOfCommits(allBranches);
            Repository repository = new Repository(activeBranch, repositoryPath, i_NameOfRepository, allBranches, allCommitsInRepositoryMap);
            this.m_CurrentRepository = repository;
            this.getCurrentRepository().setActiveBranch(activeBranch);
        }

        Path branchFolderPath = Paths.get(repositoryPath.toString() + "\\.magit\\Branches");

        // Path objectsFolderPath = Paths.get(repositoryPath.toString() + "\\.magit\\Objects");

        Path HEAD = Paths.get(branchFolderPath.toString() + "\\HEAD.txt");
        String activeBranchName = Engine.ReadLineByLine(HEAD.toFile());
        Path activeBranchPath = Paths.get(branchFolderPath.toString() + "\\" + activeBranchName + ".txt");

        if(MagitFileUtils.IsFolderExist(branchFolderPath))
            createLocalRepository();
        else
        {
            List<Branch> allBranches = Branch.GetAllBranches(branchFolderPath);
            Optional<Branch> activeBranch = Branch.GetHeadBranch(allBranches, branchFolderPath);
            Map<String, Commit> allCommitsInRepositoryMap = Commit.GetMapOfCommits(allBranches);
            //repository = new Repository(activeBranch, repositoryPath, i_NameOfRepository, allBranches);-
            Repository repository = new Repository(activeBranch.get(), repositoryPath, i_NameOfRepository, allBranches, allCommitsInRepositoryMap);
            this.m_CurrentRepository = repository;
            this.getCurrentRepository().setActiveBranch(activeBranch.get());
        }

    }

    private void createLocalRepository()
    {

    }

    public String ShowAllCurrentCommitData() {
        return this.m_CurrentRepository.getActiveBranch().getPointedCommit().getAllFolderAndBlobsData();
    }

    public void CreateNewBranchToSystem(String i_NameOfNewBranch, String i_SHA1OfCommit) throws Exception {
        this.checkIfSHA1CommitExist(i_SHA1OfCommit);
        if (this.isBranchExist(i_NameOfNewBranch)) {
            throw new Exception("Error!" + System.lineSeparator() + "Branch name already exist." + System.lineSeparator());
        } else if (this.m_CurrentRepository.getActiveBranch().getPointedCommit() != null) {
            this.m_CurrentRepository.AddingNewBranchInRepository(i_NameOfNewBranch, i_SHA1OfCommit);
        } else {
            throw new Exception("Error!" + System.lineSeparator() + "There are no commits yet" + System.lineSeparator());
        }
    }

    private boolean isaCommitExistBySHA1(String i_SHA1OfCommit) {
        return this.m_CurrentRepository.getAllCommitsSHA1ToCommit().containsKey(i_SHA1OfCommit);
    }

    private boolean isBranchExist(String i_NameOfNewBranch) {
        return this.m_CurrentRepository.getAllBranches().stream().anyMatch((branch) -> {
            return branch.getBranchName().equals(i_NameOfNewBranch);
        });
    }

    public void DeleteBranchFromSystem(String i_BranchNameToErase) throws Exception {
        if (i_BranchNameToErase.equals(this.m_CurrentRepository.getActiveBranch().getBranchName())) {
            throw new Exception("Error! Can not erase HEAD Branch");
        } else {
            String pathBranch = this.m_CurrentRepository.getRepositoryPath().toString() + "\\.magit\\Branches" + "\\" + i_BranchNameToErase + ".txt";
            File tempFileForCheckingExistence = new File(pathBranch);
            if (!tempFileForCheckingExistence.exists()) {
                throw new Exception("Error! Branch doesnt exist!");
            } else {
                this.deleteBranch(tempFileForCheckingExistence, i_BranchNameToErase);
            }
        }
    }

    private void deleteBranch(File i_TempFileForCheckingExistence, String i_BranchNameToErase) {
        i_TempFileForCheckingExistence.delete();
        this.m_CurrentRepository.getAllBranches().removeIf((branch) -> {
            return branch.getBranchName().equals(i_BranchNameToErase);
        });
    }

    public void RemoveTempFolder() throws IOException {
        Path tempFolder = this.m_CurrentRepository.GetTempFolderPath();
        if (tempFolder.toFile().exists()) {
            FileUtils.deleteDirectory(tempFolder.toFile());
        }

    }

    public boolean CheckIfRootFolderChanged() throws Exception {
        Folder rootFolderOfCommit = this.m_CurrentRepository.getActiveBranch().getPointedCommit().getRootFolder();
        Folder wc = this.m_CurrentRepository.GetUpdatedWorkingCopy(this.m_User);
        return !wc.getSHA1().equals(rootFolderOfCommit.getSHA1());
    }

    public void ExecuteUserChoice(int i_RepoChoice, MagitRepository i_MagitRepository, XMLMain i_XmlMain) throws Exception {
        switch(i_RepoChoice) {
            case 1:
                Folder.DeleteDirectory(i_MagitRepository.getLocation());
                this.m_CurrentRepository = i_XmlMain.ParseAndWriteXML(i_MagitRepository);
                break;
            case 2:
                this.PullAnExistingRepository(i_MagitRepository.getLocation(), i_MagitRepository.getName());
        }

    }

    public void CheckExistenceCurrentRepository() throws Exception {
        if (this.m_CurrentRepository == null) {
            throw new Exception("Can't execute this operation!" + System.lineSeparator() + "Repository doesn't exist in System");
        }
    }

    private void checkExistenceOfCommit() throws Exception {
        if (this.m_CurrentRepository.ThereAreNoCmmitsYet()) {
            throw new Exception("Can't execute this operation!" + System.lineSeparator() + "There are no commits in systen yet");
        }
    }

    public void CheckIfRepoAndCommitInSystem() throws Exception {
        this.CheckExistenceCurrentRepository();
        this.checkExistenceOfCommit();
    }

    public void ResetHeadBranch(String i_Sha1OfCommit) throws Exception {
        this.checkIfSHA1CommitExist(i_Sha1OfCommit);
        Commit commitRequested = (Commit)this.m_CurrentRepository.getAllCommitsSHA1ToCommit().get(i_Sha1OfCommit);
        String branchFilePath = this.m_CurrentRepository.getBranchesFolderPath().toString() + "\\" + this.m_CurrentRepository.getActiveBranch().getBranchName() + ".txt";
        MagitFileUtils.OverwriteContentInFile(commitRequested.getSHA1(), branchFilePath);
        this.m_CurrentRepository.getActiveBranch().setPointedCommit(commitRequested);
        this.removeFilesFromWCAndSpanNewCommitInActiveBranch();
    }

    private void checkIfSHA1CommitExist(String i_Sha1OfCommit) throws Exception {
        if (!this.isaCommitExistBySHA1(i_Sha1OfCommit)) {
            throw new Exception("Error!" + System.lineSeparator() + "Commit doesn't exist." + System.lineSeparator());
        }
    }

    public FolderDifferences ShowDeltaCommits(Commit i_Commit) {
        Commit prevCommit = null;
        if (i_Commit.ThereIsPrevCommit(1)) {
            prevCommit = (Commit)this.m_CurrentRepository.getAllCommitsSHA1ToCommit().get(i_Commit.GetPrevCommit().getSHA1());
            return Folder.FinedDifferences(prevCommit.getRootFolder(), i_Commit.getRootFolder());
        } else {
            return null;
        }
    }


    public void Clone(File i_DirToClone, String i_RepositoryName, File i_DirCloneFrom) throws Exception
    {
        List<RemoteBranch> remoteBranches = new ArrayList<>();
        List<RemoteTrackingBranch> remoteTrackingBranches = new ArrayList<>();

        PullAnExistingRepository(i_DirCloneFrom.getPath(), i_RepositoryName);

        createRemoteBranches(remoteBranches, i_DirCloneFrom.getName());

        RemoteTrackingBranch remoteHeadTrackingBranch = new RemoteTrackingBranch(m_CurrentRepository.getActiveBranch());

        initNewPaths(m_CurrentRepository.getAllCommitsSHA1ToCommit().values(), i_DirToClone.toPath());

        remoteTrackingBranches.add(remoteHeadTrackingBranch);

        m_CurrentLocalRepository = new LocalRepository(remoteHeadTrackingBranch, i_DirToClone.toPath(),
                i_RepositoryName, null, m_CurrentRepository.getAllCommitsSHA1ToCommit(),
                remoteTrackingBranches, remoteBranches, new RemoteRepositoryRef(i_DirCloneFrom.getName(),
                i_DirCloneFrom.toPath()));

        LocalRepositoryWriter localRepositoryWriter = new LocalRepositoryWriter(m_CurrentLocalRepository);
        localRepositoryWriter.WriteRepositoryToFileSystem(m_CurrentLocalRepository.getActiveBranch().getBranchName());
    }

    private void initNewPaths(Collection<Commit> i_Commits, Path i_NewPathOfRepository)
    {
        for (Commit currentCommit : i_Commits)
        {
            currentCommit.getRootFolder().initFolderPaths(i_NewPathOfRepository);
        }
    }

   /* private void initFolderPaths(Folder i_RootFolder, Path i_NewPathOfRepository)
    {
        for (Item item : i_RootFolder)
        {
            if(item.getTypeOfFile().equals(Item.TypeOfFile.FOLDER))
            {
                initNewPaths();
            }
        }
    }*/
    private void createRemoteBranches(List<RemoteBranch> i_RemoteBranches, String i_CloneFromRepoName)
    {
        m_CurrentRepository.getAllBranches().stream().forEach(branch ->
        {
            RemoteBranch remoteBranch = RemoteBranch.CreateRemoteBranchFromBranch(branch, i_CloneFromRepoName);
            i_RemoteBranches.add(remoteBranch);


        });
    }

    public void Fetch() throws Exception
    {
        Fetch fetcher = new Fetch(this, m_CurrentLocalRepository);
        fetcher.FetchAllObjects();
    }

    public void Pull() throws Exception
    {
        Fetch fetcher = new Fetch(this, m_CurrentLocalRepository);

        Branch activeBranchInRemote = fetcher.getRemoteRepositoryToFetchFrom().getActiveBranch();
        fetcher.FetchBranch(activeBranchInRemote);
//        fetcher.FetchBranch();
    }
}