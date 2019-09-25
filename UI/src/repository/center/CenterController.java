package repository.center;

import Objects.Commit;
import Objects.Item;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import repository.RepositoryController;

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
    private TableColumn<Commit, String> m_DateColumn;
    @FXML
    private TableColumn<Commit, String> m_AuthorColumn;

    private ObservableList<Commit> m_CommitsObservableList;

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public void InitAllComponentsInCenter()
    {
        m_TableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        preventColumnReordering(m_TableView);

        m_MessageColumn.setCellValueFactory(commit ->
                new SimpleStringProperty(commit.getValue().getCommitMessage()));

        m_AuthorColumn.setCellValueFactory(commit ->
                new SimpleStringProperty(commit.getValue().getUserCreated().getUserName()));

        m_DateColumn.setCellValueFactory(commit ->
                new SimpleStringProperty(Item.getDateStringByFormat(commit.getValue().GetDate())));

        m_SHA1Column.setCellValueFactory(commit ->
                new SimpleStringProperty(commit.getValue().getSHA1()));

        InitObservCommitList();
        loadCommitsInTableView();

        bindSelectedCommitChangedToMainController();
    }

    public void loadCommitsInTableView()
    {
        m_TableView.setItems(m_CommitsObservableList);
    }

    public void InitObservCommitList()
    {
        m_CommitsObservableList = FXCollections.observableArrayList();
        m_RepositoryController.getCurrentRepository().getAllCommitsSHA1ToCommit()
                .values()
                .stream()
                .sorted((commit1, commit2) -> commit2.GetDate().compareTo(commit1.GetDate()))
                .forEach(commit -> m_CommitsObservableList.add(commit));
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
                    m_RepositoryController.NewCommitSelectedOnCenterTableView(newValue);
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

    public void AddCommitToObservList(Commit i_NewLastCommit)
    {
        m_CommitsObservableList.add(0,i_NewLastCommit);
        loadCommitsInTableView();
    }
}
