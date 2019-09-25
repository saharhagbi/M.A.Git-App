package System;

import Objects.Blob;
import Objects.Commit;
import Objects.Item;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MergeConflictsAndMergedItems {

    Set<Item> m_mergedItemsNotSorted;
    Set<ConflictingItems> m_conflictItems;
    Boolean m_IsFastForwardCase;
    Commit m_FastForwardCommit;
    Boolean m_IsPullingAncestorOfPulled;
    Boolean m_IsPulledAncestorOfPulling;
    Map<Path, Blob> m_MapOfRelativePathToItemPullingRootFolder;
    Map<Path, Blob> m_MapOfRelativePathToItemPulledRootFolder;
    Map<Path, Blob> m_MapOfRelativePathToItemAncestorRootFolder;


    public MergeConflictsAndMergedItems(Set<Item> i_MergedItemsNotSorted,
                                        Set<ConflictingItems> i_ConflictItems,
                                        Boolean i_IsFastForward,
                                        Commit i_FastForwardCommit,
                                        Boolean i_isPullingAncestorOfPulled,
                                        Boolean i_isPulledAncestorOfPulling,
                                        Map<Path, Blob> i_MapOfRelativePathToItemPullingRootFolder,
                                        Map<Path, Blob> i_MapOfRelativePathToItemPulledRootFolder,
                                        Map<Path, Blob> i_MapOfRelativePathToItemAncestorRootFolder) {

        m_mergedItemsNotSorted = i_MergedItemsNotSorted;
        m_conflictItems = i_ConflictItems;
        m_IsFastForwardCase = i_IsFastForward;
        m_FastForwardCommit = i_FastForwardCommit;
        m_IsPulledAncestorOfPulling = i_isPulledAncestorOfPulling;
        m_IsPullingAncestorOfPulled = i_isPullingAncestorOfPulled;
        m_MapOfRelativePathToItemPullingRootFolder = i_MapOfRelativePathToItemPullingRootFolder;
        m_MapOfRelativePathToItemPulledRootFolder = i_MapOfRelativePathToItemPulledRootFolder;
        m_MapOfRelativePathToItemAncestorRootFolder = i_MapOfRelativePathToItemAncestorRootFolder;
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

    //automatic take theirs
    public static final int ONLY_THEIRS_EXIST = 0b010000;                 //=16
    public static final int THEIRS_AND_BASE_ONLY_HAS_BUT_DIFFERENT = 0x011001;                 //=25
    //automatic take theirs

    //automatic take ours
    public static final int ONLY_OURS_EXISTS = 0b100000;                //=32
    public static final int ONLY_OURS_AND_BASE_HAS_WITH_DIFFERENCE = 0b101010;                //=42
    public static final int ONLY_OURS_AND_THEIRE_HAS_WITH_NO_DIFFERENCE = 0b110000;                //=48

    //automatic take ours

    //conflict
    public static final int ONLY_OURS_AND_THEIRS_HAS_WITH_DIFFERENCE = 0b110100;                //=52
    public static final int ALL_HAVE_BUT_WITH_DIFFERENCES = 0b111111;                //=63
    //conflict

    public static boolean isConflict(int i_itemState) {
        if (i_itemState == ONLY_OURS_AND_THEIRS_HAS_WITH_DIFFERENCE || i_itemState == ALL_HAVE_BUT_WITH_DIFFERENCES)
            return true;
        else
            return false;
    }

    public static boolean ShouldTakeOurs(int i_itemState) {
        if (i_itemState == ONLY_OURS_EXISTS ||
                i_itemState == ONLY_OURS_AND_BASE_HAS_WITH_DIFFERENCE ||
                i_itemState == ONLY_OURS_AND_THEIRE_HAS_WITH_NO_DIFFERENCE)
            return true;
        else return false;
    }


    public static boolean ShouldTakeTheirs(int i_itemState) {
        if (i_itemState == ONLY_THEIRS_EXIST ||
                i_itemState == THEIRS_AND_BASE_ONLY_HAS_BUT_DIFFERENT)
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
        List<String> conflictNamesList = new ArrayList<>();
        m_conflictItems.forEach(conflictingItem -> {
            conflictNamesList.add(conflictingItem.m_PullingItem.getName());
        });
        return FXCollections.observableList(conflictNamesList);
    }

    public Item GetPullingVersionOfConflictDetails(String i_conflictingItem) {
        ConflictingItems conflicting = getConflictingItemByName(i_conflictingItem);
        return conflicting.m_PullingItem;
    }

    private ConflictingItems getConflictingItemByName(String i_conflictingItem) {
        return m_conflictItems.stream().filter(item -> item.getName().equals(i_conflictingItem)).findFirst().orElse(null);
    }
}
