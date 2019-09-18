package System;

import Objects.Item;

public class ConflictingItems {
    Item m_PullingItem;
    Item m_PulledItem;
    public ConflictingItems(Item i_PullingItem,Item i_PulledItem)
    {
        m_PullingItem = i_PullingItem;
        m_PulledItem = i_PulledItem;
    }
}
