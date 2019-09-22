package common;

import javafx.scene.text.Text;

import Objects.branch.Branch;
import javafx.collections.ObservableList;


import java.util.stream.Collectors;

public class ObservListCombinder
{
    public static ObservableList<Text> merge(ObservableList<Text> into, ObservableList<Text>... lists)
    {
        final ObservableList<Text> list = into;
        for (ObservableList<Text> l : lists)
        {
            list.addAll(l);
            l.addListener((javafx.collections.ListChangeListener.Change<? extends Text> c) ->
            {
                while (c.next())
                {
                    if (c.wasAdded())
                        list.addAll((Text) c.getAddedSubList());

                    if (c.wasRemoved())
                        list.removeAll(c.getRemoved());
                }
            });
        }
        return list;
    }
}
