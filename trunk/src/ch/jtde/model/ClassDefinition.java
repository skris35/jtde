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

/**
 * Definition of a class/type.
 * 
 * @author M. Hautle
 */
public final class ClassDefinition {
    /** The category to which this type belongs. */
    private final ElementCategory category;

    /** The eclipse type or null. */
    private final IType type;

    /** The fully qualified class name. */
    private final String name;

    /** The number of dimensions (for arrays). */
    private final int dimensions;

    /**
     * Default constructor.
     * 
     * @param name The fully qualified class name
     * @param type The eclipse type
     * @param category The type category
     * @param dimensions The number of dimensions
     */
    private ClassDefinition(String name, IType type, ElementCategory category, int dimensions) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.dimensions = dimensions;
    }

    /**
     * Convenience create method.
     * 
     * @param name The fully qualified class name
     * @param category The type category
     * @param proj The base java project (used for the type lookup)
     * @param pm A progress monitor or null
     * @return The classdefinition
     * @throws TechnicalModelException If an error occoured while looking up the class
     */
    public static ClassDefinition create(String name, ElementCategory category, IJavaProject proj, IProgressMonitor pm) throws TechnicalModelException {
        try {
            final IType type = proj.findType(name, pm);
            if (type == null)
                throw new TechnicalModelException("No type defined for " + name + "!", null);
            return new ClassDefinition(name, type, category, 0);
        } catch (JavaModelException e) {
            throw new TechnicalModelException("Error while looking up " + name, e);
        }
    }

    /**
     * Convenience create method.
     * 
     * @param name The fully qualified class name
     * @param primitiveType True if <code>name</code> denotes a wrapper type for a primitive type
     * @param dimensions The number of dimensions (>0)
     * @param proj The base java project (used for the type lookup)
     * @param pm A progress monitor or null
     * @return The classdefinition
     * @throws TechnicalModelException If an error occoured while looking up the class
     */
    public static ClassDefinition createArray(String name, boolean primitiveType, int dimensions, IJavaProject proj, IProgressMonitor pm)
            throws TechnicalModelException {
        try {
            final IType type = proj.findType(name, pm);
            if (type == null)
                throw new TechnicalModelException("No type defined for " + name + "!", null);
            return createArray(type, primitiveType, dimensions);
        } catch (JavaModelException e) {
            throw new TechnicalModelException("Error while looking up " + name, e);
        }
    }

    /**
     * Creates a {@link ClassDefinition} for an normal type.
     * 
     * @param type The eclipse type
     * @param category The type category
     * @return The definition
     */
    public static ClassDefinition create(IType type, ElementCategory category) {
        return new ClassDefinition(type.getFullyQualifiedName(), type, category, 0);
    }

    /**
     * Creates a {@link ClassDefinition} for an array.
     * 
     * @param type The eclipse type
     * @param primitiveType True if <code>type</code> is a wrapper for an primitive type
     * @param dimensions The number of dimensions (>0)
     * @return The definition
     */
    public static ClassDefinition createArray(IType type, boolean primitiveType, int dimensions) {
        if (dimensions < 1)
            throw new IllegalArgumentException("The dimensions parameter must be greater than 0!");
        final ElementCategory cat = primitiveType ? ElementCategory.PRIMITIVE_ARRAY : ElementCategory.OBJECT_ARRAY;
        return new ClassDefinition(type.getFullyQualifiedName(), type, cat, dimensions);
    }

    /**
     * Returns the category to which this class belongs.
     * 
     * @return The category
     */
    public ElementCategory getCategory() {
        return category;
    }

    /**
     * Returns the eclipse type definition of this class.
     * 
     * @return The type
     */
    public IType getType() {
        return type;
    }

    /**
     * Returns the fully qualified name of this class.
     * 
     * @return The fully qualified name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of array dimensions.
     * 
     * @return The number of dimensions - 0 for non arrays
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((category == null) ? 0 : category.hashCode());
        result = prime * result + dimensions;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ClassDefinition))
            return false;
        ClassDefinition other = (ClassDefinition) obj;
        if (category == null) {
            if (other.category != null)
                return false;
        } else if (!category.equals(other.category))
            return false;
        if (dimensions != other.dimensions)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
