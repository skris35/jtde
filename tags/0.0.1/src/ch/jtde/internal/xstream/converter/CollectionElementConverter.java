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
import ch.jtde.internal.model.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link CollectionElement}s.
 * 
 * @author M. Hautle
 */
public class CollectionElementConverter implements IDataElementConverter<CollectionElement> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(CollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        for (int i = 0, cnt = source.getChildCount(); i < cnt; i++) {
            final CollectionAttribute attr = source.getChild(i);
            final IDataElement<IAttribute> value = attr.getValue();
            if (value == null) {
                writeNull(writer);
                continue;
            }
            writer.startNode(buildTypeName(value.getType(), context));
            context.convertAnother(value);
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollectionElement unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final CollectionElement element = context.<CollectionAttribute, CollectionElement> getCurrentElement();
        while (reader.hasMoreChildren()) {
            final CollectionAttribute attr = element.add();
            reader.moveDown();
            final String name = reader.getNodeName();
            // special handling for null entries
            if (isNullValue(name)) {
                reader.moveUp();
                continue;
            }
            attr.setValue(context.convertAnother(element, createElement(name, context)));
            reader.moveUp();
        }
        return element;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class<? extends IDataElement> type) {
        return CollectionElement.class.equals(type);
    }
}
