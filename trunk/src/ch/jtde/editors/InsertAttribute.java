package ch.jtde.editors;

import java.beans.*;
import java.util.*;
import ch.jtde.model.*;

/**
 * {@link IAttribute} used to represent the insert row for {@link IExtendableDataElement}s.
 * 
 * @author M. Hautle
 */
public class InsertAttribute implements IAttribute {
    /** Singleton instance of this attribute. */
    public static final InsertAttribute ME = new InsertAttribute();

    /**
     * Hidden constructor.
     */
    private InsertAttribute() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeclaringClass() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassDefinition getLowerBound() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AttributeState getState() {
        return AttributeState.DEFINED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IAttributeConstraint> getConstraints() {
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IAttribute> IDataElement<T> getValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends IAttribute> void setValue(IDataElement<T> value) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }
}