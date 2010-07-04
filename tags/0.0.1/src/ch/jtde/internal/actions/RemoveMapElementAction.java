/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.actions;

import org.eclipse.core.runtime.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * Action removing the currently selected attribute of an {@link MapElement}.
 * 
 * @author M. Hautle
 */
public class RemoveMapElementAction extends AbstractDataElementAction<MapAttribute> {
    /**
     * Default constructor.
     */
    public RemoveMapElementAction() {
        super("Remove");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void performAction(IDataEditor editor, IDataElement<MapAttribute> element, MapAttribute attribute) throws CoreException {
        final MapElement el = (MapElement) element;
        el.remove(attribute);
    }
}
