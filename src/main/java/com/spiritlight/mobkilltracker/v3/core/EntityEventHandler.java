package com.spiritlight.mobkilltracker.v3.core;

import com.google.common.collect.ImmutableSet;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.NBTType;
import com.spiritlight.mobkilltracker.v3.utils.collections.ConcurrentTimedSet;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.math.StrictMath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.spiritlight.mobkilltracker.v3.utils.SharedConstants.TOSS_MAGIC;

public class EntityEventHandler {
    private final DropStatistics stats = new DropStatistics();

    private static final Set<UUID> viewedEntities = new ConcurrentTimedSet<>(300, TimeUnit.SECONDS);

    public EntityEventHandler() {
        Message.debugv("Constructing EntityEventHandler");

        // Preventing duplications
        if (Minecraft.getMinecraft().world != null)
            this.storedEntities.addAll(Minecraft.getMinecraft().world.getLoadedEntityList());
    }

    public DropStatistics getStats() {
        return stats;
    }

    private final Set<Entity> storedEntities = new LinkedHashSet<>();

    public static Set<UUID> getViewedEntities() {
        return ImmutableSet.copyOf(viewedEntities);
    }

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    @SubscribeEvent
    public void onEntityUpdate(EntityEvent.EntityConstructing event) {
        final Entity entity = event.getEntity();
        if (storedEntities.contains(entity)) return;
        if (viewedEntities.contains(entity.getUniqueID())) {
            Message.debugv("Avoiding duplicated UUID " + entity.getUniqueID() + " from being counted.");
            return;
        }
        // Processing items in this tab

        Message.debug("Found entity " + entity.getName());

        if (Minecraft.getMinecraft().world == null) return;

        executor.schedule(() -> processEntity(entity), Main.configuration.getDelayMills(), TimeUnit.MILLISECONDS);
    }

    private boolean processToss(EntityItem entity) {
        double entityY = entity.posY;
        double mcY = Minecraft.getMinecraft().player.posY;

        if (StrictMath.add(mcY, TOSS_MAGIC) == entityY) {
            Message.debugv("Cancelled item " + entity.getName() + " due to dropped item detection");
            storedEntities.add(entity);
            viewedEntities.add(entity.getUniqueID());
            return true;
        } else {
          return false;
        }
    }

    private void processEntity(Entity entity) {
        storedEntities.add(entity);
        // False if unchanged, implying it already exists, but we already made sure this is not the case?
        if(!viewedEntities.add(entity.getUniqueID())) {
            Message.debugv("Avoiding duplicated UUID " + entity.getUniqueID() + " in EntityEventHandler#processEntity(Entity)");
            Message.debugv("This is a strange behaviour. Please alert the mod developer if this becomes a recurring issue.");
            return;
        }
        if (entity instanceof EntityItem) {
            EntityItem entityItem = (EntityItem) entity;
            // Ignoring emerald for sake of our life
            if(Items.EMERALD.equals(entityItem.getItem().getItem())) return;

            String itemName = entityItem.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
            Rarity rarity = ItemDatabase.instance.getItemRarity(itemName);
            // Old schooled way due to involving some huge ass component that I'm too lazy to change
            if(Main.configuration.isLogging() || Main.configuration.doLogValid()) {
                ITextComponent itc = Message.builder("Processing item entity " + entity.getName()).build()
                        .setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                new TextComponentString(Message.formatJson("Wynncraft Item Name:" + entity.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name") + "\n\n" + "Item name: " + (entity.hasCustomName() ? entity.getCustomNameTag() + "(" + entity.getName() + ")" : entity.getName()) + "\n" + "Item UUID: " + entity.getUniqueID() + "\n\n" + entity.serializeNBT() + "\n\nClick to track!")))
                        ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                entity.getPosition().getX() + " " + entity.getPosition().getY() + " " + entity.getPosition().getZ())));
                Message.sendRaw(itc);
            }
            if(processToss(entityItem)) return;
            if(rarity != Rarity.UNKNOWN) {
                manageRarity(rarity);
            } else {
                // This line is only reached if no item was fetched
                Tier tier = ItemDatabase.instance.getIngredientTier(itemName);
                stats.addTier(tier);
            }
        } else {
            // Process entities here
            if(entity.getName().toLowerCase(Locale.ROOT).contains("combat xp")) {
                if(Main.configuration.doLogValid()) {
                    ITextComponent component = Message.builder(Color.MAGENTA + "Processing kill " + entity.getName()).addHoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Message.of(Message.formatJson(String.valueOf(entity.serializeNBT())))).build();
                    Message.sendRaw(component);
                }
                stats.addKill();
            }
        }
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

    private void removeRarity(Rarity rarity) {
        switch(rarity) {
            case MYTHIC:
                stats.removeMythic(); break;
            case FABLED:
                stats.removeFabled(); break;
            case LEGENDARY:
                stats.removeLegendary(); break;
            case RARE:
                stats.removeRare(); break;
            case SET:
                stats.removeSet(); break;
            case UNIQUE:
                stats.removeUnique(); break;
            case NORMAL:
                stats.removeNormal(); break;
            case UNKNOWN:
        }
    }
}
