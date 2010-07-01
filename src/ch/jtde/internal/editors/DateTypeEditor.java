/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.editors;

import java.text.*;
import java.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * {@link ICellEditor} for {@link DateValue} types.
 * 
 * @author M. Hautle
 */
public class DateTypeEditor implements ICellEditor<String, DateValue> {
    /** The editor. */
    private ValueEditor editor;

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
    public String getRenderedValue(DateValue element) {
        return String.valueOf(element.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(Composite parent) {
        editor = new ValueEditor(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable(DateValue element) {
        return true;
    }

    /**
     * Adapted {@link TextCellEditor} for date types.
     * 
     * @author M. Hautle
     */
    private static class ValueEditor extends TextCellEditor {
        /** The validator. */
        private SimpleDateFormat validator = new SimpleDateFormat(DateValue.FORMAT);

        /** The old value (the value set by {@link #setValue(Object)}). */
        private Object oldValue;

        /**
         * Default constructor.
         * 
         * @param parent The parent component
         */
        public ValueEditor(Composite parent) {
            super(parent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void doSetValue(Object value) {
            oldValue = value;
            super.doSetValue(value != null ? value.toString() : "");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object doGetValue() {
            final String val = (String) super.doGetValue();
            try {
                final Date res = validator.parse(val);
                return validator.format(res);
            } catch (Exception e) {
            }
            return oldValue;
        }
    }
}
