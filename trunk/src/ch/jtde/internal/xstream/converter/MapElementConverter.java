/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream.converter;

import static ch.jtde.internal.xstream.ElementMarshallingHelper.*;
import java.util.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link MapElement}s.
 * 
 * @author M. Hautle
 */
public class MapElementConverter implements IDataElementConverter<MapElement> {
    /** Name of {@link Map.Entry}. */
    private static final String MAP_ENTRY = Map.Entry.class.getName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(MapElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        final String entryName = context.aliasForType(MAP_ENTRY);
        for (int i = 0, cnt = source.getChildCount(); i < cnt; i++) {
            final MapAttribute attr = source.getChild(i);
            // entry start
            writer.startNode(entryName);
            writeElement(attr.getKey(), writer, context);
            writeElement(attr.getValue(), writer, context);
            // entry end
            writer.endNode();
        }
    }

    /**
     * Writes out the given element.
     * 
     * @param value The value to write
     * @param writer The writer to use
     * @param context The marshalling context
     */
    private void writeElement(final IDataElement<IAttribute> value, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        if (value == null) {
            writeNull(writer);
            return;
        }
        writer.startNode(buildTypeName(value.getType(), context));
        context.convertAnother(value);
        writer.endNode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MapElement unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final MapElement element = context.<MapAttribute, MapElement> getCurrentElement();
        while (reader.hasMoreChildren()) {
            final MapAttribute attr = element.add();
            reader.moveDown();
            final IDataElement<IAttribute> key = readElement(element, reader, context);
            final IDataElement<IAttribute> value = readElement(element, reader, context);
            attr.setKeyValue(key);
            attr.setValue(value);
            reader.moveUp();
        }
        return element;
    }

    /**
     * Unmarshalls the current element.
     * 
     * @param map The map beeing unmarshalled
     * @param reader The reader
     * @param context The unmarshalling context
     * @return The element
     */
    private IDataElement<IAttribute> readElement(MapElement map, HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        reader.moveDown();
        final String name = reader.getNodeName();
        // special handling for null entries
        if (isNullValue(name)) {
            reader.moveUp();
            return null;
        }
        final IDataElement<IAttribute> res = context.convertAnother(map, createElement(name, context));
        reader.moveUp();
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return MapElement.class.equals(type);
    }
}
