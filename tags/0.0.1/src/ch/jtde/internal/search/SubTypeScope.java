/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.search;

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.search.*;
import org.eclipse.jdt.internal.compiler.util.*;
import ch.jtde.internal.utils.*;
import ch.jtde.internal.utils.EclipseUtils.*;

/**
 * {@link IJavaSearchScope} selecting only subtypes of a specified type.
 * 
 * @author M. Hautle
 */
@SuppressWarnings("restriction")
public class SubTypeScope implements IJavaSearchScope, SuffixConstants {
    /** The base type used for the lookup. */
    private final IType baseType;

    /** The path of {@link #baseType}. */
    private final String baseTypePath;

    /** The valid sub types of {@link #baseType} or null if not yet initialized. */
    private Set<IType> validTypes;

    /** Paths of all subtypes of {@link #baseType} contained in {@link #validTypes}. */
    private final Set<String> resourcePaths = new HashSet<String>();

    /** The paths of the projects and jars to search through. */
    private IPath[] enclosingProjectsAndJars;

    /**
     * Default constructor.
     * 
     * @param type The base type
     * @throws CoreException If something went wrong
     */
    private SubTypeScope(IType type) throws CoreException {
        baseType = type;
        enclosingProjectsAndJars = computeClassPathResources(type);
        baseTypePath = getPath(type);
    }

    /**
     * Creates as sub type search scope.
     * 
     * @param type The base type
     * @return The search scope
     * @throws CoreException If something went wrong
     */
    public static IJavaSearchScope create(IType type) throws CoreException {
        return new SubTypeScope(type);
    }

    /**
     * Returns the resource path of the given type.
     * 
     * @param type The type
     * @return The path
     */
    private String getPath(IType type) {
        final IPath p = type.getPath();
        if (type.isBinary())
            return p.toString() + JAR_FILE_ENTRY_SEPARATOR + type.getFullyQualifiedName().replace('.', '/') + SUFFIX_STRING_class;
        return p.toString();
    }

    /**
     * Computes all resources referenced by the classpath of the project holding the given type.
     * 
     * @param type The base type
     * @return The resource classpaths
     * @throws CoreException If something went wrong
     */
    private IPath[] computeClassPathResources(IType type) throws CoreException {
        final Set<IPath> res = new HashSet<IPath>();
        final IJavaProject root = type.getJavaProject();
        res.add(root.getPath());
        EclipseUtils.processClassPathEntries(root, new ClassPathProcessor() {
            @Override
            public void addProject(IJavaProject p) {
                res.add(p.getPath());
            }

            @Override
            public void addLibrary(IClasspathEntry e) {
                res.add(e.getPath());
            }
        });
        final IPath[] result = new IPath[res.size()];
        res.toArray(result);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean encloses(String resourcePath) {
        if (baseTypePath.equals(resourcePath))
            return true;
        try {
            return isEnclosed(resourcePath);
        } catch (JavaModelException e) {
            return false;
        }
    }

    /**
     * Checks wherever the given type is must be considered in the search.
     * 
     * @param resourcePath The path to check
     * @return True if the given path is in the search scope
     * @throws JavaModelException
     */
    private boolean isEnclosed(String resourcePath) throws JavaModelException {
        initialize();
        return resourcePaths.contains(resourcePath);
    }

    /**
     * {@inheritDoc}
     */
    public boolean encloses(IJavaElement element) {
        if (baseType.equals(element.getAncestor(IJavaElement.TYPE)))
            return true;
        final IType type = extractType(element);
        if (type == null)
            return false;
        try {
            if (isSubType(type))
                return true;
            // be flexible: look at original element (see bug 14106 Declarations in Hierarchy does not find declarations in hierarchy)
            IType original;
            if (!type.isBinary() && (original = (IType) type.getPrimaryElement()) != null)
                return isSubType(original);
        } catch (JavaModelException e) {
            return false;
        }
        return false;
    }

    /**
     * Extracts the corresponding {@link IType}.
     * 
     * @param element A element or null
     * @return The type or null
     */
    private IType extractType(IJavaElement element) {
        if (element instanceof IType)
            return (IType) element;
        if (element instanceof IMember)
            return ((IMember) element).getDeclaringType();
        return null;
    }

    /**
     * Returns wherever the given type is a sub type of {@link #baseType}.
     * 
     * @param type The type to check
     * @return True if it's a sub type
     * @throws JavaModelException
     */
    private boolean isSubType(IType type) throws JavaModelException {
        initialize();
        return validTypes.contains(type);
    }

    /**
     * {@inheritDoc}
     */
    public IPath[] enclosingProjectsAndJars() {
        return enclosingProjectsAndJars;
    }

    /**
     * Initializes {@link #validTypes} and update {@link #enclosingProjectsAndJars()} not yet done.
     * 
     * @throws JavaModelException If something went wrong
     */
    private void initialize() throws JavaModelException {
        if (validTypes != null)
            return;
        validTypes = EclipseUtils.getSubTypes(baseType, new NullProgressMonitor());
        computeResources();
    }

    /**
     * Computes the contained resources of {@link #validTypes}.
     * 
     * @throws JavaModelException If something went wrong
     */
    private void computeResources() throws JavaModelException {
        final Set<IPath> paths = new HashSet<IPath>();
        for (IType type : validTypes)
            addTypePath(type, paths);
        enclosingProjectsAndJars = new IPath[paths.size()];
        int i = 0;
        for (IPath p : paths)
            enclosingProjectsAndJars[i++] = p;
    }

    /**
     * Adds the path of the given type to the passed set and registers it in {@link #resourcePaths}.
     * 
     * @param type The type
     * @param paths The path set
     */
    private void addTypePath(IType type, final Set<IPath> paths) {
        paths.add(type.isBinary() ? type.getPath() : type.getJavaProject().getPath());
        resourcePaths.add(getPath(type));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HierarchyScope on " + baseType.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public boolean includesBinaries() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public boolean includesClasspaths() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void setIncludesBinaries(boolean includesBinaries) {
    }

    /**
     * {@inheritDoc}
     */
    @Deprecated
    public void setIncludesClasspaths(boolean includesClasspaths) {
    }
}
