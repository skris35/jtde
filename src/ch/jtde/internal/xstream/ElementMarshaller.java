/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream;

import static ch.jtde.internal.xstream.ElementMarshallingHelper.*;
import java.util.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.mapper.*;

/**
 * Implementation of {@link IElementMarshallingContext}.
 * 
 * @author M. Hautle
 */
class ElementMarshaller extends ReferenceByXPathMarshaller implements IElementMarshallingContext {
    /** The dataholder. (Replacement for the private field in the {@link TreeMarshaller}) */
    private DataHolder dataHolder;

    /** The owning adapter. */
    private final XStreamAdapter adapter;

    /**
     * Default constructor.
     * 
     * @param adapter The owning adapter
     * @param writer The writer to use
     * @param converterLookup The converter lookup of the stream
     * @param mapper The mapper of the stream
     * @param mode The mode to use {@link ReferenceByXPathMarshallingStrategy#ABSOLUTE} or {@link ReferenceByXPathMarshallingStrategy#RELATIVE}
     */
    public ElementMarshaller(XStreamAdapter adapter, HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper, int mode) {
        super(writer, converterLookup, mapper, mode);
        this.adapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void start(Object item, DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        if (item == null)
            throw new IllegalArgumentException("Null model is not supported!");
        writer.startNode(buildTypeName(((IDataElement) item).getType(), this));
        convertAnother(item);
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String aliasForType(String name) {
        return adapter.aliasForType(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(Object key) {
        return getDataHolder().get(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void put(Object key, Object value) {
        getDataHolder().put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator keys() {
        return getDataHolder().keys();
    }

    /**
     * Returns the data holder. It will be created if it does not yet exist.
     * 
     * @return The data holder
     */
    private DataHolder getDataHolder() {
        if (dataHolder == null)
            dataHolder = new MapBackedDataHolder();
        return dataHolder;
    }
}
