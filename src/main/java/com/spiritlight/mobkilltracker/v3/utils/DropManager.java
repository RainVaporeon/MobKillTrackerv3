package com.spiritlight.mobkilltracker.v3.utils;

import com.google.gson.JsonArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DropManager {
    // We use LinkedList implementation here for ease of accessing last element
    private final LinkedList<DropStatistics> sessionData = new LinkedList<>();

    public static final DropManager instance = new DropManager();

    public void insert(DropStatistics stats) {
        this.sessionData.add(stats);
    }

    public boolean hasData() {
        return !sessionData.isEmpty();
    }

    public int size() {
        return sessionData.size();
    }

    public DropStatistics get(int index) {
        return sessionData.get(index);
    }

    public void remove(int index) {
        sessionData.remove(index);
    }

    public List<DropStatistics> getBackingList() {
        return sessionData;
    }

    public Iterator<DropStatistics> iterator() {
        return sessionData.iterator();
    }

    public void clear() {
        sessionData.clear();
    }

    public DropStatistics getLast() {
        return sessionData.getLast();
    }

    public static void exportDrops(String name, List<DropStatistics> stats) {
        Message.info("Exporting " + stats.size() + " stats...");
        name = name.contains(".json") ? name : name + ".json";
        File file = new File(name);
        if(file.exists()) {
            Message.error("This name already exists, please choose another name!");
            return;
        }
        try(FileWriter writer = new FileWriter("mkt_out/" +name)) {
            JsonArray array = new JsonArray();
            for(DropStatistics stat : stats) {
                array.add(stat.toJson());
            }
            writer.write(array.toString());
            Message.info("Exported data! You can find them in your minecraft folder, under the mkt_out folder.");
        } catch (IOException e) {
            Message.error("An error has occurred: Check the logs for details.");
            e.printStackTrace();
        }
    }

    public static void exportAllDrops() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss");
            String name = sdf.format(new Date());
            String fileName = name;
            int i = 1;
            while(new File(fileName + ".json").exists()) {
                fileName = name + i++;
            }
            exportDrops(fileName, DropManager.instance.sessionData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final DecimalFormat df = new DecimalFormat("0.00");
    public static String dropToString(DropStatistics drops) {
        int totalDrops = drops.getQuantity(DropStatistics.ALL);
        int itemDrops = drops.getQuantity(DropStatistics.ITEM);
        int ingDrops = drops.getQuantity(DropStatistics.INGREDIENT);
        int kills = drops.getKills();
        double ingRate = (ingDrops == 0 ? 0 : (double) kills / ingDrops);
        double itemRate = (itemDrops == 0 ? 0 : (double) kills / itemDrops);
        return                         "§rTotal Mobs Killed: §c" + kills + "\n" +
                "§rTotal Items Dropped: §a" + totalDrops + "\n" +
                "\n" +
                "§6§l Item Summary: \n" +
                "§rIngredient Drops: §b[✫✫✫] §rx" + drops.getIngredient3() + " §d[✫✫§8✫§d] §rx" + drops.getIngredient2() + " §e[✫§8✫✫§e] §rx" + drops.getIngredient1() + " §7[§8✫✫✫§7] §rx" + drops.getIngredient0() + "\n" +
                "§5§lMythic §rDrops: " + drops.getMythic() + "\n" +
                "§cFabled §rDrops: " + drops.getFabled() + "\n" +
                "§bLegendary §rDrops: " + drops.getLegendary() + "\n" +
                "§dRare §rDrops: " + drops.getRare() + "\n" +
                "§aSet §rDrops: " + drops.getSet() + "\n" +
                "§eUnique §rDrops: " + drops.getUnique() + "\n" +
                "§rNormal §rDrops: " + drops.getNormal() + "\n" +
                "Total drops: Item " + itemDrops + ", Ingredients " + ingDrops +
                "\n §c§lAdvanced details:\n" +
                "§rItem Rate: " + df.format(itemRate) + " §7(Mobs/item)" + "\n" +
                "§rIngredient Rate: " + df.format(ingRate) + " §7(Mobs/Ingredient)" + "\n" +
                "§rRarity Index: " + drops.getRarityIndex();
    }
}
