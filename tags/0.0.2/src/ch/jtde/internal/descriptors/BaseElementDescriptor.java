/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.descriptors;

import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * {@link IDataElementDescriptor} for some basic factories and definitions.
 * 
 * @author M. Hautle
 */
public class BaseElementDescriptor implements IDataElementDescriptor {
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerTypes(IDataElementManager manager) {
        registerStringDefinition(manager);
        registerObjectDefinitions(manager);
        registerWrapperTypeDefinition(manager);
        registerEnumDefinition(manager);
        registerDateDefinition(manager);
        registerStringBasedDefinition(manager);
    }

    /**
     * Registers {@link String} stuff.
     * 
     * @param manager The manager
     */
    private void registerStringDefinition(IDataElementManager manager) {
        final StringElementFactory factory = new StringElementFactory();
        registerValueType(String.class, factory, manager);
        registerValueType(StringBuilder.class, factory, manager);
        registerValueType(StringBuffer.class, factory, manager);
    }

    /**
     * Registers {@link Date} stuff.
     * 
     * @param manager The manager
     */
    private void registerDateDefinition(IDataElementManager manager) {
        registerValueType(Date.class, new DateValueFactory(), manager);
    }

    /**
     * Registers string like elements.
     * 
     * @param manager The manager
     */
    private void registerStringBasedDefinition(IDataElementManager manager) {
        final StringBasedValue.Factory factory = new StringBasedValue.Factory();
        registerValueType(URL.class, factory, manager);
        registerValueType(BigInteger.class, factory, manager);
        registerValueType(BigDecimal.class, factory, manager);
        registerValueType(File.class, factory, manager);
    }

    /**
     * Registers {@link Enum} stuff.
     * 
     * @param manager The manager
     */
    private void registerEnumDefinition(IDataElementManager manager) {
        final String name = Enum.class.getName();
        manager.registerSupertypeFactory(name, new EnumElementFactory());
        manager.registerSuperTypeCategory(name, ElementCategory.VALUE);
    }

    /**
     * Registers wrapper type stuff.
     * 
     * @param manager The manager
     */
    private void registerWrapperTypeDefinition(IDataElementManager manager) {
        registerValueType(Boolean.class, new WrapperTypeFactory(), manager);
        registerValueType(Byte.class, new WrapperTypeFactory(), manager);
        registerValueType(Short.class, new WrapperTypeFactory(), manager);
        registerValueType(Character.class, new WrapperTypeFactory(), manager);
        registerValueType(Integer.class, new WrapperTypeFactory(), manager);
        registerValueType(Long.class, new WrapperTypeFactory(), manager);
        registerValueType(Float.class, new WrapperTypeFactory(), manager);
        registerValueType(Double.class, new WrapperTypeFactory(), manager);
    }

    /**
     * Register the factory for the given {@link IValueElement} type.
     * 
     * @param type The type
     * @param factory The factory to use
     * @param manager The manager
     */
    private void registerValueType(final Class<?> type, final IDataElementFactory factory, IDataElementManager manager) {
        final String name = type.getName();
        manager.registerTypeFactory(name, factory);
        manager.registerTypeCategory(name, ElementCategory.VALUE);
    }

    /**
     * Registers {@link Object} stuff.
     * 
     * @param manager The manager
     */
    private void registerObjectDefinitions(IDataElementManager manager) {
        manager.registerSupertypeFactory(Object.class.getName(), new StructureElementFactory());
    }
}
