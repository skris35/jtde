/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.actions;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import ch.jtde.actions.*;
import ch.jtde.internal.utils.*;
import ch.jtde.internal.utils.ExtensionPointHelper.ActionEntry;
import ch.jtde.model.*;

/**
 * Implementation of {@link IActionManager}.
 * 
 * @author M. Hautle
 */
public class ActionManager implements IActionManager {
    /** Fully qualified name of {@link Object}. */
    private static final String OBJECT_NAME = Object.class.getName();

    /** Explicit type to action mappings. */
    @SuppressWarnings("rawtypes")
    private final Map<String, List<IDataElementAction>> typeActions = new HashMap<String, List<IDataElementAction>>();

    /** Type hierarchie to action mapping. */
    @SuppressWarnings("rawtypes")
    private final Map<String, List<IDataElementAction>> superTypeActions = new HashMap<String, List<IDataElementAction>>();

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends IAttribute> List<IDataElementAction<T>> getTypeActions(IType type, IProgressMonitor pm) throws JavaModelException {
        final Set<IDataElementAction> actions = new HashSet<IDataElementAction>();
        append(type.getFullyQualifiedName(), typeActions, actions);
        // append all supertype actions
        append(type.getFullyQualifiedName(), superTypeActions, actions);
        appendSuperTypeActions(type, actions, type.newSupertypeHierarchy(pm));
        return new ArrayList<IDataElementAction<T>>((Set) actions);
    }

    /**
     * Adds the given action into the passed map.
     * 
     * @param name The fully qualified type name
     * @param action The action to add
     * @param actionMap The map where to add the action
     */
    @SuppressWarnings("rawtypes")
    private void add(String name, IDataElementAction action, Map<String, List<IDataElementAction>> actionMap) {
        List<IDataElementAction> res = actionMap.get(name);
        if (res == null)
            actionMap.put(name, res = new ArrayList<IDataElementAction>(1));
        res.add(action);
    }

    /**
     * Appends all actions registred of an supertypes of the given type to the passed list.<br>
     * This method processes the supertypes recursively.
     * 
     * @param type The type to process
     * @param res The result list
     * @param h The type hierarchy to search throug
     * @throws JavaModelException If something went wrong
     */
    @SuppressWarnings("rawtypes")
    private void appendSuperTypeActions(IType type, final Set<IDataElementAction> res, final ITypeHierarchy h) throws JavaModelException {
        for (IType t : h.getSupertypes(type)) {
            append(t.getFullyQualifiedName(), superTypeActions, res);
            appendSuperTypeActions(t, res, h);
        }
        // interface type hierarchie does not contain object :(
        if (type.isInterface())
            append(OBJECT_NAME, superTypeActions, res);
    }

    /**
     * Appends all actions for the given type to the passed list.
     * 
     * @param name The fully qualified type name
     * @param actionMap The action map
     * @param res The list where to append the actions
     */
    @SuppressWarnings("rawtypes")
    private void append(String name, Map<String, List<IDataElementAction>> actionMap, Set<IDataElementAction> res) {
        final List<IDataElementAction> list = actionMap.get(name);
        if (list != null && list.size() > 0)
            res.addAll(list);
    }

    /**
     * Initializes the manager.
     */
    public void initialize() {
        for (ActionEntry a : ExtensionPointHelper.getActionInstances()) {
            if (a.isConcrete())
                add(a.getType(), a.getAction(), typeActions);
            else
                add(a.getType(), a.getAction(), superTypeActions);
        }
    }
}
