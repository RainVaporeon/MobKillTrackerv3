package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CompletionEvent extends Event {
    private final DataHandler handler;

    public CompletionEvent(DataHandler handler) {
        this.handler = handler;
    }

    public DataHandler getHandler() {
        return handler;
    }
}
