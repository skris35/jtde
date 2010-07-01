/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jface.operation.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.*;
import org.eclipse.ui.progress.*;
import ch.jtde.*;
import ch.jtde.editors.*;
import ch.jtde.internal.editors.EditorModel.*;
import ch.jtde.internal.search.*;
import ch.jtde.internal.utils.*;
import ch.jtde.internal.xstream.*;
import ch.jtde.model.*;
import ch.jtde.model.IExtendableDataElement.*;

/**
 * Editor for {@link IDataElement} structures.
 * 
 * @author M. Hautle
 */
public class DataEditor extends EditorPart implements IDataEditor, IEditorModelListener {
    /** Fully qualified name of {@link Object}. */
    private static final String OBJECT_NAME = Object.class.getName();

    /** The editor model. */
    private final EditorModel model = new EditorModel();

    /** The tableviewer. */
    private TableWidget viewer;

    /** Panel holding the history buttons. */
    private Composite buttonPanel;

    /** Stack of the history buttons. */
    private java.util.List<HistoryButton> history = new ArrayList<HistoryButton>();

    /** Helper reading/writing the model from/to a file. */
    private XStreamAdapter elementIO;

    /** The project holding the edited resource. */
    private IJavaProject project;

    /** Flag indicating if the editor is dirty. */
    private boolean dirty;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        try {
            final IFile f = getInputFile();
            elementIO.write(model.getRootElement(), f.getLocation().toFile());
            f.refreshLocal(IResource.DEPTH_ONE, monitor);
            setDirty(false);
        } catch (IOException e) {
            EclipseUtils.showError(getShell(), "Error while saving", e);
        } catch (CoreException e) {
            EclipseUtils.showError(getShell(), "Error while saving", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
        setInput(input);
        setPartName(input.getName());
        try {
            project = (IJavaProject) getInputFile().getProject().getNature(JavaCore.NATURE_ID);
            elementIO = new XStreamAdapter(project);
        } catch (CoreException e) {
            throw new PartInitException("Enclosing project is not a java project!", e);
        }
    }

    /**
     * Returns the input file.
     * 
     * @return The file
     */
    private IFile getInputFile() {
        return ((IFileEditorInput) getEditorInput()).getFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E getRootElement() {
        return (E) model.getRootElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E getCurrentElement() {
        return (E) model.getCurrentElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        project = null;
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout());
        createButtonPanel(parent);
        viewer = createViewer(parent);
        readModel();
    }

    /**
     * Creates and configures the table viewer.
     * 
     * @param parent The parent widget
     * @return The viewer
     */
    private TableWidget createViewer(Composite parent) {
        final TableWidget viewer = new TableWidget(parent, SWT.FILL);
        final Table table = viewer.getTable();
        CellManager cellManager = new CellManager(viewer);
        viewer.addColumn("Name", 100);
        viewer.addColumn("Type", 100);
        viewer.addColumn("Declaring Type", 100);
        viewer.addColumn("Value", 400).setEditingSupport(cellManager.getEditingSupport());
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                final IStructuredSelection sel = viewer.getSelection();
                if (sel.size() != 1)
                    return;
                final IAttribute attr = (IAttribute) sel.getFirstElement();
                // handler insertion attribute differently
                if (InsertAttribute.ME == attr) {
                    extendCurrentElement();
                    return;
                }
                // create attribute content if necessary
                if (attr.getValue() == null) {
                    initializeAttribute(attr);
                    return;
                }
                // complete attribute - just step into it
                stepInto(attr);
            }
        });
        viewer.setLabelProvider(cellManager.getLabelProvier());
        viewer.setContentProvider(model);
        ContextMenu.createMenu(this, viewer, model);
        return viewer;
    }

    /**
     * Creates the buttonpanel.
     * 
     * @param parent The parent element
     */
    private void createButtonPanel(Composite parent) {
        buttonPanel = new Composite(parent, SWT.BORDER_SOLID);
        buttonPanel.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false));
        buttonPanel.setLayout(new RowLayout());
        new HistoryButton("/", 0);
        buttonPanel.layout(true);
    }

    /**
     * Reads the model from the editor input file.
     */
    private void readModel() {
        final IWorkbenchSiteProgressService s = getProgressService();
        try {
            s.run(true, false, new IRunnableWithProgress() {
                @Override
                @SuppressWarnings("unchecked")
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    try {
                        setModel(elementIO.read(getInputFile().getContents()));
                    } catch (CoreException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e1) {
            EclipseUtils.showError(getShell(), "Error while parsing the file", e1.getCause());
        } catch (InterruptedException e1) {
            // should not be thrown
        }
    }

    /**
     * Sets the model content to the passed root element.<br>
     * This set operations were executed in the SWT thread.
     * 
     * @param model The model
     */
    private void setModel(final IDataElement<IAttribute> model) {
        EclipseUtils.synchSWTCall(new Runnable() {
            @Override
            public void run() {
                viewer.setInput(model);
                viewer.setItemCount(model.getChildCount());
                DataEditor.this.model.addModelListener(DataEditor.this);
            }
        });
    }

    /**
     * Executes the {@link IExtendableDataElementHandler} for the current element in a seperate thread.
     */
    private void extendCurrentElement() {
        try {
            final IWorkbenchSiteProgressService s = getProgressService();
            s.run(true, false, new IRunnableWithProgress() {
                @Override
                @SuppressWarnings("unchecked")
                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    Activator.getElementManager().extend((IExtendableDataElement) model.getCurrentElement());
                }
            });
        } catch (InvocationTargetException e1) {
            EclipseUtils.showError(getShell(), "Error while creating element content", e1.getCause());
        } catch (InterruptedException e1) {
            // should not be thrown
        }
    }

    /**
     * Initializes the content of the given attribute in a seperate thread.
     * 
     * @param attr The attribute
     */
    private void initializeAttribute(final IAttribute attr) {
        final ClassDefinition type = attr.getLowerBound();
        final IType base = type.getType();
        if (base == null)
            throw new IllegalArgumentException("The type of " + attr.getName() + " is not known by eclipse!");
        final IType concreteType = resolveTypeToUse(base);
        if (concreteType != null)
            createAttributeContent(attr, concreteType);
    }

    /**
     * Creates an element for the given type and sets it as value of the passed attribute.
     * 
     * @param attr The attribute
     * @param concreteType The concrete type to create
     */
    private void createAttributeContent(final IAttribute attr, final IType concreteType) {
        try {
            final IWorkbenchSiteProgressService s = getProgressService();
            s.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException {
                    // create attribute content
                    try {
                        attr.setValue(createElement(attr.getLowerBound(), concreteType, new SubProgressMonitor(monitor, 0)));
                        EclipseUtils.synchSWTCall(new Runnable() {
                            public void run() {
                                stepInto(attr);
                            }
                        });
                    } catch (JavaModelException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e1) {
            EclipseUtils.showError(getShell(), "Error while creating element content", e1.getCause());
        } catch (InterruptedException e1) {
            // should not be thrown
        }
    }

    /**
     * Creates the element for the given description.
     * 
     * @param type The lower bound type definition
     * @param concreteType The concrete type to use
     * @param pm A progressmonitor
     * @return The created element
     * @throws JavaModelException
     */
    private IDataElement<IAttribute> createElement(final ClassDefinition type, final IType concreteType, IProgressMonitor pm) throws JavaModelException {
        final int dim = type.getDimensions();
        if (dim == 0)
            return Activator.getElementManager().create(concreteType, pm);
        final ElementCategory cat = type.getCategory();
        return Activator.getElementManager().createArray(concreteType, ElementCategory.PRIMITIVE_ARRAY == cat, dim, pm);
    }

    /**
     * Figures out the concrete type to use for the proposed type.<br>
     * A type lookup dialog will be displayed to the user if it's not clear which type should be used.
     * 
     * @param type The base type
     * @return A concrete sub type or null
     */
    private IType resolveTypeToUse(IType type) {
        try {
            // just return final types (makes no sense to search it's subtypes)
            if (Flags.isFinal(type.getFlags()))
                return type;
            // check for dedicated factories
            if (Activator.getElementManager().hasDedicatedFactory(type))
                return type;
            return EclipseUtils.showConcreteTypeDialog(getShell(), createScope(type), type.getFullyQualifiedName());
        } catch (Exception e1) {
            EclipseUtils.showError(getShell(), "Error while opening type dialog", e1);
        }
        return null;
    }

    /**
     * Creates the search scope to get all subtypes of the given type.
     * 
     * @param type The type
     * @return The search scope
     * @throws CoreException If something went wrong
     */
    private IJavaSearchScope createScope(IType type) throws CoreException {
        if (OBJECT_NAME.equals(type.getFullyQualifiedName()))
            return SearchEngine.createJavaSearchScope(new IJavaElement[] { type.getJavaProject() });
        return SubTypeScope.create(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        if (viewer != null)
            viewer.getTable().setFocus();
    }

    /**
     * Steps up to the given history level.
     * 
     * @param level The level
     */
    private void stepTo(int level) {
        // drop all entries after this one
        for (int i = history.size() - 1; i >= level; i--)
            history.remove(i).dispose();
        buttonPanel.layout(true);
        model.stepTo(level);
    }

    /**
     * Step into the value element of the given attribute.<br>
     * If the passed attribute contains nothing or a {@link IValueElement} the method just returns (it makes no sense to step into a value...)
     * 
     * @param attr The attribute in which to step
     */
    @SuppressWarnings("unchecked")
    private void stepInto(IAttribute attr) {
        final IDataElement<IAttribute> value = attr.getValue();
        if (value == null || value instanceof IValueElement)
            return;
        stepInto(attr.getName(), value);
    }

    /**
     * {@inheritDoc}
     */
    public <T extends IAttribute> void stepInto(final String name, final IDataElement<T> value) {
        history.add(new HistoryButton(name, history.size() + 1));
        buttonPanel.layout(true);
        model.stepInto(value);
    }

    /**
     * {@inheritDoc}
     */
    public Shell getShell() {
        return getSite().getShell();
    }

    /**
     * {@inheritDoc}
     */
    public IWorkbenchSiteProgressService getProgressService() {
        return (IWorkbenchSiteProgressService) getSite().getService(IWorkbenchSiteProgressService.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void currentElementChanged(IDataElement<IAttribute> m) {
        EclipseUtils.synchSWTCall(new Runnable() {
            @Override
            public void run() {
                viewer.refresh();
                viewer.setItemCount(model.getRowCount());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contentChanged(IDataElement<IAttribute> m) {
        EclipseUtils.synchSWTCall(new Runnable() {
            @Override
            public void run() {
                viewer.refresh();
                viewer.setItemCount(model.getRowCount());
                setDirty(true);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeChanged(final IAttribute attr) {
        EclipseUtils.synchSWTCall(new Runnable() {
            @Override
            public void run() {
                viewer.update(attr);
                setDirty(true);
            }
        });
    }

    /**
     * Sets the editor dirty state.
     * 
     * @param dirty The new state of dirty
     */
    private void setDirty(boolean dirty) {
        this.dirty = dirty;
        firePropertyChange(PROP_DIRTY);
    }

    /**
     * A 'history' path button on {@link DataEditor#buttonPanel}.
     * 
     * @author hautle
     */
    private class HistoryButton implements SelectionListener {
        /** The level to which to jump. */
        private final int level;

        /** The button. */
        private final Button button;

        /**
         * Default constructor.
         * 
         * @param label The button label
         * @param level The level to which to jump
         */
        public HistoryButton(String label, int level) {
            button = new Button(buttonPanel, SWT.PUSH);
            button.setText(label);
            button.addSelectionListener(this);
            this.level = level;
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            stepTo(level);
        }

        /**
         * Dispose this button.
         */
        void dispose() {
            button.removeSelectionListener(this);
            button.dispose();
        }
    }
}
