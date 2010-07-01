/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import java.beans.*;
import java.util.*;
import org.eclipse.jface.viewers.*;
import ch.jtde.editors.*;
import ch.jtde.model.*;
import ch.jtde.model.IExtendableDataElement.*;

/**
 * The model of {@link DataEditor}.
 * 
 * @author hautle
 */
class EditorModel implements IStructuredContentProvider {
    /** The model listeners. */
    private final List<IEditorModelListener> listeners = new ArrayList<IEditorModelListener>(2);

    /** Manager for {@link IAttribute}s and {@link IExtendableDataElement}s. */
    private final ListenerManager listenerManager = new ListenerManager();

    /** The root model. */
    private IDataElement<IAttribute> rootModel = null;

    /** The path from {@link #rootModel} (inclusive) to {@link #currentPart}. */
    private List<IDataElement<IAttribute>> path = new ArrayList<IDataElement<IAttribute>>();

    /** The currently displayed part of the model. */
    private IDataElement<IAttribute> currentPart;

    /**
     * Steps into the given element.
     * 
     * @param element The element index
     */
    @SuppressWarnings("unchecked")
    void stepInto(IDataElement element) {
        path.add(element);
        setCurrentElement(element);
    }

    /**
     * Steps up to the given index in the path.
     * 
     * @param index The index inside the path
     */
    void stepTo(int index) {
        // drop all elements after the index
        for (int i = path.size() - 1; i > index; i--)
            path.remove(i);
        setCurrentElement(path.get(index));
    }

    /**
     * Sets the attributes of the given element as current model.
     * 
     * @param el The element to set
     */
    private void setCurrentElement(final IDataElement<IAttribute> el) {
        currentPart = el;
        listenerManager.setElement(el);
        for (IEditorModelListener l : listeners)
            l.currentElementChanged(el);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        final int cnt = currentPart.getChildCount();
        final boolean ext = currentPart instanceof IExtendableDataElement;
        final Object[] data = new Object[cnt + (ext ? 1 : 0)];
        for (int i = 0; i < cnt; i++)
            data[i] = currentPart.getChild(i);
        // append the insert line for extendable elements
        if (ext)
            data[cnt] = InsertAttribute.ME;
        return data;
    }

    /**
     * Returns the current number of rows returned by {@link #getElements(Object)}.
     * 
     * @return The number of rows
     */
    @SuppressWarnings("unchecked")
    int getRowCount() {
        final int cnt = currentPart.getChildCount();
        return currentPart instanceof IExtendableDataElement ? cnt + 1 : cnt;
    }

    /**
     * Returns the root element of this model.
     * 
     * @return The root element
     */
    IDataElement<IAttribute> getRootElement() {
        return rootModel;
    }

    /**
     * Returns the currently displayed element.
     * 
     * @return The current element or null
     */
    IDataElement<IAttribute> getCurrentElement() {
        return currentPart;
    }

    /**
     * Adds the given {@link IEditorModelListener}s.
     * 
     * @param l The listener
     */
    void addModelListener(IEditorModelListener l) {
        listeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        rootModel = (IDataElement<IAttribute>) newInput;
        path.clear();
        path.add(rootModel);
        if (newInput != null)
            setCurrentElement(rootModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        rootModel = null;
        currentPart = null;
        listenerManager.detatchListeners();
        listeners.clear();
        path.clear();
    }

    /**
     * Listenermanager for {@link IAttribute}s and {@link IExtendableDataElement}s.<br>
     * It converts/propagates the events to all registered {@link IEditorModelListener}s.
     * 
     * @author M. Hautle
     */
    @SuppressWarnings("unchecked")
    private class ListenerManager implements PropertyChangeListener, IAttributeChangeListener {
        /** The currently observed element or null. */
        private IDataElement element;

        /**
         * Sets the element to observe.
         * 
         * @param el The new element
         */
        public void setElement(IDataElement el) {
            detatchListeners();
            element = el;
            if (el == null)
                return;
            // add extendableelement listener
            if (element instanceof IExtendableDataElement)
                ((IExtendableDataElement) element).addAttributeChangeListener(this);
            // add attribute listeners
            for (int i = 0, cnt = el.getChildCount(); i < cnt; i++)
                el.getChild(i).addPropertyChangeListener(this);
        }

        /**
         * Detaches the current listeners.
         */
        public void detatchListeners() {
            if (element == null)
                return;
            // drop extendableelement listener
            if (element instanceof IExtendableDataElement)
                ((IExtendableDataElement) element).removeAttributeChangeListener(this);
            // drop attribute listeners
            for (int i = 0, cnt = element.getChildCount(); i < cnt; i++)
                element.getChild(i).removePropertyChangeListener(this);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            final IAttribute attr = (IAttribute) evt.getSource();
            for (IEditorModelListener l : listeners)
                l.attributeChanged(attr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributesAdded(IExtendableDataElement element, IAttribute... attributes) {
            for (IAttribute a : attributes)
                a.addPropertyChangeListener(this);
            fireContentChanged();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void attributesRemoved(IExtendableDataElement element, IAttribute... attributes) {
            for (IAttribute a : attributes)
                a.removePropertyChangeListener(this);
            fireContentChanged();
        }

        /**
         * Fires a {@link IEditorModelListener#contentChanged(IDataElement)} event for the current model.
         */
        private void fireContentChanged() {
            for (IEditorModelListener l : listeners)
                l.contentChanged(currentPart);
        }
    }

    /**
     * Listener for {@link EditorModel}.
     * 
     * @author M. Hautle
     */
    interface IEditorModelListener {
        /**
         * Method called everty time something in the current {@link IDataElement} changes.
         * 
         * @param model The concerned element
         */
        void contentChanged(IDataElement<IAttribute> model);

        /**
         * Method called everty time the current {@link IDataElement} changes.
         * 
         * @param model The new model element
         */
        void currentElementChanged(IDataElement<IAttribute> model);

        /**
         * Method called after a attribute changed.
         * 
         * @param attr The changed attribute
         */
        void attributeChanged(IAttribute attr);
    }
}
