package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import net.minecraftforge.fml.common.eventhandler.Event;

public class TotemEvent extends Event {
    public TotemEvent() {
        Message.debugv("Constructing TotemEvent");
    }
}
