package nl.rutgerkok.betterenderchest.command;

import java.util.Map;

import nl.rutgerkok.betterenderchest.BetterEnderChest;
import nl.rutgerkok.betterenderchest.BetterEnderInventoryHolder;
import nl.rutgerkok.betterenderchest.Translations;
import nl.rutgerkok.betterenderchest.WorldGroup;
import nl.rutgerkok.betterenderchest.io.Consumer;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends BaseCommand {

    public GiveCommand(BetterEnderChest plugin) {
        super(plugin);
    }

    @Override
    public boolean execute(final CommandSender sender, final String[] args) {
        if (args.length < 2) {
            return false;
        }

        String inventoryName = getInventoryName(args[0]);

        // Group
        WorldGroup group = getGroup(args[0], sender);
        if (group == null) {
            sender.sendMessage(ChatColor.RED + Translations.GROUP_NOT_FOUND.toString(args[0]));
        }

        // Material
        Material material = Material.matchMaterial(args[1]);
        if (material == null) {
            sender.sendMessage("" + ChatColor.RED + args[1] + " is not a valid material!");
            return true;
        }

        // Count
        int count = 1;
        if (args.length >= 3) { // set the count
            try {
                count = Integer.parseInt(args[2]);
                if (count > material.getMaxStackSize()) {
                    sender.sendMessage(ChatColor.RED + "Amount was capped at " + material.getMaxStackSize() + ".");
                    count = material.getMaxStackSize();
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("" + ChatColor.RED + args[2] + " is not a valid amount!");
                return true;
            }
        }

        // Damage value
        short damage = 0;
        if (args.length >= 4) { // Set the damage
            try {
                damage = Short.parseShort(args[3]);
            } catch (NumberFormatException e) {
                sender.sendMessage("" + ChatColor.RED + args[3] + " is not a valid damage value!");
                return true;
            }
        }

        // Add the item to the inventory
        final ItemStack adding = new ItemStack(material, count, damage);
        this.getInventory(sender, inventoryName, group, new Consumer<Inventory>() {
            @Override
            public void consume(Inventory inventory) {
                Map<Integer, ItemStack> overflow = inventory.addItem(adding);
                if (overflow.isEmpty()) {
                    sender.sendMessage("Item added to the Ender Chest inventory of " + args[0]);
                } else {
                    sender.sendMessage("One or more items have not been added; Ender Chest inventory of " + args[0] + " was full.");
                }

                // Mark for resave,
                // now that there are changed items.
                ((BetterEnderInventoryHolder) inventory.getHolder()).setHasUnsavedChanges(true);
            }
        });

        return true;
    }

    @Override
    public String getHelpText() {
        return "gives an item to an Ender inventory.";
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getUsage() {
        return "<player> <item> [count] [damage]";
    }

}
