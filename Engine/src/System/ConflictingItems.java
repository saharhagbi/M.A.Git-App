package System;

import Objects.Item;

public class ConflictingItems {
    Item m_PullingItem;
    Item m_PulledItem;
    Item m_BaseVersionItem;
    public ConflictingItems(Item i_PullingItem,Item i_PulledItem,Item i_BaseVersionItem)
    {
        m_PullingItem = i_PullingItem;
        m_PulledItem = i_PulledItem;
        m_BaseVersionItem = i_BaseVersionItem;
    }
}
