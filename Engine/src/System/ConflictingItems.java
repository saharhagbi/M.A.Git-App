package System;

import Objects.Blob;
import Objects.Item;

public class ConflictingItems {
    Blob m_PullingItem;
    Blob m_PulledItem;
    Blob m_BaseVersionItem;

    public ConflictingItems(Blob i_PullingItem, Blob i_PulledItem, Blob i_BaseVersionItem) {
        m_PullingItem = i_PullingItem;
        m_PulledItem = i_PulledItem;
        m_BaseVersionItem = i_BaseVersionItem;
    }

    public String getName() {
        if (m_PullingItem != null && !m_PullingItem.equals("null")) {
            return m_PullingItem.getName();
        } else if (m_PulledItem != null && !m_PulledItem.equals("null"))
            return m_PulledItem.getName();
        else return null;
    }
}
