package com.spiritlight.mobkilltracker.v3.utils;

public class Test implements ConfigObject<Test> {

    private final int i;

    public Test(int i) {
        this.i = i;
        System.out.println(i);
    }

    @Override
    public String serialize() {
        return String.valueOf(i);
    }

    @Override
    public ConfigObject<Test> deserialize(String in) {
        return new Test(Integer.parseInt(in));
    }
}
