package com.spiritlight.mobkilltracker.v3.enums;

// Ingredient tier, we already know the probability of
// ingredient is actually nearly universal and henceforth
// calculation of the weight is not needed at all here.

// I'll still add it in comments for anyone curious anyway
public enum Tier {
    // Rare T3: 15% (Regional 10%) / Default 0.05%
    THREE,
    // Rare T2: 100% / Default 0.5%
    TWO,
    // Rare T1: 100% / Default 5%
    ONE,
    // 15% (Quests are mostly 50%, mainly on the Dodegar's weapon one)
    ZERO,
    // Cannot fetch
    UNKNOWN
}
