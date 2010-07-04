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
 * {@link IExtendableDataElement} describing a {@link Map}.
 * 
 * @author M. Hautle
 */
public class MapElement extends AbstractExtendableDataElement<MapAttribute> {
    /** The type of this element. */
    private final ClassDefinition type;

    /** The type of the collection keys. */
    private final ClassDefinition keyType;

    /** The type of the collection values. */
    private final ClassDefinition valueType;

    /** The attributes of this element. */
    private final List<MapAttribute> attributes = new ArrayList<MapAttribute>();

    /**
     * Default constructor.
     * 
     * @param type The collection type
     * @param keyType The key type
     * @param valueType The value type
     */
    public MapElement(ClassDefinition type, ClassDefinition keyType, ClassDefinition valueType) {
        this.type = type;
        this.keyType = keyType;
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
    public MapAttribute getChild(int index) {
        return attributes.get(index);
    }

    /**
     * Creates a new map entry.<br>
     * Calls {@link #fireAttributesAdded(MapAttribute...)} after the entry was added.
     * 
     * @return The new entry
     */
    public MapAttribute add() {
        final MapAttribute attr = new MapAttribute(this);
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
     * Returns the type of the collection keys.
     * 
     * @return The key type
     */
    public ClassDefinition getKeyType() {
        return keyType;
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
     * Removes the given attribute.
     * 
     * @param attribute The attribute to remove
     */
    public void remove(MapAttribute attribute) {
        attributes.remove(attribute);
    }
}
