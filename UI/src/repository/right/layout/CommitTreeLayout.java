package repository.right.layout;

import Objects.Commit;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.layout.Layout;

import java.util.*;
import java.util.stream.Collectors;

// simple test for scattering commits in imaginary tree, where every 3rd node is in a new 'branch' (moved to the right)
public class CommitTreeLayout implements Layout
{
    private final Map<Commit, ICell> mf_MapCommitToIcell;
    private double m_LayoutX = 10;
    private double m_LayoutY = 50;

    public CommitTreeLayout(Map<Commit, ICell> i_MapCommitToIcell)
    {
        mf_MapCommitToIcell = i_MapCommitToIcell;
    }

    @Override
    public void execute(Graph graph)
    {
        List<Commit> commitListByOrder = sortListByOrderOfCreation();
        Map<String, Commit> mapPrevSHA1ToCommitFather = initMap();
        List<Commit> openCommits = new ArrayList<Commit>();

        /*= initSet(mapPrevSHA1ToCommitFather);*/
        /*ICell branchNode = new BranchNode("master");
        graph.getModel().addCell(branchNode);
*/
        //relocateOpenCommits(openCommits, graph);

        for (Commit currentCommit : commitListByOrder)
        {
            Commit commitSon;

            commitSon = findSon(currentCommit, openCommits);
            if (commitSon != null)
            {
                ICell sonICell = mf_MapCommitToIcell.get(commitSon);
                double layoutSonX = graph.getGraphic(sonICell).getLayoutX();
               /* Commit commitFather = mapPrevSHA1ToCommitFather.get(currentCommit.getSHA1());
                mapPrevSHA1ToCommitFather.values().removeIf(commit -> commit == commitFather);

                if (thereIsNotAnotherSon(commitFather, mapPrevSHA1ToCommitFather))
                {*/
                relocateCommit(graph, currentCommit, layoutSonX);
                openCommits.add(currentCommit);
                openCommits.remove(commitSon);

                //relocate below
            } else
            {
                m_LayoutX += 50;
                relocateCommit(graph, currentCommit, m_LayoutX);
                openCommits.add(currentCommit);
            }

               /* openCommits.remove(commitFather);
                openCommits.add(currentCommit);*/
        } /*else
        {
            relocateOpenCommit(currentCommit, graph);
        }*/
    }


    private Commit findSon(Commit i_CurrentCommit, List<Commit> i_OpenCommits)
    {
        return i_OpenCommits
                .stream()
                .filter(commit ->
                {
                    boolean firstPrev = false, secondPrev = false;

                    if (thereIsPrevCommit(commit.GetPrevCommit()))
                        firstPrev = commit.GetPrevCommit().getSHA1().equals(i_CurrentCommit.getSHA1());

                    if (thereIsPrevCommit(commit.GetSecondPrevCommit()))
                        secondPrev = commit.GetSecondPrevCommit().getSHA1().equals(i_CurrentCommit.getSHA1());

                    return secondPrev || firstPrev;
                })
                .findAny()
                .orElse(null);
    }

    private void relocateCommit(Graph i_Graph, Commit i_CurrentCommit, double i_LayoutXToRelocate)
    {
//        ICell sonICell = mf_MapCommitToIcell.get(i_CurrentCommit);
        ICell fatherICell = mf_MapCommitToIcell.get(i_CurrentCommit);


//        double layoutXFather = i_Graph.getGraphic(fatherICell).getLayoutX();
        m_LayoutY += 50;

        /*if (isSonFound)
        {*/
        i_Graph.getGraphic(fatherICell).relocate(i_LayoutXToRelocate, m_LayoutY);
        /*} else
        {*/

//        i_Graph.getGraphic(sonICell).relocate(layoutXFather + 50, m_LayoutY);
    }
//        increaseLayouts();


   /* private boolean thereIsNotAnotherSon(Commit i_CommitFather, Map<String, Commit> i_MapPrevSHA1ToCommitFather)
    {
        return !i_MapPrevSHA1ToCommitFather.containsValue(i_CommitFather);
    }

    private void relocateOpenCommit(Commit i_CommitToRelocate, Graph i_Graph)
    {
        ICell cell = mf_MapCommitToIcell.get(i_CommitToRelocate);
        increaseLayouts();


        i_Graph.getGraphic(cell).relocate(m_LayoutX, m_LayoutY);
    }

    private void increaseLayouts()
    {
        m_LayoutX += 50;
        m_LayoutY += 50;
    }


    /*private void relocateOpenCommits(List<Commit> i_OpenCommits, Graph i_Graph)
    {
        int startX = 10;
        int startY = 100;

        for (Commit commit : i_OpenCommits)
        {
            CommitNode cell = (CommitNode) mf_MapCommitToIcell.get(commit);
            i_Graph.getGraphic(cell).relocate(startX, startY);

            startX += 50;
            startY += 50;
        }
    }*/

   /* private List<Commit> initSet(Map<String, Commit> i_MapPrevSHA1ToCommitFather)
    {
        return mf_MapCommitToIcell
                .keySet()
                .stream()
                .filter(commit ->
                        i_MapPrevSHA1ToCommitFather.get(commit.getSHA1()) == null)
                .collect(Collectors.toList());
    }*/

    private List<Commit> sortListByOrderOfCreation()
    {
        List<Commit> temp = mf_MapCommitToIcell
                .keySet()
                .stream()
                .sorted(Comparator.comparing(Commit::GetDate))
                .collect(Collectors.toList());

        Collections.reverse(temp);

        return temp;
    }

    private Map<String, Commit> initMap()
    {
        Map<String, Commit> mapPrevSHA1ToCommitFather = new HashMap<>();

        mf_MapCommitToIcell
                .keySet()
                .stream()
                .forEach(commit ->
                {
                    addPrevSha1AndCommit(mapPrevSHA1ToCommitFather, commit, commit.GetPrevCommit());
                    addPrevSha1AndCommit(mapPrevSHA1ToCommitFather, commit, commit.GetSecondPrevCommit());
                });

        return mapPrevSHA1ToCommitFather;
    }

    private void addPrevSha1AndCommit(Map<String, Commit> mapPrevSHA1ToCommitFather, Commit commit, Commit prevCommit)
    {
        if (thereIsPrevCommit(prevCommit))
            mapPrevSHA1ToCommitFather.put(prevCommit.getSHA1(), commit);
    }

    private boolean thereIsPrevCommit(Commit i_GetPrevCommit)
    {
        return i_GetPrevCommit != null;
    }
}
