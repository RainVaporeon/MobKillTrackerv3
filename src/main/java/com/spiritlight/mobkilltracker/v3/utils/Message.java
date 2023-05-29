package com.spiritlight.mobkilltracker.v3.utils;

import com.spiritlight.mobkilltracker.v3.enums.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class Message {
    public static final String TITLE = Color.GOLD + "[" + Color.GREEN + "MKT " + Color.YELLOW + "v3" + Color.GOLD + "] " + Color.RESET;

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

    public static void debug(String s) {
        send(s, Color.MAGENTA);
    }

    public static void send(String s) {
        send0(new TextComponentString(TITLE + s));
    }

    public static void send(String... s) {
        if(s == null) throw new NullPointerException();
        for(String str : s)
            send(str);
    }

    public static void send(String s, Color color) {
        send(color + s);
    }

    public static void sendRaw(String s) {
        send0(new TextComponentString(s));
    }

    public static void sendRaw(ITextComponent component) {
        send0(component);
    }

    public static String formatJson(String s) {
        return s
                .replace("{", TextFormatting.AQUA + "{" + TextFormatting.GOLD)
                .replace("}", TextFormatting.AQUA + "}" + TextFormatting.GOLD)
                .replace("[", TextFormatting.RESET + "[" + TextFormatting.GOLD)
                .replace("]", TextFormatting.RESET + "]" + TextFormatting.GOLD)
                .replace(",", TextFormatting.RESET + "," + TextFormatting.GOLD)
                .replace(":", TextFormatting.RESET + ":" + TextFormatting.AQUA)
                .replace("'", TextFormatting.YELLOW + "'" + TextFormatting.RESET)
                .replace("\"", TextFormatting.GREEN + "\"" + TextFormatting.GOLD);
    }

    private static void send0(ITextComponent content) {
        if(Minecraft.getMinecraft().player == null) return;
        Minecraft.getMinecraft().player.sendMessage(content);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String s) {
        return new Builder(s);
    }

    public static ITextComponent of(String content) {
        return new TextComponentString(content);
    }

    public static class Builder {
        private final ITextComponent component;

        public Builder() {
            this("");
        }

        public Builder(String content) {
            this.component = new TextComponentString(content);
        }

        public Builder addClickEvent(ClickEvent.Action action, String value) {
            this.component.setStyle(component.getStyle().setClickEvent(new ClickEvent(
                    action, value
            )));
            return this;
        }

        public Builder addHoverEvent(HoverEvent.Action action, ITextComponent value) {
            this.component.setStyle(component.getStyle().setHoverEvent(new HoverEvent(
                    action, value
            )));
            return this;
        }

        public Builder addSibling(ITextComponent component) {
            this.component.appendSibling(component);
            return this;
        }

        public Builder appendText(String value) {
            this.component.appendText(value);
            return this;
        }

        public Builder setStyle(Style style) {
            this.component.setStyle(style);
            return this;
        }

        /**
         * @return The component so far, technically no building process
         * is needed, so the component is just given without modification
         */
        public ITextComponent get() {
            return component;
        }

        // if you insist
        public ITextComponent build() {
            return this.get();
        }
    }
}
