package com.spiritlight.mobkilltracker.v3;

import com.spiritlight.mobkilltracker.v3.config.Config;
import com.spiritlight.mobkilltracker.v3.core.EntityEventHandler;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "mktv3";
    public static final String NAME = "MobKillTracker v3";
    public static final String VERSION = "3.0";

    public static final Config configuration = new Config();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        try {
            ItemDatabase.instance.fetchItem();
        } catch (Exception e) {
            LogManager.getLogger(MODID).error("Cannot fetch the API: ", e);
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new com.spiritlight.mobkilltracker.v3.events.EventHandler());
    }
}
