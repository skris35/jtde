/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream;

import java.io.*;
import java.util.*;
import org.eclipse.jdt.core.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.io.xml.*;
import com.thoughtworks.xstream.mapper.*;

/**
 * Adpater for {@link XStream} permiting serialization/desrialization between {@link IDataElement} contnet and it's corresponding 'native' xstream
 * representation.
 * 
 * @author M. Hautle
 */
public class XStreamAdapter {
    /** Mapping from fully qualified type name to alias. */
    final Map<String, String> typeToAlias = new HashMap<String, String>();

    /** Mapping alias to fully qualified type name. */
    final Map<String, String> aliasToType = new HashMap<String, String>();

    /** The {@link XStream}. */
    private Stream stream;

    /** The concerned project. */
    private final IJavaProject project;

    /**
     * Default constructor.
     * 
     * @param proj The owning project
     */
    public XStreamAdapter(IJavaProject proj) {
        project = proj;
        stream = new Stream();
    }

    /**
     * Writes out the given {@link IDataElement} into the passed file.
     * 
     * @param el The element
     * @param file The destination file
     * @throws IOException If something went wrong
     */
    @SuppressWarnings("unchecked")
    public void write(IDataElement el, File file) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            stream.toXML(el, out);
        } finally {
            if (out != null)
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    /**
     * Reads a {@link IDataElement} from the given {@link InputStream}.<br>
     * The passed stream will be closed by this method.
     * 
     * @param in The inputstream
     * @return The contained {@link IDataElement}
     */
    @SuppressWarnings("unchecked")
    public IDataElement read(InputStream in) {
        try {
            return (IDataElement) stream.fromXML(in);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns the owning project
     * 
     * @return The project
     */
    IJavaProject getProject() {
        return project;
    }

    /**
     * Returns the fully qualified name for a given alias/name.
     * 
     * @param alias The alias name or a fully qualified name
     * @return The fully qualified name
     */
    String typeForAlias(String alias) {
        final String val = aliasToType.get(alias);
        return val != null ? val : alias;
    }

    /**
     * Returns the name to use for a given type name.
     * 
     * @param name The fully qualified name
     * @return The alias name or the passed one
     */
    String aliasForType(String name) {
        final String val = typeToAlias.get(name);
        return val != null ? val : name;
    }

    /**
     * Registers the given alias information in {@link #typeToAlias} and {@link #aliasToType}.
     * 
     * @param alias The alias name
     * @param type The fully qualified type name
     */
    private void alias(String alias, String type) {
        typeToAlias.put(type, alias);
        aliasToType.put(alias, type);
    }

    /**
     * Adapted {@link XStream} for {@link IDataElement} marshalling.
     * 
     * @author M. Hautle
     */
    private class Stream extends XStream {
        /**
         * Default constructor.
         */
        public Stream() {
            super(new DomDriver());
            configure();
            setMarshallingStrategy(new MarshallerStrategy());
        }

        /**
         * Configures the {@link XStream}.
         */
        private void configure() {
            final DispatchingConverter conv = new DispatchingConverter();
            for (IDataElementConverter<?> c : ExtensionPointHelper.getXStreamConverters())
                conv.registerConverter(c);
            registerConverter(conv, XStream.PRIORITY_VERY_HIGH);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public void alias(String name, Class type) {
            XStreamAdapter.this.alias(name, type.getName());
            super.alias(name, type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public void aliasType(String name, Class type) {
            XStreamAdapter.this.alias(name, type.getName());
            super.aliasType(name, type);
        }
    }

    /**
     * {@link MarshallingStrategy} creating {@link IElementMarshallingContext} and {@link IElementUnmarshallingContext} implementing marshallers.
     * 
     * @author M. Hautle
     */
    private class MarshallerStrategy extends AbstractTreeMarshallingStrategy {
        /**
         * {@inheritDoc}
         */
        @Override
        protected TreeUnmarshaller createUnmarshallingContext(Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
            return new ElementUnmarshaller(XStreamAdapter.this, root, reader, converterLookup, mapper);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected TreeMarshaller createMarshallingContext(HierarchicalStreamWriter writer, ConverterLookup converterLookup, Mapper mapper) {
            return new ElementMarshaller(XStreamAdapter.this, writer, converterLookup, mapper, ReferenceByXPathMarshallingStrategy.RELATIVE);
        }
    }
}
