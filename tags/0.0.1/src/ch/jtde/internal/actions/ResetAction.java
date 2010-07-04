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
import ch.jtde.model.*;

/**
 * Action to reset {@link IAttribute#getValue()} of non primitive attributes.
 * 
 * @author M. Hautle
 */
public class ResetAction extends AbstractDataElementAction<IAttribute> {
    /**
     * Default constructor.
     */
    public ResetAction() {
        super("Reset");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute) {
        return super.isEnabledFor(element, attribute) && attribute != null && !attribute.getLowerBound().getCategory().isPrimitive();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(IDataEditor editor, IDataElement<IAttribute> element, IAttribute attribute) throws CoreException {
        attribute.setValue(null);
    }
}
