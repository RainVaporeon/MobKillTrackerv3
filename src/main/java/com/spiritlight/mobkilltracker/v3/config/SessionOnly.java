package com.spiritlight.mobkilltracker.v3.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares that this setting is session-only, that is,
 * this configuration is not saved in the save file.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SessionOnly {

}
