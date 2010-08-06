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
import java.util.*;
import ch.jtde.model.*;

/**
 * Abstract implementation of {@link IAttribute} with built in {@link PropertyChangeSupport}.
 * 
 * @author M. Hautle
 */
abstract class AbstractAttribute implements IAttribute, PropertyChangeListener {
    /** The property change support. */
    protected final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /** The state of this attribute. */
    private AttributeState state = AttributeState.DEFINED;

    /** The contained value or null. */
    @SuppressWarnings("rawtypes")
    private IDataElement value;

    /** The constraints. */
    private List<IAttributeConstraint> constraints = Collections.emptyList();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeState getState() {
        return state;
    }

    /**
     * Sets the state
     * 
     * @param state The state.
     */
    public void setState(AttributeState state) {
        propertyChangeSupport.firePropertyChange(ATTR_STATE, this.state, this.state = state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IAttribute> IDataElement<T> getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAttributeConstraint> getConstraints() {
        return constraints;
    }

    /**
     * Sets the constraints of this attribute.
     * 
     * @param constraints The constraints
     */
    public void setConstraints(List<IAttributeConstraint> constraints) {
        this.constraints = constraints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IAttribute> void setValue(IDataElement<T> value) {
        deregisterValueListener(this.value);
        propertyChangeSupport.firePropertyChange(ATTR_VALUE, this.value, this.value = value);
        registerValueListener(value);
    }

    /**
     * Deregisters the {@link PropertyChangeListener} if the passed value is a {@link IValueElement}.
     * 
     * @param value The value or null
     */
    @SuppressWarnings("rawtypes")
    private void registerValueListener(IDataElement value) {
        if (value instanceof IValueElement)
            ((IValueElement) value).addPropertyChangeListener(this);
    }

    /**
     * Registers the {@link PropertyChangeListener} if the passed value is a {@link IValueElement}.
     * 
     * @param value The value or null
     */
    @SuppressWarnings("rawtypes")
    private void deregisterValueListener(IDataElement value) {
        if (value instanceof IValueElement)
            ((IValueElement) value).removePropertyChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // propagate the IValueElement change
        // currently that's enough, but maybe we must change this in future...
        propertyChangeSupport.firePropertyChange(ATTR_VALUE, null, value);
    }
}
