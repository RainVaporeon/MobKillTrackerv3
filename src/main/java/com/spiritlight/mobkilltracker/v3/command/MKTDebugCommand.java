package com.spiritlight.mobkilltracker.v3.command;

import com.spiritlight.mobkilltracker.v3.events.TerminationEvent;
import com.spiritlight.mobkilltracker.v3.utils.Message;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;
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
            Message.error("Invalid syntax. /mktd stop");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "stop":
                MinecraftForge.EVENT_BUS.post(new TerminationEvent(null, TerminationEvent.Type.COMPLETE));
                Message.info("OK");
                return;
            default:
                Message.warn("/mktd");
        }
    }
}
