package ch.jtde.internal.editors;

import ch.jtde.internal.editors.PrimitiveTypeEditor.*;

/**
 * Validator for {@link ValueEditor}.
 * 
 * @author M. Hautle
 */
public interface InputValidator {
    /**
     * Checks wherever the given string value is valid.
     * 
     * @param value The value
     * @return True if it's valid
     */
    boolean isValid(String value);
}