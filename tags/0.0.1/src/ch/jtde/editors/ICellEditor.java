/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.editors;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.*;
import ch.jtde.model.*;

/**
 * Interface of an cell editor and renderer for {@link IValueElement}s.
 * 
 * @param <V> The value type
 * @param <E> The element type
 * @author M. Hautle
 */
public interface ICellEditor<V, E extends IValueElement<V>> {
    /** Representation of <code>null</code>. */
    public static final String NULL_REPRESANTATION = "<null>";

    /**
     * Initializes the editor.<br>
     * This method will be called before the editor get's used the first time.
     * 
     * @param parent The parent composite
     */
    void initialize(Composite parent);

    /**
     * Returns the string representation of the given element.
     * 
     * @param element The element
     * @return It's string representation
     */
    String getRenderedValue(E element);

    /**
     * Returns the cell editor for the {@link IValueElement} hold by the given attribute.<br>
     * The value of{@link IValueElement#getValue()} will be passed to {@link CellEditor#setValue(Object)}.<br>
     * The return value of{@link CellEditor#getValue()} must be the value to pass to {@link IValueElement#setValue(Object)}.
     * 
     * @param attribute The attribute holding the element
     * @return The cell editor
     */
    CellEditor getEditor(IAttribute attribute);

    /**
     * Returns wherever the given element is editable.
     * 
     * @param element The element
     * @return True if the given element is editable
     */
    boolean isEditable(E element);
}
