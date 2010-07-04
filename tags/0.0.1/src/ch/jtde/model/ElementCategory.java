/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

/**
 * Category to which a referenced {@link IDataElement} belongs.
 * 
 * @author M. Hautle
 */
public enum ElementCategory {
    /** Primitive java type - must implement {@link IValueElement}. */
    PRIMITIVE,
    /** Simple value - must implements {@link IValueElement}. */
    VALUE,
    /** A collection type. */
    COLLECTION,
    /** A object array. */
    OBJECT_ARRAY,
    /** A primitive type array. */
    PRIMITIVE_ARRAY,
    /** A structure type - i.e. a normal class. */
    STRUCTURE;

    /**
     * Returns wherever this describes an array.
     * 
     * @return True if it's an array
     */
    public boolean isArray() {
        return this == OBJECT_ARRAY || this == PRIMITIVE_ARRAY;
    }

    /**
     * Returns wherever this describes an primitive type.
     * 
     * @return True if this is an primitive type
     */
    public boolean isPrimitive() {
        return this == PRIMITIVE;
    }
}
