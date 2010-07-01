package ch.jtde.internal.editors;


/**
 * Validator for numbers checking number type, upper and lower bound.
 * 
 * @author M. Hautle
 */
public class BoundedNumberInputValidator extends NumberInputValidator {
    /** The minimum value. */
    private final double min;

    /** The maximum value. */
    private final double max;

    /**
     * Default constructor
     * 
     * @param min The minimum value
     * @param max The maximum value
     * @param integer True for integer values, false for decimal values
     */
    public BoundedNumberInputValidator(double min, double max, boolean integer) {
        super(integer);
        this.min = min;
        this.max = max;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String txt) {
        if (!super.isValid(txt))
            return false;
        final double d = Double.parseDouble(txt);
        if (d < min || d > max)
            return false;
        return true;
    }
}