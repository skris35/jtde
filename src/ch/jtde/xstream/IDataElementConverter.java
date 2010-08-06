/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.xstream;

import ch.jtde.model.*;
import com.thoughtworks.xstream.converters.*;
import com.thoughtworks.xstream.io.*;

/**
 * Adapted version of {@link Converter} interface for {@link IDataElement} marshalling.
 * 
 * @param <E> The element type
 * @author M. Hautle
 */
public interface IDataElementConverter<E extends IDataElement<? extends IAttribute>> {
    /**
     * Convert an object to textual data.
     * 
     * @param source The object to be marshalled.
     * @param writer A stream to write to.
     * @param context A context that allows nested objects to be processed by XStream.
     */
    void marshal(E source, HierarchicalStreamWriter writer, IElementMarshallingContext context);

    /**
     * Convert textual data back into an object.
     * 
     * @param reader The stream to read the text from.
     * @param context
     * @return The resulting object.
     */
    E unmarshal(HierarchicalStreamReader reader, IElementUnmarshallingContext context);

    /**
     * Determines whether the converter can marshall a particular type.
     * 
     * @param type the Class representing the object type to be converted
     * @return True if the given type is supported
     */
    @SuppressWarnings("rawtypes")
    boolean canConvert(Class<? extends IDataElement> type);
}
