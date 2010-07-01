/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import java.beans.*;
import ch.jtde.model.*;

/**
 * Abstract default implementation of {@link IValueElement}.
 * 
 * @param <V> The value type
 * @author M. Hautle
 */
public abstract class AbstractValueElement<V> implements IValueElement<V> {
    /** The property change support. */
    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /** The value type. */
    private final ClassDefinition type;

    /**
     * Default constructor.
     * 
     * @param type The value type
     */
    public AbstractValueElement(ClassDefinition type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAttribute getChild(int index) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getChildCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassDefinition getType() {
        return type;
    }

    /**
     * Fires a property change event for {@link IValueElement#ATTR_VALUE} property.
     * 
     * @param <T> The type
     * @param oldValue The old value or null
     * @param newValue The new value or null
     * @return The new value
     */
    protected <T> T fireValueChanged(T oldValue, T newValue) {
        propertyChangeSupport.firePropertyChange(ATTR_VALUE, oldValue, newValue);
        return newValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
}
