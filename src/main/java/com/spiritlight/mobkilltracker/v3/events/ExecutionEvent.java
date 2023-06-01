package com.spiritlight.mobkilltracker.v3.events;

import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Utility event type to allow the main thread to be executing certain
 * instructions rather than on a parallel thread.
 */
public class ExecutionEvent extends Event {
    private final Runnable action;
    private final Object target;
    private final Class<?> targetClass;

    public ExecutionEvent(Class<?> target, Runnable action) {
        this.action = action;
        this.targetClass = target;
        this.target = target;
    }

    public ExecutionEvent(Object target, Runnable action) {
        this.action = action;
        this.target = target;
        this.targetClass = null;
    }

    public ExecutionEvent(Runnable action) {
        this.action = action;
        this.target = null;
        this.targetClass = null;
    }

    /**
     * Whether this object is supposed to execute this action
     * @param invoker The calling object
     * @return Whether execution is for this object or not
     */
    public boolean shouldExecute(Object invoker) {
        if(this.target == invoker) return true;
        if(this.targetClass == invoker.getClass()) return true;
        return target == null && targetClass == null;
    }

    public Runnable getAction() {
        return action;
    }
}
