package com.spiritlight.mobkilltracker.v3.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates that this field have changed its name and that
 * the config file should adapt to it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LegacyField {
    /**
     * The previous name(s) of this field
     * @return The old name of this field
     */
    String[] value();
}
