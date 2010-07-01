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
 * Interface for a descriptor registring different {@link IDataElement} related elements/information.
 * 
 * @author M. Hautle
 */
public interface IDataElementDescriptor {
    /**
     * Registration hook.<br>
     * Register the different elements using the register methods of the passed manager.<br>
     * 
     * @param manager The manager
     */
    void registerTypes(IDataElementManager manager);
}
