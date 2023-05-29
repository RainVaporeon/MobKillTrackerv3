package com.spiritlight.mobkilltracker.v3.utils;

/**
 * Declares a config object.
 * <p>
 *     A config object by contract should be able to be
 *     serialized into a {@link String}, and deserialized
 *     back to an object. The deserialized object should
 *     maintain meaningful equality with the object that
 *     generated the serializing string.
 * </p>
 * @param <T> The type of object to return
 */
public interface ConfigObject<T> {

    /**
     * Deserializes using this given String.
     * @param in The input string
     * @return The deserialized object
     */
    ConfigObject<T> deserialize(String in); // Needed during config loading

    /**
     * Serialize the object into a String object.
     * @return The String object representing this object.
     */
    String serialize();
}
