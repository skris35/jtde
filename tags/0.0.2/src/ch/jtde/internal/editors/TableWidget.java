/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Extended {@link TableViewer}.
 * 
 * @author M. Hautle
 */
public class TableWidget {
    /** The table sorter. */
    private Sorter sorter = new Sorter();

    /** The viewer. */
    private TableViewer viewer;

    /**
     * Default constructor.
     * 
     * @param parent The parent widget
     * @param style The SWT style to use
     */
    public TableWidget(Composite parent, int style) {
        viewer = new TableViewer(parent, style);
        final Table table = viewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        viewer.setSorter(sorter);
    }

    /**
     * Adds a column to the given viewer.
     * 
     * @param name The name of the column
     * @param size The size of the column
     * @return The added column
     */
    public TableViewerColumn addColumn(final String name, int size) {
        final Table table = viewer.getTable();
        final TableViewerColumn tc = new TableViewerColumn(viewer, SWT.LEFT);
        final TableColumn col = tc.getColumn();
        col.setText(name);
        col.setWidth(size);
        col.setMoveable(true);
        col.setResizable(true);
        col.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event e) {
                final boolean toggle = table.getSortColumn() == col;
                final int oldDir = table.getSortDirection();
                final int dir = getDirection(oldDir, toggle);
                sorter.setColumn(table.indexOf(col));
                sorter.setSortAscending(dir == SWT.UP);
                table.setSortDirection(dir);
                // set the column or reset it
                table.setSortColumn(toggle && oldDir == SWT.NONE ? null : col);
                viewer.refresh();
            }

            /**
             * Returns the new sort direction.
             * 
             * @param oldDir The old sort direction
             * @param toggle True to toggle
             * @return The new sort direction
             */
            private int getDirection(int oldDir, final boolean toggle) {
                if (!toggle)
                    return SWT.UP;
                if (oldDir == SWT.UP)
                    return SWT.DOWN;
                return SWT.NONE;
            }
        });
        return tc;
    }

    /**
     * Returns the table.
     * 
     * @return The table
     */
    public Table getTable() {
        return viewer.getTable();
    }

    /**
     * Returns the viewer.
     * 
     * @return The viewer
     */
    public TableViewer getViewer() {
        return viewer;
    }

    /**
     * Sets the label provider for the viewer.
     * 
     * @param labelProvier The label provider
     */
    public void setLabelProvider(ITableLabelProvider labelProvier) {
        viewer.setLabelProvider(labelProvier);
    }

    /**
     * Sets the content provider.
     * 
     * @param provider The provider
     */
    public void setContentProvider(IContentProvider provider) {
        viewer.setContentProvider(provider);
    }

    /**
     * Adds a selection change listener.
     * 
     * @param listener The listener to add
     */
    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        viewer.addSelectionChangedListener(listener);
    }

    /**
     * Returns the current selection.
     * 
     * @return The selection
     */
    public IStructuredSelection getSelection() {
        return (IStructuredSelection) viewer.getSelection();
    }

    /**
     * Sets the input of the viewer.
     * 
     * @param input The viewer input or null
     */
    public final void setInput(Object input) {
        viewer.setInput(input);
    }

    /**
     * Sets the number of rows in the table.
     * 
     * @param count The number of rows
     */
    public void setItemCount(int count) {
        viewer.setItemCount(count);
    }

    /**
     * Refreshes the viewer.
     */
    public void refresh() {
        viewer.refresh();
    }

    /**
     * Updates the representation of the given element.
     * 
     * @param element The changed element
     */
    public void update(Object element) {
        viewer.update(element, null);
    }

    /**
     * Table sorter.
     * 
     * @author hautle
     */
    private class Sorter extends ViewerSorter {
        /** The sort order. */
        private boolean sortAscending = false;

        /** The column used for the order. */
        private int column = 0;

        /**
         * Sets the sort order.
         * 
         * @param asc True if the list should be sorted in ascending order
         */
        void setSortAscending(boolean asc) {
            sortAscending = asc;
        }

        /**
         * Sets the column used for the ordering.
         * 
         * @param index The column index
         */
        void setColumn(int index) {
            column = index;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            final ITableLabelProvider labelProvider = (ITableLabelProvider) TableWidget.this.viewer.getLabelProvider();
            final String c1 = labelProvider.getColumnText(e1, column);
            final String c2 = labelProvider.getColumnText(e2, column);
            if (sortAscending)
                return super.compare(viewer, c2, c1);
            return super.compare(viewer, c1, c2);
        }
    }
}
