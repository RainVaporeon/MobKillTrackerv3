package com.spiritlight.mobkilltracker.v3.utils.math;

import java.math.BigDecimal;

/**
 * Utility class to do some maths. All methods use
 * the {@code strictfp} modifier.
 */
public class StrictMath {

    public static strictfp double decimalPointOf(double val) {
        return BigDecimal.valueOf(val)
                .subtract(BigDecimal.valueOf((int) val))
                .doubleValue();
    }

    public static strictfp double subtract(double a, double b) {
        return BigDecimal.valueOf(a)
                .subtract(BigDecimal.valueOf(b))
                .doubleValue();
    }

    public static strictfp double add(double a, double b) {
        return BigDecimal.valueOf(a)
                .add(BigDecimal.valueOf(b))
                .doubleValue();
    }
}
