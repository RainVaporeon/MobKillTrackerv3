package com.spiritlight.mobkilltracker.v3.command;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.core.DataHandler;
import com.spiritlight.mobkilltracker.v3.events.TerminationEvent;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.Message;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class MKTDebugCommand extends CommandBase implements IClientCommand {
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "mktd";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if(args.length == 0) {
            Message.error("Invalid syntax. /mktd stop|start <duration>|refetch|delay <duration>|log|dump");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "stop":
                MinecraftForge.EVENT_BUS.post(new TerminationEvent(null, TerminationEvent.Type.COMPLETE));
                Message.info("OK");
                return;
            case "start":
                int length = args.length == 1 ? 30 : Integer.parseInt(args[1]);
                DataHandler.newListenedHandler().start(length);
                Message.info("OK (Not Logged)");
                return;
            case "refetch":
                Message.info("Processing...");
                ItemDatabase.instance.fetchItem();
                Message.info("Fetched.");
                return;
            case "delay":
                if(args.length == 1) {
                    Message.warn("Invalid usage: /mktd delay <duration/ms>");
                    Message.warn("Default: 100(ms)");
                    Message.info("Description: Delay before an item is added to scan queue.");
                    return;
                }
                Main.configuration.setDelayMills(Integer.parseInt(args[1]));
                Message.info("OK=" + args[1] + "ms");
                return;
            case "log":
                Main.configuration.setLogging(!Main.configuration.isLogging());
                Message.info(String.valueOf(Main.configuration.isLogging()));
                return;
            case "logvalid":
                Main.configuration.setLogValid(!Main.configuration.doLogValid());
                Message.info(String.valueOf(Main.configuration.doLogValid()));
                return;
            case "exportall":
                Main.export();
                Message.info("OK");
                return;
            case "save":
                Main.save();
                Message.info("OK");
                return;
            case "load":
                try {
                    Main.configuration.load();
                } catch (Exception e) {
                    Message.fatal("Cannot load config: " + e.getMessage());
                    e.printStackTrace();
                }
                Message.info("OK");
                return;
            case "dump":
                List<Entity> loadedEntities = Minecraft.getMinecraft().world.getLoadedEntityList();
                for(Entity e : loadedEntities) {
                    try {
                        ITextComponent itc = Message.builder("Entity " + e.getName() + ":")
                                .addHoverEvent(HoverEvent.Action.SHOW_TEXT, Message.of(Message.formatJson(String.valueOf(e.serializeNBT())))).build();
                        Message.sendRaw(itc);
                        System.out.println(e.getName() + "#" + e.serializeNBT());
                    } catch (Exception ex) {
                        Message.error("Error whilst dumping entity: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                return;
            default:
                Message.warn("/mktd");
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
