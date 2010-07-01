/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.extenders;

import java.util.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.IExtendableDataElement.*;

/**
 * {@link IExtendableDataElementHandler} for {@link List}.
 * 
 * @author M. Hautle
 */
public class ListExtender implements IExtendableDataElementHandler<IndexCollectionElement> {
    /**
     * {@inheritDoc}
     */
    @Override
    public void extendDataElement(IndexCollectionElement element) {
        element.add();
    }
}
