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
import java.util.regex.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.PrimitiveValue.*;
import ch.jtde.model.*;

/**
 * {@link ICellEditor} for primitive/wrapper types.
 * 
 * @param <V> The value type
 * @author M. Hautle
 */
public class PrimitiveTypeEditor<V> implements ICellEditor<V, IValueElement<V>> {
    /** Map holding the validator for each {@link Type}. */
    private static Map<Type, InputValidator> VALIDATORS = new HashMap<Type, InputValidator>();

    /** The editor. */
    private ValueEditor editor;

    static {
        VALIDATORS.put(Type.BOOLEAN, new RegexInputValidator("true|false", Pattern.CASE_INSENSITIVE));
        VALIDATORS.put(Type.CHAR, new RegexInputValidator("."));
        VALIDATORS.put(Type.BYTE, new BoundedNumberInputValidator(Byte.MIN_VALUE, Byte.MAX_VALUE, true));
        VALIDATORS.put(Type.SHORT, new BoundedNumberInputValidator(Short.MIN_VALUE, Short.MAX_VALUE, true));
        VALIDATORS.put(Type.INTEGER, new BoundedNumberInputValidator(Integer.MIN_VALUE, Integer.MAX_VALUE, true));
        VALIDATORS.put(Type.LONG, new BoundedNumberInputValidator(Long.MIN_VALUE, Long.MAX_VALUE, true));
        VALIDATORS.put(Type.FLOAT, new BoundedNumberInputValidator(Float.MIN_VALUE, Float.MAX_VALUE, false));
        VALIDATORS.put(Type.DOUBLE, new BoundedNumberInputValidator(Double.MIN_VALUE, Double.MAX_VALUE, false));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellEditor getEditor(IAttribute attribute) {
        editor.setType(attribute.getLowerBound().getName());
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
    static class ValueEditor extends TextCellEditor {
        /** The current type. */
        private Type type;

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
            type = Type.getType(name);
            validator = VALIDATORS.get(type);
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
            if (validator.isValid(val))
                return val.isEmpty() ? type.getDefaultValue() : type.convert(val);
            return oldValue;
        }
    }
}
