/*
 * Copyright (c) 2010 M. Hautle.
 * 
 * All rights reserved. This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributor: M. Hautle - initial API and implementation
 */
package ch.jtde.model;

/**
 * State of an {@link IAttribute}.
 * 
 * @author M. Hautle
 */
public enum AttributeState {
    /** The attribute is defined - i.e. default state. */
    DEFINED,
    /** The attribute was added to the structure - i.e. it did not exist in the persisted model. */
    ADDED;
}
