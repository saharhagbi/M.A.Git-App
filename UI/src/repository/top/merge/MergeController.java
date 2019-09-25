package repository.top.merge;

import Objects.Item;
import common.MAGitUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import repository.top.TopController;

import javax.xml.soap.Text;

public class MergeController {
    @FXML
    private ListView<String> m_ConflictsListView;
    @FXML
    private Text m_ourVersionText;
    @FXML
    private Text m_TheirVersionText;
    @FXML
    private Text m_BaseVersionText;
    @FXML
    private ListView<String> m_SelectedBranchListView;
    @FXML
    private Text m_TheirText;
    @FXML
    private Button m_TakeResultVersionButton;
    @FXML
    private Button m_DeleteFileButton;
    @FXML
    private ListView<String> m_BranchesListView;

    private TopController m_TopController;

    public void setController(TopController topController) {
        m_TopController = topController;
        m_BranchesListView.setItems(m_TopController.GetBranchNameList());
    }

    @FXML
    void deleteFileButtonAction(ActionEvent event) {

    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event) {

    }

    @FXML
    void ChooseBranchBtn_OnClick(ActionEvent actionEvent) throws Exception {
        if (noItemWasChosen()) {
            MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Merge ERROR", "Please Choose a Branch To merge first", "");
        } else {
            String selectedItem = m_BranchesListView.getSelectionModel().getSelectedItems().get(0);
            if (isHeadBranchSelected(selectedItem)) {
                MAGitUtils.InformUserPopUpMessage(Alert.AlertType.ERROR, "Merge ERROR", "You chose the Head Branch", "please choose a different branch");
            } else {
                //MergeConflictsAndMergedItems conflicts = this.m_TopController.SetConflictsForMergeInRepository(selectedItem);
                this.m_TopController.SetConflictsForMergeInRepository(selectedItem);
                //if (conflicts.IsFastForwardCase()) {
                if (this.m_TopController.IsFastForwardCase()) {
                    //if (conflicts.IsPulledAncestorOfPulling()) {// chosen branch is an ancestor of HEAD branch
                    if (this.m_TopController.IsPulledAncestorOfPulling()) {
                        // point head branch to i_selectedBranch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - selected Branch is ancestor of head branch", "Head branch will point to current Commit\nno changes have been made");
                    } else {// HEAD branch is Ancestor of chosen branch
                        MAGitUtils.InformUserPopUpMessage(Alert.AlertType.INFORMATION, "Merge - Fast Forward", "This is a Fast Forward Merge - HEAD branch is Ancestor of selected branch", "HEAD branch will point to the same commit as selected branch");
                    }

                } else { //not FF

                    //m_ConflictsListView.setItems(this.m_TopController.GetConflictItemsNames());
                }
            }
        }
    }

    private boolean isHeadBranchSelected(String i_selectedItem) {
        return m_TopController.isHeadBranch(i_selectedItem);
    }


    private boolean noItemWasChosen() {
        if (m_BranchesListView.getSelectionModel().getSelectedItems().get(0) == null)
            return true;
        else return false;
    }

    public void conflictChose_OnClick(MouseEvent mouseEvent) {
        String conflictingItem = m_ConflictsListView.getSelectionModel().getSelectedItems().get(0);

        //Item pullingItem = this.m_TopController.GetPullingVersionOfConflict(conflictingItem);
        //m_ourVersionListView.setItems(pullingItem.content);

        //ObservableList<String> pulledItemDetails = this.m_TopController.GetPulledVersionOfConflictDetails(conflictingItem);
        //m_SelectedBranchListView.setItems(pulledItemDetails);

        //ObservableList<String> baseVersionItemDetails = this.m_TopController.GetBaseVersionOfConflictDetails(conflictingItem);
        //m_BaseVersionListView.setItems(baseVersionItemDetails );
    }
}
