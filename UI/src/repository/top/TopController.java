package repository.top;

import System.Branch;
import System.Repository;
import common.MAGitResourceConstants;
import common.MAGitUtilities;
import common.StringConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import repository.RepositoryController;

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
    @FXML
    private MenuItem m_CheckoutMenuItem;
    private RepositoryController m_RepositoryController;
    // see if needed this property
    private List<Branch> m_AllBranches;
    private ObservableList<MenuItem> m_BranchesListMenuBar;
    private ObservableList<Text> m_BranchesListComboBox;


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
    void Commit_OnClick()
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
                        if (m_RepositoryController.ShowStatus() != null)
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
        initBranchesInComboBox();
        initBranchesInMenuBar();
    }

    private void initBranchesInMenuBar()
    {
       /* m_BranchesListMenuBar = FXCollections.observableList(m_AllBranches
                .stream()
                .map(branch -> new MenuItem(branch.getBranchName()))
                .collect(Collectors.toList()));
*/
        m_AllBranches
                .stream()
                .forEach(branchItem -> addBranchToMenuBar(branchItem.getBranchName()));
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
                deleteBranchFromMenuBar(i_BranchNameToErase);
                MAGitUtilities.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Deleting Branch", "Deleting Branch",
                        "Branch was deleted Successfully!");
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

    // see if can avoid duplicate
    private void deleteBranchFromMenuBar(String i_BranchNameToErase)
    {
        m_DeletsBranchMenu.getItems().removeIf(branch -> branch.getText().equals(i_BranchNameToErase));
    }

    private void deleteBranchFromComboBox(String i_BranchNameToErase)
    {
        m_BranchesListComboBox.removeIf(branch -> branch.getText().equals(i_BranchNameToErase));
    }

    private void initBranchesInComboBox()
    {
        m_BranchesListComboBox = FXCollections.observableList(m_AllBranches
                .stream()
                .map(branch ->
                {
                    Text txt = new Text(branch.getBranchName());

                    if (isHeadBranch(branch.getBranchName()))
                        MAGitUtilities.HighlightText(txt);

                    return txt;
                }).collect(Collectors.toList()));

        m_ComboBoxBranches.setItems(m_BranchesListComboBox);
    }

    private void hightlightHeadBranchInComboBox(boolean i_Highlight)
    {
        m_BranchesListComboBox
                .filtered(txt ->
                        txt.getText().equals(m_RepositoryController.GetCurrentRepository().getActiveBranch().getBranchName()))
                .forEach(txt ->
                {
                    if (i_Highlight)
                        MAGitUtilities.HighlightText(txt);
                    else
                    {
                        MAGitUtilities.UnhighlightText(txt);
                        /*m_BranchesListComboBox.remove(txt);
                        addBranchToComboBox(new Text(txt.getText()));*/
                    }
                });
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
        String newBranch;
        String SHA1Commit;
        try
        {
            newBranch = MAGitUtilities.GetString("Enter the name of the new Branch", "Name", "New Branch");
            SHA1Commit = MAGitUtilities.GetString("Enter the SHA1 of the commit you want the branch will point",
                    "SHA1:", "Commit SHA1");

            //Todo:
            // thomas function of creating new commit needed
            m_RepositoryController.CreateNewBranch(newBranch, SHA1Commit);
            addBranchToComboBox(new Text(newBranch));
            addBranchToMenuBar(newBranch);
        } catch (Exception e)
        {
            //Todo:
            // handle exception, inside get string or proper to each case proper message
            e.printStackTrace();
        }
    }

    @FXML
    void Checkout_OnClick(ActionEvent event)
    {
        Task checkoutTask = new Task()
        {
            @Override
            protected Object call()
            {
                try
                {
                    updateMessage("Checkout...");
                    if (m_RepositoryController.RootFolderChanged())
                    {
                        getUserChoiceAndCheckout();
                    } else
                    {
                        getBranchNameAndCheckOut();
                    }
                } catch (Exception e)
                {
                    //Todo:
                    // handle with proper message
                    e.printStackTrace();
                } finally
                {
                    updateProgress(1, 1);
                    updateMessage("Progress:");
                }
                return null;
            }
        };

        bindCommitTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), checkoutTask);
        new Thread(checkoutTask).start();
    }

    private void getUserChoiceAndCheckout() throws Exception
    {
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtilities.GetUserChoice("Changes in WC", "There were changes since last commit"
                        + System.lineSeparator() +
                        "Are you sure you want to continue?" + System.lineSeparator() +
                        "Choose no, to commit first", StringConstants.YES, new String[]{StringConstants.YES, StringConstants.NO}));
        Platform.runLater(futureTask);

        String answer = futureTask.get();
        handleUserChoice(answer);
    }

    private void handleUserChoice(String i_Answer) throws Exception
    {
        if (i_Answer.equals(StringConstants.NO))
            doCommit();

        getBranchNameAndCheckOut();
    }

    private void getBranchNameAndCheckOut() throws Exception
    {
        List<String> tempList = m_AllBranches
                .stream()
                .map(branch -> branch.getBranchName())
                .collect(Collectors.toList());

        String[] choices = tempList.stream().toArray(String[]::new);

        //String[] choices = (String[]) tempList.toArray();
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtilities.GetUserChoice(
                        "Branch Name", "Now, Choose one branch from below for checkout", "choose branch here",
                        choices)
        );

        Platform.runLater(futureTask);

        String branchName = futureTask.get();
        hightlightHeadBranchInComboBox(false);
        m_RepositoryController.CheckOut(branchName);
        hightlightHeadBranchInComboBox(true);
    }

    @FXML
    void Reset_OnClick(ActionEvent event)
    {
        try
        {
            String SHA1OfCommit = MAGitUtilities.GetString("Enter the sha1 of the commit you want to reset to", "SHA1:",
                    StringConstants.COMMIT + " SHA1");

            m_RepositoryController.ResetHeadBranch(SHA1OfCommit);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}



