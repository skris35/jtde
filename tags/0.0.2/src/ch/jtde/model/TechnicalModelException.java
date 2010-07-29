/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

import org.eclipse.core.runtime.*;

/**
 * {@link RuntimeException} container for technical {@link CoreException}s.<br>
 * Use this to avoid declared exception propagation for rare occouring technical exceptions.
 * 
 * @author M. Hautle
 */
public class TechnicalModelException extends RuntimeException {
    /**
     * Default constructor.
     * 
     * @param message The exception message
     * @param cause The exception cause
     */
    public TechnicalModelException(String message, CoreException cause) {
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CoreException getCause() {
        return (CoreException) super.getCause();
    }
}
