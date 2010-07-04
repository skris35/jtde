/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.actions;

import ch.jtde.editors.*;
import ch.jtde.model.*;

/**
 * Abstract implementation of {@link IDataElementAction}.<br>
 * The action will be show by default for all attributes excepting for the {@link InsertAttribute}.
 * 
 * @param <T> The attribute type
 * @author M. Hautle
 */
public abstract class AbstractDataElementAction<T extends IAttribute> implements IDataElementAction<T> {
    /** The action name. */
    private final String name;

    /**
     * Default constructor.
     * 
     * @param name The action name
     */
    public AbstractDataElementAction(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLabel() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute) {
        return attribute != InsertAttribute.ME;
    }
}
