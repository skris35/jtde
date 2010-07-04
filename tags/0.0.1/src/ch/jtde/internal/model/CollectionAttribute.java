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
 * {@link IAttribute} implementation for {@link CollectionElement}s.<br>
 * Describes an value only entry.
 * 
 * @author M. Hautle
 */
public class CollectionAttribute extends AbstractAttribute {
    /** The collection holding this attribute. */
    private final CollectionElement parent;

    /**
     * Default constructor.
     * 
     * @param parent The parent collection of this attribute
     */
    public CollectionAttribute(CollectionElement parent) {
        this.parent = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassDefinition getLowerBound() {
        return parent.getValueType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaringClass() {
        return "";
    }
}
