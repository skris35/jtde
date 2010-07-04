/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.descriptors;

import java.util.*;
import ch.jtde.internal.extenders.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * {@link IDataElementDescriptor} for {@link Collection}s.
 * 
 * @author M. Hautle
 */
public class CollectionElementDescriptor implements IDataElementDescriptor {
    /**
     * {@inheritDoc}
     */
    @Override
    public void registerTypes(IDataElementManager manager) {
        CollectionElementFactory.registerFactories(manager);
        manager.registerExtendableDataElementHandler(List.class.getName(), new ListExtender());
        manager.registerExtendableDataElementHandler(Collection.class.getName(), new CollectionExtender());
        manager.registerExtendableDataElementHandler(Set.class.getName(), new CollectionExtender());
        registerMap(manager);
    }

    /**
     * Registers the elements for a map.
     * 
     * @param manager The manager
     */
    private void registerMap(IDataElementManager manager) {
        manager.registerExtendableDataElementHandler(Map.class.getName(), new MapExtender());
    }
}
