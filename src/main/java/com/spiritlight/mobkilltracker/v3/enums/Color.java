package com.spiritlight.mobkilltracker.v3.enums;

public enum Color {
    BLACK(0),
    DARK_BLUE(1),
    DARK_GREEN(2),
    DARK_AQUA(3),
    DARK_RED(4),
    DARK_PURPLE(5),
    GOLD(6),
    GRAY(7),
    DARK_GRAY(8),
    BLUE(9),
    GREEN("a"),
    AQUA("b"),
    RED("c"),
    LIGHT_PURPLE("d"), MAGENTA("d"),
    YELLOW("e"),
    WHITE("f"),
    RESET("r"), ITALIC("o"), UNDERLINE("n"), STRIKETHROUGH("m"), BOLD("l"), OBFUSCATED("k")
    ;

    final String id;

    Color(int id) {
        this.id = String.valueOf(id);
    }

    Color(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getFormatString() {
        return "§" + id;
    }

    @Override
    public String toString() {
        return this.getFormatString();
    }
}
