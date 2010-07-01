/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.utils;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.ui.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.dialogs.*;
import ch.jtde.*;

/**
 * Utility methods for eclipse stuff.
 * 
 * @author hautle
 */
public final class EclipseUtils {
    /** Name of {@link Object}. */
    private static final String OBJECT_NAME = Object.class.getName();

    /**
     * Hidden constructor.
     */
    private EclipseUtils() {
    }

    /**
     * Displays a type search dialog.
     * 
     * @param shell The shell
     * @param scope The search scope
     * @param searchText The initial search filter text
     * @return The selected type or null
     * @throws JavaModelException If something went wrong
     */
    public static IType showConcreteTypeDialog(Shell shell, IJavaSearchScope scope, String searchText) throws JavaModelException {
        final SelectionDialog d = JavaUI.createTypeDialog(shell, new ProgressMonitorDialog(shell), scope, IJavaElementSearchConstants.CONSIDER_ALL_TYPES,
                false, searchText);
        d.setTitle("Select the concreate type");
        d.setMessage("Select a type (* = any string, ? = any char):");
        d.open();
        final Object[] sel = d.getResult();
        if (sel != null && sel.length > 0)
            return (IType) sel[0];
        return null;
    }

    /**
     * Returns a set of all subtypes of the given type (including itself).<br>
     * Cause it makes no sense to return all subtypes of <code>java.lang.Object</code> the method returns just <code>null</code> in this case.
     * 
     * @param type The type
     * @param pm A fresh progressmonitor
     * @return The sub types or null for all types
     * @throws JavaModelException If something went wrong
     */
    public static Set<IType> getSubTypes(IType type, IProgressMonitor pm) throws JavaModelException {
        // skip subtype list for objects
        if (isObject(type))
            return null;
        final Set<IType> t = new HashSet<IType>();
        Collections.addAll(t, type.newTypeHierarchy(pm).getSubtypes(type));
        t.add(type);
        return t;
    }

    /**
     * Checks wherever the given type represents the object class.
     * 
     * @param type The type to check
     * @return True if the type is <code>java.lang.Object</code>
     */
    public static boolean isObject(IType type) {
        return isObject(type.getFullyQualifiedName());
    }

    /**
     * Checks wherever the given type name represents the object class.
     * 
     * @param name A fully qualified name
     * @return True if the type name is <code>java.lang.Object</code>
     */
    public static boolean isObject(final String name) {
        return OBJECT_NAME.equals(name);
    }

    /**
     * Shows an error dialog for the given exception.
     * 
     * @param shell The shell to use
     * @param message The error message
     * @param e The exception
     */
    public static void showError(final Shell shell, final String message, final Throwable e) {
        final StringBuilder stack = new StringBuilder();
        if (e != null) {
            final StackTraceElement[] trace = e.getStackTrace();
            for (int i = 0; i < trace.length && i < 10; i++)
                stack.append(trace[i].toString()).append('\n');
        }
        // display
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(shell, "Error", message, new Status(IStatus.ERROR, Activator.PLUGIN_ID, stack.toString(), e));
            }
        });
    }

    /**
     * Displays a message box.
     * 
     * @param shell The shell
     * @param title The box title
     * @param message The box message
     * @param buttons OR'ed SWT flags describing the desired answer buttons (like {@link SWT#YES} {@link SWT#NO} {@link SWT#OK} etc.) and the dialog style (like
     *            {@link SWT#ICON_INFORMATION} {@link SWT#ICON_WARNING} etc.)
     * @return The SWT value of the pressed button
     */
    public static int showMessageBox(final Shell shell, final String title, final String message, final int buttons) {
        MessageBox mb = new MessageBox(shell, buttons);
        mb.setText(title);
        mb.setMessage(message);
        return mb.open();
    }

    /**
     * Passes all class path entries to the given handler.<br>
     * 
     * @param project The project
     * @param handler The handler to use
     * @throws CoreException If something went wrong
     */
    public static void processClassPathEntries(IJavaProject project, ClassPathProcessor handler) throws CoreException {
        for (IClasspathEntry cp : project.getResolvedClasspath(true)) {
            switch (cp.getEntryKind()) {
                case IClasspathEntry.CPE_PROJECT:
                    final IJavaProject pr = getJavaProject(cp.getPath());
                    if (pr != null)
                        handler.addProject(pr);
                    break;
                case IClasspathEntry.CPE_LIBRARY:
                    handler.addLibrary(cp);
                    break;
            }
        }
    }

    /**
     * Returns the path to the workspace.
     * 
     * @return The absolute path to the workspace
     */
    public static IPath getWorkspacePath() {
        return ResourcesPlugin.getWorkspace().getRoot().getLocation();
    }

    /**
     * Returns the project holding the given path.
     * 
     * @param path The path
     * @return The project holding the given path
     */
    public static IProject getProject(IPath path) {
        return ResourcesPlugin.getWorkspace().getRoot().findMember(path).getProject();
    }

    /**
     * Returns the project with the given name.
     * 
     * @param name The project name
     * @return The project holding the given path
     */
    public static IProject getProject(String name) {
        return ResourcesPlugin.getWorkspace().getRoot().findMember(name).getProject();
    }

    /**
     * Retruns the current shell.
     * 
     * @return The shell
     */
    public static Shell getCurrentShell() {
        return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    }

    /**
     * Returns the java project holding the given path.
     * 
     * @param path The path
     * @return The java project
     * @throws CoreException
     */
    public static IJavaProject getJavaProject(IPath path) throws CoreException {
        return getJavaProject(path.toString());
    }

    /**
     * Returns the java project nature of the given project.
     * 
     * @param proj The project
     * @return The java project or null
     * @throws CoreException
     */
    public static IJavaProject getJavaProject(IProject proj) throws CoreException {
        return (IJavaProject) proj.getNature(JavaCore.NATURE_ID);
    }

    /**
     * Returns the java project with the given name.
     * 
     * @param name The project name
     * @return The java project
     * @throws CoreException
     */
    public static IJavaProject getJavaProject(String name) throws CoreException {
        return getJavaProject(getProject(name));
    }

    /**
     * Makes a path absolute by adding the workspace path to it (if neccessary)
     * 
     * @param workspacePath The absolute workspace path
     * @param path The path
     * @return The given path made absolute
     */
    public static String getAbsolutePath(String workspacePath, IPath path) {
        if (path.getDevice() != null)
            return path.toString();
        return workspacePath + path.toString();
    }

    /**
     * Makes a path absolute by adding the workspace path to it (if neccessary)
     * 
     * @param workspacePath The absolute workspace path
     * @param path The path
     * @return The given path made absolute
     */
    public static IPath getAbsolutePath(IPath workspacePath, IPath path) {
        if (path.getDevice() != null)
            return path;
        return workspacePath.append(path);
    }

    /**
     * Executes the given taks in the SWT thread.<br>
     * This method returns after the task was executed.
     * 
     * @param taks The task
     */
    public static void synchSWTCall(Runnable taks) {
        PlatformUI.getWorkbench().getDisplay().syncExec(taks);
    }

    /**
     * Handler interface for {@link EclipseUtils#processClassPathEntries(IJavaProject, ClassPathProcessor)} .
     * 
     * @author hautle
     */
    public interface ClassPathProcessor {
        /**
         * Processes a library entry.
         * 
         * @param e The entry of the library
         * @throws CoreException
         */
        void addLibrary(IClasspathEntry e) throws CoreException;

        /**
         * Processes a project entry.<br>
         * The exported libraries of the passed project will be handled automatically.
         * 
         * @param p A project
         * @throws CoreException
         */
        void addProject(IJavaProject p) throws CoreException;
    }
}
