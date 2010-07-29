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
 * {@link IValueElement} for string based types.
 * 
 * @author M. Hautle
 */
public class StringBasedValue extends StringValue {
    /**
     * Default constructor.
     * 
     * @param type The type as {@link ClassDefinition}
     * @throws TechnicalModelException If an technical error occoured
     */
    StringBasedValue(ClassDefinition type) throws TechnicalModelException {
        super(type);
    }

    /**
     * The factory for this type.
     * 
     * @author M. Hautle
     */
    public static class Factory implements IDataElementFactory {
        /**
         * {@inheritDoc}
         */
        @Override
        public IDataElement<IAttribute> create(IType type, IProgressMonitor pm) throws TechnicalModelException {
            return new StringBasedValue(ClassDefinition.create(type, ElementCategory.VALUE));
        }
    }
}
