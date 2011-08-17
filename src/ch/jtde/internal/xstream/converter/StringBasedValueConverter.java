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
 * Converter for {@link StringBasedValue}s.
 * 
 * @author M. Hautle
 */
public class StringBasedValueConverter implements IDataElementConverter<StringBasedValue> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(StringBasedValue source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        context.convertAnother(source.getValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringBasedValue unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final StringBasedValue value = context.getCurrentElement();
        value.setValue((String) context.convertAnother(value, String.class));
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return StringBasedValue.class.equals(type);
    }
}
