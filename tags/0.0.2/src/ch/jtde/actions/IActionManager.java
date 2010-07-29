/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.actions;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.model.*;

/**
 * Manager for {@link IDataElementAction}s.
 * 
 * @author M. Hautle
 */
public interface IActionManager {
    /**
     * Returns all actions for the given type.<br>
     * This method evaluates the actions registered by the extension point.
     * 
     * @param <T> The attribute type
     * @param type The class
     * @param pm A progressmonitor
     * @return The actions
     * @throws JavaModelException If something went wrong during type analysis
     */
    <T extends IAttribute> List<IDataElementAction<T>> getTypeActions(IType type, IProgressMonitor pm) throws JavaModelException;
}
