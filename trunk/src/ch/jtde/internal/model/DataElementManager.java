/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;
import ch.jtde.model.IExtendableDataElement.IExtendableDataElementHandler;

/**
 * {@link IDataElementManager} implementation.
 * 
 * @author M. Hautle
 */
public final class DataElementManager implements IDataElementManager {
    /** Extender for arrays. */
    private final ArrayExtender arrayExtender = new ArrayExtender();

    /** Explicit category mapping. */
    private final Map<String, ElementCategory> categories = new HashMap<String, ElementCategory>();

    /** Type hierarchy to category mapping. */
    private final Map<String, ElementCategory> superTypeCategories = new HashMap<String, ElementCategory>();

    /** Explicit type to factory mappings. */
    private final Map<String, IDataElementFactory> typeFactories = new HashMap<String, IDataElementFactory>();

    /** Type hierarchie to factory mapping. */
    private final Map<String, IDataElementFactory> superTypeFactories = new HashMap<String, IDataElementFactory>();

    /** Explicit type to extend handler mappings. */
    @SuppressWarnings("rawtypes")
    private final Map<String, IExtendableDataElementHandler> extendHandler = new HashMap<String, IExtendableDataElementHandler>();

    /**
     * Initializes the manager by fetching all registered {@link IDataElementDescriptor}s.
     */
    public void initialize() {
        for (IDataElementDescriptor d : ExtensionPointHelper.getElementDescriptors())
            d.registerTypes(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E create(IType type, IProgressMonitor pm) throws JavaModelException {
        final IDataElementFactory fact = getFactory(type, pm);
        if (fact == null)
            throw new IllegalArgumentException("No element factory for " + type.getFullyQualifiedName() + " found!");
        return (E) fact.create(type, pm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <A extends IAttribute, E extends IDataElement<A>> E createArray(IType type, boolean primitiveType, int dimensions, IProgressMonitor pm)
            throws JavaModelException {
        if (dimensions < 1)
            throw new IllegalArgumentException("An array must have at least one dimension!");
        return (E) createArrayField(type, primitiveType, dimensions, pm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T extends IAttribute> void extend(IExtendableDataElement<T> element) {
        final ClassDefinition type = element.getType();
        // handle arrays
        if (type.getDimensions() > 0) {
            assert element instanceof IndexCollectionElement : "Array must be of type KeyCollectionElement!";
            arrayExtender.extendDataElement((IndexCollectionElement) element);
            return;
        }
        final IExtendableDataElementHandler handler = extendHandler.get(type.getName());
        if (handler != null)
            handler.extendDataElement(element);
    }

    /**
     * Creates an array type.
     * 
     * @param type The element type
     * @param primitive True if this is a primitive type array
     * @param dimensions The number of dimensions
     * @param pm A progressmonitor or null
     * @return The array
     * @throws JavaModelException If something went wrong
     */
    private IndexCollectionElement createArrayField(IType type, boolean primitive, int dimensions, IProgressMonitor pm) throws JavaModelException {
        final ClassDefinition valueType;
        if (dimensions == 1)
            valueType = ClassDefinition.create(type, primitive ? ElementCategory.PRIMITIVE : getTypeCategory(type, pm));
        else
            valueType = ClassDefinition.createArray(type, primitive, dimensions - 1);
        final ClassDefinition arrayType = ClassDefinition.createArray(type, primitive, dimensions);
        return new IndexCollectionElement(arrayType, valueType);
    }

    /**
     * Returns the factory to use for the given type.
     * 
     * @param type The type
     * @param pm A progressmonitor or null
     * @return The factory to use
     * @throws JavaModelException If something went wrong during type analysis
     */
    private IDataElementFactory getFactory(IType type, IProgressMonitor pm) throws JavaModelException {
        final IDataElementFactory fact = typeFactories.get(type.getFullyQualifiedName());
        if (fact != null)
            return fact;
        if (Flags.isAbstract(type.getFlags()))
            throw new IllegalArgumentException(
                    "Abstract elements were not supported - currently the JVM does not support intanciation of abstract classes or interfaces.");
        return lookup(type, superTypeFactories, pm);
    }

    /**
     * Looks up the first matching element for the given type or one of it's supertypes.<br>
     * 
     * @param <T> The result type
     * @param type The type to check
     * @param map The map to use for the resolving (the key must be a fully qualified class name)
     * @param pm A progressmonitor
     * @return The matching element or null if none was found
     * @throws JavaModelException If something went wrong during type analysis
     */
    private <T> T lookup(IType type, Map<String, T> map, IProgressMonitor pm) throws JavaModelException {
        final T res = map.get(type.getFullyQualifiedName());
        if (res != null)
            return res;
        return lookup(type.newSupertypeHierarchy(pm), type, map);
    }

    /**
     * Looks up the first matching element for the supertypes of the given type.<br>
     * This method travels recursively throug the supertype hierarchy of the given type until it get's a match in the passed map.<br>
     * Don't call this method directly - call {@link #lookup(IType, Map, IProgressMonitor)} instead.
     * 
     * @param <T> The result type
     * @param h The super type hierarchy to search through
     * @param type The type to check
     * @param map The map to use for the resolving (the key must be a fully qualified class name)
     * @return The matching element or null if none was found
     */
    private <T> T lookup(ITypeHierarchy h, IType type, Map<String, T> map) {
        final IType[] supertypes = h.getSupertypes(type);
        // look for a match on this level
        for (IType t : supertypes) {
            final T f = map.get(t.getFullyQualifiedName());
            if (f != null)
                return f;
        }
        // no match on this level - search through the superlevels
        for (IType t : supertypes) {
            final T f = lookup(h, t, map);
            if (f != null)
                return f;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElementCategory getTypeCategory(IType type, IProgressMonitor pm) throws JavaModelException {
        ElementCategory cat = categories.get(type.getFullyQualifiedName());
        if (cat != null)
            return cat;
        cat = lookup(type, superTypeCategories, pm);
        return cat != null ? cat : ElementCategory.STRUCTURE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSupertypeFactory(String name, IDataElementFactory factory) {
        superTypeFactories.put(name, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerTypeFactory(String name, IDataElementFactory factory) {
        typeFactories.put(name, factory);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerTypeCategory(String name, ElementCategory category) {
        categories.put(name, category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerSuperTypeCategory(String name, ElementCategory category) {
        categories.put(name, category);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IExtendableDataElement<?>> void registerExtendableDataElementHandler(String name, IExtendableDataElementHandler<T> handler) {
        extendHandler.put(name, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDedicatedFactory(IType type) throws JavaModelException {
        return typeFactories.get(type.getFullyQualifiedName()) != null;
    }
}
