/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde;

import org.eclipse.jface.resource.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.*;
import ch.jtde.actions.*;
import ch.jtde.internal.actions.*;
import ch.jtde.internal.model.*;
import ch.jtde.model.*;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
    /** The plugin id. */
    public static final String PLUGIN_ID = "ch.jtde";

    /** The plugins instance. */
    private static Activator plugin;

    /** The data element manager. */
    private final DataElementManager manager = new DataElementManager();

    /** The constraint manager. */
    private final ConstraintManager analyzer = new ConstraintManager();

    /** The action manager. */
    private final ActionManager actions = new ActionManager();

    /**
     * The constructor
     */
    public Activator() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        manager.initialize();
        analyzer.initialize();
        actions.initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns the {@link IDataElementManager} of this plugin.
     * 
     * @return The manager
     */
    public static IDataElementManager getElementManager() {
        if (plugin == null)
            throw new IllegalStateException("The plugin is currently not active!");
        return plugin.manager;
    }

    /**
     * Returns the {@link IActionManager} of this plugin.
     * 
     * @return The manager
     */
    public static IActionManager getActionManager() {
        if (plugin == null)
            throw new IllegalStateException("The plugin is currently not active!");
        return plugin.actions;
    }

    /**
     * Returns the {@link IConstraintManager} of this plugin.
     * 
     * @return The manager
     */
    public static IConstraintManager getConstraintManager() {
        if (plugin == null)
            throw new IllegalStateException("The plugin is currently not active!");
        return plugin.analyzer;
    }

    /**
     * Returns an image descriptor for the image file at the given plug-in relative path
     * 
     * @param path the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }
}
