package repository.top.merge;

import Objects.branch.Branch;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import primaryController.PrimaryController;
import repository.top.TopController;
import System.MergeConflictsAndMergedItems;

public class MergeController
{

    @FXML
    private ListView<?> m_ConflictsListView;
    @FXML
    private TextArea m_OurTextArea;
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

    private TopController m_TopController;

    public void setController(TopController topController)
    {
        m_TopController = topController;
    }

    void chooseBranchToMerge(Branch i_selectedBranchToMerge, PrimaryController primaryController)
    {
        try {
            MergeConflictsAndMergedItems conflicts=  this.m_TopController.GetConflictsForMerge(i_selectedBranchToMerge);
            if(conflicts.IsFastForwardCase())
            {
                // point head branch to i_selectedBranch
            }
            else{
                // 1. show user conflicts let him choose which he wants
                // 2. take chosen items and add to -> conflicts.GetMergedItemsNotSorted();
                // 3. create new folder FromNotSorted mergedItems
                // 4. create the new commit and point the branch to it
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void deleteFileButtonAction(ActionEvent event)
    {

    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event)
    {

    }

}
