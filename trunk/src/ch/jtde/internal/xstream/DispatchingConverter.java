/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream;

import java.util.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/**
 * {@link Converter} managing all converters for {@link IDataElement}s.
 * 
 * @author M. Hautle
 */
public class DispatchingConverter implements Converter {
    /** The list of converters. */
    @SuppressWarnings("rawtypes")
    private List<IDataElementConverter> converters = new ArrayList<IDataElementConverter>();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        final IDataElementConverter conv = lookupConverterForType((Class<? extends IDataElement>) source.getClass());
        conv.marshal((IDataElement) source, writer, (IElementMarshallingContext) context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        final IElementUnmarshallingContext ctx = (IElementUnmarshallingContext) context;
        final Class<? extends IDataElement> type = ctx.getCurrentElement().getClass();
        final IDataElementConverter conv = lookupConverterForType(type);
        return conv.unmarshal(reader, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("rawtypes")
    public boolean canConvert(Class type) {
        return IDataElement.class.isAssignableFrom(type);
    }

    /**
     * Registers the given converter.
     * 
     * @param converter The converter to register
     */
    @SuppressWarnings("rawtypes")
    public void registerConverter(IDataElementConverter converter) {
        converters.add(converter);
    }

    /**
     * Returns the converter for the given type.
     * 
     * @param type The type
     * @return The converter
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IDataElementConverter lookupConverterForType(Class<? extends IDataElement> type) {
        for (IDataElementConverter c : converters)
            if (c.canConvert(type))
                return c;
        throw new ConversionException("No converter specified for " + type);
    }
}
