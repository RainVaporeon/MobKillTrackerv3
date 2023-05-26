package com.spiritlight.mobkilltracker.v3.events;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.utils.DropManager;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * A general event handler in which processes most events
 */
public class EventHandler {

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {

    }

    @SubscribeEvent
    public void onTotemPlacement(TotemEvent event) {
        DataHandler.newHandler().whenComplete(DropManager.instance::insert).start();
    }

    @SubscribeEvent
    public void onMessageReceived(ClientChatReceivedEvent chat) {
        if(!Main.configuration.isModEnabled()) return;
        final String message = chat.getMessage().getUnformattedText();
        if((message.contains("placed a mob totem") && !message.contains("["))) {
            MinecraftForge.EVENT_BUS.post(new TotemEvent());
        }
    }
}
