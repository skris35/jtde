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
import java.math.*;
import java.net.*;
import java.util.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.PrimitiveValue.Type;
import ch.jtde.model.*;

/**
 * {@link ICellEditor} for string based types.
 * 
 * @param <V> The value type
 * @author M. Hautle
 */
public class StringBasedTypeEditor<V> implements ICellEditor<V, IValueElement<V>> {
    /** Map holding the validator for each {@link Type}. */
    private static Map<String, InputValidator> VALIDATORS = new HashMap<String, InputValidator>();

    static {
        VALIDATORS.put(BigDecimal.class.getName(), new NumberInputValidator(false));
        VALIDATORS.put(BigInteger.class.getName(), new NumberInputValidator(true));
        VALIDATORS.put(File.class.getName(), new RegexInputValidator(".+"));
        VALIDATORS.put(URL.class.getName(), new URLValidator());
    }

    /** The editor. */
    private ValueEditor editor;

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor getEditor(IAttribute attribute) {
        editor.setType(attribute.getValue().getType().getName());
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getRenderedValue(IValueElement<V> element) {
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
    public boolean isEditable(IValueElement<V> element) {
        return true;
    }

    /**
     * Adapted {@link TextCellEditor} for primitive types.
     * 
     * @author M. Hautle
     */
    private static class ValueEditor extends TextCellEditor {
        /** The current validator. */
        private InputValidator validator;

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
         * Sets the type of the value.
         * 
         * @param name The fully qualified name of the type
         */
        void setType(String name) {
            validator = VALIDATORS.get(name);
            oldValue = null;
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
            if (validator == null || validator.isValid(val))
                return val;
            return oldValue;
        }
    }

    /**
     * Validator for {@link URL}.
     * 
     * @author M. Hautle
     */
    private static class URLValidator implements InputValidator {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unused")
        @Override
        public boolean isValid(String value) {
            if (value.isEmpty())
                return false;
            try {
                new URL(value);
            } catch (MalformedURLException e) {
                return false;
            }
            return true;
        }
    }
}
