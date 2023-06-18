package com.spiritlight.mobkilltracker.v3.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.enums.Rarity;
import com.spiritlight.mobkilltracker.v3.enums.Tier;
import com.spiritlight.mobkilltracker.v3.enums.Type;
import com.spiritlight.mobkilltracker.v3.utils.ItemDatabase;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.Message;
import com.spiritlight.mobkilltracker.v3.utils.minecraft.NBTType;
import com.spiritlight.mobkilltracker.v3.utils.collections.ConcurrentTimedSet;
import com.spiritlight.mobkilltracker.v3.utils.drops.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.math.StrictMath;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.spiritlight.mobkilltracker.v3.utils.SharedConstants.TOSS_MAGIC;

public class EntityEventHandler {
    private final DropStatistics stats = new DropStatistics();

    private static final List<String> KILL_INDICATOR = ImmutableList.of("combat xp", "guild xp", "shared");

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

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
    @SubscribeEvent
    public void onEntityUpdate(EntityEvent.EntityConstructing event) {
        final Entity entity = event.getEntity();
        if (entity == null) return; // EDGE-CASE
        if (storedEntities.contains(entity)) return;
        if (viewedEntities.contains(entity.getUniqueID())) {
            Message.debugv("Avoiding duplicated UUID " + entity.getUniqueID() + " from being counted.");
            return;
        }
        // Processing items in this tab

        Message.debug("Found entity " + entity.getName());

        if (Minecraft.getMinecraft().world == null) return;

        if(Main.configuration.getDelayMills() == 0) {
            CompletableFuture.runAsync(() -> this.processEntity(entity));
        } else {
            executor.schedule(() -> processEntity(entity), Main.configuration.getDelayMills(), TimeUnit.MILLISECONDS);
        }
    }

    private boolean processToss(EntityItem entity) {

        if(Main.configuration.getDelayMills() != 0) return false;

        double entityY = entity.posY;
        List<EntityPlayer> player = Minecraft.getMinecraft().world.playerEntities;
        double[] yAxis = player.stream()
                .filter(p -> !(p instanceof FakePlayer))
                .filter(p -> !p.isDead).mapToDouble(p -> p.posY).toArray();
        for(double playerY : yAxis) {
            if(StrictMath.add(playerY, TOSS_MAGIC) == entityY) {
                Message.debugv("Cancelled item " + entity.getName() + " due to dropped item detection");
                storedEntities.add(entity);
                viewedEntities.add(entity.getUniqueID());
                return true;
            }
        }
        return false;
    }

    private void processEntity(Entity entity) {
        storedEntities.add(entity);
        if(entity == null) return;
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

            Type type = ItemDatabase.instance.getItemType(itemName);

            if(type == Type.UNKNOWN) return;

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
            switch(type) {
                case ITEM:
                    this.manageRarity(ItemDatabase.instance.getItemRarity(itemName)); break;
                case INGREDIENT:
                    this.stats.addTier(ItemDatabase.instance.getIngredientTier(itemName)); break;
            }
        } else {
            // Process entities here
            if(KILL_INDICATOR.stream().anyMatch(str -> entity.getName().toLowerCase(Locale.ROOT).contains(str))) {
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
                System.err.println("Found unknown rarity!");
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
