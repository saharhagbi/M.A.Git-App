package repository.right;

import Objects.Commit;
import Objects.Item;
import System.Branch;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import com.fxgraph.graph.PannableCanvas;
import common.NumConstants;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import repository.RepositoryController;
import repository.right.layout.CommitTreeLayout;
import repository.right.node.CommitNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RightController
{
    private RepositoryController m_RepositoryController;
    private Map<Commit, ICell> m_MapCommitToIcell = new HashMap<>();
    private Graph m_TreeGraph;
    //  private Map<String, Branch> m_MapSHA1CommitToBranch = new HashMap<>();

    @FXML
    private ScrollPane m_TreeScrollPane;

    public void SetRepositoryController(RepositoryController i_RepositoryController)
    {
        this.m_RepositoryController = i_RepositoryController;
    }

    public void InitAllComponentsInRight()
    {
        if (m_RepositoryController.getCurrentRepository().getActiveBranch().getPointedCommit() == null)
            return;

        //initCommitToBranchMap();

        ResetCommitsTree();
    }

    public void ResetCommitsTree()
    {
        m_TreeGraph = new Graph();

        initComponentsInTree();

        PannableCanvas canvas = m_TreeGraph.getCanvas();
        m_TreeScrollPane.setContent(canvas);
    }

    private void initComponentsInTree()
    {
        m_TreeGraph.beginUpdate();

        createCommits();
        addEdgesToModel(m_TreeGraph.getModel());

        m_TreeGraph.endUpdate();
        m_TreeGraph.layout(new CommitTreeLayout(m_MapCommitToIcell));
    }

    private void addEdgesToModel(Model i_Model)
    {
        m_MapCommitToIcell
                .keySet()
                .stream()
                .forEach(commit -> addEdgesToCommit(commit, i_Model));
    }

    private void addEdgesToCommit(Commit i_Commit, Model i_Model)
    {
        if (thereIsPrevCommit(i_Commit.GetPrevCommit()))
        {
            final Edge edge = new Edge(m_MapCommitToIcell.get(i_Commit), m_MapCommitToIcell.get(i_Commit.GetPrevCommit()));
            i_Model.addEdge(edge);
        }

        if (thereIsPrevCommit(i_Commit.GetSecondPrevCommit()))
            i_Model.addEdge(new Edge(m_MapCommitToIcell.get(i_Commit), m_MapCommitToIcell.get(i_Commit.GetSecondPrevCommit())));
    }

    private boolean thereIsPrevCommit(Commit i_GetPrevCommit)
    {
        return i_GetPrevCommit != null;
    }

    private void createCommits()
    {
        m_RepositoryController.getCurrentRepository().getAllCommitsSHA1ToCommit()
                .values()
                .stream()
                .forEach(commit ->
                {
                    CommitNode commitNode = new CommitNode
                            (Item.getDateStringByFormat(commit.GetDate()),
                                    commit.getUserCreated().getUserName(),
                                    commit.getCommitMessage());

                    List<Branch> branchesOfCommit = getBranchesPointOn(commit);
                    if (branchesOfCommit.size() != NumConstants.ZERO)
                    {
                        String branchesString = appendBranchNames(branchesOfCommit);
                        commitNode.SetBranchName(branchesString);
                    }

                    //todo:
                    // get an event of pressed on gridpane??
                    /*commitNode.getGraphic(m_TreeGraph).lookup("CommitCircle")
                            .setOnMouseClicked(event -> m_RepositoryController.ShowDeltaCommits(commit));*/
                    m_TreeGraph.getGraphic(commitNode).setOnMouseClicked(event -> m_RepositoryController.ShowDeltaCommits(commit));

                    //     commitNode.getGraphic(tree).lookup("");
                    m_TreeGraph.getModel().addCell(commitNode);

                    m_MapCommitToIcell.put(commit, commitNode);
                });
    }

    private String appendBranchNames(List<Branch> i_BranchesOfCommit)
    {
        List<String> branchNames = i_BranchesOfCommit
                .stream()
                .map(branch -> branch.getBranchName())
                .collect(Collectors.toList());

        return String.join(", ", branchNames);
    }

    private List<Branch> getBranchesPointOn(Commit i_Commit)
    {
        return m_RepositoryController.getCurrentRepository().getAllBranches()
                .stream()
                .filter(branch -> branch.getPointedCommit() == i_Commit)
                .collect(Collectors.toList());
    }

    public void ResetHeadBranchInTree(Commit i_Commit)
    {
        m_TreeGraph.layout(new CommitTreeLayout(m_MapCommitToIcell));

        /*Commit currentCommit = m_RepositoryController.getCurrentRepository().getActiveBranch().getPointedCommit();
        CommitNode currentCommitNode = (CommitNode) m_MapCommitToIcell.get(currentCommit);
        currentCommitNode.SetBranchName(null);

        String headBranchName  = m_RepositoryController.getCurrentRepository().getActiveBranch().getBranchName();
        CommitNode commitNodeToResetTo = (CommitNode) m_MapCommitToIcell.get(i_Commit);
        commitNodeToResetTo.SetBranchName(headBranchName);*/
    }
}
