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
import ch.jtde.model.*;

/**
 * {@link IDataElement} factory for {@link Collection} types.
 * 
 * @author M. Hautle
 */
public class CollectionElementFactory {
    /**
     * Hidden constructor.
     */
    private CollectionElementFactory() {
    }

    /**
     * Registers all default collection factories.
     * 
     * @param manager The element manager
     */
    public static void registerFactories(IDataElementManager manager) {
        register(new ListFactory(List.class), manager);
        register(new SimpleFactory(Collection.class), manager);
        register(new SimpleFactory(Set.class), manager);
        register(new MapFactory(Map.class), manager);
    }

    /**
     * Registers the given collection factory.
     * 
     * @param factory The factory to register
     * @param manager The element manager
     */
    private static void register(Factory factory, IDataElementManager manager) {
        final String type = factory.getColType();
        manager.registerTypeCategory(type, ElementCategory.COLLECTION);
        manager.registerTypeFactory(type, factory);
    }

    /**
     * Abstract factory definition for {@link Collection}s.
     * 
     * @author M. Hautle
     */
    private static abstract class Factory implements IDataElementFactory {
        /** The fully qualified name of the collection type. */
        private final String colType;

        /**
         * Default constructor.
         * 
         * @param collectionType The collection type
         */
        protected Factory(Class<?> collectionType) {
            colType = collectionType.getName();
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public final IDataElement<IAttribute> create(IType type, IProgressMonitor pm) throws TechnicalModelException {
            try {
                return create(ClassDefinition.create(type, ElementCategory.COLLECTION), type, type.getJavaProject(), pm);
            } catch (JavaModelException e) {
                throw new TechnicalModelException("Error while creating " + type.getFullyQualifiedName(), e);
            }
        }

        /**
         * Creates the element
         * 
         * @param colType The collection type
         * @param type The collection type as eclipse type
         * @param proj The type owning project
         * @param pm A progressmonitor or null
         * @return The element
         * @throws JavaModelException If something went wrong
         */
        @SuppressWarnings("unchecked")
        protected abstract IDataElement create(ClassDefinition colType, IType type, IJavaProject proj, IProgressMonitor pm) throws JavaModelException;

        /**
         * Returns the fully qualified name of the represented {@link Collection} class.
         * 
         * @return The collection type.
         */
        String getColType() {
            return colType;
        }
    }

    /**
     * {@link Factory} for list base {@link Collection}s.
     * 
     * @author M. Hautle
     */
    private static class ListFactory extends Factory {
        /**
         * Default constructor.
         * 
         * @param collectionType The collection type
         */
        public ListFactory(Class<?> collectionType) {
            super(collectionType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        protected IDataElement create(ClassDefinition colType, IType type, IJavaProject proj, IProgressMonitor pm) throws JavaModelException {
            // TODO parse generics type
            final ClassDefinition obj = ClassDefinition.create(Object.class.getName(), ElementCategory.STRUCTURE, proj, pm);
            return new IndexCollectionElement(colType, obj);
        }
    }

    /**
     * {@link Factory} for key and value based {@link Collection}s.
     * 
     * @author M. Hautle
     */
    private static class MapFactory extends Factory {
        /**
         * Default constructor.
         * 
         * @param collectionType The collection type
         */
        public MapFactory(Class<?> collectionType) {
            super(collectionType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        protected IDataElement create(ClassDefinition colType, IType type, IJavaProject proj, IProgressMonitor pm) throws JavaModelException {
            // TODO parse generics type
            final ClassDefinition obj = ClassDefinition.create(Object.class.getName(), ElementCategory.STRUCTURE, proj, pm);
            return new MapElement(colType, obj, obj, obj);
        }
    }

    /**
     * {@link Factory} for key less {@link Collection}s.
     * 
     * @author M. Hautle
     */
    private static class SimpleFactory extends Factory {
        /**
         * Default constructor.
         * 
         * @param collectionType The collection type
         */
        public SimpleFactory(Class<?> collectionType) {
            super(collectionType);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        protected IDataElement create(ClassDefinition colType, IType type, IJavaProject proj, IProgressMonitor pm) {
            // TODO parse generics type
            return new CollectionElement(colType, ClassDefinition.create(Object.class.getName(), ElementCategory.STRUCTURE, proj, pm));
        }
    }
}
