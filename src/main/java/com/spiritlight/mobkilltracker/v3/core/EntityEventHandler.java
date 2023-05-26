package com.spiritlight.mobkilltracker.v3.core;

import com.spiritlight.mobkilltracker.v3.utils.DropStatistics;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EntityEventHandler {
    private final DropStatistics stats = new DropStatistics();


    public EntityEventHandler() {

    }

    public DropStatistics getStats() {
        return stats;
    }

    private final Set<Entity> storedEntities = new LinkedHashSet<>();

    @SubscribeEvent
    public void onEntityUpdate(EntityEvent event) {
        final Entity entity = event.getEntity();
        if(storedEntities.contains(entity)) return;
        storedEntities.add(entity);
        // Processing items in this tab
        if (entity instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) entity;
            // Ignoring emerald for sake of our life
            if(Items.EMERALD.equals(entityItem.getItem().getItem())) return;
        } else {
            // Process entities here
            if(event.getEntity().getName().contains("combat xp")) {
                stats.addKill();
            }
        }
    }
}
