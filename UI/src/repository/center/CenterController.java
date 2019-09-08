package repository.center;

import Objects.Commit;
import System.User;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import repository.RepositoryController;

import java.io.IOException;
import java.util.Date;

public class CenterController
{
    private RepositoryController m_RepositoryController;
    @FXML
    private TableView<Commit> m_TableView;
    @FXML
    private TableColumn<Commit, String> m_MessageColumn;
    @FXML
    private TableColumn<Commit, String> m_SHA1Column;
    @FXML
    private TableColumn<Commit, Date> m_DateColumn;
    @FXML
    private TableColumn<Commit, User> m_AuthorColumn;

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    @FXML
    public void initialize()
    {
        m_TableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        preventColumnReordering(m_TableView);

        m_MessageColumn.setCellValueFactory(new PropertyValueFactory("Message"));
        m_AuthorColumn.setCellValueFactory(new PropertyValueFactory("Author"));
        m_DateColumn.setCellValueFactory(new PropertyValueFactory("Date"));
        m_SHA1Column.setCellValueFactory(new PropertyValueFactory("SHA1"));

//        bindSelectedCommitChangedToMainController();

    }

    private void bindSelectedCommitChangedToMainController()
    {
        m_TableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Commit>()
        {
            @Override
            public void changed(ObservableValue<? extends Commit> observable, Commit oldValue, Commit newValue)
            {
                if (newValue != null)
                {
                    String commitSHA1 = m_SHA1Column.getCellData(m_TableView.getSelectionModel().getSelectedIndex());
                    /*try
                    {*/
                        m_RepositoryController.newCommitSelectedOnCenterTableView(newValue, commitSHA1);
                    /*}
                    catch (IOException e)
                    {
                        //TODO
                        e.printStackTrace();
                    }*/
                }
            }
        });
    }
    private <T> void preventColumnReordering(TableView<T> tableView)
    {
        Platform.runLater(() ->
        {
            for (Node header : tableView.lookupAll(".column-header"))
            {
                header.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
            }
        });
    }

}
