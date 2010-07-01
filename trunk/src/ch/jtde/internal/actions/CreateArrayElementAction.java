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
import java.util.regex.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.operation.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.progress.*;
import ch.jtde.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * Action to create an array as content of an {@link IAttribute}.<br>
 * This action should be registred on {@link Object} to provide the posisbilit to put an array inside an object field.
 * 
 * @author M. Hautle
 */
public class CreateArrayElementAction extends AbstractDataElementAction<IAttribute> {
    /** Pattern for numbers > 0. */
    private static final Pattern NUBMER = Pattern.compile("\\d*[1-9]");

    /**
     * Default constructor.
     */
    public CreateArrayElementAction() {
        super("Create array");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(final IDataEditor editor, IDataElement<IAttribute> element, final IAttribute attr) throws CoreException {
        final Shell shell = editor.getShell();
        final int dim = getDimensions(shell);
        if (dim == 0)
            return;
        final IType concreteType = getConcreteType(attr, shell);
        if (concreteType == null)
            return;
        try {
            final IWorkbenchSiteProgressService s = editor.getProgressService();
            s.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException {
                    // create attribute content
                    try {
                        final IDataElement<IAttribute> value = Activator.getElementManager().createArray(concreteType, false, dim, monitor);
                        attr.setValue(value);
                        EclipseUtils.synchSWTCall(new Runnable() {
                            public void run() {
                                editor.stepInto(attr.getName(), attr.getValue());
                            }
                        });
                    } catch (JavaModelException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
        } catch (InvocationTargetException e1) {
            EclipseUtils.showError(shell, "Error while creating element content", e1.getCause());
        } catch (InterruptedException e1) {
            // should not be thrown
        }
    }

    /**
     * Return the concrete type to use.
     * 
     * @param attr The attribute
     * @param shell The shell
     * @return The type or null
     * @throws JavaModelException
     */
    private IType getConcreteType(final IAttribute attr, final Shell shell) throws JavaModelException {
        final IJavaProject project = attr.getLowerBound().getType().getJavaProject();
        final IType concreteType = EclipseUtils.showConcreteTypeDialog(shell, SearchEngine.createJavaSearchScope(new IJavaElement[] { project }), "");
        return concreteType;
    }

    /**
     * Returns the number of desired array dimensions.
     * 
     * @param shell The shell
     * @return The number of dimensions, 0 to cancel
     */
    private int getDimensions(final Shell shell) {
        final InputDialog d = new InputDialog(shell, "Dimensions", "Number of dimensions", "1", new IInputValidator() {
            @Override
            public String isValid(String newText) {
                return NUBMER.matcher(newText).matches() ? null : "Input must be a number";
            }
        });
        if (d.open() != InputDialog.OK)
            return 0;
        return Integer.parseInt(d.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute) {
        if (!super.isEnabledFor(element, attribute) || attribute == null)
            return false;
        return EclipseUtils.isObject(attribute.getLowerBound().getName());
    }
}
