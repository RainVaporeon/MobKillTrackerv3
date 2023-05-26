package com.spiritlight.mobkilltracker.v3.enums;

public enum Rarity {
    // How the weight is actually determined in v3:
    // The mythic is 1000000/7 (the probability, inverted)
    // Everything else is roughly 7 times less of the higher
    // tier's weight, that is, mythic has 7 times of fableds' weight,
    // and so on, it's a rough estimation but on this scale it doesn't
    // really matter i imagine

    MYTHIC(142857),
    FABLED(20408),
    LEGENDARY(2915),
    RARE(416),
    SET(238),
    UNIQUE(59),
    NORMAL(8),
    UNKNOWN(0);

    final int weight;

    Rarity(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }
}
