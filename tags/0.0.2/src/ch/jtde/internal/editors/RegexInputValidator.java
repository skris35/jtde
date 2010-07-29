package ch.jtde.internal.editors;

import java.util.regex.*;

/**
 * Regex based validator.
 * 
 * @author M. Hautle
 */
public class RegexInputValidator implements InputValidator {
    /** The pattern. */
    private final Pattern pattern;

    /**
     * Default constructor
     * 
     * @param pattern The pattern to validate against
     */
    public RegexInputValidator(String pattern) {
        this(pattern, 0);
    }

    /**
     * Default constructor
     * 
     * @param pattern The pattern to validate against
     * @param flags The pattern flags
     */
    public RegexInputValidator(String pattern, int flags) {
        this.pattern = Pattern.compile(pattern, flags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String txt) {
        return pattern.matcher(txt).matches();
    }
}