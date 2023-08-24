package org.skyme.ui.model;

import javax.swing.*;
import java.util.Vector;

/**
 * @author:Skyme
 * @create: 2023-08-18 21:13
 * @Description:
 */
public class FriendListModel<E> extends AbstractListModel<E> {
    private Vector<E> delegate = new Vector<E>();
    @Override
    public int getSize() {
        return delegate.size();
    }
    public void addElement(E element) {
        int index = delegate.size();
        delegate.addElement(element);
        fireIntervalAdded(this, index, index);
    }

    @Override
    public E getElementAt(int index) {
        return delegate.elementAt(index);
    }
}
