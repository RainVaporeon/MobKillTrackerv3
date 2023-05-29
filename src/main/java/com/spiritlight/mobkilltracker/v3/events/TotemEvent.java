package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.utils.Message;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TotemEvent extends Event {
    public TotemEvent() {
        if(Main.configuration.isLogging()) {
            Message.debug("Constructing TotemEvent");
        }
    }
}
