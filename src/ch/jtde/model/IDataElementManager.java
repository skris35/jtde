/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.model.IExtendableDataElement.*;

/**
 * Manager for {@link IDataElement}s.<br>
 * The {@link IDataElementFactory} can register their supported types in this manager.<br>
 * You may use it to create other types or to figure out the {@link ElementCategory} of an type.
 * 
 * @author M. Hautle
 */
public interface IDataElementManager {
    // TODO ensure that progressmonitors get passed!
    /**
     * Registers the given factory for the specified type.
     * 
     * @param name The fully qualified type name (will be compared against {@link IDataElement#getType()})
     * @param factory The factory to use
     */
    public void registerTypeFactory(String name, IDataElementFactory factory);

    /**
     * Registers the given factory for the specified type and it's subtypes.
     * 
     * @param name The fully qualified type name (will be compared against {@link IDataElement#getType()})
     * @param factory The factory to use
     */
    public void registerSupertypeFactory(String name, IDataElementFactory factory);

    /**
     * Registers the category for a given type.
     * 
     * @param name The fully qualified type name
     * @param category The category to which it belongs
     */
    public void registerTypeCategory(String name, ElementCategory category);

    /**
     * Registers the category for a given type and it's subtypes.
     * 
     * @param name The fully qualified type name
     * @param category The category to which it belongs
     */
    public void registerSuperTypeCategory(String name, ElementCategory category);

    /**
     * Registers the given extend handler on the specified class.
     * 
     * @param <T> The element type
     * @param name The fully qualified name of the target class (will be compared against {@link IExtendableDataElement#getType()})
     * @param handler The handler
     */
    public <T extends IExtendableDataElement<?>> void registerExtendableDataElementHandler(String name, IExtendableDataElementHandler<T> handler);

    /**
     * Returns wherever for the given type a factory was explicite registered by {@link #registerTypeFactory(String, IDataElementFactory)}.
     * 
     * @param type The type
     * @return True if a dedicated factory exists
     * @throws JavaModelException If something went wrong during type analysis
     */
    public boolean hasDedicatedFactory(IType type) throws JavaModelException;

    /**
     * Creates an element of the given type.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param type The type
     * @param pm A progressmonitor
     * @return The type
     * @throws JavaModelException If something went wrong during type analysis
     */
    public <A extends IAttribute, E extends IDataElement<A>> E create(IType type, IProgressMonitor pm) throws JavaModelException;

    /**
     * Creates an element of the given type.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param type The type
     * @param primitiveType True if <code>type</code> is a wrapper type for a primitive type
     * @param dimensions The number of array dimensions
     * @param pm A progressmonitor
     * @return The type
     * @throws JavaModelException If something went wrong during type analysis
     */
    public <A extends IAttribute, E extends IDataElement<A>> E createArray(IType type, boolean primitiveType, int dimensions, IProgressMonitor pm)
            throws JavaModelException;

    /**
     * Extends the given element according to a registered {@link IExtendableDataElementHandler}.
     * 
     * @param <T> The attribute type
     * @param element The element to extend
     */
    public <T extends IAttribute> void extend(IExtendableDataElement<T> element);

    /**
     * Returns the type category of the given type.
     * 
     * @param type The type
     * @param pm A progressmonitor
     * @return The category
     * @throws JavaModelException If something went wrong during type analysis
     */
    public ElementCategory getTypeCategory(IType type, IProgressMonitor pm) throws JavaModelException;
}
