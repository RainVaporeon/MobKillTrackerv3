package com.spiritlight.mobkilltracker.v3.utils.test;

import com.spiritlight.mobkilltracker.v3.utils.collections.ConcurrentTimedSet;

import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws Exception {
        ConcurrentTimedSet<Integer> timedSet = new ConcurrentTimedSet<>(5, TimeUnit.SECONDS);
        for(int i = 0; i < 100; i++) {
            timedSet.add(i);
            Thread.sleep(50);
        }
        while(timedSet.size() > 0)
            System.out.println(timedSet.size());
    }
}
