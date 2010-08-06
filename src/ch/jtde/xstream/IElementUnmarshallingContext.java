/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.xstream;

import org.eclipse.jdt.core.*;
import ch.jtde.model.*;
import com.thoughtworks.xstream.converters.*;

/**
 * {@link UnmarshallingContext} with some additional features for unmarshalling into {@link IDataElement}s.
 * 
 * @author M. Hautle
 */
public interface IElementUnmarshallingContext extends UnmarshallingContext {
    /**
     * Calls the responsible converter for the given element.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @param parent The parent element
     * @param value The value to populate
     * @return The populated value (may be an other instance than the passed one!)
     */
    @SuppressWarnings("rawtypes")
    <A extends IAttribute, E extends IDataElement<A>> E convertAnother(IDataElement parent, E value);

    /**
     * Returns the current element of the process stack.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @return The top element or null
     */
    <A extends IAttribute, E extends IDataElement<A>> E getCurrentElement();

    /**
     * Looks up the {@link IType} for the given class name.
     * 
     * @param name The fully qualified class name
     * @return The corresponding type or null if not found
     */
    IType findType(String name);

    /**
     * Returns the fully qualified name for a given alias/name.
     * 
     * @param alias The alias name or a fully qualified name
     * @return The fully qualified name
     */
    String typeForAlias(String alias);
}
