/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.actions;

import org.eclipse.core.runtime.*;
import ch.jtde.editors.*;
import ch.jtde.model.*;

/**
 * Context menu action for {@link IDataElement}s.
 * 
 * @param <T> The attribute type of the element
 * @author M. Hautle
 */
public interface IDataElementAction<T extends IAttribute> {
    /**
     * Returns the label text for the action.
     * 
     * @return The action label
     */
    String getLabel();

    /**
     * Returns wherever the action should be shown for the given selection.
     * 
     * @param <S> The attribute type of the selection (may be different from <code>T</code>)
     * @param element The parent element
     * @param attribute The selected attribute or null
     * @return True if the action should be shown for this element
     */
    <S extends IAttribute> boolean isEnabledFor(IDataElement<S> element, S attribute);

    /**
     * Performs the action on the given selection.
     * 
     * @param editor The editor on which the action was called
     * @param element The parent element
     * @param attribute The selected attribute or null
     * @throws CoreException If something went wrong
     */
    void performAction(IDataEditor editor, IDataElement<T> element, T attribute) throws CoreException;
}
