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
import org.eclipse.jdt.core.*;
import ch.jtde.internal.model.*;
import ch.jtde.internal.model.PrimitiveValue.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.core.util.*;
import com.thoughtworks.xstream.io.*;

/**
 * Converter for {@link IndexCollectionElement}s.
 * 
 * @author M. Hautle
 */
public class IndexCollectionElementConverter implements IDataElementConverter<IndexCollectionElement> {
    /** The special converter. */
    private static final SpecialConverter[] CONVERTERS = { new CharArrayConverter(), new ByteArrayConverter() };

    /**
     * {@inheritDoc}
     */
    @Override
    public void marshal(IndexCollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        final SpecialConverter conv = getSpecialConverter(source.getType(), source.getValueType());
        if (conv != null)
            conv.marshal(source, writer, context);
        else
            marshal0(source, writer, context);
    }

    /**
     * Marshalls the given collection as 'ordinary' collection/array.
     * 
     * @param source The source object
     * @param writer The writer
     * @param context The marshalling context
     */
    private void marshal0(IndexCollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
        for (int i = 0, cnt = source.getChildCount(); i < cnt; i++) {
            final IndexCollectionAttribute attr = source.getChild(i);
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
     * Returns the special converter to use or null if this represents just a 'ordinary' collection/array.
     * 
     * @param type The collection/array type
     * @param valueType The value type
     * @return The special converter or null
     */
    private SpecialConverter getSpecialConverter(ClassDefinition type, ClassDefinition valueType) {
        // currently only simple arrays may have different strategies
        if (type.getDimensions() != 1)
            return null;
        for (SpecialConverter c : CONVERTERS)
            if (c.canConvert(valueType))
                return c;
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexCollectionElement unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
        final IndexCollectionElement element = context.<IndexCollectionAttribute, IndexCollectionElement> getCurrentElement();
        final SpecialConverter conv = getSpecialConverter(element.getType(), element.getValueType());
        if (conv != null)
            conv.unmarshal(element, reader, context);
        else
            unmarshal0(reader, element, context);
        return element;
    }

    /**
     * Unmarshalls a 'ordinary' collection/array.
     * 
     * @param reader The reader
     * @param element The element into which the data should be unmarshalled
     * @param context The unmarshalling context
     */
    private void unmarshal0(HierarchicalStreamReader reader, IndexCollectionElement element, IElementUnmarshallingContext context) {
        while (reader.hasMoreChildren()) {
            final IndexCollectionAttribute attr = element.add();
            reader.moveDown();
            final String name = reader.getNodeName();
            // convert only non null values
            if (!isNullValue(name))
                attr.setValue(context.convertAnother(element, createElement(name, context)));
            reader.moveUp();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class<? extends IDataElement> type) {
        return IndexCollectionElement.class.equals(type);
    }

    /**
     * Definition for the internal special converters.
     * 
     * @author M. Hautle
     */
    private interface SpecialConverter {
        /**
         * Convert an object to textual data.
         * 
         * @param source The collection
         * @param writer A stream to write to.
         * @param context A context that allows nested objects to be processed by XStream.
         */
        void marshal(IndexCollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context);

        /**
         * Convert textual data back into an object.
         * 
         * @param collection The collection to write in
         * @param reader The stream to read the text from.
         * @param context
         */
        void unmarshal(IndexCollectionElement collection, HierarchicalStreamReader reader, IElementUnmarshallingContext context);

        /**
         * Checks wherever this converter is able to handle the given type.
         * 
         * @param type The content type
         * @return True if this type can be converted
         */
        boolean canConvert(ClassDefinition type);
    }

    /**
     * Converter for char arrays.
     * 
     * @author M. Hautle
     */
    private static class CharArrayConverter implements SpecialConverter {
        /** Name of {@link Character}. */
        private static final String CHAR_NAME = Character.class.getName();

        /**
         * {@inheritDoc}
         */
        @Override
        public void marshal(IndexCollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
            final char[] str = new char[source.getChildCount()];
            for (int i = 0; i < str.length; i++) {
                final PrimitiveValue<Character> c = (PrimitiveValue<Character>) source.getChild(i).getValue();
                str[i] = c.getValue().charValue();
            }
            writer.setValue(new String(str));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unmarshal(IndexCollectionElement collection, HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
            final IJavaProject proj = collection.getValueType().getType().getJavaProject();
            final String str = reader.getValue();
            for (int i = 0, cnt = str.length(); i < cnt; i++) {
                final PrimitiveValue<Character> val = new PrimitiveValue<Character>(Type.CHAR, proj, null);
                val.setValue(Character.valueOf(str.charAt(i)));
                collection.add().setValue(val);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canConvert(ClassDefinition type) {
            return type.getCategory().isPrimitive() && CHAR_NAME.equals(type.getName());
        }
    }

    /**
     * Converter for byte arrays.
     * 
     * @author M. Hautle
     */
    private static class ByteArrayConverter implements SpecialConverter {
        /** Name of {@link Character}. */
        private static final String BYTE_NAME = Byte.class.getName();

        /** The encoder to use. */
        private static final Base64Encoder ENCODER = new Base64Encoder();

        /**
         * {@inheritDoc}
         */
        @Override
        public void marshal(IndexCollectionElement source, HierarchicalStreamWriter writer, IElementMarshallingContext context) {
            final byte[] bytes = new byte[source.getChildCount()];
            for (int i = 0; i < bytes.length; i++) {
                final PrimitiveValue<Byte> c = (PrimitiveValue<Byte>) source.getChild(i).getValue();
                bytes[i] = c.getValue().byteValue();
            }
            writer.setValue(ENCODER.encode(bytes));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void unmarshal(IndexCollectionElement collection, HierarchicalStreamReader reader, IElementUnmarshallingContext context) {
            final IJavaProject proj = collection.getValueType().getType().getJavaProject();
            final byte[] bytes = ENCODER.decode(reader.getValue());
            for (int i = 0; i < bytes.length; i++) {
                final PrimitiveValue<Byte> val = new PrimitiveValue<Byte>(Type.BYTE, proj, null);
                val.setValue(Byte.valueOf(bytes[i]));
                collection.add().setValue(val);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canConvert(ClassDefinition type) {
            return type.getCategory().isPrimitive() && BYTE_NAME.equals(type.getName());
        }
    }
}
