/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import org.eclipse.jdt.core.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * {@link ICellEditor} for {@link EnumElement}s.
 * 
 * @author M. Hautle
 */
public class EnumEditor implements ICellEditor<String, EnumElement> {
    /** The editor. */
    private ComboBoxViewerCellEditor editor;

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor getEditor(IAttribute attribute) {
        final EnumElement el = (EnumElement) attribute.getValue();
        editor.setInput(null);
        try {
            editor.setInput(el.getConstants());
        } catch (JavaModelException e) {
            EclipseUtils.showError(EclipseUtils.getCurrentShell(), "Error while fetching constant list.", e);
        }
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRenderedValue(EnumElement element) {
        final String value = element.getValue();
        return value != null ? value : NULL_REPRESANTATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Composite parent) {
        editor = new ComboBoxViewerCellEditor(parent);
        editor.setContenProvider(ArrayContentProvider.getInstance());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(EnumElement element) {
        return true;
    }
}
