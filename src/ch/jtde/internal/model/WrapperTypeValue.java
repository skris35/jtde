/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import ch.jtde.internal.model.PrimitiveValue.Type;
import ch.jtde.model.*;

/**
 * {@link IValueElement} implementation for wrapper types.
 * 
 * @param <V> The value type
 * @author M. Hautle
 */
public class WrapperTypeValue<V> extends AbstractValueElement<V> {
    /**
     * Default constructor.
     * 
     * @param type The type
     */
    @SuppressWarnings("unchecked")
    WrapperTypeValue(ClassDefinition type) {
        super(type);
        this.value = (V) Type.getType(type.getName()).getDefaultValue();
    }

    /** The value. */
    private V value;

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(V value) {
        if (value == null)
            throw new IllegalArgumentException("Null values not supported!");
        this.value = fireValueChanged(this.value, value);
    }
}
