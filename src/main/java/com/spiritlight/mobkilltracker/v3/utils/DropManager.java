package com.spiritlight.mobkilltracker.v3.utils;

import java.util.LinkedList;
import java.util.List;

public class DropManager {
    private final List<DropStatistics> sessionData = new LinkedList<>();

    public static final DropManager instance = new DropManager();

    public void insert(DropStatistics stats) {
        this.sessionData.add(stats);
    }
}
