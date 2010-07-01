package ch.jtde.internal.editors;

import java.util.regex.*;

/**
 * Validator for numbers checking only number type (i.e. float or integer 'compatibility').
 * 
 * @author M. Hautle
 */
public class NumberInputValidator implements InputValidator {
    /** The pattern. */
    private final static Pattern INT = Pattern.compile("[+-]?\\d+");

    /** The pattern. */
    private final static Pattern FLOAT = Pattern.compile("[+-]?\\d+(.\\d*)?");

    /** The pattern for the prevalidation. */
    private final Pattern pattern;

    /**
     * Default constructor
     * 
     * @param integer True for integer values, false for decimal values
     */
    public NumberInputValidator(boolean integer) {
        pattern = integer ? INT : FLOAT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String txt) {
        return pattern.matcher(txt).matches();
    }
}