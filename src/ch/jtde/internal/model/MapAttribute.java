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
public class MapAttribute extends AbstractAttribute {
    /** The collection holding this attribute. */
    private final MapElement parent;

    /** The attribute key. */
    private final KeyAttribute key = new KeyAttribute();

    /**
     * Default constructor.
     * 
     * @param parent The parent collection of this attribute
     */
    public MapAttribute(MapElement parent) {
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
    @SuppressWarnings("rawtypes")
    public String getName() {
        final IDataElement<IAttribute> val = key.getValue();
        if (val == null)
            return "<null>";
        // display key value directly
        if (val instanceof IValueElement)
            return String.valueOf(((IValueElement) val).getValue());
        return val.getType().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaringClass() {
        return "";
    }

    /**
     * Returns a element containing the map key as sole attribute.
     * 
     * @return The key
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public IDataElement<IAttribute> getKey() {
        return (IDataElement) key;
    }

    /**
     * Returns the value of the map key.
     * 
     * @return The key value
     */
    public IDataElement<IAttribute> getKeyValue() {
        return key.getValue();
    }

    /**
     * Sets the value of the map key.
     * 
     * @param <T> The attribute type
     * @param value The value
     */
    public <T extends IAttribute> void setKeyValue(IDataElement<T> value) {
        key.setValue(value);
    }

    /**
     * Attribute holding the key of the map entry.
     * 
     * @author M. Hautle
     */
    private class KeyAttribute extends AbstractAttribute implements IDataElement<KeyAttribute> {
        /**
         * {@inheritDoc}
         */
        @Override
        public String getDeclaringClass() {
            return "";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassDefinition getLowerBound() {
            return parent.getKeyType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return "key";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public KeyAttribute getChild(int index) {
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getChildCount() {
            return 1;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ClassDefinition getType() {
            return parent.getObjectType();
        }
    }
}
