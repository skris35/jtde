/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import ch.jtde.model.*;

/**
 * {@link IValueElement} for {@link String}s and other string 'holding' classes like {@link StringBuilder} etc.
 * 
 * @author M. Hautle
 */
public class StringValue extends AbstractValueElement<String> {
    /** The value. */
    private String value = "";

    /**
     * Default constructor.
     * 
     * @param type Concrete String representation class as {@link ClassDefinition}
     * @throws TechnicalModelException If an technical error occurred
     */
    StringValue(ClassDefinition type) throws TechnicalModelException {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String value) {
        if (value == null)
            throw new IllegalArgumentException("Null values not supported!");
        this.value = fireValueChanged(this.value, value);
    }
}
