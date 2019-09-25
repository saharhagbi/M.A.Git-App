package repository.top.merge;

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
    private Button m_TakeResultVersionButton;
    @FXML
    private Button m_DeleteFileButton;
    @FXML
    private Button m_ChooseBranchBtn;
    @FXML
    private ListView<String> m_BranchesListView;
    @FXML
    private TextArea m_ResultTextArea;
    private TopController m_TopController;
    private ObservableList<String> m_ConflictsNameList;

    public void setController(TopController topController)
    {
        m_TopController = topController;

        /*------------------------Erase!!--------------------------------------*/
        m_ourVersionText.setText("a.txt content after change");
        m_BaseVersionText.setText("a.txt content");
        m_SelectedBranchText.setText("a.txt content in selected Branch");
        /*-------------------------------------------------------------------*/

        initAllComponents();
    }

    private void initAllComponents()
    {
        m_BranchesListView.setItems(m_TopController.GetActiveBranchesNameList());
    }

    private void createCommitMerge(String selectedBranchName) throws Exception
    {
        MAGitUtils.CloseCurrentStageByControl(m_DeleteFileButton);

        String commitMessage = MAGitUtils.GetString("Enter your commit message: ",
                "Message: ", StringConstants.COMMIT);

        m_TopController.CreateCommitMerge(commitMessage, selectedBranchName);
    }

    @FXML
    void deleteFileButtonAction(ActionEvent event)
    {
        m_BaseVersionText.setText(null);
        m_SelectedBranchText.setText(null);
        m_ourVersionText.setText(null);
        m_ResultTextArea.setText(null);
    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event)
    {
        /*try
        {*/
        removeConflictSelected();

        // m_TopController.CreateChosenBlobInWC(m_ResultTextArea.getText(), m_CurrentConflictingItem);
        /*} catch (IOException e)
        {
            //todo
            e.printStackTrace();
        }*/
    }

    private void removeConflictSelected()
    {
        m_ConflictsNameList.remove(m_BranchesListView.getSelectionModel().getSelectedItems().get(0));
        loadConflictsView();
    }

    @FXML
    void ChooseBranchBtn_OnClick(ActionEvent actionEvent) throws Exception
    {
        m_ChooseBranchBtn.setDisable(true);

        if (noItemWasChosen())
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Merge ERROR", "Please Choose a Branch To merge first", "");
        } else
        {
            String selectedItem = m_BranchesListView.getSelectionModel().getSelectedItems().get(0);
            if (isHeadBranchSelected(selectedItem))
            {
                MAGitUtils.CloseCurrentStageByControl(m_DeleteFileButton);
            } else
            {
                //MergeConflictsAndMergedItems conflicts = this.m_TopController.SetConflictsForMergeInRepository(selectedItem);
                this.m_TopController.SetConflictsForMergeInRepository(selectedItem);
                //if (conflicts.IsFastForwardCase()) {
                if (this.m_TopController.IsFastForwardCase())
                {
                    //if (conflicts.IsPulledAncestorOfPulling()) {// chosen branch is an ancestor of HEAD branch
                    if (this.m_TopController.IsPulledAncestorOfPulling())
                    {
                        // point head branch to i_selectedBranch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - selected Branch is ancestor of head branch", "Head branch will point to current Commit\nno changes have been made");
                    } else
                    {// HEAD branch is Ancestor of chosen branch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - HEAD branch is Ancestor of selected branch", "HEAD branch will point to the same commit as selected branch");
                    }

                } else
                { //not FF
                    m_ConflictsNameList = GetAllConflictsNames();

                    loadConflictsView();
                    initAllMergeComponents();
                }
            }
        }
    }

    private void loadConflictsView()
    {
        m_ConflictsListView.setItems(GetAllConflictsNames());
    }

    private void initAllMergeComponents()
    {
        m_ConflictsNameList.addListener((ListChangeListener<String>) c ->
        {
            if (m_ConflictsNameList.isEmpty())
            {
                try
                {
                    createCommitMerge(m_BranchesListView.getSelectionModel().getSelectedItems().get(0));
                } catch (Exception e)
                {
                    //todo
                    e.printStackTrace();
                }
            }
        });

        m_OurVersionScrollPane.setOnMouseClicked(event -> m_ResultTextArea.setText(m_ourVersionText.getText()));
        m_SelectedBranchScrollPane.setOnMouseClicked(event -> m_ResultTextArea.setText(m_SelectedBranchText.getText()));
        m_BaseVersionScrollPane.setOnMouseClicked(event -> m_ResultTextArea.setText(m_BaseVersionText.getText()));
    }

    private ObservableList<String> GetAllConflictsNames()
    {
        return m_TopController.GetAllConflictsNames();
    }

    private boolean isHeadBranchSelected(String i_selectedItem)
    {
        return m_TopController.isHeadBranch(i_selectedItem);
    }

    private boolean noItemWasChosen()
    {
        return m_BranchesListView.getSelectionModel().getSelectedItems().get(0) == null;
    }

    public void conflictChose_OnClick(MouseEvent mouseEvent)
    {
        String conflictingItemName = m_ConflictsListView.getSelectionModel().getSelectedItems().get(0);

        m_CurrentConflictingItem = this.m_TopController.getConflictingItemsByName(conflictingItemName);

        //assign values
        m_BaseVersionText.setText(m_CurrentConflictingItem.getBaseVersionBlob().getContent());
        m_SelectedBranchText.setText(m_CurrentConflictingItem.getTheirBlob().getContent());
        m_ourVersionText.setText(m_CurrentConflictingItem.getOurBlob().getContent());
    }
}
