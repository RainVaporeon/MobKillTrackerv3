package com.spiritlight.mobkilltracker.v3.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.spiritlight.mobkilltracker.v3.utils.ConfigObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * The configuration class. <p>
 *     Annotate a field with {@link SessionOnly} to declare that the
 *     property does not persist over saves and should be ignored
 *     during saving process
 * </p><p>
 *     Any fields not annotated with {@link SessionOnly} and is not
 *     an instance of {@link ConfigObject} will also be ignored during
 *     serialization/deserialization process.
 * </p>
 */
public class Config {
    @ConfigIgnore
    private static final String FILE_NAME = "MobKillTracker3.json";

    @SessionOnly // Mod enabled
    private boolean modEnabled = true;

    @SessionOnly // Logging
    private boolean logging = false;

    @SessionOnly // Logging, but only valid stuffs
    private boolean logValid = false;

    private int delayMills = 100;

    public int getDelayMills() {
        return delayMills;
    }

    public void setDelayMills(int delayMills) {
        this.delayMills = delayMills;
    }

    public boolean isModEnabled() {
        return modEnabled;
    }

    public void setModEnabled(boolean modEnabled) {
        this.modEnabled = modEnabled;
    }

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean doLogValid() {
        return logValid;
    }

    public void setLogValid(boolean logValid) {
        this.logValid = logValid;
    }

    public void save() throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter(FILE_NAME));
        writer.beginObject();
        try {
            for(Field f : this.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                // Skip session variables
                if(f.isAnnotationPresent(SessionOnly.class) || f.isAnnotationPresent(ConfigIgnore.class)) continue;
                // Ignore static fields
                if(Modifier.isStatic(f.getModifiers())) continue;
                if(Modifier.isFinal(f.getModifiers())) continue;
                String name = f.getName();
                Object value = f.get(this);

                if(value instanceof String) {
                    writer.name(name).value((String) value);
                } else if (value instanceof Number) {
                    writer.name(name).value((Number) value);
                } else if (value instanceof Boolean) {
                    writer.name(name).value((Boolean) value);
                } else if (value instanceof ConfigObject) {
                    writer.name(name).value(((ConfigObject<?>) value).serialize());
                } else {
                    System.out.println("Found ambiguous field " + name + " with type " + value.getClass());
                }
                // Ignore other types
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        } finally {
            writer.endObject();
            writer.close();
        }
    }

    public void load() throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = (JsonObject)parser.parse(Files.newBufferedReader(new File(FILE_NAME).toPath(), StandardCharsets.UTF_8));
        try {
            for(Field f : this.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                // Skip session variables
                if(f.isAnnotationPresent(SessionOnly.class) || f.isAnnotationPresent(ConfigIgnore.class)) continue;
                if(Modifier.isFinal(f.getModifiers())) continue;
                if(Modifier.isStatic(f.getModifiers())) continue;
                String name = f.getName();
                Class<?> type = f.getType();
                if(type == String.class) {
                    f.set(jsonObject.get(name).getAsString(), this);
                } else if (type == boolean.class) {
                    f.setBoolean(this, jsonObject.get(name).getAsBoolean());
                } else if (type == double.class) {
                    f.setDouble(this, jsonObject.get(name).getAsDouble());
                } else if (type == int.class) {
                    f.setInt(this, jsonObject.get(name).getAsInt());
                } else if (ConfigObject.class.isAssignableFrom(type)) {
                    try {
                        ConfigObject<?> cfg = ((ConfigObject<?>) type.newInstance()).deserialize(jsonObject.get(name).getAsString());
                        f.set(cfg, this);
                    } catch (Exception e) {
                        throw new JsonParseException("Cannot find suitable type for name " + name + " with type " + type);
                    }
                } else {
                    System.out.println("Found ambiguous field " + name + " with type " + type);
                }
                // We do not try to serialize other fields
            }
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
