package repository.top.merge;

import System.MergeConflictsAndMergedItems;
import common.MAGitUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import repository.top.TopController;

public class MergeController
{
    @FXML
    private ListView<String> m_ConflictsListView;
    @FXML
    private ListView<String> m_ourVersionListView;
    @FXML
    private ListView<String> m_BaseVersionListView;
    @FXML
    private ListView<String> m_SelectedBranchListView;

    @FXML
    private TextArea m_AncestorTextArea;
    @FXML
    private TextArea m_TheirTextArea;
    @FXML
    private TextArea m_ResultTextArea;
    @FXML
    private Button m_TakeResultVersionButton;
    @FXML
    private Button m_DeleteFileButton;
    @FXML
    private ListView<String> m_BranchesListView;

    private TopController m_TopController;

    public void setController(TopController topController)
    {
        m_TopController = topController;
        m_BranchesListView.setItems(m_TopController.GetBranchNameList());
    }

    @FXML
    void deleteFileButtonAction(ActionEvent event)
    {

    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event)
    {

    }

    @FXML
    void ChooseBranchBtn_OnClick(ActionEvent actionEvent) throws Exception
    {
        if (noItemWasChosen())
        {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Merge ERROR", "Please Choose a Branch To merge first", "");
        } else
        {
            String selectedItem = m_BranchesListView.getSelectionModel().getSelectedItems().get(0);
            if (isHeadBranchSelected(selectedItem))
            {
                MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Merge ERROR", "You chose the Head Branch", "please choose a different branch");
            } else
            {
                MergeConflictsAndMergedItems conflicts = this.m_TopController.GetConflictsForMerge(selectedItem);
                if (conflicts.IsFastForwardCase())
                {
                    if (conflicts.IsPulledAncestorOfPulling())
                    {// chosen branch is an ancestor of HEAD branch
                        // point head branch to i_selectedBranch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - selected Branch is ancestor of head branch", "Head branch will point to current Commit\nno changes have been made");

                    } else
                    {// HEAD branch is Ancestor of chosen branch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - HEAD branch is Ancestor of selected branch", "HEAD branch will point to the same commit as selected branch");
                    }
                } else
                { //not FF

                    m_ConflictsListView.setItems(conflicts.GetConflictItemsNames());
                    //m_ourVersionListView.setItems(conflicts.GetPullingItemsInConflictNames());
                    //m_BaseVersionListView.setItems(conflicts.GetBaseVersionItemsInConflictNames());
                    //m_SelectedBranchListView.setItems(conflicts.GetPulledItemsInConflictNames());
                    // 1. show user conflicts let him choose which he wants
                    // 2. take chosen items and add to -> conflicts.GetMergedItemsNotSorted();
                    // 3. create new folder FromNotSorted mergedItems

                    // 4. create the new commit and point the branch to it


                }


            }
        }
    }

    private boolean isHeadBranchSelected(String i_selectedItem)
    {
        return m_TopController.isHeadBranch(i_selectedItem);
    }


    private boolean noItemWasChosen()
    {
        if (m_BranchesListView.getSelectionModel().getSelectedItems().get(0) == null)
            return true;
        else return false;
    }
}
