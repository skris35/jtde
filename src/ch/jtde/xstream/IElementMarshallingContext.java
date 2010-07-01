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

/**
 * {@link MarshallingContext} with some additional features for marshalling {@link IDataElement} contents.
 * 
 * @author M. Hautle
 */
public interface IElementMarshallingContext extends MarshallingContext {
    /**
     * Returns the name to use for a given type name.
     * 
     * @param name The fully qualified name
     * @return The alias name or the passed one
     */
    String aliasForType(String name);
}
