package repository.right.node.branch;

import com.fxgraph.cells.AbstractCell;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.IEdge;
import common.MAGitResourceConstants;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;
import java.net.URL;

public class BranchNode extends AbstractCell
{
    private String m_Name;
    private BranchNodeController branchNodeController;

    public BranchNode(String i_Name)
    {
        m_Name = i_Name;
    }

    @Override
    public Region getGraphic(Graph graph)
    {
        HBox root = null;
        try
        {
            URL branchNodeFXML = BranchNodeController.class.getResource(MAGitResourceConstants.STARTING_SCENE);
            FXMLLoader loader = new FXMLLoader(branchNodeFXML);

            loader.setLocation(branchNodeFXML);
            root = loader.load(branchNodeFXML.openStream());


        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return root;
    }
/*
    @Override
    public DoubleBinding getXAnchor(Graph graph, IEdge edge) {
        final Region graphic = graph.getGraphic(this);
        return graphic.layoutXProperty().add(branchNodeController.);
    }*/
}
