/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import org.eclipse.jdt.core.*;
import ch.jtde.internal.model.PrimitiveValue.Type;
import ch.jtde.model.*;
import ch.jtde.model.IExtendableDataElement.IExtendableDataElementHandler;

/**
 * {@link IExtendableDataElementHandler} for arrays.<br>
 * This handler must not be registered!
 * 
 * @author M. Hautle
 */
class ArrayExtender implements IExtendableDataElementHandler<IndexCollectionElement> {
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void extendDataElement(IndexCollectionElement element) {
        final IndexCollectionAttribute attr = element.add();
        // create the attribute content for primitive type arrays.
        final ClassDefinition valueType = element.getValueType();
        if (!valueType.getCategory().isPrimitive())
            return;
        final IJavaProject project = valueType.getType().getJavaProject();
        attr.setValue(new PrimitiveValue(Type.getType(valueType.getName()), project, null));
    }
}
