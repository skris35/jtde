/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.actions;

import org.eclipse.core.runtime.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * Action moving the selected {@link IndexCollectionAttribute} a position upwards.
 * 
 * @author M. Hautle
 */
public class MoveUpAction extends AbstractDataElementAction<IndexCollectionAttribute> {
    /**
     * Default constructor.
     */
    public MoveUpAction() {
        super("Move up");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(IDataEditor editor, IDataElement<IndexCollectionAttribute> element, IndexCollectionAttribute attribute) throws CoreException {
        final IndexCollectionElement el = (IndexCollectionElement) element;
        el.moveUp(attribute);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute) {
        if (!(element instanceof IndexCollectionElement && super.isEnabledFor(element, attribute)))
            return false;
        return ((IndexCollectionAttribute) attribute).getKey().intValue() > 0;
    }
}
