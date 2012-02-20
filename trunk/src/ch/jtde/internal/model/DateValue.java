/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import java.text.*;
import java.util.*;
import ch.jtde.model.*;

/**
 * {@link IValueElement} for {@link Date}s.
 * 
 * @author M. Hautle
 */
public class DateValue extends AbstractValueElement<String> {
    /** The used date format. */
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss.S z";

    /** The value. */
    private String value;

    /**
     * Default constructor.
     * 
     * @param type {@link Date} as {@link ClassDefinition}
     * @throws TechnicalModelException If an technical error occoured
     */
    DateValue(ClassDefinition type) throws TechnicalModelException {
        super(type);
        value = new SimpleDateFormat(FORMAT).format(new Date());
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
