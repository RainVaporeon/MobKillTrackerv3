package com.spiritlight.mobkilltracker.v3.events;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

public class DelayedEvent<E extends Event> extends GenericEvent<E> {

    private final E event;

    @SuppressWarnings("unchecked")
    public DelayedEvent(E event) {
        super((Class<E>) event.getClass());
        this.event = event;
    }

    public E getEvent() {
        return event;
    }
}
