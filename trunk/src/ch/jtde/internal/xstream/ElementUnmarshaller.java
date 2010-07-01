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
import org.eclipse.jdt.core.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.core.*;
import com.thoughtworks.xstream.core.util.*;
import com.thoughtworks.xstream.io.*;
import com.thoughtworks.xstream.mapper.*;

/**
 * Implementation of {@link IElementUnmarshallingContext}.
 * 
 * @author M. Hautle
 */
class ElementUnmarshaller extends ReferenceByXPathUnmarshaller implements IElementUnmarshallingContext {
    /** Property string to retrive the {@link IDataElement} to populate. Call {@link MarshallingContext#get(Object)} to retrive the element. */
    public static final String CURRENT_ELEMENT = "currentElement";

    /** The owning adapter. */
    private final XStreamAdapter adapter;

    /** The dataholder. (Replacement for the private field in the {@link TreeMarshaller}) */
    private DataHolder dataHolder;

    /** The validation list. (Replacement for the private field in the {@link TreeMarshaller}) */
    private final PrioritizedList validationList = new PrioritizedList();

    /** Stack of {@link IDataElement}s to populate. */
    private final FastStack elements = new FastStack(2);

    /**
     * Default constructor.
     * 
     * @param adapter The owning adapter
     * @param root The initialy passed root object ({@link XStream#fromXML(java.io.InputStream, Object)}) or null
     * @param reader The reader to use
     * @param converterLookup The converter lookup of the stream
     * @param mapper The mapper of the stream
     */
    public ElementUnmarshaller(XStreamAdapter adapter, Object root, HierarchicalStreamReader reader, ConverterLookup converterLookup, Mapper mapper) {
        super(root, reader, converterLookup, mapper);
        this.adapter = adapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object start(DataHolder dataHolder) {
        final String name = reader.getNodeName();
        this.dataHolder = dataHolder;
        final IDataElement<IAttribute> element = createElement(name, this);
        if (element == null)
            throw new ConversionException("Unknown root element type!");
        final Object result = convertAnother(null, element);
        runValidations();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E convertAnother(IDataElement parent, E value) {
        pushElement(value);
        final E res = (E) convertAnother(parent, value.getClass());
        popElement();
        return res;
    }

    /**
     * Executes the validations hold in {@link #validationList}.
     */
    @SuppressWarnings("unchecked")
    private void runValidations() {
        final Iterator<Runnable> validations = validationList.iterator();
        while (validations.hasNext())
            validations.next().run();
    }

    /**
     * Pushes the given element on the process stack.
     * 
     * @param element The element
     */
    @SuppressWarnings("unchecked")
    private void pushElement(IDataElement element) {
        elements.push(element);
    }

    /**
     * Pops the top element of the process stack.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @return The top element
     */
    @SuppressWarnings("unchecked")
    private <A extends IAttribute, E extends IDataElement<A>> E popElement() {
        return (E) elements.pop();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E getCurrentElement() {
        return (E) elements.peek();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String typeForAlias(String alias) {
        return adapter.typeForAlias(alias);
    }

    /**
     * {@inheritDoc}
     */
    public IType findType(String name) {
        try {
            return adapter.getProject().findType(name);
        } catch (JavaModelException e) {
            throw new ConversionException("Error while looking up " + name, e);
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCompletionCallback(Runnable work, int priority) {
        validationList.add(work, priority);
    }
}
