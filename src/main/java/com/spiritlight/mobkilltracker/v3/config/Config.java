package com.spiritlight.mobkilltracker.v3.config;

import com.google.gson.stream.JsonWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

public class Config {
    private static final String FILE_NAME = "MobKillTracker3.json";

    @SessionOnly
    private boolean modEnabled = true;

    @SessionOnly
    private boolean logging = false;

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

    public void save() throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter(FILE_NAME));
        try {
            for(Field f : this.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                // Skip session variables
                if(f.isAnnotationPresent(SessionOnly.class)) continue;
                String name = f.getName();
                Object value = f.get(this);

                if(value instanceof String) {
                    writer.name(name).value((String) value);
                } else if (value instanceof Number) {
                    writer.name(name).value((Number) value);
                } else if (value instanceof Boolean) {
                    writer.name(name).value((Boolean) value);
                } else {
                    writer.name(name).value(String.valueOf(value));
                }
            }
        } catch (ReflectiveOperationException ex) {
            // handle...
        }
    }
}
