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
import org.eclipse.jdt.core.*;
import ch.jtde.model.*;

/**
 * Element representing an {@link Enum} field.
 * 
 * @author M. Hautle
 */
public class EnumElement extends AbstractValueElement<String> {
    /** The value. */
    private String value;

    /**
     * Default constructor.
     * 
     * @param type The enum type
     */
    EnumElement(ClassDefinition type) {
        super(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String value) {
        this.value = fireValueChanged(this.value, value);
    }

    /**
     * Returns the constant names of the referenced enum.
     * 
     * @return The constant names
     * @throws JavaModelException
     */
    public List<String> getConstants() throws JavaModelException {
        final IType type = getType().getType();
        final List<String> res = new ArrayList<String>();
        for (IField f : type.getFields())
            if (Flags.isEnum(f.getFlags()))
                res.add(f.getElementName());
        return res;
    }
}
