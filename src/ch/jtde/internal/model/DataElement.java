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
import ch.jtde.model.*;

/**
 * {@link IDataElement} implementation for normal classes.
 * 
 * @author M. Hautle
 */
public class DataElement implements IDataElement<ElementAttribute> {
    /** The type this element. */
    private final ClassDefinition type;

    /** The attributes of this element. */
    private final List<ElementAttribute> attributes = new ArrayList<ElementAttribute>();

    /**
     * Default constructor.
     * 
     * @param type The fully qualified class name
     */
    public DataElement(ClassDefinition type) {
        this.type = type;
    }

    /**
     * {@inheritDoc}
     */
    public int getChildCount() {
        return attributes.size();
    }

    /**
     * {@inheritDoc}
     */
    public ElementAttribute getChild(int index) {
        return attributes.get(index);
    }

    /**
     * {@inheritDoc}
     */
    public ClassDefinition getType() {
        return type;
    }

    /**
     * Creates and adds a attribute according to the specified parameters.
     * 
     * @param name The attribute name
     * @param type The attribute type
     * @param declaringClass The declaring class
     * @return The created attribute
     */
    ElementAttribute defineAttribute(String name, ClassDefinition type, String declaringClass) {
        final ElementAttribute attr = new ElementAttribute(type, name, declaringClass);
        attributes.add(attr);
        return attr;
    }
}
