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
 * {@link IAttribute} implementation for {@link IndexCollectionElement}s.<br>
 * Describes an entry like an array/list slot with value and it's index or a map entry with it's key.
 * 
 * @author M. Hautle
 */
public class IndexCollectionAttribute extends AbstractAttribute {
    /** The collection holding this attribute. */
    private final IndexCollectionElement parent;

    /** The attribute key. */
    private Integer key;

    /**
     * Default constructor.
     * 
     * @param parent The parent collection of this attribute
     * @param key The key of the attribute
     */
    public IndexCollectionAttribute(IndexCollectionElement parent, Integer key) {
        this.parent = parent;
        this.key = key;
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
        return key.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaringClass() {
        return "";
    }

    /**
     * Returns the attribute key.
     * 
     * @return The key
     */
    public Integer getKey() {
        return key;
    }

    /**
     * Sets the key.
     * 
     * @param key The key to set.
     */
    void setKey(Integer key) {
        propertyChangeSupport.firePropertyChange(ATTR_NAME, getName(), key.toString());
        this.key = key;
    }
}
