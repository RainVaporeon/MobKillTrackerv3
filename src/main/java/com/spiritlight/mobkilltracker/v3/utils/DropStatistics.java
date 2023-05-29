package com.spiritlight.mobkilltracker.v3.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spiritlight.mobkilltracker.v3.annotations.Synchronized;
import com.spiritlight.mobkilltracker.v3.enums.Tier;

import java.util.Objects;

import static com.spiritlight.mobkilltracker.v3.enums.Rarity.*;

public class DropStatistics {
    private final Object lock = new Object();

    private volatile int
    mythic = 0,
    fabled = 0,
    legendary = 0,
    rare = 0,
    set = 0,
    unique = 0,
    normal = 0,
    ingredient3 = 0,
    ingredient2 = 0,
    ingredient1 = 0,
    ingredient0 = 0,
    kills = 0;

    private String note;

    public DropStatistics() {}

    public DropStatistics(DropStatistics that) {
        this.mythic = that.mythic;
        this.fabled = that.fabled;
        this.legendary = that.legendary;
        this.rare = that.rare;
        this.set = that.set;
        this.unique = that.unique;
        this.normal = that.normal;
        this.ingredient3 = that.ingredient3;
        this.ingredient2 = that.ingredient2;
        this.ingredient1 = that.ingredient1;
        this.ingredient0 = that.ingredient0;
        this.kills = that.kills;
        this.note = that.note;
    }

    public static final int ITEM = 0, INGREDIENT = 1, ALL = 2;
    public int getQuantity(int type) {
        return type == ITEM ?
                // item total
                mythic + fabled + legendary + rare + set + unique + normal :
                type == INGREDIENT ?
                // ingredient total
                ingredient3 + ingredient2 + ingredient1 + ingredient0 :
                mythic + fabled + legendary + rare + set + unique+ normal +
                ingredient3 + ingredient2 + ingredient1 + ingredient0;
    }

    /**
     * Gets this session's total weight
     * @return The total weight
     */
    public double getTotalWeight() {
        return mythic * MYTHIC.getWeight() +
                fabled * FABLED.getWeight() +
                legendary * LEGENDARY.getWeight() +
                rare * RARE.getWeight() +
                set * SET.getWeight() +
                unique * UNIQUE.getWeight() +
                normal * NORMAL.getWeight();
    }

    /**
     * Gets this session's average weight
     * @return The total weight, divided by the items
     */
    public double getAverageWeight() {
        return getTotalWeight() / getQuantity(ITEM);
    }

    /**
     * Gets this session's rarity index
     * This shows the average rarity weight from each mob kill
     * @return The average weight, divided by the kills
     */
    public double getRarityIndex() {
        return getAverageWeight() / kills;
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        object.addProperty("mythic", mythic);
        object.addProperty("fabled", fabled);
        object.addProperty("legendary", legendary);
        object.addProperty("rare", rare);
        object.addProperty("set", set);
        object.addProperty("unique", unique);
        object.addProperty("normal", normal);
        object.addProperty("ingredient3", ingredient3);
        object.addProperty("ingredient2", ingredient2);
        object.addProperty("ingredient1", ingredient1);
        object.addProperty("ingredient0", ingredient0);
        object.addProperty("kills", kills);
        object.addProperty("note", note);
        return object.toString();
    }

    public JsonElement toJson() {
        return new Gson().fromJson(this.toString(), JsonObject.class);
    }

    @Synchronized
    public void addKill() {
        synchronized (lock) {
            kills++;
        }
    }

    @Synchronized
    public void addMythic() {
        synchronized (lock) {
            mythic++;
        }
    }

    @Synchronized
    public void addFabled() {
        synchronized (lock) {
            fabled++;
        }
    }

    @Synchronized
    public void addLegendary() {
        synchronized (lock) {
            legendary++;
        }
    }

    @Synchronized
    public void addRare() {
        synchronized (lock) {
            rare++;
        }
    }

    @Synchronized
    public void addSet() {
        synchronized (lock) {
            set++;
        }
    }

    @Synchronized
    public void addUnique() {
        synchronized (lock) {
            unique++;
        }
    }

    @Synchronized
    public void addNormal() {
        synchronized (lock) {
            normal++;
        }
    }

    @Synchronized
    public void addTier(Tier tier) {
        synchronized (lock) {
            switch (tier) {
                case THREE:
                    ingredient3++; break;
                case TWO:
                    ingredient2++; break;
                case ONE:
                    ingredient1++; break;
                case ZERO:
                    ingredient0++; break;
                case UNKNOWN:
                    System.out.println("Found unknown rarity");
            }
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public String getNote() {
        return note;
    }

    public boolean hasNote() {
        return note != null && !note.isEmpty();
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getMythic() {
        return mythic;
    }

    public void setMythic(int mythic) {
        this.mythic = mythic;
    }

    public int getFabled() {
        return fabled;
    }

    public void setFabled(int fabled) {
        this.fabled = fabled;
    }

    public int getLegendary() {
        return legendary;
    }

    public void setLegendary(int legendary) {
        this.legendary = legendary;
    }

    public int getRare() {
        return rare;
    }

    public void setRare(int rare) {
        this.rare = rare;
    }

    public int getSet() {
        return set;
    }

    public void setSet(int set) {
        this.set = set;
    }

    public int getUnique() {
        return unique;
    }

    public void setUnique(int unique) {
        this.unique = unique;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getIngredient3() {
        return ingredient3;
    }

    public void setIngredient3(int ingredient3) {
        this.ingredient3 = ingredient3;
    }

    public int getIngredient2() {
        return ingredient2;
    }

    public void setIngredient2(int ingredient2) {
        this.ingredient2 = ingredient2;
    }

    public int getIngredient1() {
        return ingredient1;
    }

    public void setIngredient1(int ingredient1) {
        this.ingredient1 = ingredient1;
    }

    public int getIngredient0() {
        return ingredient0;
    }

    public void setIngredient0(int ingredient0) {
        this.ingredient0 = ingredient0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DropStatistics that = (DropStatistics) o;
        return mythic == that.mythic && fabled == that.fabled && legendary == that.legendary && rare == that.rare && set == that.set && unique == that.unique && normal == that.normal && ingredient3 == that.ingredient3 && ingredient2 == that.ingredient2 && ingredient1 == that.ingredient1 && ingredient0 == that.ingredient0 && kills == that.kills && Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mythic, fabled, legendary, rare, set, unique, normal, ingredient3, ingredient2, ingredient1, ingredient0, kills, note);
    }
}
