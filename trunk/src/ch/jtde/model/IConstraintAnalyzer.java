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
 * Interface describing an constraint analyzer returning the {@link IAttributeConstraint}s for a {@link IAttribute}.<br>
 * You may create a analyzer to respect annotation based field constraints.<br>
 * The {@link IConstraintManager} is responsible to call {@link #appendConstraints(IField, List)}.<br>
 * The analyzer must be stateless.
 * 
 * @author M. Hautle
 */
public interface IConstraintAnalyzer {
    /**
     * Appends the constraints for the given field to the passed list.
     * 
     * @param field The field
     * @param result The list were to append the constraints
     * @throws TechnicalModelException If something while resolving the constraints
     */
    void appendConstraints(IField field, List<IAttributeConstraint> result) throws TechnicalModelException;
}
