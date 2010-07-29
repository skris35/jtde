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
 * {@link IValueElement} for primitive values.
 * 
 * @param <V> The wrapper type
 * @author M. Hautle
 */
public class PrimitiveValue<V> extends AbstractValueElement<V> {
    /** The value. */
    private V value;

    /** The value type. */
    private final Type wrapperType;

    /**
     * Default constructor.
     * 
     * @param type The primitive type
     * @param proj The base project (used for type lookups)
     * @param pm A progressmonitor or null
     */
    @SuppressWarnings("unchecked")
    public PrimitiveValue(Type type, IJavaProject proj, IProgressMonitor pm) {
        super(type.getDefinition(proj, pm));
        this.wrapperType = type;
        this.value = (V) type.getDefaultValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(V value) {
        this.value = fireValueChanged(this.value, value);
    }

    /**
     * Returns the wrapper type used.
     * 
     * @return The type
     */
    public Type getWrapperType() {
        return wrapperType;
    }

    /**
     * Wrapper type for primitive values.
     * 
     * @author M. Hautle
     */
    public enum Type {
        /** {@link Boolean}. */
        BOOLEAN(boolean.class, Boolean.class, Boolean.FALSE) {
            @Override
            public Object convert(String str) {
                return Boolean.valueOf(str);
            }
        },
        /** {@link Byte}. */
        BYTE(byte.class, Byte.class, Byte.valueOf((byte) 0)) {
            @Override
            public Object convert(String str) {
                return Byte.valueOf(str);
            }
        },
        /** {@link Short}. */
        SHORT(short.class, Short.class, Short.valueOf((short) 0)) {
            @Override
            public Object convert(String str) {
                return Short.valueOf(str);
            }
        },
        /** {@link Integer}. */
        INTEGER(int.class, Integer.class, Integer.valueOf(0)) {
            @Override
            public Object convert(String str) {
                return Integer.valueOf(str);
            }
        },
        /** {@link Long}. */
        LONG(long.class, Long.class, Long.valueOf(0l)) {
            @Override
            public Object convert(String str) {
                return Long.valueOf(str);
            }
        },
        /** {@link Float}. */
        FLOAT(float.class, Float.class, Float.valueOf(0f)) {
            @Override
            public Object convert(String str) {
                return Float.valueOf(str);
            }
        },
        /** {@link Double}. */
        DOUBLE(double.class, Double.class, Double.valueOf(0d)) {
            @Override
            public Object convert(String str) {
                return Double.valueOf(str);
            }
        },
        /** {@link Character}. */
        CHAR(char.class, Character.class, Character.valueOf(Character.MIN_VALUE)) {
            @Override
            public Object convert(String str) {
                return Character.valueOf(str.charAt(0));
            }
        };
        /** Mapping between the wrapper type name and it's enum representation. */
        private static final Map<String, Type> MAPPING = new HashMap<String, Type>();

        static {
            // create the mapping (must be done here cause it's not allowed to declare something before the enum constants)
            for (Type t : values())
                MAPPING.put(t.name, t);
        }

        /**
         * Default constructor.
         * 
         * @param <T> The value type
         * @param primitive The primitive type class
         * @param defaultValue The default value
         * @param type The class type
         */
        private <T> Type(Class<?> primitive, Class<T> type, T defaultValue) {
            this.name = type.getName();
            this.primitiveName = primitive.getName();
            this.defaultValue = defaultValue;
            this.wrapperType = type;
        }

        /** The wrapper type name. */
        private final String name;

        /** The name of the primitive type. */
        private final String primitiveName;

        /** The wrapper type. */
        private final Class<?> wrapperType;

        /** The default value. */
        private final Object defaultValue;

        /**
         * Returns the eclipse representation of this type.
         * 
         * @param proj The base project
         * @param pm A progressmonitor or null
         * @return The type
         */
        public IType getType(IJavaProject proj, IProgressMonitor pm) {
            try {
                return proj.findType(name, pm);
            } catch (JavaModelException e) {
                throw new TechnicalModelException("Error while resolving " + name, e);
            }
        }

        /**
         * Returns the {@link ClassDefinition} for this type.
         * 
         * @param proj The base project
         * @param pm A progressmonitor or null
         * @return The classdefinition
         */
        public ClassDefinition getDefinition(IJavaProject proj, IProgressMonitor pm) {
            return ClassDefinition.create(name, ElementCategory.PRIMITIVE, proj, pm);
        }

        /**
         * Returns the default/initial value of this type.
         * 
         * @return The default value
         */
        public Object getDefaultValue() {
            return defaultValue;
        }

        /**
         * Returns the name of the represented primitive type.
         * 
         * @return Returns the primitiveName.
         */
        public String getPrimitiveName() {
            return primitiveName;
        }

        /**
         * Returns the wrapper type for this primitive value type.
         * 
         * @return The wrapper type
         */
        public Class<?> getWrapperType() {
            return wrapperType;
        }

        /**
         * Returns the type definition for a given wrapper type name.
         * 
         * @param name The fully qualified wrapper type name
         * @return The enum type
         */
        public static Type getType(String name) {
            final Type value = MAPPING.get(name);
            if (value == null)
                throw new IllegalArgumentException("The type " + name + " is not a wrapper type!");
            return value;
        }

        /**
         * Converts the given string into an wrapped primitive value.
         * 
         * @param str A string representation
         * @return The wrapped value
         */
        public abstract Object convert(String str);
    }
}
