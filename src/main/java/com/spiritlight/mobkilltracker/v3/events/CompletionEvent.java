package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.utils.Message;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CompletionEvent extends Event {
    private final DataHandler handler;

    public CompletionEvent(DataHandler handler) {
        Message.debugv("Constructing CompletionEvent for DataHandler " + handler);
        this.handler = handler;
    }

    public DataHandler getHandler() {
        return handler;
    }
}
