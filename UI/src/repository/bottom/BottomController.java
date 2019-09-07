package repository.bottom;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import repository.RepositoryController;

public class BottomController
{
    private RepositoryController m_RepositoryController;

    @FXML
    private ProgressBar m_ProgressBar;
    @FXML
    private Label m_ProgressLabel;


    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public ProgressBar GetProgressBar()
    {
        return m_ProgressBar;
    }

    public Label GetLabelBar()
    {
        return m_ProgressLabel;
    }

    public void InitProgress(String i_Label)
    {
        m_ProgressBar.setProgress(-1);
        m_ProgressLabel.setText(i_Label);
    }

    public void UpdateProgress()
    {
        m_ProgressBar.setProgress(1);
        m_ProgressLabel.setText("Progress");

    }
}
