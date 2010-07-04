/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.model.*;

/**
 * {@link IDataElementFactory} for {@link EnumElement}s.
 * 
 * @author M. Hautle
 */
public class EnumElementFactory implements IDataElementFactory {
    /**
     * {@inheritDoc}
     */
    @Override
    public IDataElement<IAttribute> create(IType type, IProgressMonitor pm) throws TechnicalModelException {
        return new EnumElement(ClassDefinition.create(type, ElementCategory.VALUE));
    }
}
