/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

import java.util.*;
import org.eclipse.jdt.core.*;

/**
 * Manager for the {@link IConstraintAnalyzer}s.<br>
 * The {@link IDataElementFactory}s were responsible to call {@link #resolveConstraints(IField)} and to process the result.
 * 
 * @author M. Hautle
 */
public interface IConstraintManager {
    /**
     * Returns all constraints for the given field.<br>
     * This method simply calls {@link IConstraintAnalyzer#appendConstraints(IField, List)} for all registered analyzers.
     * 
     * @param field The field
     * @return The list of the constraints
     * @throws TechnicalModelException If something while resolving the constraints
     */
    List<IAttributeConstraint> resolveConstraints(IField field) throws TechnicalModelException;
}
