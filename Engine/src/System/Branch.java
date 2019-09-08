package System;

import Objects.Commit;
import Objects.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Branch
{
    private String m_BranchName;
    private Commit m_PointedCommit;


    public Branch(String i_BranchName, Commit i_CurrentCommit)
    {
        m_PointedCommit = i_CurrentCommit;
        m_BranchName = i_BranchName;
    }

    public static String GetCommitHistory(Branch i_Branch, Path i_ObjectsFolder) throws IOException
    {
        //TODO: apply for second prev commit
        StringBuilder commitHistoryBuilder = new StringBuilder();
        String headline = "Commits details:\n";
        commitHistoryBuilder.append(headline);
        commitHistoryBuilder.append(Commit.GetInformation(i_Branch.getPointedCommit()));
        if (i_Branch.getPointedCommit().GetPrevCommit().getSHA1() != null)
        {
            if (!i_Branch.getPointedCommit().GetPrevCommit().getSHA1().equals("null"))
            {
                commitHistoryBuilder.append("Previous Commit:\n");
                Path prevCommitTextFileZipped = Paths.get(i_ObjectsFolder.toString() + "\\" + i_Branch.m_PointedCommit.GetPrevCommit().getSHA1());
                Path PrevCommitTextFileUnzipped = Item.UnzipFile(prevCommitTextFileZipped, Paths.get(i_ObjectsFolder.getParent().toString() + "\\Temp"));
                String prevCommitsDetails = Commit.GetInformationFromCommitTextFile(i_Branch.m_PointedCommit.GetPrevCommit().getSHA1(), PrevCommitTextFileUnzipped, i_ObjectsFolder);
                commitHistoryBuilder.append(prevCommitsDetails);
            }
        }
        return commitHistoryBuilder.toString();
    }

    public static List<Branch> GetAllBranches(Path i_BranchFolderPath) throws Exception
    {
        List<Branch> allBranches = new ArrayList<Branch>();
        File[] allBranchesFiles = i_BranchFolderPath.toFile().listFiles();
        for (int i = 0; i < allBranchesFiles.length; i++)
        {
            if (!allBranchesFiles[i].getName().equals("HEAD.txt"))
            {
                allBranches.add(Branch.createBranchInstanceFromExistBranch(allBranchesFiles[i].toPath()));
            }
        }
        return allBranches;
    }

    public static Branch createBranchInstanceFromExistBranch(Path i_BranchesPath) throws Exception
    {
        String branchName;
        Path realPathToBranch = i_BranchesPath;
        // 1. get branch name
        // if the path is to the HEAD Branch then we want to extract the real branch name
        if (realPathToBranch.getFileName().getFileName().toString().equals("HEAD.txt"))
        {
            branchName = extractBranchName(i_BranchesPath);
            realPathToBranch = Paths.get(i_BranchesPath.getParent().toString() + "\\" + branchName + ".txt");
        } else
        {
            String[] fileNameAndExtension = i_BranchesPath.getFileName().toString().split("\\.(?=[^\\.]+$)");
            branchName = fileNameAndExtension[0];
        }

        String branchCommitsSha1 = Branch.getCommitSha1FromBranchFile(realPathToBranch);
        Path ObjectsFolderPath = Paths.get(realPathToBranch.getParent().getParent().toString() + "\\Objects");
        Commit branchCommit = Commit.CreateCommitFromSha1(branchCommitsSha1,ObjectsFolderPath);

        return new Branch(branchName, branchCommit);
    }

    //TODO: if there is more then one line throw exception
    private static String getCommitSha1FromBranchFile(Path i_Branch) throws FileNotFoundException
    {
        String commitsSha1 = null;
        Scanner lineScanner = new Scanner(i_Branch.toFile());
        while (lineScanner.hasNext())
        {
            commitsSha1 = lineScanner.nextLine();
        }
        return commitsSha1;
    }

    private static String extractBranchName(Path i_branchesPath) throws FileNotFoundException
    {
        Scanner lineScanner = new Scanner(i_branchesPath.toFile());
        String branchName = null;
        while (lineScanner.hasNext())
        {
            branchName = lineScanner.next();

        }
        return branchName;
    }

    public String getBranchName()
    {
        return m_BranchName;
    }

    public Commit getPointedCommit()
    {
        return m_PointedCommit;
    }

    public void SetCurrentCommit(Commit i_Commit)
    {
        m_PointedCommit = i_Commit;
    }
}


