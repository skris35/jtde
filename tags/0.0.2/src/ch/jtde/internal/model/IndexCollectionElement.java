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
 * {@link IExtendableDataElement} describing {@link Integer} index based collection like elements like {@link List} or arrays.
 * 
 * @author M. Hautle
 */
public class IndexCollectionElement extends AbstractExtendableDataElement<IndexCollectionAttribute> {
    /** The type of this element. */
    private final ClassDefinition type;

    /** The type of the collection values. */
    private final ClassDefinition valueType;

    /** The attributes of this element. */
    private final List<IndexCollectionAttribute> attributes = new ArrayList<IndexCollectionAttribute>();

    /**
     * Default constructor.
     * 
     * @param type The collection type
     * @param valueType The value type
     */
    public IndexCollectionElement(ClassDefinition type, ClassDefinition valueType) {
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
    public IndexCollectionAttribute getChild(int index) {
        return attributes.get(index);
    }

    /**
     * Creates a new entry.<br>
     * Calls {@link #fireAttributesAdded(IndexCollectionAttribute...)} after the entry was added.
     * 
     * @return The new entry
     */
    public IndexCollectionAttribute add() {
        final IndexCollectionAttribute attr = new IndexCollectionAttribute(this, Integer.valueOf(attributes.size()));
        attributes.add(attr);
        fireAttributesAdded(attr);
        return attr;
    }

    /**
     * Removes the given attribute.
     * 
     * @param attr The attribute to remove
     */
    public void remove(IndexCollectionAttribute attr) {
        final int index = attr.getKey().intValue();
        for (int i = attributes.size() - 1; i > index; i--)
            attributes.get(i).setKey(Integer.valueOf(i - 1));
        attributes.remove(index);
        fireAttributesRemoved(attr);
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

    /**
     * Moves the given attribute one position up.
     * 
     * @param attribute The attribute to move up.
     */
    public void moveUp(IndexCollectionAttribute attribute) {
        final int index = attribute.getKey().intValue();
        if (index == 0)
            return;
        final IDataElement<IAttribute> value = attribute.getValue();
        final IndexCollectionAttribute upperAttr = attributes.get(index - 1);
        attribute.setValue(upperAttr.getValue());
        upperAttr.setValue(value);
    }

    /**
     * Moves the given attribute one position down.
     * 
     * @param attribute The attribute to move down.
     */
    public void moveDown(IndexCollectionAttribute attribute) {
        final int index = attribute.getKey().intValue();
        if (index + 1 >= attributes.size())
            return;
        final IDataElement<IAttribute> value = attribute.getValue();
        final IndexCollectionAttribute lowerAttr = attributes.get(index + 1);
        attribute.setValue(lowerAttr.getValue());
        lowerAttr.setValue(value);
    }
}
