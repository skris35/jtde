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
 * Marker interface for extendable types.<br>
 * A extendable type is a {@link IDataElement} where {@link IAttribute}s can be added by the plugin user.
 * 
 * @param <T> The attribute type
 * @author M. Hautle
 */
public interface IExtendableDataElement<T extends IAttribute> extends IDataElement<T> {
    /**
     * Method to be called after attributes were added.<br>
     * This method must call {@link IAttributeChangeListener#attributesAdded(IExtendableDataElement, IAttribute...)} on all registered listeners.
     * 
     * @param attributes The added attributes
     */
    void fireAttributesAdded(T... attributes);

    /**
     * Method to be called after attributes were removed.<br>
     * This method must call {@link IAttributeChangeListener#attributesRemoved(IExtendableDataElement, IAttribute...)} on all registered listeners.
     * 
     * @param attributes The removed attributes
     */
    void fireAttributesRemoved(T... attributes);

    /**
     * Adds the given listener.
     * 
     * @param l The listener to add
     */
    void addAttributeChangeListener(IAttributeChangeListener<T> l);

    /**
     * Removes the given listener.
     * 
     * @param l The listener to remove
     */
    void removeAttributeChangeListener(IAttributeChangeListener<T> l);

    /**
     * Handler to use for extending {@link IExtendableDataElement}s.
     * 
     * @param <T> The element type
     * @author M. Hautle
     */
    public interface IExtendableDataElementHandler<T extends IExtendableDataElement<?>> {
        /**
         * Method called to extend the given element by some {@link IAttribute}s.<br>
         * This method may ask for some userinput or simply execute some internal logic...<br>
         * The action must ensure that {@link IExtendableDataElement#fireAttributesAdded(IAttribute...)} gets called if it added attributes.
         * 
         * @param element The concerned element
         */
        void extendDataElement(T element);
    }

    /**
     * Attribute listener for {@link IExtendableDataElement}s.
     * 
     * @param <T> The attribute type
     * @author M. Hautle
     */
    public static interface IAttributeChangeListener<T extends IAttribute> {
        /**
         * Method called after attributes were added to the given element.
         * 
         * @param element The changed element
         * @param attributes The added attributes
         */
        void attributesAdded(IExtendableDataElement<T> element, T... attributes);

        /**
         * Method called after a attributes were removed from the given element.
         * 
         * @param element The changed element
         * @param attributes The removed attributes
         */
        void attributesRemoved(IExtendableDataElement<T> element, T... attributes);
    }
}
