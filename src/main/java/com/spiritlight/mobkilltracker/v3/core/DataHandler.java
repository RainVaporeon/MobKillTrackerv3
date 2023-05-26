package com.spiritlight.mobkilltracker.v3.core;

import com.spiritlight.mobkilltracker.v3.events.CompletionEvent;
import com.spiritlight.mobkilltracker.v3.utils.DropStatistics;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DataHandler {
    private final EntityEventHandler handler;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Consumer<DropStatistics> completionAction = null;

    private DataHandler() {
        handler = new EntityEventHandler();
    }

    public void start() {
        start(300);
    }

    public void start(int duration) {
        MinecraftForge.EVENT_BUS.register(handler);
        scheduler.schedule(this::stop, duration, TimeUnit.SECONDS);
    }

    public DataHandler whenComplete(Consumer<DropStatistics> handler) {
        this.completionAction = handler;
        return this;
    }

    public DropStatistics getStats() {
        return handler.getStats();
    }

    public DropStatistics stop() {
        completion();
        return handler.getStats();
    }

    public void terminate() {
        this.scheduler.shutdownNow();
        completion();
    }

    private void completion() {
        MinecraftForge.EVENT_BUS.unregister(handler);
        if(completionAction != null) completionAction.accept(handler.getStats());
        // unregister and post so the termination handler is not called twice
        MinecraftForge.EVENT_BUS.post(new CompletionEvent(this));
    }

    public static DataHandler newHandler() {
        return new DataHandler();
    }
}
