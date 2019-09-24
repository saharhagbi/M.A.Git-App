package System;

import Objects.Commit;
import Objects.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MergeConflictsAndMergedItems {

    Set<Item> m_mergedItemsNotSorted;
    Set<ConflictingItems> m_conflictItems;
    Boolean m_IsFastForwardCase;
    Commit m_FastForwardCommit;
    Boolean m_IsPullingAncestorOfPulled;
    Boolean m_IsPulledAncestorOfPulling;


    public MergeConflictsAndMergedItems(Set<Item> i_MergedItemsNotSorted, Set<ConflictingItems> i_ConflictItems, Boolean i_IsFastForward, Commit i_FastForwardCommit, Boolean i_isPullingAncestorOfPulled, Boolean i_isPulledAncestorOfPulling) {
        m_mergedItemsNotSorted = i_MergedItemsNotSorted;
        m_conflictItems = i_ConflictItems;
        m_IsFastForwardCase = i_IsFastForward;
        m_FastForwardCommit = i_FastForwardCommit;
        m_IsPulledAncestorOfPulling = i_isPulledAncestorOfPulling;
        m_IsPullingAncestorOfPulled = i_isPullingAncestorOfPulled;
    }

    public Boolean IsFastForwardCase() {
        return m_IsFastForwardCase;
    }

    public Set<Item> GetMergedItemsNotSorted() {
        return m_mergedItemsNotSorted;
    }

    public Set<ConflictingItems> GetConflictItems() {
        return m_conflictItems;
    }

    // states for merge
    // bits built this way ->       ______              ______              ______                  ______                  ______                  ______
    //                          pulling existence    pulled existence   difference pull-pulled    pulling existence   ancestor existence    pulling-ancestor difference
    //automatic take pulled one
    public static final int ONLY_PULLED_HAS = 0b010000;                 //=16
    public static final int PULLING_AND_ANSCESTOR_THE_SAME_PULLED_IS_DIFFERENT = 0x111110;                 //=62

    //automatic take pulled

    //check if ancestor have no difference than pulled we can delete - else than pulled one used it and needs its so pull it
    public static final int PULLED_HAS_BUT_ALSO_ANSCESTOR = 0b010010;   //=18
    //check if ancestor have no difference than pulled we can delete - else than pulled one used it and needs its so pull it

    //automatic take pulling
    public static final int ONLY_PULLING_HAS = 0b100100;                //=36
    public static final int PULLING_AND_ANSCESTOR_HAS_WITH_NO_DIFFERENCE = 0b100110;                //=38
    public static final int PULLING_AND_ANSCESTOR_HAS_WITH_DIFFERENCE = 0b100111;                //=39
    public static final int PULLING_AND_PULLED_HAS_WITH_NO_DIFFERENCE = 0b110100;                //=52
    public static final int PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_NO_DIFFERENCE = 0b110110;                //=54
    public static final int PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_DIFFERENCE = 0b110111;                //=55
    //automatic take pulling

    //conflict
    public static final int PULLING_AND_PULLED_HAS_WITH_DIFFERENCE = 0b111100;                //=60
    public static final int PULLING_PULLED_AND_ANSCESTOR_HAS_WITH_DIFFERENCE = 0b111111;                //=63
    //conflict

    public static boolean isConflict(int i_itemState) {
        if (i_itemState == PULLING_AND_PULLED_HAS_WITH_DIFFERENCE || i_itemState == PULLING_PULLED_AND_ANSCESTOR_HAS_WITH_DIFFERENCE)
            return true;
        else
            return false;
    }

    public static boolean ShouldTakePullingItem(int i_itemState) {
        if (i_itemState == ONLY_PULLING_HAS ||
                i_itemState == PULLING_AND_ANSCESTOR_HAS_WITH_DIFFERENCE ||
                i_itemState == PULLING_AND_ANSCESTOR_HAS_WITH_NO_DIFFERENCE ||
                i_itemState == PULLING_AND_PULLED_HAS_WITH_NO_DIFFERENCE ||
                i_itemState == PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_NO_DIFFERENCE ||
                i_itemState == PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_DIFFERENCE)
            return true;
        else return false;
    }


    public static boolean ShouldTakePulledItem(int i_itemState) {
        if (i_itemState == ONLY_PULLED_HAS ||
                i_itemState == PULLING_AND_ANSCESTOR_THE_SAME_PULLED_IS_DIFFERENT)
            return true;
        else return false;
    }

    public static boolean NeedToCheckWithAncstor(int i_itemState) {
        if (i_itemState == PULLED_HAS_BUT_ALSO_ANSCESTOR)
            return true;
        else return false;
    }

    public Boolean IsPullingAncestorOfPulled() {
        return m_IsPullingAncestorOfPulled;
    }

    public Boolean IsPulledAncestorOfPulling() {
        return m_IsPulledAncestorOfPulling;
    }

    public ObservableList<String> GetConflictItemsNames() {
        List<String > conflictNamesList = new ArrayList<>();
        m_conflictItems.forEach(conflictingItem -> {
            conflictNamesList.add(conflictingItem.m_PullingItem.getName());
        });
        return FXCollections.observableList(conflictNamesList);
    }

}
