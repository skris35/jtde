/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.xstream;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/**
 * Helper methods for {@link IDataElementConverter}s.<br>
 * Use {@link #buildTypeName(ClassDefinition, IElementMarshallingContext)} to get a valid {@link String} representation of the given type.<br>
 * To restore the {@link IDataElement} from such a {@link String} call {@link #createElement(String, IElementUnmarshallingContext)}.
 * 
 * @author M. Hautle
 */
public class ElementMarshallingHelper {
    /** Node name of a <code>null</code> representation. */
    private static final String NULL_PLACEHOLDER = "null";

    /** Suffix for array dimension. */
    private static final String ARRAY_SUFFIX = "-array";

    /** Name mapping from wrapper type to primitive type. */
    private static final Map<String, String> WRAPPER_TO_PRIMITIVE = new HashMap<String, String>();

    /** Name mapping from primtive type to wrapper type. */
    private static final Map<String, String> PRIMITIVE_TO_WRAPPER = new HashMap<String, String>();

    /** The wrapper types - name to class mapping. */
    @SuppressWarnings("unchecked")
    private static final Map<String, Class> WRAPPER_TYPES = new HashMap<String, Class>();

    static {
        register(Boolean.class, Boolean.TYPE);
        register(Byte.class, Byte.TYPE);
        register(Character.class, Character.TYPE);
        register(Short.class, Short.TYPE);
        register(Integer.class, Integer.TYPE);
        register(Long.class, Long.TYPE);
        register(Float.class, Float.TYPE);
        register(Double.class, Double.TYPE);
    }

    /**
     * Registers the given type in {@link #WRAPPER_TO_PRIMITIVE}, {@link #PRIMITIVE_TO_WRAPPER} and {@link #WRAPPER_TYPES}.
     * 
     * @param <T> The wrapper type
     * @param wrapper The wrapper type
     * @param primitive The primitive type
     */
    private static <T> void register(Class<T> wrapper, Class<T> primitive) {
        WRAPPER_TO_PRIMITIVE.put(wrapper.getName(), primitive.getName());
        PRIMITIVE_TO_WRAPPER.put(primitive.getName(), wrapper.getName());
        WRAPPER_TYPES.put(wrapper.getName(), wrapper);
    }

    /**
     * Returns the class for the name of a wrapper type.
     * 
     * @param name The fully qualified name of the wrapper type
     * @return The class
     */
    public static Class<?> getWrapperFromName(String name) {
        return WRAPPER_TYPES.get(name);
    }

    /**
     * Builds the name string for the given concrete type.<br>
     * Use this method to get a string representation of a type which is a subtype of the type specified in {@link IAttribute#getLowerBound()}.
     * 
     * @param type The concrete type
     * @param context The context
     * 
     * @return The type string
     */
    public static String buildTypeName(ClassDefinition type, IElementMarshallingContext context) {
        final String name = type.getName();
        final int cnt = type.getDimensions();
        if (cnt == 0)
            return context.aliasForType(name);
        final StringBuilder str = new StringBuilder(getArrayTypeName(type, context));
        for (int i = 0; i < cnt; i++)
            str.append(ARRAY_SUFFIX);
        return str.toString();
    }

    /**
     * Returns the string representation for the given array type.
     * 
     * @param type The type
     * @param context The marshalling context
     * @return The string representation
     */
    private static String getArrayTypeName(ClassDefinition type, IElementMarshallingContext context) {
        final String name = type.getName();
        final String primitive = WRAPPER_TO_PRIMITIVE.get(name);
        // this is a primitive type - so return the primitive name
        if (type.getCategory().isPrimitive())
            return primitive;
        // this is a wrapper type - return it's fully qualified name
        if (primitive != null)
            return type.getName();
        // this is a other type - return it's alias
        return context.aliasForType(name);
    }

    /**
     * Creates a {@link IDataElement} for the given type specification.<br>
     * Returns null if the specified type was not found.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param concreteType The concrete type to create
     * @param context The unmarshalling context
     * @return The corresponding {@link IDataElement} or null
     */
    public static <A extends IAttribute, E extends IDataElement<A>> E createElement(String concreteType, IElementUnmarshallingContext context) {
        final int index = concreteType.indexOf(ARRAY_SUFFIX);
        int dim = 0;
        boolean primitive = false;
        if (index > 0) {
            dim = (concreteType.length() - index) / ARRAY_SUFFIX.length();
            concreteType = concreteType.substring(0, index);
            final String wrapper = PRIMITIVE_TO_WRAPPER.get(concreteType);
            // handle primitive types (if there is a wrapper it's a primitive type)
            if (wrapper != null) {
                concreteType = wrapper;
                primitive = true;
            }
        }
        final String name = context.typeForAlias(concreteType);
        final IType type = context.findType(name);
        if (type == null)
            return null;
        return ElementMarshallingHelper.<A, E> createElement(type, primitive, dim);
    }

    /**
     * Creates a {@link IDataElement} for the given type specification.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param type The type to create
     * @param primitive True if the array is a primitive type
     * @param dim The number of array dimensions (0 for normal types)
     * @return The corresponding {@link IDataElement}
     */
    public static <A extends IAttribute, E extends IDataElement<A>> E createElement(IType type, boolean primitive, int dim) {
        try {
            if (dim == 0)
                return Activator.getElementManager().<A, E> create(type, new NullProgressMonitor());
            return Activator.getElementManager().<A, E> createArray(type, primitive, dim, new NullProgressMonitor());
        } catch (JavaModelException e) {
            throw new ConversionException("Error while creating element for " + type.getFullyQualifiedName(), e);
        }
    }

    /**
     * Writes a null value to the stream.
     * 
     * @param writer The writer to use
     */
    public static void writeNull(HierarchicalStreamWriter writer) {
        writer.startNode(NULL_PLACEHOLDER);
        writer.endNode();
    }

    /**
     * Returns wherever the given node name represents a null value.
     * 
     * @param name The node name
     * @return True if this is a null value
     */
    public static boolean isNullValue(String name) {
        return NULL_PLACEHOLDER.equals(name);
    }
}
