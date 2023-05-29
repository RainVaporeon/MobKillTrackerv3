package com.spiritlight.mobkilltracker.v3.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates that this field is not for configuration
 * purposes and should be ignored during saving/loading.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigIgnore {
}
