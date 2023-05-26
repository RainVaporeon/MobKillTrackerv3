package com.spiritlight.mobkilltracker.v3.utils;

import com.spiritlight.mobkilltracker.v3.enums.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class Message {
    public static final String TITLE = Color.GOLD + "[" + Color.GREEN + "MKT " + Color.RED + "v3" + Color.GOLD + "] " + Color.RESET;

    public static void info(String s) {
        send(s, Color.WHITE);
    }

    public static void warn(String s) {
        send(s, Color.YELLOW);
    }

    public static void error(String s) {
        send(s, Color.RED);
    }

    public static void fatal(String s) {
        send(s, Color.DARK_RED);
    }

    public static void send(String s) {
        send0(new TextComponentString(TITLE + s));
    }

    public static void send(String s, Color color) {
        send(TITLE + color + s);
    }

    public static void sendRaw(String s) {
        send0(new TextComponentString(s));
    }

    public static void sendRaw(ITextComponent component) {
        send0(component);
    }

    private static void send0(ITextComponent content) {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(content);
    }
}
