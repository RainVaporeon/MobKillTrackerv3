package com.spiritlight.mobkilltracker.v3.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import com.spiritlight.mobkilltracker.v3.enums.Type;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class ItemDatabase {
    public static final ItemDatabase instance = new ItemDatabase();

    private final Map<String, Rarity> itemMap = new HashMap<>();
    private final Map<String, Tier> ingredientMap = new HashMap<>();

    public Type getItemType(String item) {
        if(itemMap.containsKey(item)) return Type.ITEM;
        if(ingredientMap.containsKey(item)) return Type.INGREDIENT;
        return Type.UNKNOWN;
    }

    @Nonnull
    public Rarity getItemRarity(String item) {
        return itemMap.getOrDefault(item, Rarity.UNKNOWN);
    }

    @Nonnull
    public Tier getIngredientTier(String ingredient) {
        return ingredientMap.getOrDefault(ingredient, Tier.UNKNOWN);
    }

    public void fetchItem() {
        System.out.println("Collecting to db...");
        itemMap.clear();
        ingredientMap.clear();
        try {
            JsonElement items = new Gson().fromJson(Request.get("https://api.wynncraft.com/public_api.php?action=itemDB&category=all"), JsonElement.class);
            JsonArray arr = items.getAsJsonObject().getAsJsonArray("items");
            for (JsonElement element : arr) {
                String rarity = element.getAsJsonObject().get("tier").getAsString();
                Rarity itemRarity;
                switch(rarity) {
                    case "Mythic":
                        itemRarity = Rarity.MYTHIC;
                        break;
                    case "Fabled":
                        itemRarity = Rarity.FABLED;
                        break;
                    case "Legendary":
                        itemRarity = Rarity.LEGENDARY;
                        break;
                    case "Rare":
                        itemRarity = Rarity.RARE;
                        break;
                    case "Set":
                        itemRarity = Rarity.SET;
                        break;
                    case "Unique":
                        itemRarity = Rarity.UNIQUE;
                        break;
                    case "Normal":
                        itemRarity = Rarity.NORMAL;
                        break;
                    default:
                        System.out.println("Found ambiguous item " + element);
                        itemRarity = Rarity.UNKNOWN;
                        break;
                }
                // Item Name : Tier
                itemMap.put(element.getAsJsonObject().get("name").getAsString(), itemRarity);
            }
            for(int i=0; i<4; i++) {
                JsonElement ingredients = new Gson().fromJson(Request.get("https://api.wynncraft.com/v2/ingredient/search/tier/" + i), JsonElement.class);
                for (JsonElement element : ingredients.getAsJsonObject().getAsJsonArray("data")) {
                    Tier tier = (i == 0 ? Tier.ZERO : i == 1 ? Tier.ONE : i == 2 ? Tier.TWO : Tier.THREE);
                    ingredientMap.put(element.getAsJsonObject().get("name").getAsString(), tier);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("API fetched.");
    }
}
