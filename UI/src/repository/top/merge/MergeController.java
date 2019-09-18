package repository.top.merge;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import repository.top.TopController;

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

    @FXML
    void deleteFileButtonAction(ActionEvent event)
    {

    }

    @FXML
    void takeResultVersionButtonAction(ActionEvent event)
    {

    }

}
