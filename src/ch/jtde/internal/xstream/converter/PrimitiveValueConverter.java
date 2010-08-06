/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream.converter;

import ch.jtde.internal.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link PrimitiveValue}s.
 * 
 * @author M. Hautle
 */
public class PrimitiveValueConverter implements IDataElementConverter<PrimitiveValue<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(PrimitiveValue<?> source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        context.convertAnother(source.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public PrimitiveValue<?> unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final PrimitiveValue value = (PrimitiveValue) context.getCurrentElement();
        final Class<?> type = value.getWrapperType().getWrapperType();
        value.setValue(context.convertAnother(value, type));
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return PrimitiveValue.class.equals(type);
    }
}
