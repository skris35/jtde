/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

import java.beans.*;
import java.util.*;

/**
 * Interface describing an attribute of an {@link IDataElement}.<br>
 * The attribute must fire a {@link PropertyChangeEvent} every time {@link #getValue()} or {@link #getState()} changes.
 * 
 * @author M. Hautle
 */
public interface IAttribute {
    /** Property name of {@link #getName()}. */
    public static final String ATTR_NAME = "name";

    /** Property name of {@link #getValue()}. */
    public static final String ATTR_VALUE = "value";

    /** Property name of {@link #getState()}. */
    public static final String ATTR_STATE = "state";

    /**
     * Returns the attribute 'name' - this name will be used to render the label of the attribute.
     * 
     * @return The name
     */
    String getName();

    /**
     * Returns the name of the declaring class.
     * 
     * @return The fully qualified name
     */
    String getDeclaringClass();

    /**
     * Returns the attribute value.
     * 
     * @param <T> The attribute content type
     * @return The value or null
     */
    <T extends IAttribute> IDataElement<T> getValue();

    /**
     * Sets the attribute value.
     * 
     * @param <T> The attribute content type
     * @param value The value to set or null
     */
    <T extends IAttribute> void setValue(IDataElement<T> value);

    /**
     * Returns the lower bound type of this attribute i.e. the declared type of the attribute.<br>
     * This type must be assignable from the type of {@link #getValue()}.
     * 
     * @return The lower bound type
     */
    ClassDefinition getLowerBound();

    /**
     * Returns the state of this attribute.
     * 
     * @return The attributes state.
     */
    AttributeState getState();

    /**
     * Returns the constraints of this attribute.
     * 
     * @return The constraints
     */
    List<IAttributeConstraint> getConstraints();

    /**
     * Adds the given listener.
     * 
     * @param l The listener to add
     */
    void addPropertyChangeListener(PropertyChangeListener l);

    /**
     * Removes the given listener.
     * 
     * @param l The listener to remove
     */
    void removePropertyChangeListener(PropertyChangeListener l);
}
