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
import ch.jtde.internal.xstream.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link DataElement}s.
 * 
 * @author M. Hautle
 */
public class DataElementConverter implements IDataElementConverter<DataElement> {
    /** Name of the attribute holding the concrete type of an field. */
    private static final String CLASS_ATTRIBUTE = "class";

    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(DataElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        for (int i = 0, cnt = source.getChildCount(); i < cnt; i++) {
            final ElementAttribute attr = source.getChild(i);
            final IDataElement<IAttribute> value = attr.getValue();
            // skip empty attributes
            if (value == null)
                continue;
            writer.startNode(attr.getName());
            // store name of concrete type if it's not the same as the declared field type
            final ClassDefinition concreteType = value.getType();
            if (!attr.getLowerBound().equals(concreteType))
                writer.addAttribute(CLASS_ATTRIBUTE, buildTypeName(concreteType, context));
            context.convertAnother(value);
            writer.endNode();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataElement unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final DataElement element = context.<ElementAttribute, DataElement> getCurrentElement();
        final Map<String, ElementAttribute> attrs = getAttributes(element);
        while (reader.hasMoreChildren()) {
            reader.moveDown();
            final ElementAttribute attr = attrs.get(reader.getNodeName());
            // skip unknown fields...
            if (attr == null) {
                reader.moveUp();
                continue;
            }
            final String concreteType = reader.getAttribute(CLASS_ATTRIBUTE);
            createAttributeContent(element, attr, concreteType, context);
            attr.setState(AttributeState.DEFINED);
            reader.moveUp();
        }
        return element;
    }

    /**
     * Creates the content {@link IDataElement} for the given attribute.
     * 
     * @param parent The parent element
     * @param attr The attribute
     * @param concreteType The fully qualified name of the concrete type or null
     * @param context The context
     */
    @SuppressWarnings("unchecked")
    private void createAttributeContent(IDataElement parent, IAttribute attr, String concreteType, IElementUnmarshallingContext context) {
        final ClassDefinition lowerBound = attr.getLowerBound();
        final IDataElement<IAttribute> el;
        // fetch value of primitive types instead of creating it (they were already set by the factory)
        if (lowerBound.getCategory().isPrimitive())
            el = attr.getValue();
        else
            el = createElement(lowerBound, concreteType, context);
        // may be null if createElement was not able to find the specified type
        if (el != null)
            attr.setValue(context.convertAnother(parent, el));
    }

    /**
     * Creates a {@link IDataElement} for the given type specification.<br>
     * Returns null if the concrete type was not found.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param lowerBound The lowerbound type definition
     * @param concreteType The concrete type to create
     * @param context The unmarshalling context
     * @return The corresponding {@link IDataElement} or null
     */
    private <A extends IAttribute, E extends IDataElement<A>> E createElement(ClassDefinition lowerBound, String concreteType,
            IElementUnmarshallingContext context) {
        // resolve the concrete type if one was defined
        if (concreteType == null || concreteType.length() == 0)
            return ElementMarshallingHelper.<A, E> createElement(lowerBound.getType(), ElementCategory.PRIMITIVE_ARRAY == lowerBound.getCategory(), lowerBound
                    .getDimensions());
        return ElementMarshallingHelper.<A, E> createElement(concreteType, context);
    }

    /**
     * Returns the attributes of the given {@link DataElement} as map.
     * 
     * @param element The element
     * @return The attributes as map
     */
    private Map<String, ElementAttribute> getAttributes(DataElement element) {
        final Map<String, ElementAttribute> res = new HashMap<String, ElementAttribute>();
        for (int i = 0, cnt = element.getChildCount(); i < cnt; i++) {
            final ElementAttribute attr = element.getChild(i);
            res.put(attr.getName(), attr);
            attr.setState(AttributeState.ADDED);
        }
        return res;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return DataElement.class.equals(type);
    }
}
