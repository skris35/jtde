/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.utils;

import static ch.jtde.internal.utils.EclipseUtils.*;
import java.util.*;
import org.eclipse.core.runtime.*;
import ch.jtde.actions.*;
import ch.jtde.editors.*;
import ch.jtde.model.*;
import ch.jtde.xstream.*;

/**
 * Helper for the extension point stuff.
 * 
 * @author M. Hautle
 */
public class ExtensionPointHelper {
    /** Extensionpoint ID for the celleditor point. */
    private static final String CELL_EDITORS = "ch.jtde.cellEditors";

    /** Extensionpoint ID for the constraint analyzer point. */
    private static final String ANALYZERS = "ch.jtde.constraintAnalyzers";

    /** Extensionpoint ID for the element point. */
    private static final String ELEMENTS = "ch.jtde.elements";

    /** Extensionpoint ID for the action point. */
    private static final String ACTIONS = "ch.jtde.elementActions";

    /** Extensionpoint ID for the xstream converter point. */
    private static final String CONVERTER = "ch.jtde.xstream.converter";

    /**
     * Returns a fresh {@link ICellEditor} instance for the given {@link IValueElement} type.
     * 
     * @param <E> The element type
     * @param type The element type
     * @return The editor or null if none registred for the type
     */
    @SuppressWarnings("rawtypes")
    public static <E extends IValueElement> ICellEditor getEditorInstanceFor(Class<E> type) {
        final String name = type.getName();
        try {
            for (IConfigurationElement e : getExtension(CELL_EDITORS)) {
                if (name.equals(e.getAttribute("name")))
                    return (ICellEditor) e.createExecutableExtension("editor");
            }
        } catch (InvalidRegistryObjectException e) {
            showError(getCurrentShell(), "Error while fetching celleditors for " + name, e);
        } catch (CoreException e) {
            showError(getCurrentShell(), "Error while fetching celleditors for " + name, e);
        }
        return null;
    }

    /**
     * Returns an list with all instances defined for the action extension point.
     * 
     * @return The instances
     */
    @SuppressWarnings("rawtypes")
    public static List<ActionEntry> getActionInstances() {
        final List<ActionEntry> res = new ArrayList<ActionEntry>();
        try {
            for (IConfigurationElement e : getExtension(ACTIONS))
                res.add(new ActionEntry(e.getAttribute("type"), (IDataElementAction) e.createExecutableExtension("name"), "action".equals(e.getName())));
        } catch (InvalidRegistryObjectException e) {
            showError(getCurrentShell(), "Error while fetching actions", e);
        } catch (CoreException e) {
            showError(getCurrentShell(), "Error while fetching actions", e);
        }
        return res;
    }

    /**
     * Returns all registered {@link IConstraintAnalyzer}s.
     * 
     * @return The analyzers
     */
    public static List<IConstraintAnalyzer> getConstraintAnalyzers() {
        return getInstances(ANALYZERS, "Error while fetching constraint analyzers");
    }

    /**
     * Returns all registered {@link IConstraintAnalyzer}s.
     * 
     * @return The analyzers
     */
    public static List<IDataElementDescriptor> getElementDescriptors() {
        return getInstances(ELEMENTS, "Error while fetching element descriptors");
    }

    /**
     * Returns all registered {@link IDataElementConverter}s.
     * 
     * @return The converters
     */
    public static List<IDataElementConverter<?>> getXStreamConverters() {
        return getInstances(CONVERTER, "Error while fetching XStream converters");
    }

    /**
     * Returns an list with all instances defined for the given extension point.<br>
     * Simply instanciates every <code>name</code> attribute of the points children.
     * 
     * @param <T> The element type
     * @param point The extension point id
     * @param error The message for the exception in case of an error
     * @return The instances
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> getInstances(final String point, final String error) {
        final List<T> res = new ArrayList<T>();
        try {
            for (IConfigurationElement e : getExtension(point))
                res.add((T) e.createExecutableExtension("name"));
        } catch (InvalidRegistryObjectException e) {
            showError(getCurrentShell(), error, e);
        } catch (CoreException e) {
            showError(getCurrentShell(), error, e);
        }
        return res;
    }

    /**
     * Returns the sub elements of the given extension point.
     * 
     * @param point The point name
     * @return The extension sub elements
     */
    private static IConfigurationElement[] getExtension(String point) {
        return Platform.getExtensionRegistry().getConfigurationElementsFor(point);
    }

    /**
     * Object describing an action entry in the action extension point.
     * 
     * @author M. Hautle
     */
    @SuppressWarnings("rawtypes")
    public static class ActionEntry {
        /** The type on which to regiser. */
        private final String type;

        /** The action */
        private final IDataElementAction action;

        /** True for a concrete action, false for a supertype action. */
        private final boolean concrete;

        /**
         * Default constructor.
         * 
         * @param type The type on which to regiser
         * @param action The action
         * @param concrete True for a concrete action, false for a supertype action
         */
        public ActionEntry(String type, IDataElementAction action, boolean concrete) {
            this.type = type;
            this.action = action;
            this.concrete = concrete;
        }

        /**
         * @return Returns the type.
         */
        public String getType() {
            return type;
        }

        /**
         * @return Returns the action.
         */
        public IDataElementAction getAction() {
            return action;
        }

        /**
         * @return Returns the concrete.
         */
        public boolean isConcrete() {
            return concrete;
        }
    }
}
