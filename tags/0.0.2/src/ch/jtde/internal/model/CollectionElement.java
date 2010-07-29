/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import java.util.*;
import ch.jtde.model.*;

/**
 * {@link IExtendableDataElement} describing key less collections like {@link Collection}s or {@link Set}s.
 * 
 * @author M. Hautle
 */
public class CollectionElement extends AbstractExtendableDataElement<CollectionAttribute> {
    /** The type of this element. */
    private final ClassDefinition type;

    /** The type of the collection values. */
    private final ClassDefinition valueType;

    /** The attributes of this element. */
    private final List<CollectionAttribute> attributes = new ArrayList<CollectionAttribute>();

    /**
     * Default constructor.
     * 
     * @param type The collection type
     * @param valueType The value type
     */
    public CollectionElement(ClassDefinition type, ClassDefinition valueType) {
        this.type = type;
        this.valueType = valueType;
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount() {
        return attributes.size();
    }

    /**
     * {@inheritDoc}
     */
    public CollectionAttribute getChild(int index) {
        return attributes.get(index);
    }

    /**
     * Creates a new entry.
     * 
     * @return The new entry
     */
    public CollectionAttribute add() {
        final CollectionAttribute attr = new CollectionAttribute(this);
        attributes.add(attr);
        fireAttributesAdded(attr);
        return attr;
    }

    /**
     * {@inheritDoc}
     */
    public ClassDefinition getType() {
        return type;
    }

    /**
     * Returns the type of the collection values.
     * 
     * @return The value type
     */
    public ClassDefinition getValueType() {
        return valueType;
    }
}
