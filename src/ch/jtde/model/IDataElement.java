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
 * Interface describing an data element.
 * 
 * @param <T> The attribute type
 * @author M. Hautle
 */
public interface IDataElement<T extends IAttribute> {
    /**
     * Returns the number of attributes/child elements hold by this element.
     * 
     * @return The number of sub elements
     */
    int getChildCount();

    /**
     * Returns the child at the given index.
     * 
     * @param index The index of the child
     * @return The child/attribute at the given index
     * @throws IndexOutOfBoundsException if the index is not valid
     */
    T getChild(int index);

    /**
     * Returns the element type.
     * 
     * @return The type this element
     */
    ClassDefinition getType();
}
