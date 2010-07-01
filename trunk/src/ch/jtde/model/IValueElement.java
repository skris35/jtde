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

/**
 * Interface implemented by {@link IDataElement}s if they contain only a value and no attributes.<br>
 * This interface is designates for types like {@link String}, {@link Number}, primitive types and other 'atomic' elements.
 * 
 * @param <V> The value type
 * @author M. Hautle
 */
public interface IValueElement<V> extends IDataElement<IAttribute> {
    /** Property name of {@link #getValue()}. */
    public static final String ATTR_VALUE = "value";

    /**
     * Returns the value of an leaf element.
     * 
     * @return The leaf value or null
     */
    V getValue();

    /**
     * Sets the value of the leaf element.
     * 
     * @param value The value to set
     */
    void setValue(V value);

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
