package com.spiritlight.mobkilltracker.v3.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Information annotation to note that the annotated
 * method is synchronized or contains synchronized
 * operation in the processing.
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Synchronized {

}
