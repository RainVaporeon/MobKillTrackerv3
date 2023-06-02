package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TerminationEvent extends Event {
    private final DataHandler handler;
    private final Type type;

    public TerminationEvent(DataHandler terminate, Type type) {
        Message.debugv("Constructing TerminationEvent for DataHandler " + terminate + " and type " + type);
        this.handler = terminate;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public DataHandler getHandler() {
        return handler;
    }

    public enum Type {
        TERMINATE,
        COMPLETE
    }
}
