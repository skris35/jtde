/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.internal.model;

import java.util.*;
import org.eclipse.jdt.core.*;
import ch.jtde.internal.utils.*;
import ch.jtde.model.*;

/**
 * Implementation of {@link IConstraintManager}.
 * 
 * @author M. Hautle
 */
public class ConstraintManager implements IConstraintManager {
    /** The analyzers. */
    private List<IConstraintAnalyzer> analyzers;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAttributeConstraint> resolveConstraints(IField field) throws TechnicalModelException {
        if (analyzers.isEmpty())
            return Collections.emptyList();
        final List<IAttributeConstraint> res = new ArrayList<IAttributeConstraint>();
        for (IConstraintAnalyzer a : analyzers)
            a.appendConstraints(field, res);
        return res;
    }

    /**
     * Initializes the manager by fetching all registered {@link IConstraintAnalyzer}s.
     */
    public void initialize() {
        analyzers = ExtensionPointHelper.getConstraintAnalyzers();
    }
}
