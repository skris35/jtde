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
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.internal.editors.EditorModel.IEditorModelListener;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * Helper class for the context menu of {@link DataEditor}.
 * 
 * @author M. Hautle
 */
class ContextMenu implements IMenuListener, ISelectionChangedListener, IEditorModelListener {
    /** The owning editor. */
    private final IDataEditor editor;

    /** The current model. */
    private IDataElement<IAttribute> currentElement;

    /** The selected attribute. */
    private IAttribute selectedAttribute;

    /** The possible actions for the current model. */
    private List<IDataElementAction<IAttribute>> actions = Collections.emptyList();

    /**
     * Creates a context menu for the given widget.
     * 
     * @param editor The editor
     * @param viewer The table viewer
     * @param model The table model
     */
    public static void createMenu(IDataEditor editor, TableWidget viewer, EditorModel model) {
        final ContextMenu ctx = new ContextMenu(editor);
        final MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager.addMenuListener(ctx);
        model.addModelListener(ctx);
        viewer.addSelectionChangedListener(ctx);
        final Table table = viewer.getTable();
        table.setMenu(manager.createContextMenu(table));
    }

    /**
     * Hidden constructor.
     * 
     * @param editor The owning editor
     */
    private ContextMenu(IDataEditor editor) {
        this.editor = editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void menuAboutToShow(IMenuManager manager) {
        // add 'compatible' actions
        for (IDataElementAction a : actions)
            if (a.isEnabledFor(currentElement, selectedAttribute))
                manager.add(new Wrapper(a));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(SelectionChangedEvent e) {
        final IStructuredSelection sel = (IStructuredSelection) e.getSelection();
        selectedAttribute = (IAttribute) sel.getFirstElement();
        currentElement = editor.getCurrentElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void currentElementChanged(IDataElement<IAttribute> model) {
        final IType type = getType(model);
        if (type == null) {
            actions = Collections.emptyList();
            return;
        }
        try {
            actions = Activator.getActionManager().getTypeActions(type, new NullProgressMonitor());
            Collections.sort(actions, ActionComparator.ME);
        } catch (JavaModelException e) {
            EclipseUtils.showError(EclipseUtils.getCurrentShell(), "Error while resolving the actions", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contentChanged(IDataElement<IAttribute> model) {
        currentElementChanged(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeChanged(IAttribute attr) {
        // so it did...
    }

    /**
     * Returns the type of the given model.
     * 
     * @param model The model or null
     * @return The type or null
     */
    private IType getType(IDataElement<IAttribute> model) {
        if (model == null)
            return null;
        return model.getType().getType();
    }

    /**
     * {@link IDataElementAction} wrapper.
     * 
     * @author M. Hautle
     */
    @SuppressWarnings("unchecked")
    private class Wrapper extends Action {
        /** The wrapped action */
        @SuppressWarnings("rawtypes")
        private final IDataElementAction wrapped;

        /**
         * Default constructor.
         * 
         * @param wrapped The wrapped action
         */
        @SuppressWarnings("rawtypes")
        Wrapper(IDataElementAction wrapped) {
            super(wrapped.getLabel());
            this.wrapped = wrapped;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run() {
            try {
                wrapped.performAction(editor, currentElement, selectedAttribute);
            } catch (CoreException e) {
                EclipseUtils.showError(EclipseUtils.getCurrentShell(), "Error while executing the actions", e);
            }
        }
    }

    /**
     * {@link IDataElementAction} comparator - compares {@link IDataElementAction#getLabel()} of the actions.
     * 
     * @author M. Hautle
     */
    private static class ActionComparator implements Comparator<IDataElementAction<IAttribute>> {
        /** Singleton. */
        static final ActionComparator ME = new ActionComparator();

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(IDataElementAction<IAttribute> o1, IDataElementAction<IAttribute> o2) {
            return o1.getLabel().compareTo(o2.getLabel());
        }
    }
}
