package repository.top.merge;

import Objects.Blob;
import Objects.Folder;
import System.ConflictingItems;
import common.MAGitUtils;
import common.constants.StringConstants;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import repository.top.TopController;

import java.io.IOException;
import java.util.Set;


public class MergeController
{
    ConflictingItems m_CurrentConflictingItem;
    @FXML
    private ListView<String> m_ConflictsListView;
    @FXML
    private Text m_ourVersionText;
    @FXML
    private Text m_SelectedBranchText;
    @FXML
    private Text m_BaseVersionText;
    @FXML
    private ScrollPane m_BaseVersionScrollPane;
    @FXML
    private ScrollPane m_SelectedBranchScrollPane;
    @FXML
    private ScrollPane m_OurVersionScrollPane;
    @FXML
    private Button m_ChooseBranchBtn;
    @FXML
    private Button m_DeleteBtn;
    @FXML
    private Button m_TakeResultBtn;
    @FXML
    private ListView<String> m_BranchesListView;
    @FXML
    private TextArea m_ResultTextArea;

    private TopController m_TopController;
    private ObservableList<String> m_ConflictsNameList;

    private boolean isTheirBlob = false;
    private boolean isOurBlob = false;
    private boolean isBaseBlob = false;

    public void setController(TopController topController)
    {
        m_TopController = topController;

        initAllComponents();
    }

    private void initAllComponents()
    {
        m_BranchesListView.setItems(m_TopController.GetActiveBranchesNameList());

        disabeButtons(true);
    }

    private void createCommitMerge() throws Exception
    {
        MAGitUtils.GetStage(m_ChooseBranchBtn).close();

        String commitMessage = MAGitUtils.GetString("Enter your merge commit message: ",
                "Message: ", StringConstants.COMMIT);

        m_TopController.CreateCommitMerge(commitMessage, m_BranchesListView.getSelectionModel().getSelectedItems().get(0));
    }

    @FXML
    void deleteFileButtonAction(ActionEvent event)
    {
        clearAllTexts();

        removeConflictSelected();
    }

    private void clearAllTexts()
    {
        m_BaseVersionText.setText(null);
        m_SelectedBranchText.setText(null);
        m_ourVersionText.setText(null);
        m_ResultTextArea.setText(null);
    }

    private void removeConflictSelected()
    {
        m_ConflictsNameList.remove(m_ConflictsListView.getSelectionModel().getSelectedItems().get(0));
        loadConflictsView();
    }

    @FXML
    void ChooseBranchBtn_OnClick(ActionEvent actionEvent)
    {
        try
        {
            String selectedBranch = m_BranchesListView.getSelectionModel().getSelectedItems().get(0);
            this.m_TopController.SetConflictsForMergeInRepository(selectedBranch);

            if (isHeadBranchSelected(selectedBranch))
            {
                MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!",
                        "Head Branch",
                        "can not merge head branch to head branch");
                MAGitUtils.GetStage(m_ChooseBranchBtn).close();
            } else
            {
                //MergeConflictsAndMergedItems conflicts = this.m_TopController.SetConflictsForMergeInRepository(selectedBranch);
                //if (conflicts.IsFastForwardCase()) {
                if (this.m_TopController.IsFastForwardCase())
                {
                    //if (conflicts.IsPulledAncestorOfPulling()) {// chosen branch is an ancestor of HEAD branch
                    if (this.m_TopController.IsSelectedBranchAncestorOfHeadBranch())
                    {
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - selected Branch is ancestor of head branch", "Head branch will point to current Commit\nno changes have been made");
                    } else
                    {
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - HEAD branch is Ancestor of selected branch", "HEAD branch will point to the same commit as selected branch");
                        m_TopController.getRepositoryController().FastForwardBranch(selectedBranch);
                        //  m_TopController.getRepositoryController().FastForwardBranch(selectedBranch, false);
                    }
                    MAGitUtils.GetStage(m_ChooseBranchBtn).close();
                } else
                { //not FF
                    Folder.RemoveFilesAndFoldersWithoutMagit(m_TopController.getRepositoryController().getCurrentRepository()
                            .getActiveBranch().getPointedCommit().getRootFolder().GetPath());


                    m_ChooseBranchBtn.setDisable(true);
                    m_ConflictsNameList = GetAllConflictsNames();

                    if (m_ConflictsNameList.size() == 0)
                        prepareAllForCommitMerge();

                    loadConflictsView();
                    initAllMergeComponents();
                }
            }
        } catch (Exception e)
        {

            e.printStackTrace();
           /* MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!",
                    "Error Occured",
                    e.getMessage());*/
        }
    }

    private void loadConflictsView()
    {
        m_ConflictsListView.setItems(m_ConflictsNameList);
    }

    private void initAllMergeComponents()
    {
        m_ConflictsNameList.addListener((ListChangeListener<String>) c ->
        {
            if (m_ConflictsNameList.isEmpty())
            {
                try
                {
                    prepareAllForCommitMerge();
                } catch (Exception e)
                {
                    MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!",
                            "Writing blob",
                            "Error in access to writting  blob in WC for merge");
                }
            }
        });

        m_OurVersionScrollPane.setOnMouseClicked(event ->
        {
            m_ResultTextArea.setText(m_ourVersionText.getText());
            setAllFalse();
            isOurBlob = true;

        });
        m_SelectedBranchScrollPane.setOnMouseClicked(event ->
        {
            m_ResultTextArea.setText(m_SelectedBranchText.getText());
            setAllFalse();
            isTheirBlob = true;
        });
        m_BaseVersionScrollPane.setOnMouseClicked(event ->
        {
            m_ResultTextArea.setText(m_BaseVersionText.getText());
            setAllFalse();
            isBaseBlob = true;
        });
    }

    private void prepareAllForCommitMerge() throws Exception
    {
        writeAllMergedBlobsInWC();
        m_ChooseBranchBtn.setDisable(false);
        createCommitMerge();
    }

    private void writeAllMergedBlobsInWC() throws IOException
    {
        Set<Blob> mergedItems = m_TopController.getRepositoryController().
                getCurrentRepository().getConflictsItemsAndNames().getMergedItemsNotSorted();

        for (Blob blob : mergedItems)
        {
            m_TopController.CreateChosenBlobInWC(blob.getContent(), blob);
        }
    }

    private void setAllFalse()
    {
        isOurBlob = false;
        isBaseBlob = false;
        isTheirBlob = false;
    }

    private ObservableList<String> GetAllConflictsNames()
    {
        return m_TopController.GetAllConflictsNames();
    }

    private boolean isHeadBranchSelected(String i_selectedItem)
    {
        return m_TopController.isHeadBranch(i_selectedItem);
    }

    public void conflictChose_OnClick(MouseEvent mouseEvent)
    {
        clearAllTexts();
        disabeButtons(false);

        String conflictingItemName = m_ConflictsListView.getSelectionModel().getSelectedItems().get(0);

        m_CurrentConflictingItem = this.m_TopController.getConflictingItemsByName(conflictingItemName);

        //assign values
        if (m_CurrentConflictingItem.getBaseVersionBlob() != null)
            m_BaseVersionText.setText(m_CurrentConflictingItem.getBaseVersionBlob().getContent());

        if (m_CurrentConflictingItem.getTheirBlob() != null)
            m_SelectedBranchText.setText(m_CurrentConflictingItem.getTheirBlob().getContent());

        if (m_CurrentConflictingItem.getOurBlob() != null)
            m_ourVersionText.setText(m_CurrentConflictingItem.getOurBlob().getContent());
    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event)
    {
        disabeButtons(true);
        Blob chosenBlob = takeFitBlob();

        try
        {
            m_TopController.CreateChosenBlobInWC(m_ResultTextArea.getText(), chosenBlob);

            removeConflictSelected();
        } catch (IOException e)
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Error!",
                    "Writing blob",
                    "Error in access to writting  blob in WC for merge");
        }
    }

    private void disabeButtons(boolean isDisable)
    {
        m_DeleteBtn.setDisable(isDisable);
        m_TakeResultBtn.setDisable(isDisable);
    }

    private Blob takeFitBlob()
    {
        if (isBaseBlob)
            return m_CurrentConflictingItem.getBaseVersionBlob();

        if (isTheirBlob)
            return m_CurrentConflictingItem.getTheirBlob();

        if (isOurBlob)
            return m_CurrentConflictingItem.getOurBlob();
        else return null;
    }
}