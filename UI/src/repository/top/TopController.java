package repository.top;

import Objects.branch.Branch;
import System.FolderDifferences;
import common.MAGitResourceConstants;
import common.MAGitUtils;
import common.constants.StringConstants;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import repository.RepositoryController;
import repository.top.merge.MergeController;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;
import System.ConflictingItems;

public class TopController
{

    @FXML
    private MenuBar m_MenuBar;
    @FXML
    private MenuItem m_SwitchRepoMenuItem;
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
    @FXML
    private MenuItem m_ResetBtn;
    @FXML
    private MenuItem m_FetchBtn;
    @FXML
    private MenuItem m_PullBtn;
    @FXML
    private MenuItem m_PushBtn;
    @FXML
    private Menu m_RemoteRepo;


    private RepositoryController m_RepositoryController;
    private List<Branch> m_AllBranches;
    private ObservableList<MenuItem> m_BranchesListMenuBar;
    private ObservableList<Text> m_BranchesList;


    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    @FXML
    void SwitchRepository_OnClick() throws IOException
    {
        Stage currentStage = MAGitUtils.GetStage(m_CommitBtn);
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
                        if (m_RepositoryController.ShowStatus() == null)
                        {
                            Platform.runLater(() ->
                            {
                                MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, StringConstants.COMMIT, "Can't Execute Commit"
                                        , StringConstants.NOTHING_TO_COMMIT_ON);
                            });
                        } else
                        {
                            doCommit();
                        }
                    }
                } catch (Exception e)
                {
                    Platform.runLater(() ->
                            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage())
                    );

                } finally
                {
                    updateMessage("Progress: ");
                    updateProgress(1, 1);
                }
                return null;
            }
        };

        bindTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), commitTask);
        commitTask.
                setOnSucceeded(event ->
                {
                    m_RepositoryController.UpdateCommitTable();
                    m_RepositoryController.UpdateCommitTree();
                    m_RepositoryController.ClearTableView();
                });

        new Thread(commitTask).start();
    }

    //check if needed to do it in generic method in repository controller
    private void bindTaskComponentsToUI(Label i_LabelBar, ProgressBar i_ProgressBar, Task i_CommitTask)
    {
        i_LabelBar.textProperty().bind(i_CommitTask.messageProperty());
        i_ProgressBar.progressProperty().bind(i_CommitTask.progressProperty());
    }

    private void doCommit() throws Exception
    {
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtils.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT
                ));

        Platform.runLater(futureTask);

        String commitMessage = futureTask.get();

        //commitMessage = MAGitUtils.GetString("Enter your commit message please", "Message:", StringConstants.COMMIT);
        m_RepositoryController.CommitChanges(commitMessage);
    }

    @FXML
    void SetUserName_OnClick(ActionEvent event)
    {
        try
        {
            // m_RepositoryController.InitProgress("Setting User..");
            String newUserName = MAGitUtils.GetString("Enter a new user name please", "Name", "Switch User");
            m_UserNameTxt.setText(newUserName);
            m_RepositoryController.SetUser(newUserName);
            //m_RepositoryController.UpdateProgress();

        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
    }

    public void InitAllComponentsInTop()
    {
        initPathAndUserName();
        initBranchesInComboBox();
        initBranchesInMenuBar();
        m_RemoteRepo.setDisable(!m_RepositoryController.IsLocalRepository());

    }

    private void initBranchesInMenuBar()
    {
        m_RepositoryController.getCurrentRepository().getAllBranches().stream()
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
        //m_RepositoryController.InitProgress("Deleting Branch...");
        try
        {

            m_RepositoryController.DeleteBranch(i_BranchNameToErase);
            //m_RepositoryController.UpdateProgress();
        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());

        }
    }

    public void UpdateBoardAfterDeletingBranch(String i_BranchNameToErase)
    {
        deleteBranchFromComboBox(i_BranchNameToErase);
        deleteBranchFromMenuBar(i_BranchNameToErase);
        //m_RepositoryController.UpdateCommitTree();
    }

    //todo:
    // see if can avoid duplicate,
    // can avoid duplicate! sending branch nametoerase and collection as param
    //one is m_DeletsBranchMenu.getItems(), and second m_BranchesListComboBox
    private void deleteBranchFromMenuBar(String i_BranchNameToErase)
    {
        m_DeletsBranchMenu.getItems().removeIf(branch -> branch.getText().equals(i_BranchNameToErase));
    }

    private void deleteBranchFromComboBox(String i_BranchNameToErase)
    {
        m_BranchesList.removeIf(branch -> branch.getText().equals(i_BranchNameToErase));
    }

    private void initBranchesInComboBox()
    {
        m_BranchesList = showListOfBranches(m_RepositoryController.getCurrentRepository().getAllBranches());
        m_ComboBoxBranches.setItems(m_BranchesList);
    }

    private ObservableList<Text> showListOfBranches(List<Branch> allBranches)
    {
        return FXCollections.observableList(allBranches
                .stream()
                .map(branch ->
                {
                    Text txt = new Text(branch.getBranchName());

                    if (m_RepositoryController.IsHeadBranch(branch.getBranchName()))
                        MAGitUtils.HighlightText(txt);

                    return txt;
                }).collect(Collectors.toList()));
    }

    private void hightlightHeadBranchInComboBox(boolean i_Highlight)
    {
        m_BranchesList
                .filtered(txt ->
                        txt.getText().equals(m_RepositoryController.getCurrentRepository().getActiveBranch().getBranchName()))
                .forEach(txt ->
                {
                    if (i_Highlight)
                        MAGitUtils.HighlightText(txt);
                    else
                        MAGitUtils.UnhighlightText(txt);
                });
    }

    private void addBranchToComboBox(Text i_BranchName)
    {
        m_ComboBoxBranches.getItems().add(i_BranchName);
    }

    private void initPathAndUserName()
    {
        m_UserNameTxt.setText(StringConstants.ADMINISTRATOR);
        m_PathTxt.setText(m_RepositoryController.getCurrentRepository().getRepositoryPath().toString());
    }

    @FXML
    void ShowBracnhes_OnClick(ActionEvent event) throws IOException
    {
        List<Branch> allBranches = m_RepositoryController.getCurrentRepository().getAllBranches();
        StringBuilder allBranchesInfo = new StringBuilder();

        allBranches
                .stream()
                .map((branch ->
                {
                    StringBuilder branchInfo = new StringBuilder();

                    if (m_RepositoryController.IsHeadBranch(branch.getBranchName()))
                        branchInfo.append("------>");

                    return branchInfo.append("Branch Name: " + branch.getBranchName() + System.lineSeparator())
                            .append("The SHA1 of pointed commit is: " + branch.getPointedCommit().getSHA1() + System.lineSeparator())
                            .append("Message of the pointedCommit is: " + branch.getPointedCommit().getCommitMessage()
                                    + System.lineSeparator() + System.lineSeparator());
                }))
                .forEach((branchInfo) ->
                        allBranchesInfo.append(branchInfo)
                );

        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Show All Branches",
                "Info of All Branches:", allBranchesInfo.toString());
    }

    @FXML
    void CreateBranch_OnClick(ActionEvent event)
    {
        try
        {
            m_RepositoryController.CreateNewBranch();

        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
    }

    public void updateBoardAfterCreatingNewBranch(String newBranch)
    {
        addBranchToComboBox(new Text(newBranch));
        addBranchToMenuBar(newBranch);

        m_RepositoryController.UpdateCommitTree();
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
                    Platform.runLater(() ->
                            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage())
                    );
                } finally
                {
                    updateTask();
                }
                return null;
            }

            private void updateTask()
            {
                updateProgress(1, 1);
                updateMessage("Progress:");
            }
        };

        bindTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), checkoutTask);
        new Thread(checkoutTask).start();

        checkoutTask.
                setOnSucceeded(evnt ->
                        m_RepositoryController.UpdateCommitTree());
    }

    private void getUserChoiceAndCheckout() throws Exception
    {
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtils.GetUserChoice("Changes in WC", "There were changes since last commit"
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
        /*List<?> branchesChoices;

        if (m_RepositoryController.IsLocalRepository())
        {
            LocalRepository localRepository = (LocalRepository) m_RepositoryController.getCurrentRepository();
            branchesChoices = localRepository.getRemoteTrackingBranches();
        } else
        {
            branchesChoices = m_RepositoryController.getCurrentRepository().getAllBranches();
        }
*/
        List<String> tempList = m_RepositoryController.getCurrentRepository().getActiveBranches()
                .stream()
                .map(branch -> ((Branch) branch).getBranchName())
                .collect(Collectors.toList());

        String[] choices = tempList.stream().toArray(String[]::new);

        //String[] choices = (String[]) tempList.toArray();
        FutureTask<String> futureTask = new FutureTask<String>(() ->
                MAGitUtils.GetUserChoice(
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
            String SHA1OfCommit = MAGitUtils.GetString("Enter the sha1 of the commit you want to reset to", "SHA1:",
                    StringConstants.COMMIT + " SHA1");

            m_RepositoryController.ResetHeadBranch(SHA1OfCommit);


        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
    }

    @FXML
    void ShowStatus_OnClick(ActionEvent event)
    {

        Task showStatusTask = new Task()
        {
            @Override
            protected Object call() throws Exception
            {
                try
                {
                    updateMessage("Show Status...");
                    FolderDifferences folderDifferences = m_RepositoryController.ShowStatus();

                    if (folderDifferences == null)
                        Platform.runLater(() ->
                        {
                            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Show Status", "No Changes",
                                    "There are no Changes since last commit");

                        });
                    else
                        m_RepositoryController.ShowDifferencesFiles(folderDifferences);
                } catch (Exception e)
                {
                    Platform.runLater(() ->
                            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage())
                    );
                } finally
                {
                    updateProgress(1, 1);
                    updateMessage(StringConstants.PROGRESS);
                    return null;
                }
            }
        };

        bindTaskComponentsToUI(m_RepositoryController.GetLabelBar(), m_RepositoryController.GetProgressBar(), showStatusTask);
        new Thread(showStatusTask).start();
    }

    @FXML
    void Fetch_OnClick(ActionEvent event)
    {
        try
        {
            // m_RepositoryController.InitProgress("Fetch...");

            m_RepositoryController.Fetch();

            //m_RepositoryController.UpdateProgress();
        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
    }

    @FXML
    void Pull_OnClick(ActionEvent event)
    {
        try
        {
            //   m_RepositoryController.InitProgress("Pull...");
            m_RepositoryController.Pull();
        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
        //    m_RepositoryController.UpdateProgress();
    }


    @FXML
    void Merge_OnClick(ActionEvent event) throws IOException
    {
        // show merge scene after user chose branch for the merge
        URL urlFXML = getClass().getResource(MAGitResourceConstants.MERGE_STAGE);
        FXMLLoader loader = new FXMLLoader(urlFXML);

        Parent root = loader.load(urlFXML.openStream());

        MergeController mergeController = loader.getController();
        mergeController.setController(this);

        Stage stage = new Stage();
        stage.setTitle(StringConstants.MERGE);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void Push_OnClick(ActionEvent event)
    {
        try
        {
            m_RepositoryController.Push();
        } catch (Exception e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!", e.getMessage(), e.getMessage());
        }
    }

    public void SetConflictsForMergeInRepository(String i_selectedBranchNameToMerge) throws Exception
    {
        m_RepositoryController.SetConflictsForMergeInRepository(i_selectedBranchNameToMerge);
    }

    public ObservableList<String> GetBranchNameList()
    {
        return FXCollections.observableList(this.m_RepositoryController.getCurrentRepository().getBranchNameList());
    }

    public boolean isHeadBranch(String i_BranchName)
    {
        return this.m_RepositoryController.getCurrentRepository().isHeadBranch(i_BranchName);

    }

    public boolean IsFastForwardCase()
    {
        return this.m_RepositoryController.IsFastForwardCase();
    }

    public boolean IsPulledAncestorOfPulling()
    {
        return this.m_RepositoryController.IsPulledAncestorOfPulling();
    }

    public ConflictingItems getConflictingItemsByName(String conflictingItemName)
    {
        return m_RepositoryController.getConflictingItemsByName(conflictingItemName);
    }

    public void CreateChosenBlobInWC(String blobText, ConflictingItems currentConflictingItem) throws IOException
    {
        m_RepositoryController.CreateChosenBlobInWC(blobText, currentConflictingItem);
    }
}
