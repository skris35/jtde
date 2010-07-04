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
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.model.*;

/**
 * {@link ICellEditor} for {@link String}s.
 * 
 * @author M. Hautle
 */
public class StringEditor implements ICellEditor<String, IValueElement<String>> {
    /** The editor. */
    private TextCellEditor editor;

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor getEditor(IAttribute attribute) {
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRenderedValue(IValueElement<String> element) {
        return element.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Composite parent) {
        editor = new TextCellEditor(parent) {
            @Override
            protected void doSetValue(Object value) {
                super.doSetValue(value != null ? value : "");
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(IValueElement<String> element) {
        return true;
    }
}
