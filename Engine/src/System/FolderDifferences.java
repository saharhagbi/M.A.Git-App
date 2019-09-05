package System;

import Objects.Item;

import java.util.ArrayList;
import java.util.List;

public class FolderDifferences {
    List<Item> m_AddedItemList;
    List<Item> m_RemovedItemList;
    List<Item> m_ChangedItemList;

    public FolderDifferences (){
        m_AddedItemList = new ArrayList<>();
        m_RemovedItemList = new ArrayList<>();
        m_ChangedItemList = new ArrayList<>();
    }

    public void AddToAddedItemList(Item i_AddedItem){
        m_AddedItemList.add(i_AddedItem);
    }
    public void AddToRemovedItemList(Item i_RemovedItem){
        m_AddedItemList.add(i_RemovedItem);
    }
    public void AddToChangedItemList(Item i_ChangedItem){
        m_AddedItemList.add(i_ChangedItem);
    }

    public void AddAnEntireFolderDiffernce(FolderDifferences i_FolderDifference){
        for(int i=0;i<i_FolderDifference.m_AddedItemList.size();i++){
            m_AddedItemList.add(i_FolderDifference.m_AddedItemList.get(i));
        }
        for(int i=0;i<i_FolderDifference.m_ChangedItemList.size();i++){
            m_ChangedItemList.add(i_FolderDifference.m_ChangedItemList.get(i));
        }
        for(int i=0;i<i_FolderDifference.m_RemovedItemList.size();i++){
            m_RemovedItemList.add(i_FolderDifference.m_RemovedItemList.get(i));
        }
    }

    public void AddFolderRecursivelyToAddedItemList(Item i_FolderItem) {
        //TODO: finish
    }

    public void AddFolderRecursivelyToRemovedItemList(Item item) {
        //TODO: finish
    }
}

