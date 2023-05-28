package com.spiritlight.mobkilltracker.v3.core;

import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import com.spiritlight.mobkilltracker.v3.utils.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EntityEventHandler {
    private final DropStatistics stats = new DropStatistics();


    public EntityEventHandler() {

    }

    public DropStatistics getStats() {
        return stats;
    }

    private final Set<Entity> storedEntities = new LinkedHashSet<>();

    private final Set<Entity> queuedEntities = new CopyOnWriteArraySet<>();

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    @SubscribeEvent
    public void onEntityUpdate(EntityEvent.EntityConstructing event) {
        final Entity entity = event.getEntity();
        if(storedEntities.contains(entity)) return;
        // Processing items in this tab
        executor.schedule(() -> queuedEntities.add(entity), 100, TimeUnit.MILLISECONDS);
    }

    // Somewhat preferred method rather than scanning literally everything
    @SubscribeEvent
    public void analyzeEntities(TickEvent.WorldTickEvent event) {
        for(Entity entity : queuedEntities) {
            if(storedEntities.contains(entity)) {
                // We can already assume that this is processed, so skip
                queuedEntities.remove(entity);
                continue;
            }
            if (entity instanceof EntityItem) {
                EntityItem entityItem = (EntityItem) entity;
                // Ignoring emerald for sake of our life
                if(Items.EMERALD.equals(entityItem.getItem().getItem())) {
                    exclusion(entity); continue;
                }
                String itemName = entityItem.getItem().getDisplayName();
                Rarity rarity = ItemDatabase.instance.getItemRarity(itemName);
                if(rarity != null) {
                    manageRarity(rarity);
                    exclusion(entity);
                    continue;
                }

                // This line is only reached if no item was fetched
                Tier tier = ItemDatabase.instance.getIngredientTier(itemName);
                stats.addTier(tier);
            } else {
                // Process entities here
                if(entity.getName().toLowerCase(Locale.ROOT).contains("combat xp")) {
                    stats.addKill();
                    exclusion(entity);
                }
            }

        }
    }

    /**
     * Adds this entity to stored entities and remove it from
     * the queue. This is ideally called after all processing
     * has been done and is OK to discard it.
     * @param entity The entity to add
     */
    private void exclusion(Entity entity) {
        storedEntities.add(entity);
        queuedEntities.remove(entity);
    }

    private void manageRarity(Rarity rarity) {
        switch(rarity) {
            case MYTHIC:
                stats.addMythic(); break;
            case FABLED:
                stats.addFabled(); break;
            case LEGENDARY:
                stats.addLegendary(); break;
            case RARE:
                stats.addRare(); break;
            case SET:
                stats.addSet(); break;
            case UNIQUE:
                stats.addUnique(); break;
            case NORMAL:
                stats.addNormal(); break;
            case UNKNOWN:
        }
    }
}
