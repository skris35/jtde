/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.editors;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.progress.*;
import ch.jtde.model.*;

/**
 * Offical API of the data editor.<br>
 * Only the methods published in this interface were expected to be called outside of the editor.
 * 
 * @author M. Hautle
 */
public interface IDataEditor {
    /** The editor id. */
    public static final String EDITOR_ID = "ch.jtde.editors.DataEditor";

    /**
     * Steps into the given element.<br>
     * This method must be called inside the SWT thread!
     * 
     * @param <T> The attribute type
     * @param name The name of the element (used for the history)
     * @param value The element
     */
    <T extends IAttribute> void stepInto(final String name, final IDataElement<T> value);

    /**
     * Returns the root element of the editors data model.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @return The root element
     */
    <A extends IAttribute, E extends IDataElement<A>> E getRootElement();

    /**
     * Returns the currently displayed element of the editor.
     * 
     * @param <A> The attribute type
     * @param <E> The element type
     * @return The current element
     */
    <A extends IAttribute, E extends IDataElement<A>> E getCurrentElement();

    /**
     * Returns the shell of the editor.
     * 
     * @return The shell
     */
    Shell getShell();

    /**
     * Returns the progress service for this editor.
     * 
     * @return The progress service
     */
    IWorkbenchSiteProgressService getProgressService();
}
