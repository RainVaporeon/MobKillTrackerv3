package com.spiritlight.mobkilltracker.v3.command;

import com.spiritlight.mobkilltracker.v3.Main;
import com.spiritlight.mobkilltracker.v3.enums.Color;
import com.spiritlight.mobkilltracker.v3.utils.DropManager;
import com.spiritlight.mobkilltracker.v3.utils.DropStatistics;
import com.spiritlight.mobkilltracker.v3.utils.Message;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.IClientCommand;

import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.util.*;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class MKTCommand extends CommandBase implements IClientCommand {

    private static final DecimalFormat df = new DecimalFormat("0.00");
    
    @Override
    public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
        return false;
    }

    @Override
    public String getName() {
        return "mkt";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            help(); return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "toggle":
                Main.configuration.setModEnabled(!Main.configuration.isModEnabled());
                Message.send("Toggled mod to " + (Main.configuration.isModEnabled() ? Color.GREEN + "ON" : Color.RED + "OFF"));
                return;
            case "last":
                if(!DropManager.instance.hasData()) {
                    Message.warn("There are no data available right now.");
                    return;
                }
                Message.send(DropManager.dropToString(DropManager.instance.getLast()));
                return;


                // trace is really long so it's at last
            case "trace":
                if(args.length == 1) {
                    Message.send("There are currently " + DropManager.instance.size() + (DropManager.instance.size() == 1 ? " stat" : " stats") + " available.");
                    Message.send("Do /" + getName() + " trace list to see all of them in brief context.");
                    Message.send("Or do /" + getName() + " trace <index> to see the specific of that stat.");
                    Message.send("Do /" + getName() + " trace delete <index> to delete that specific index.");
                    Message.send("Additionally you can do /" + getName() + " trace delete all to wipe them.");
                    return;
                }
                int idx;
                int kills = 0;
                int items = 0;
                int ingredients = 0;
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "list":
                        Message.send("- - - Current Session Caches - - -");
                        for (int i = 0; i < DropManager.instance.size(); i++) {
                            final DropStatistics tmp = DropManager.instance.get(i);
                            final int item = tmp.getQuantity(DropStatistics.ITEM);
                            final int ing = tmp.getQuantity(DropStatistics.INGREDIENT);
                            final double iAvg = (item <= 0 ? 0 : (double) tmp.getKills() / item);
                            final double inAvg = (tmp.getQuantity(DropStatistics.INGREDIENT) <= 0 ? 0 : (double) tmp.getKills() / ing);
                            Message.send("Cache #" + (i + 1) + ": §r" + tmp.getKills() + "§a kills; §r" + tmp.getQuantity(DropStatistics.ALL) + "§a drops §7(§r" + item + "§7 items, §r" + ing + "§7 ingredients)" + " §c(§7" + df.format(iAvg) + ":" + df.format(inAvg) + "§c)" + " " + tmp.getRarityIndex());
                            if (tmp.hasNote()) {
                                Message.send("§7Notes of this data: " + tmp.getNote());
                            }
                            kills += tmp.getKills();
                            items += item;
                            ingredients += ing;
                        }
                            final double divisor = DropManager.instance.size();
                            Message.send("Stats (Avg.): Kills: " + kills + " (" + df.format(kills / divisor) + "), Items: " + items + " (" + df.format(items / divisor) + "), Ingredients: " + ingredients + " (" + df.format(ingredients / divisor) + ")");
                        return;
                    case "delete":
                        if(args.length == 2) {
                            Message.send("Invalid input. Try /" + getName() + " trace for more info.");
                            return;
                        }
                        switch (args[2].toLowerCase(Locale.ROOT)) {
                            case "all":
                                Message.send("Cleared ALL session data!");
                                DropManager.instance.clear();
                                break;
                            case "last":
                                Message.send("Deleted last session data!");
                                DropManager.instance.remove(DropManager.instance.size()-1);
                                return;
                            default:
                                try {
                                    idx = Integer.parseInt(args[2]) - 1;
                                } catch (NumberFormatException ex) {
                                    Message.send("Invalid index.");
                                    return;
                                }
                                if (idx < 0 || idx >= DropManager.instance.size()) {
                                    Message.send("Invalid index.");
                                    return;
                                }
                                DropManager.instance.remove(idx);
                                Message.send("Successfully removed index #" + (idx + 1) + "!");
                                break;
                        }
                        return;
                    default:
                        try {
                            idx = Integer.parseInt(args[1])-1;
                        } catch (NumberFormatException ex) {
                            Message.send("Invalid index.");
                            return;
                        }
                        if(idx > DropManager.instance.size() || idx < 0) {
                            Message.send("Index illegal. Max index allowed: " + DropManager.instance.size());
                            return;
                        }
                        Message.send(DropManager.dropToString(DropManager.instance.get(idx)));
                }
                return;
                // and note
            case "note":
                if(args.length == 1) {
                    Message.send("/" + getName() + " note all [note] - Sets all drop data with the note.");
                    Message.send("/" + getName() + " note last [note] - Sets last drop data with the note.");
                    Message.send("/" + getName() + " note # [note] - Sets specified drop data with this note.");
                    Message.send("If note was left empty, the notes are cleared.");
                    return;
                }
                final String note;
                if(args.length >= 3) {
                    note = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                } else note = "";
                switch (args[1].toLowerCase(Locale.ROOT)) {
                    case "all":
                        Message.send("Attaching note...");
                        for (Iterator<DropStatistics> it = DropManager.instance.iterator(); it.hasNext(); ) {
                            DropStatistics drops = it.next();
                            drops.setNote(note);
                        }
                        Message.send("Finished!");
                        break;
                    case "last":
                        Message.send("Attaching note...");
                        DropManager.instance.get(DropManager.instance.size()-1).setNote(note);
                        Message.send("Finished!");
                        break;
                    default:
                        try {
                            idx = Integer.parseInt(args[1]) - 1;
                            DropManager.instance.get(idx).setNote(note);
                            Message.send("Successfully attached note " + note + " to data #" + (idx + 1));
                        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                            Message.send("Invalid operation.");
                            return;
                        }
                        break;
                }
                break;
            case "export":
                final int ENTRY_SIZE = DropManager.instance.size();
                if(args.length == 1) {
                    Message.send("/" + getName() + " export all [name]: Exports all session data as JSON.");
                    Message.send("/" + getName() + " export range [name] <min> [max]: Exports session data in range as JSON.");
                    Message.send("/" + getName() + " export last [name]: Exports last session data as JSON.");
                    Message.send("/" + getName() + " export # [name]: Exports the #th index as JSON.");
                    Message.send("View stats via /" + getName() + " trace. There are " + ENTRY_SIZE + (ENTRY_SIZE == 1 ? " entry" : " entries") + " available.");
                    return;
                } else {
                    if(DropManager.instance.size() == 0) {
                        Message.send("There are no available entry yet. Try recording some?");
                        return;
                    }
                    if(args.length == 2) {
                        Message.warn("Invalid syntax, you need a file name to export to.");
                        return;
                    }
                    String fileName = args[2];
                    switch(args[1].toLowerCase(Locale.ROOT)) {
                        case "all":
                            DropManager.exportDrops(fileName, new ArrayList<>(DropManager.instance.getBackingList()));
                            break;
                        case "last":
                            DropManager.exportDrops(fileName, Collections.singletonList(DropManager.instance.getLast()));
                            break;
                        case "range":
                            if(args.length == 3) {
                                Message.send("Invalid range.");
                            } else if(args.length == 4) {
                                try {
                                    int x = Integer.parseInt(args[3])-1;
                                    final List<DropStatistics> drops = new ArrayList<>();
                                    for(int i=x; i<DropManager.instance.size(); i++) {
                                        drops.add(DropManager.instance.get(i));
                                    }
                                    DropManager.exportDrops(fileName, drops);
                                } catch (NumberFormatException ex) {
                                    Message.send("Invalid range specified.");
                                    return;
                                } catch (IndexOutOfBoundsException e) {
                                    Message.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                    return;
                                }
                            } else try {
                                final List<DropStatistics> drops = new ArrayList<>();
                                int x = Integer.parseInt(args[3])-1;
                                int y = Integer.parseInt(args[4]);
                                for(int i=x; i<y; i++) {
                                    drops.add(DropManager.instance.get(i));
                                }
                                DropManager.exportDrops(fileName, drops);
                            } catch (NumberFormatException ex) {
                                Message.send("Invalid range specified.");
                                return;
                            } catch (IndexOutOfBoundsException e) {
                                Message.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                return;
                            }
                            break;
                        default:
                            try {
                                int x = Integer.parseInt(args[1])-1;
                                DropManager.exportDrops(fileName, Collections.singletonList(DropManager.instance.get(x)));
                            } catch (NumberFormatException ex) {
                                Message.send("Invalid range specified.");
                                return;
                            } catch (IndexOutOfBoundsException e) {
                                Message.send("Index out of bounds. Max allowed size: " + ENTRY_SIZE);
                                return;
                            }
                            break;
                    }
                }
                break;
            default:
                help();
                return;
        }
    }

    private void help() {
        Message.send("[ +-- MobKillTracker v3 --+ ]");
        Message.send("/mkt toggle - Toggles mod",
                        "/mkt last - Shows last stat",
                        "/mkt trace - Traces previous stats",
                        "/mkt note - Note related commands",
                        "/mkt export - Export related commands");
    }
}
