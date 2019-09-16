package Objects;

import Objects.branches.Branch;
import System.FolderDifferences;
import System.User;
import common.NumConstants;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Commit {
    private Folder m_RootFolder;
    private String m_SHA1 = null;
    private Commit m_PrevCommit = null;
    private Commit m_SecondPrevCommit = null;
    private String m_CommitMessage;
    private User m_UserCreated;
    private Date m_Date;

    /*public Commit(String i_CommitsSha1, Folder i_RootFolder, String i_SHA1PrevCommit, String i_SHA1SecondPrevCommit, String i_CommitMessage, User i_UserCreated, Date i_Date) throws Exception
    {
        Path objectsFolderPath = null;
        this.m_SHA1 = i_CommitsSha1;
        this.m_RootFolder = i_RootFolder;
        if (i_SHA1PrevCommit != null)
        {
            if (!i_SHA1PrevCommit.equals("null"))
            {
                objectsFolderPath = Paths.get(m_RootFolder.GetPath().toString() + "\\.magit\\Objects");
                m_PrevCommit = CreateCommitFromSha1(i_SHA1PrevCommit, objectsFolderPath);
            }
        }
        if (i_SHA1SecondPrevCommit != null)
        {
            if (!i_SHA1SecondPrevCommit.equals("null"))
            {
                m_SecondPrevCommit = CreateCommitFromSha1(i_SHA1SecondPrevCommit, objectsFolderPath);
            }
        }
        this.m_CommitMessage = i_CommitMessage;
        this.m_UserCreated = i_UserCreated;
        this.m_Date = i_Date;
    }*/

    public Commit(Folder i_RootFolder, String i_SHA1, Commit i_PrevCommit, Commit i_SecondPrevCommit, String i_CommitMessage, User i_UserCreated, Date i_Date) {
        this.m_RootFolder = i_RootFolder;
        this.m_SHA1 = i_SHA1;
        this.m_PrevCommit = i_PrevCommit;
        this.m_SecondPrevCommit = i_SecondPrevCommit;
        this.m_CommitMessage = i_CommitMessage;
        this.m_UserCreated = i_UserCreated;
        this.m_Date = i_Date;
    }

    public static Commit MergeCommits(Commit i_PullingCommit, Commit i_PushingCommit) throws Exception {
        Commit closestCommonAncestorCommit = getClosestCommonAncestor(i_PullingCommit, i_PushingCommit);
        //1.
        // TODO: check if in one of the commit is an ancestor of the other - if so then is a FF (Fast Forward) merge

        //2. if not FF


        return null;
    }

    private static Commit getClosestCommonAncestor(Commit i_pullingCommit, Commit i_pushingCommit) throws Exception {
        //TODO: implement and remove this Exception
        throw new Exception("implement getClosestCommonAncestor() in Commit Class");
    }

    public static Map<String, Commit> GetMapOfCommits(List<Branch> i_allBranches) throws Exception {
        Map<String, Commit> resMap = new HashMap<>();
        for (int i = 0; i < i_allBranches.size(); i++) {
            Branch currentBranch = i_allBranches.get(i);
            Commit pointedCommit = currentBranch.getPointedCommit();
            Map<String, Commit> pointedCommitMap = pointedCommit.CreateMapOfSha1ToCommit();
            Set<String> pointedCommitMapKeys = pointedCommitMap.keySet();
            pointedCommitMapKeys.forEach(sha1Key -> {
                if (resMap.get(sha1Key) == null) {
                    resMap.put(sha1Key, pointedCommitMap.get(sha1Key));
                }
            });
        }

        return resMap;
    }

    private Map<String,Commit> CreateMapOfSha1ToCommit() {
        Map<String,Commit> sha1ToCommitMap = new HashMap<String, Commit>();
        Commit commitIterator = this;
        while(commitIterator!=null)
        {
            sha1ToCommitMap.put(commitIterator.getSHA1(),commitIterator);
            if(commitIterator.GetSecondPrevCommit()!=null){
                Map<String,Commit> secondMapForSha1ToCommit = commitIterator.GetSecondPrevCommit().CreateMapOfSha1ToCommit();
                Set<String> sha1Keys = secondMapForSha1ToCommit.keySet();
                sha1Keys.forEach(sha1->{
                    if(sha1ToCommitMap.get(sha1)==null){
                        sha1ToCommitMap.put(sha1,secondMapForSha1ToCommit.get(sha1));
                    }
                });

            }
            commitIterator = commitIterator.GetPrevCommit();

        }
        return sha1ToCommitMap;
    }

    public static List<String> GetCommitFieldsFromCommitTextFile(Path i_CommitTextFilePath) throws IOException {
        Scanner lineScanner = new Scanner(i_CommitTextFilePath);
        List<String> commitTextFileFields = new ArrayList<>();

        while (lineScanner.hasNext()) {
            commitTextFileFields.add(lineScanner.nextLine());

        }
        return commitTextFileFields;
    }

    public static String GetInformation(Commit i_Commit) {
        String dateFormatted = Item.getDateStringByFormat(i_Commit.m_Date);
        StringBuilder commitInfo = new StringBuilder();
        commitInfo.append("Sha1: " + i_Commit.m_SHA1 + "\n");
        commitInfo.append("Message: " + i_Commit.m_CommitMessage + "\n");
        commitInfo.append("Date: " + dateFormatted + "\n");
        commitInfo.append("User: " + i_Commit.m_UserCreated.getUserName() + "\n");
        return commitInfo.toString();
    }

    public static String GetInformationFromCommitTextFile(String i_commitsSha1, Path i_commitTextFileUnzipped, Path i_ObjectsFolderPath) throws IOException {
        StringBuilder commitHistoryBuilder = new StringBuilder();
        String headline = "Commits details:" + System.lineSeparator();
        commitHistoryBuilder.append(headline);
        List<String> commitsDetails = Commit.GetCommitFieldsFromCommitTextFile(i_commitTextFileUnzipped);
        String[] rootFolderFields = Item.GetItemsDetails(commitsDetails.get(0));
        commitHistoryBuilder.append("Sha1: " + i_commitsSha1 + System.lineSeparator());
        commitHistoryBuilder.append("Message: " + commitsDetails.get(2) + System.lineSeparator());
        commitHistoryBuilder.append("Date: " + commitsDetails.get(3) + System.lineSeparator());
        commitHistoryBuilder.append("User: " + commitsDetails.get(4) + System.lineSeparator() + System.lineSeparator());

        if (!commitsDetails.get(1).equals("null")) {
            Path prevCommitTextFileZipped = Paths.get(i_ObjectsFolderPath.toString() + "\\" + commitsDetails.get(1));
            Path prevCommitTextFileUnzipped = Item.UnzipFile(prevCommitTextFileZipped, Paths.get(i_ObjectsFolderPath.getParent().toString() + "\\Temp"));
            commitHistoryBuilder.append("Previous Commit:" + System.lineSeparator());
            commitHistoryBuilder.append(GetInformationFromCommitTextFile(commitsDetails.get(1), prevCommitTextFileUnzipped, i_ObjectsFolderPath));
        }
        return commitHistoryBuilder.toString();

    }

    public static String createSha1ForCommit(Folder i_rootFolder, String i_sha1PrevCommit, String i_sha1OfSecondPrevCommit, String i_commitMessage, User i_user, Date date) {
        StringBuilder strForCalculatingSHA1 = new StringBuilder();
        strForCalculatingSHA1.append(i_rootFolder.getSHA1());
        strForCalculatingSHA1.append(i_sha1PrevCommit);
        strForCalculatingSHA1.append(i_sha1OfSecondPrevCommit);
        strForCalculatingSHA1.append(i_commitMessage);

        return DigestUtils.sha1Hex(strForCalculatingSHA1.toString());
    }

    public static FolderDifferences findDifferences(Commit i_LatestCommit, Commit i_OtherCommit) {
        FolderDifferences differences = null;
        if (!i_LatestCommit.getSHA1().equals(i_OtherCommit.m_RootFolder.getSHA1())) {
            differences = Folder.FinedDifferences(i_LatestCommit.m_RootFolder, i_OtherCommit.m_RootFolder);
        } else {
            //TODO: Throw exception
        }
        return differences;
    }

    public static Boolean IsSha1ValidForCommit(String i_Sha1ForCommit, Path i_ObjectsFolderPath) throws IOException {
        Boolean validSha1ForCommit = false;
        Path commitZippedPath = Paths.get(i_ObjectsFolderPath.toString() + "\\" + i_Sha1ForCommit);
        Path TempFolderPath = Paths.get(i_ObjectsFolderPath.getParent().toString() + "\\Temp");
        if (commitZippedPath.toFile().exists()) {
            Path commitUnzipped = Item.UnzipFile(commitZippedPath, TempFolderPath);
            List<String> commitText = Commit.GetCommitFieldsFromCommitTextFile(commitUnzipped);
            if ((commitText.size() == 6)) {
                if ((commitText.get(1).length() != 40) && (!commitText.get(1).toUpperCase().equals("NULL")))
                    validSha1ForCommit = false;
                else
                    validSha1ForCommit = true;
            }
        }
        return validSha1ForCommit;
    }

    public static Commit CreateCommitFromSha1(String i_CommitSha1, Path i_ObjectsFolder) throws Exception {
        Commit newCommit = null;
        Commit prevCommit = null;
        Commit secondPrevCommit = null;
        if (IsSha1ValidForCommit(i_CommitSha1, i_ObjectsFolder)) {
            Path unzippedCommitFile = Paths.get(i_ObjectsFolder.toString() + "\\" + i_CommitSha1);
            Path tempFolderPath = Paths.get(i_ObjectsFolder.getParent().toString() + "\\Temp");
            Path tempUnzippedCommitTextPath = Item.UnzipFile(unzippedCommitFile, tempFolderPath);

            List<String> CommitsFields = Commit.GetCommitFieldsFromCommitTextFile(tempUnzippedCommitTextPath);
            String[] rootFolderDetails = Item.GetItemsDetails(CommitsFields.get(0));
            String rootFolderSha1 = rootFolderDetails[1];
            String prevCommitSha1 = CommitsFields.get(1);
            String secondPrevCommitSha1 = CommitsFields.get(2);
            String message = CommitsFields.get(3);
            User rootFolderUser = new User(rootFolderDetails[3]);
            User commitUser = new User(CommitsFields.get(5));

            Date commitsDate = Item.ParseDateWithFormat(CommitsFields.get(4));
            Path WCTextFileZipped = Paths.get(i_ObjectsFolder.toString() + "\\" + rootFolderSha1);
            Path WCTextFileUnzippedPath = Item.UnzipFile(WCTextFileZipped, tempFolderPath);
            Path workingCopyPath = Paths.get(i_ObjectsFolder.getParent().getParent().toString());
            Folder commitsRootFolder = Folder.CreateFolderFromTextFolder(WCTextFileUnzippedPath.toFile(), workingCopyPath, rootFolderSha1, rootFolderUser, commitsDate, i_ObjectsFolder);
            if (!prevCommitSha1.toUpperCase().equals("NULL"))
                prevCommit = CreateCommitFromSha1(prevCommitSha1, i_ObjectsFolder);
            if (!secondPrevCommitSha1.toUpperCase().equals("NULL"))
                secondPrevCommit = CreateCommitFromSha1(secondPrevCommitSha1, i_ObjectsFolder);
            newCommit = new Commit(commitsRootFolder, i_CommitSha1, prevCommit, secondPrevCommit, message, commitUser, commitsDate);
            //newCommit = new Commit(i_CommitSha1, commitsRootFolder, prevCommit, secondPrevCommit, message, commitUser, commitsDate);

        }
        return newCommit;
    }

    public Date GetDate() {
        return m_Date;
    }

    public Folder getRootFolder() {
        return m_RootFolder;
    }

    public String getSHA1() {
        return m_SHA1;
    }

    public String getCommitMessage() {
        return m_CommitMessage;
    }

    public String CreatingContentOfCommit() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:SSS");

        StringBuilder rootFolderStringBuilder = new StringBuilder();
        rootFolderStringBuilder.append(this.m_RootFolder.getName() + ",");//Name
        rootFolderStringBuilder.append(this.m_RootFolder.getSHA1() + ",");//sha1
        rootFolderStringBuilder.append(this.m_RootFolder.getTypeOfFile().toString() + ",");//type
        rootFolderStringBuilder.append(this.m_RootFolder.getUser().getUserName() + ",");//user
        rootFolderStringBuilder.append(dateFormat.format(this.m_RootFolder.getDate()) + "\n");//date

        //example: 123,50087888a7c34344416ec0fd600f394dadf3d9d8,FOLDER,Administrator,06.39.2019-06:39:27:027
        StringBuilder contentOfCommitTextFile = new StringBuilder(rootFolderStringBuilder);//[0]rootFolder line of details name,sha1,type,user,date
        if (m_PrevCommit == null) {
            contentOfCommitTextFile.append("null" + '\n');//[1]prevCommit sha1
        } else {
            contentOfCommitTextFile.append(m_PrevCommit.m_SHA1 + '\n');//[1]prevCommit sha1
        }
        if (m_SecondPrevCommit == null) {
            contentOfCommitTextFile.append("null" + '\n');//[2]prevCommit sha1
        } else {
            contentOfCommitTextFile.append(m_SecondPrevCommit.m_SHA1 + '\n');//[2]prevCommit sha1
        }

        contentOfCommitTextFile.append(m_CommitMessage + '\n');//[3]message
        contentOfCommitTextFile.append(dateFormat.format(m_Date) + "\n");//[4]date
        contentOfCommitTextFile.append(m_UserCreated.getUserName());//[5]user

        return contentOfCommitTextFile.toString();
    }

    public String getAllFolderAndBlobsData() {
        StringBuilder commitsDataBuilder = new StringBuilder();
        commitsDataBuilder.append("All data of commit:\n");
        String commitDetails = Commit.GetInformation(this);
        commitsDataBuilder.append("Commit details:");
        commitsDataBuilder.append(commitDetails);
        commitsDataBuilder.append("Root folder information:\n");
        String rootFolderInformation = Folder.GetInformation(this.m_RootFolder);
        commitsDataBuilder.append(rootFolderInformation);
        return commitsDataBuilder.toString();
    }

    public User getUserCreated() {
        return m_UserCreated;
    }

    public Commit GetPrevCommit() {
        return m_PrevCommit;
    }

    public Commit GetSecondPrevCommit() {
        return m_SecondPrevCommit;
    }

    public boolean ThereIsPrevCommit(int i_NumOfPrev) {
        boolean isExist = false;

        switch (i_NumOfPrev) {
            case NumConstants.FIRST:
                isExist = m_PrevCommit != null;
                break;

            case NumConstants.SECOND:
                isExist = m_SecondPrevCommit != null;
                break;
        }

        return isExist;
    }
}
