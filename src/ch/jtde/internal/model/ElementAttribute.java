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
 * {@link IAttribute} implementation for normal attributes.
 * 
 * @author M. Hautle
 */
public class ElementAttribute extends AbstractAttribute {
    /** The declared type of this attribute - i.e. the lower bound of it. */
    private final ClassDefinition declaredType;

    /** The attribute name. */
    private final String name;

    /** The name of the declaring class. */
    private final String declaringClass;

    /**
     * Default constructor.
     * 
     * @param declaredType The declared type of the attribute
     * @param name The name of the attribute
     * @param declaringClass The name of the declaring class
     */
    public ElementAttribute(ClassDefinition declaredType, String name, String declaringClass) {
        this.declaredType = declaredType;
        this.name = name;
        this.declaringClass = declaringClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassDefinition getLowerBound() {
        return declaredType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaringClass() {
        return declaringClass;
    }
}
