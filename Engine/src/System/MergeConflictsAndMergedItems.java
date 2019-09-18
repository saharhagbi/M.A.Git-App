package System;

import Objects.Item;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Set;

public class MergeConflictsAndMergedItems {

    Set<Item> m_mergedItemsNotSorted;
    Set<ConflictingItems> m_conflictItems;
    Boolean m_IsFastForwardCase;

    public MergeConflictsAndMergedItems(Set<Item> i_MergedItemsNotSorted, Set<ConflictingItems> i_ConflictItems, Boolean i_IsFastForward) {
        m_mergedItemsNotSorted = i_MergedItemsNotSorted;
        m_conflictItems = i_ConflictItems;
    }

    // states for merge
    // bits built this way ->       ______              ______              ______                  ______                  ______                  ______
    //                          pulling existence    pulled existence   difference pull-pulled    pulling existence   ancestor existence    pulling-ancestor difference
    //automatic take pulled one
    public static final int ONLY_PULLED_HAS = 0x010000;                 //=16
    public static final int PULLING_AND_ANSCESTOR_THE_SAME_PULLED_IS_DIFFERENT = 0x111110;                 //=62

    //automatic take pulled

    //check if ancestor have no difference than pulled we can delete - else than pulled one used it and needs its so pull it
    public static final int PULLED_HAS_BUT_ALSO_ANSCESTOR = 0x010010;   //=18
    //check if ancestor have no difference than pulled we can delete - else than pulled one used it and needs its so pull it

    //automatic take pulling
    public static final int ONLY_PULLING_HAS = 0x100100;                //=36
    public static final int PULLING_AND_ANSCESTOR_HAS_WITH_NO_DIFFERENCE = 0x100110;                //=38
    public static final int PULLING_AND_ANSCESTOR_HAS_WITH_DIFFERENCE = 0x100111;                //=39
    public static final int PULLING_AND_PULLED_HAS_WITH_NO_DIFFERENCE = 0x110100;                //=52
    public static final int PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_NO_DIFFERENCE = 0x110110;                //=54
    public static final int PULLING_AND_PULLED_AND_ANSCETOR_HAS_WITH_DIFFERENCE = 0x110111;                //=55
    //automatic take pulling

    //conflict
    public static final int PULLING_AND_PULLED_HAS_WITH_DIFFERENCE = 0x111100;                //=60
    public static final int PULLING_PULLED_AND_ANSCESTOR_HAS_WITH_DIFFERENCE = 0x111111;                //=63
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

}