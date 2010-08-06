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
import ch.jtde.internal.xstream.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link WrapperTypeValue}s.
 * 
 * @author M. Hautle
 */
public class WrapperTypeValueConverter implements IDataElementConverter<WrapperTypeValue<?>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(WrapperTypeValue<?> source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        context.convertAnother((source).getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public WrapperTypeValue<?> unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final WrapperTypeValue value = (WrapperTypeValue) context.getCurrentElement();
        final Class type = ElementMarshallingHelper.getWrapperFromName(value.getType().getName());
        value.setValue(context.convertAnother(value, type));
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class<? extends IDataElement> type) {
        return WrapperTypeValue.class.equals(type);
    }
}
