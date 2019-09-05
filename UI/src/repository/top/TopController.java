package repository.top;

import System.Branch;
import System.Repository;
import common.MAGitResourceConstants;
import common.MAGitUtilities;
import common.StringConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import repository.RepositoryController;
import java.lang.String;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

public class TopController
{

    @FXML
    private MenuBar m_MenuBar;
    @FXML
    private MenuItem m_SwitchRepoMenuItem;
    @FXML
    private MenuItem m_ExportToXmlMenuItem;
    @FXML
    private MenuItem m_SetUserNameMenuItem;
    @FXML
    private MenuItem m_ShowBranches;
    @FXML
    private Menu m_DeletsBranchMenu;
    @FXML
    private ComboBox m_ComboBoxBranches;
    @FXML
    private Text m_UserNameTxt;
    @FXML
    private Text m_PathTxt;
    @FXML
    private Control m_CommitBtn;
    private RepositoryController m_RepositoryController;
    private List<Branch> m_AllBranches;


    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    @FXML
    void SwitchRepository_OnClick() throws IOException
    {
        Stage currentStage = MAGitUtilities.GetStage(m_CommitBtn);
        m_RepositoryController.SwitchScenes(MAGitResourceConstants.STARTING_SCENE, currentStage);
    }

    @FXML
    void Commit_OnClick(ActionEvent event)
    {
        Task commitTask = new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                try
                {
                    updateMessage("Commit...");
                    if (m_RepositoryController.IsFirstCommit())
                    {
                        doCommit();
                    } else
                    {
                        if (m_RepositoryController.ShowStatus().equals(StringConstants.NOTHING_TO_COMMIT_ON))
                        {
                            Platform.runLater(() ->
                            {
                                MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.INFORMATION, StringConstants.COMMIT, "Can't Execute Commit"
                                        , StringConstants.NOTHING_TO_COMMIT_ON);
                            });
                        } else
                        {
                            doCommit();
                        }
                    }
                } catch (Exception commitException)
                {
                    //TODO:
                    // handle proper message if commit failed(look at exceptions from ui)
                    commitException.printStackTrace();
                } finally
                {
                    updateMessage("Progress: ");
                    updateProgress(1, 1);
                }
                return null;
            }
        };

        bindCommitTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), commitTask);
        new Thread(commitTask).start();

        // todo: finish update components when commit is added
      /*  commitTask.
                setOnSucceeded(() ->
                        m_RepositoryController.UpdateTableColumnAccordingToLastCommit(),
                m_RepositoryController.AddNodeCommitToTree());*/

        //function below if needed to execute to one function
    }

    //check if needed to do it in generic method in repository controller
    private void bindCommitTaskComponentsToUI(Label i_LabelBar, ProgressBar i_ProgressBar, Task i_CommitTask)
    {
        i_LabelBar.textProperty().bind(i_CommitTask.messageProperty());
        i_ProgressBar.progressProperty().bind(i_CommitTask.progressProperty());
    }

    private void doCommit() throws Exception
    {
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtilities.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT
                ));

        Platform.runLater(futureTask);

        String commitMessage = futureTask.get();

        //commitMessage = MAGitUtilities.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT);
        m_RepositoryController.CommitChanges(commitMessage);
    }

    @FXML
    void SetUserName_OnClick(ActionEvent event)
    {
        try
        {
            m_UserNameTxt.setText(
                    MAGitUtilities.GetString("Enter a new user name please", "Name", "Switch User"
                    ));
        } catch (Exception e)
        {
            //TODO:
            // handle exception in case of cancelling or 'X'
            e.printStackTrace();
        }
    }

    public void InitAllComponentsInTop(Repository i_CurrentRepository)
    {
        initPathAndUserName(i_CurrentRepository.getRepositoryPath());

        m_AllBranches = m_RepositoryController.GetCurrentRepository().getAllBranches();
        /*initBranchesInComboBox();
        initBranchesInMenuBar();*/


        List<Text> list = m_AllBranches.stream().map(branch ->{
                if(isHeadBranch(branch.getBranchName()))
                    new Text(branch.getBranchName());
                    else

                }
        //ObservableList<Text> branchesList = FXCollections(() -> m_AllBranches.stream());



        m_ComboBoxBranches.setItems(branchesList);



    }

    private void initBranchesInMenuBar()
    {
        //   List<Branch> allBranches = m_RepositoryController.GetCurrentRepository().getAllBranches();

        for (Branch branch : m_AllBranches)
        {
            addBranchToMenuBar(branch.getBranchName());
        }
    }

    private void addBranchToMenuBar(String i_BranchName)
    {
        MenuItem newBranch = new MenuItem(i_BranchName);
        newBranch.setOnAction((event -> deleteBranch(i_BranchName)));
        m_DeletsBranchMenu.getItems().add(newBranch);
    }

    private void deleteBranch(String i_BranchNameToErase)
    {
        try
        {
            if (!isHeadBranch(i_BranchNameToErase))
            {
                m_RepositoryController.DeleteBranch(i_BranchNameToErase);
                deleteBranchFromComboBox(i_BranchNameToErase);
                //deleteBranchFromMenuBar(i_BranchNameToErase);
            } else
            {
                MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.ERROR, "Deleting Head Branch", "Error!",
                        "Can not delete head branch");
            }
        } catch (Exception e)
        {
            //Todo:
            // handle exception proper message!
            e.printStackTrace();
        }
    }

    private void deleteBranchFromComboBox(String i_branchNameToErase)
    {
        //m_ComboBoxBranches
    }

    private void initBranchesInComboBox()
    {
        // List<Branch> allBranches = m_RepositoryController.GetCurrentRepository().getAllBranches();

       // ObservableArray<Branch> observableArray =


        for (Branch branch : m_AllBranches)
        {
            Text branchName = new Text(branch.getBranchName());

            if (isHeadBranch(branch.getBranchName()))
            {
                branchName.setStyle("-fx-font-weight: bold; -fx-stroke: #2638ff");
            }
            addBranchToComboBox(branchName);
        }
    }

    private boolean isHeadBranch(String i_BranchName)
    {
        return i_BranchName.equals(m_RepositoryController.GetCurrentRepository().getActiveBranch().getBranchName());
    }

    private void addBranchToComboBox(Text i_BranchName)
    {
        m_ComboBoxBranches.getItems().add(i_BranchName);
    }

    private void initPathAndUserName(Path i_RepositoryPath)
    {
        m_UserNameTxt.setText(StringConstants.ADMINISTRATOR);
        m_PathTxt.setText(i_RepositoryPath.toString());
    }

    @FXML
    void ShowBracnhes_OnClick(ActionEvent event) throws IOException
    {
        List<Branch> allBranches = m_RepositoryController.GetCurrentRepository().getAllBranches();
        StringBuilder allBranchesInfo = new StringBuilder();

        allBranches
                .stream()
                .map((branch ->
                {
                    StringBuilder branchInfo = new StringBuilder();

                    if (isHeadBranch(branch.getBranchName()))
                        branchInfo.append("------>");

                    return branchInfo.append("Branch Name: " + branch.getBranchName() + System.lineSeparator())
                            .append("The SHA1 of pointed commit is: " + branch.getCurrentCommit().getSHA1() + System.lineSeparator())
                            .append("Message of the pointedCommit is: " + branch.getCurrentCommit().getCommitMessage()
                                    + System.lineSeparator() + System.lineSeparator());
                }))
                .forEach((branchInfo) ->
                        allBranchesInfo.append(branchInfo)
                );

        MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Show All Branches",
                "Info of All Branches:", allBranchesInfo.toString());
    }

           /* Node wrapTxt = Borders.wrap(new TextArea(branchInfo.toString()))
                    .lineBorder().color(Color.GREEN)
                    .thickness(1)
                    .radius(0,5, 5, 0)
                    .build().build();*/

    @FXML
    void CreateBranch_OnClick(ActionEvent event)
    {
        String newBranch = null;
        String SHA1Commit = null;
        try
        {
            newBranch = MAGitUtilities.GetString("Enter the name of the new Branch", "Name", "New Branch");
            SHA1Commit = MAGitUtilities.GetString("Enter the SHA1 of the commit you want the branch will point",
                    "SHA1:", "Commit SHA1");

            //todo:
            // find commit pointed by sha1
            m_RepositoryController.CreateNewBranch(newBranch);
            addBranchToComboBox(new Text(newBranch));
            addBranchToMenuBar(newBranch);
        } catch (Exception e)
        {
            //Todo:
            // handle exception, inside get string or proper to each case proper message
            e.printStackTrace();
        }
    }
}



