/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import static ch.jtde.internal.model.PrimitiveValue.Type.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.*;
import org.eclipse.jdt.core.util.*;
import ch.jtde.*;
import ch.jtde.internal.model.PrimitiveValue.Type;
import ch.jtde.model.*;

/**
 * Factory for structure {@link IDataElement}s - i.e. ordinary objects.
 * 
 * @author M. Hautle
 */
public class StructureElementFactory implements IDataElementFactory {
    /**
     * {@inheritDoc}
     */
    public IDataElement<IAttribute> create(IType type, IProgressMonitor pm) throws TechnicalModelException {
        try {
            return create0(type, pm);
        } catch (JavaModelException e) {
            throw new TechnicalModelException("Error while creating " + type.getFullyQualifiedName(), e);
        }
    }

    /**
     * Creates the given type.
     * 
     * @param type The type
     * @param pm A progressmonitor or null
     * @return The created type
     * @throws JavaModelException If something went wrong
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private IDataElement<IAttribute> create0(IType type, IProgressMonitor pm) throws JavaModelException {
        final IJavaProject project = type.getJavaProject();
        final DataElement el = new DataElement(ClassDefinition.create(type, ElementCategory.STRUCTURE));
        while (type != null) {
            for (IField f : type.getFields()) {
                // skip static fields
                if ((f.getFlags() & IModifierConstants.ACC_STATIC) == 0)
                    processField(el, f, type, project, pm);
            }
            final String parent = type.getSuperclassTypeSignature();
            type = parent != null ? resolveType(parent, type, pm) : null;
        }
        return (IDataElement) el;
    }

    /**
     * Create an attribute entry for the given field.
     * 
     * @param element The ownig element
     * @param field The field to process
     * @param parentType The parent type of the field
     * @param project The project owning the parent type
     * @param pm A progressmonitor or null
     * @throws JavaModelException
     */
    private void processField(DataElement element, IField field, IType parentType, IJavaProject project, IProgressMonitor pm) throws JavaModelException {
        final String name = field.getElementName();
        final String sig = field.getTypeSignature();
        final int sigType = Signature.getTypeSignatureKind(sig);
        final ElementAttribute attr;
        switch (sigType) {
            case Signature.BASE_TYPE_SIGNATURE:
                final Type type = resolvePrimitiveType(sig);
                final IValueElement<Object> val = new PrimitiveValue<Object>(type, project, pm);
                attr = element.defineAttribute(name, val.getType(), parentType.getFullyQualifiedName());
                attr.setValue(val);
                break;
            case Signature.CLASS_TYPE_SIGNATURE:
                final IType fType = resolveType(sig, parentType, pm);
                attr = element.defineAttribute(name, ClassDefinition.create(fType, Activator.getElementManager().getTypeCategory(fType, pm)),
                        parentType.getFullyQualifiedName());
                break;
            case Signature.ARRAY_TYPE_SIGNATURE:
                ClassDefinition ttype = getElementType(sig, parentType, pm);
                attr = element.defineAttribute(name, ttype, parentType.getFullyQualifiedName());
                break;
            case Signature.TYPE_VARIABLE_SIGNATURE:
            case Signature.WILDCARD_TYPE_SIGNATURE:
            case Signature.CAPTURE_TYPE_SIGNATURE:
            default:
                throw new IllegalArgumentException("Unsupported type: " + sigType);
        }
        // process the constraints
        attr.setConstraints(Activator.getConstraintManager().resolveConstraints(field));
    }

    /**
     * Returns the definition of an array field.
     * 
     * @param sig The type signature of the field
     * @param parentType The owning type
     * @param pm A progress monitor
     * @return The definition
     * @throws JavaModelException If something went wrong
     */
    private ClassDefinition getElementType(String sig, IType parentType, IProgressMonitor pm) throws JavaModelException {
        final int dim = Signature.getArrayCount(sig);
        final String elementSig = Signature.getElementType(sig);
        final int sigType = Signature.getTypeSignatureKind(elementSig);
        switch (sigType) {
            case Signature.BASE_TYPE_SIGNATURE:
                final Type type = resolvePrimitiveType(elementSig);
                return ClassDefinition.createArray(type.getType(parentType.getJavaProject(), pm), true, dim);
            case Signature.CLASS_TYPE_SIGNATURE:
                return ClassDefinition.createArray(resolveType(elementSig, parentType, pm), false, dim);
            default:
                throw new IllegalArgumentException("Unsupported element type: " + elementSig);
        }
    }

    /**
     * Returns the fully qualified type name of the given signature.
     * 
     * @param sig The signature
     * @param type The type containing the element with the given signature
     * @param pm A progressmonitor or null
     * @return The corresponding type
     * @throws JavaModelException
     */
    private IType resolveType(String sig, IType type, IProgressMonitor pm) throws JavaModelException {
        sig = extractType(sig);
        if (!type.isResolved()) {
            final String[][] res = type.resolveType(sig);
            sig = res[0][0] + "." + res[0][1];
        }
        return type.getJavaProject().findType(sig, pm);
    }

    /**
     * Extracts a qualifier from the type.
     * 
     * @param sig The signature
     * @return The extracted type
     */
    private String extractType(String sig) {
        sig = Signature.getElementType(sig);
        final String pkg = Signature.getSignatureQualifier(sig);
        final String name = Signature.getSignatureSimpleName(sig);
        return pkg.length() > 0 ? pkg + "." + name : name;
    }

    /**
     * Returns the {@link Type} for the given primitive type.
     * 
     * @param sig The signature
     * @return The value element
     */
    private Type resolvePrimitiveType(String sig) {
        switch (sig.charAt(0)) {
            case Signature.C_BOOLEAN:
                return BOOLEAN;
            case Signature.C_CHAR:
                return CHAR;
            case Signature.C_BYTE:
                return BYTE;
            case Signature.C_SHORT:
                return SHORT;
            case Signature.C_INT:
                return INTEGER;
            case Signature.C_LONG:
                return LONG;
            case Signature.C_FLOAT:
                return FLOAT;
            case Signature.C_DOUBLE:
                return DOUBLE;
        }
        throw new IllegalArgumentException("Unknown primitive type: " + sig);
    }
}
