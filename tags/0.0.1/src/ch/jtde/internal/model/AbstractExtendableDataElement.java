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
 * Abstract implementation of {@link IExtendableDataElement} providing the event handling.
 * 
 * @param <T> The attribute type
 * @author M. Hautle
 */
public abstract class AbstractExtendableDataElement<T extends IAttribute> implements IExtendableDataElement<T> {
    /** The listeners. */
    private final List<IAttributeChangeListener<T>> listeners = new ArrayList<IAttributeChangeListener<T>>(1);

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAttributeChangeListener(IAttributeChangeListener<T> l) {
        if (!listeners.contains(l))
            listeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAttributeChangeListener(IAttributeChangeListener<T> l) {
        listeners.remove(l);
    }

    /**
     * {@inheritDoc}
     */
    public void fireAttributesRemoved(T... attributes) {
        for (IAttributeChangeListener<T> l : listeners)
            l.attributesRemoved(this, attributes);
    }

    /**
     * {@inheritDoc}
     */
    public void fireAttributesAdded(T... attributes) {
        for (IAttributeChangeListener<T> l : listeners)
            l.attributesAdded(this, attributes);
    }
}