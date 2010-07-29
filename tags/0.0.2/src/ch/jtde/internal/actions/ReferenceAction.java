/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.actions;

import java.lang.reflect.*;
import java.util.*;
import java.util.List;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.*;
import org.eclipse.ui.model.*;
import ch.jtde.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * Action to set the value of an {@link IAttribute} to an already defined element (in the model).<br>
 * The action shows a checkboxtree containing all defined 'instances' of the data model.
 * 
 * @author M. Hautle
 */
public class ReferenceAction extends AbstractDataElementAction<IAttribute> {
    /**
     * Default constructor.
     */
    public ReferenceAction() {
        super("Reference");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute) {
        if (attribute == null || !super.isEnabledFor(element, attribute))
            return false;
        final ElementCategory cat = attribute.getLowerBound().getCategory();
        return !cat.isArray() && !cat.isPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(final IDataEditor editor, IDataElement<IAttribute> element, final IAttribute attribute) throws CoreException {
        try {
            editor.getProgressService().run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        final Set<IType> validTypes = EclipseUtils.getSubTypes(attribute.getLowerBound().getType(), monitor);
                        if (monitor.isCanceled())
                            return;
                        EclipseUtils.synchSWTCall(new Runnable() {
                            @Override
                            public void run() {
                                final IDataElement<IAttribute> el = showReferenceDialog(editor.getShell(), editor.getRootElement(), validTypes);
                                if (el != null)
                                    attribute.setValue(el);
                            }
                        });
                    } catch (JavaModelException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e) {
            throw (CoreException) e.getTargetException();
        } catch (InterruptedException e) {
            // ignore
        }
    }

    /**
     * Displays the given model as a checkboxtree.
     * 
     * @param shell The shell to use
     * @param rootElement The root element of the model to display
     * @param validTypes The valid types for the selection or null for all types
     * @return The selected element or null
     */
    private IDataElement<IAttribute> showReferenceDialog(Shell shell, IDataElement<IAttribute> rootElement, Set<IType> validTypes) {
        final Dialog d = new Dialog(shell, new LabelProvier(), new ContentProvider(), validTypes);
        d.setTitle("Set instance reference...");
        d.setMessage("Select the instance to reference from the current attribute");
        d.setInput(rootElement);
        if (d.open() == Dialog.OK)
            return ((IAttribute) d.getFirstResult()).getValue();
        return null;
    }

    /**
     * Simple label provider.
     * 
     * @author M. Hautle
     */
    private static class LabelProvier extends LabelProvider {
        /** Label provider. */
        private WorkbenchLabelProvider labelProvider = new WorkbenchLabelProvider();

        /**
         * {@inheritDoc}
         */
        @Override
        public String getText(Object element) {
            final IAttribute attr = (IAttribute) element;
            return attr.getName() + " : " + attr.getValue().getType().getName();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getImage(Object element) {
            final IDataElement<IAttribute> val = ((IAttribute) element).getValue();
            return val != null ? labelProvider.getImage(val.getType().getType()) : null;
        }
    }

    /**
     * Contentprovider to represent a {@link IDataElement} as a tree.
     * 
     * @author M. Hautle
     */
    private static class ContentProvider implements ITreeContentProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getChildren(Object parentElement) {
            final IDataElement<IAttribute> el = getElement(parentElement);
            if (el == null)
                return new Object[0];
            final List<IAttribute> childs = new ArrayList<IAttribute>();
            for (int i = 0, cnt = el.getChildCount(); i < cnt; i++) {
                final IAttribute a = el.getChild(i);
                if (a.getValue() != null)
                    childs.add(a);
            }
            return childs.toArray(new IAttribute[childs.size()]);
        }

        /**
         * Simply returns {@link IAttribute#getValue()}.
         * 
         * @param parentElement An {@link IAttribute}
         * @return The value
         */
        private IDataElement<IAttribute> getElement(Object parentElement) {
            return ((IAttribute) parentElement).<IAttribute> getValue();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object getParent(Object element) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasChildren(Object element) {
            final IDataElement<IAttribute> el = getElement(element);
            return el != null && el.getChildCount() > 0;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public Object[] getElements(Object inputElement) {
            final IDataElement root = (IDataElement) inputElement;
            final ElementAttribute attr = new ElementAttribute(root.getType(), "/", "");
            attr.setValue(root);
            return new Object[] { attr };
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
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    /**
     * A {@link CheckedTreeSelectionDialog} allowing only one selection and forcing it to be a valid {@link IType}.
     * 
     * @author M. Hautle
     */
    private static class Dialog extends CheckedTreeSelectionDialog implements ICheckStateListener {
        /** Status for emtpy selection. */
        private static final IStatus NO_SELECTION = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "You must select a attribute!");

        /** Status for invalid type selection. */
        private static final IStatus INVALID_TYPE = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                "The selected type is not assignable from the attribute type!");

        /** Status for valid selection. */
        private static final IStatus SELECTION_VALID = new Status(IStatus.OK, Activator.PLUGIN_ID, "");

        /** The valid types or null if all types were valid. */
        private final Set<IType> validTypes;

        /**
         * Default constructor.
         * 
         * @param parent The parent component
         * @param labelProvider The label provider
         * @param contentProvider The content provider
         * @param validTypes The valid types or null if all types were valid
         */
        public Dialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider, Set<IType> validTypes) {
            super(parent, labelProvider, contentProvider);
            updateStatus(NO_SELECTION);
            this.validTypes = validTypes;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected CheckboxTreeViewer createTreeViewer(Composite parent) {
            final CheckboxTreeViewer viewer = super.createTreeViewer(parent);
            viewer.addCheckStateListener(this);
            return viewer;
        }

        /**
         * Creates an empty panel instead of the button panel.
         * 
         * @return An empty panel
         */
        @Override
        protected Composite createSelectionButtons(Composite composite) {
            return new Composite(composite, SWT.RIGHT);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void checkStateChanged(CheckStateChangedEvent event) {
            final CheckboxTreeViewer viewer = getTreeViewer();
            if (!event.getChecked()) {
                if (viewer.getCheckedElements().length == 0)
                    updateStatus(NO_SELECTION);
                return;
            }
            final IAttribute attr = (IAttribute) event.getElement();
            viewer.setCheckedElements(new Object[] { attr });
            updateStatus(checkSelection(attr));
        }

        /**
         * Returns the dialog state according to the selected attribute.
         * 
         * @param attr The attribute to check
         * @return The result state
         */
        private IStatus checkSelection(IAttribute attr) {
            final IType type = attr.getValue().getType().getType();
            if (validTypes == null || validTypes.contains(type))
                return SELECTION_VALID;
            return INVALID_TYPE;
        }
    }
}
