package com.spiritlight.mobkilltracker.v3;

import com.spiritlight.mobkilltracker.v3.command.MKTCommand;
import com.spiritlight.mobkilltracker.v3.command.MKTDebugCommand;
import com.spiritlight.mobkilltracker.v3.config.Config;
import com.spiritlight.mobkilltracker.v3.utils.DropManager;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.UncheckedIOException;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main {
    public static final String MODID = "mktv3";
    public static final String NAME = "MobKillTracker v3";
    public static final String VERSION = "3.0";

    public static final Config configuration = new Config();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Main::export));
        Runtime.getRuntime().addShutdownHook(new Thread(Main::save));
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        try {
            Class.forName("com.spiritlight.mobkilltracker.v3.config.Config");
            configuration.load();
        } catch (Exception e) {
            LogManager.getLogger(MODID).error("Failed to fetch config: ", e);
        }

        try {
            ItemDatabase.instance.fetchItem();
        } catch (Exception e) {
            LogManager.getLogger(MODID).error("Cannot fetch the API: ", e);
        }

        ClientCommandHandler.instance.registerCommand(new MKTCommand());
        ClientCommandHandler.instance.registerCommand(new MKTDebugCommand());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new com.spiritlight.mobkilltracker.v3.events.EventHandler());
    }

    public static void export() {
        try {
            System.out.println("Exporting drops...");
            DropManager.exportAllDrops();
            System.out.println("Drops exported.");
        } catch (Throwable t) {
            t.printStackTrace();
            if (t instanceof ThreadDeath) throw (ThreadDeath) t;
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
        }
    }

    public static void save() {
        try {
            System.out.println("Saving config...");
            configuration.save();
            System.out.println("Config saved successfully.");
        } catch (Throwable t) {
            t.printStackTrace();
            if (t instanceof ThreadDeath) throw (ThreadDeath) t;
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
        }
    }
}
