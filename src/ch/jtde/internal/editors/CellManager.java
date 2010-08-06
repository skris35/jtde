/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import java.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.PrimitiveValue.Type;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * Helper managing the cell rendering and editing.
 * 
 * @author M. Hautle
 */
class CellManager {
    /** The parent widget. */
    private final Composite parent;

    /** The label provider. */
    private final LabelProvider labelProvider = new LabelProvider();

    /** The editor. */
    private final Editor editor;

    /** The already initialized editors. */
    @SuppressWarnings("rawtypes")
    private Map<Class, ICellEditor> editorChache = new HashMap<Class, ICellEditor>();

    /**
     * Default constructor.
     * 
     * @param parent
     */
    public CellManager(TableWidget parent) {
        this.parent = parent.getTable();
        editor = new Editor(parent.getViewer());
    }

    /**
     * Returns the label provider.
     * 
     * @return The label provider
     */
    ITableLabelProvider getLabelProvier() {
        return labelProvider;
    }

    /**
     * Returns the editing support.
     * 
     * @return The editing support
     */
    EditingSupport getEditingSupport() {
        return editor;
    }

    /**
     * Returns the editor for the given {@link IValueElement} type.
     * 
     * @param <E> The element type
     * @param type The element type
     * @return The editor or null
     */
    @SuppressWarnings("rawtypes")
    private <E extends IValueElement> ICellEditor getEditor(Class<E> type) {
        ICellEditor ed = editorChache.get(type);
        if (ed == null) {
            ed = ExtensionPointHelper.getEditorInstanceFor(type);
            // initialize and cache.
            if (ed != null) {
                ed.initialize(parent);
                editorChache.put(type, ed);
            }
        }
        return ed;
    }

    /**
     * Labelprovider which delegates the 'work' for the value columnt to a {@link ICellEditor}.
     * 
     * @author M. Hautle
     */
    private class LabelProvider implements ITableLabelProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("rawtypes")
        public String getColumnText(Object element, int columnIndex) {
            final IAttribute row = ((IAttribute) element);
            switch (columnIndex) {
                case 0:
                    return row.getName();
                case 1:
                    return getTypeString(row);
                case 2:
                    return row.getDeclaringClass();
                case 3:
                    final IDataElement<IAttribute> val = row.getValue();
                    if (val == null)
                        return ICellEditor.NULL_REPRESANTATION;
                    if (val instanceof IValueElement)
                        return getStringRepresentation((IValueElement) val);
                    return "<" + getTypeString(val.getType()) + ">";
            }
            return null;
        }

        /**
         * Returns a user readable string representation of the attribute type.
         * 
         * @param attr The attribute
         * @return String representation of the type
         */
        private String getTypeString(final IAttribute attr) {
            final ClassDefinition low = attr.getLowerBound();
            if (low == null)
                return null;
            return getTypeString(low);
        }

        /**
         * Returns a string representation of the given type.
         * 
         * @param type The type
         * @return A string representation
         */
        private String getTypeString(final ClassDefinition type) {
            final ElementCategory cat = type.getCategory();
            String name = type.getName();
            if (cat.isPrimitive() || ElementCategory.PRIMITIVE_ARRAY.equals(cat))
                name = Type.getType(type.getName()).getPrimitiveName();
            if (cat.isArray())
                name = name + "[" + type.getDimensions() + "]";
            return name;
        }

        /**
         * Returns the string representation of the given element.
         * 
         * @param val The value
         * @return The string representation
         */
        @SuppressWarnings({ "rawtypes", "unchecked" })
        private String getStringRepresentation(IValueElement val) {
            final ICellEditor ed = getEditor(val.getClass());
            if (ed != null)
                return ed.getRenderedValue(val);
            return getFallBackString(val);
        }

        /**
         * Fallback handler if no {@link ICellEditor} is registered for this element.
         * 
         * @param val The value
         * @return The string representation
         */
        @SuppressWarnings("rawtypes")
        private String getFallBackString(IValueElement val) {
            final Object cont = (val).getValue();
            return cont != null ? cont.toString() : ICellEditor.NULL_REPRESANTATION;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void addListener(ILabelProviderListener listener) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void removeListener(ILabelProviderListener listener) {
        }
    }

    /**
     * Celleditor delegating the 'work' to a {@link ICellEditor}.
     * 
     * @author hautle
     */
    private class Editor extends EditingSupport {
        /**
         * Default constructor.
         * 
         * @param viewer The viewer
         */
        public Editor(TableViewer viewer) {
            super(viewer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        protected boolean canEdit(Object element) {
            final IValueElement el = getValueElement(element);
            if (el == null)
                return false;
            final ICellEditor editor = getEditor(el.getClass());
            return editor != null && editor.isEditable(el);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("rawtypes")
        protected CellEditor getCellEditor(Object element) {
            final IAttribute attr = (IAttribute) element;
            final IDataElement<IAttribute> val = attr.getValue();
            final ICellEditor editor = getEditor(((IValueElement) val).getClass());
            return editor.getEditor(attr);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object getValue(Object element) {
            return getValueElement(element).getValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings({ "unchecked", "rawtypes" })
        protected void setValue(Object element, Object value) {
            final IValueElement val = getValueElement(element);
            if (val != null)
                val.setValue(value);
            getViewer().update(element, null);
        }

        /**
         * Returns the {@link IValueElement} hold by the given {@link IAttribute}, if any.
         * 
         * @param attr The attribute
         * @return The element or null if no {@link IValueElement} is present
         */
        @SuppressWarnings("rawtypes")
        private IValueElement getValueElement(Object attr) {
            final IDataElement<IAttribute> val = ((IAttribute) attr).getValue();
            if (!(val instanceof IValueElement))
                return null;
            return (IValueElement) val;
        }
    }
}
