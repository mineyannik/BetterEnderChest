package nl.rutgerkok.BetterEnderChest;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EnderHandler implements Listener {
    private BetterEnderChest plugin;
    private Bridge protectionBridge;
    private BetterEnderStorage chests;

    public EnderHandler(BetterEnderChest plugin, Bridge protectionBridge) {
        this.plugin = plugin;
        this.protectionBridge = protectionBridge;
        chests = plugin.getEnderChests();
    }

    // Makes sure the chests show up
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();
        String groupName = plugin.getGroups().getGroup(player.getWorld().getName());

        if (event.getClickedBlock().getType().equals(plugin.getChestMaterial())) {
            // clicked on an Ender Chest
            event.setCancelled(true);

            if (protectionBridge.isProtected(event.getClickedBlock())) {
                // protected Ender Chest
                if (protectionBridge.canAccess(player, event.getClickedBlock())) {
                    // player can access the chest
                    if (plugin.hasPermission(player, "betterenderchest.use.privatechest", true)) {
                        // and has the correct permission node

                        // Get the owner's name
                        String inventoryName = protectionBridge.getOwnerName(event.getClickedBlock());

                        // Show the chest
                        player.openInventory(chests.getInventory(inventoryName, groupName));
                        
                        // DEBUG
                        plugin.logThis("Rows: " + plugin.getPlayerRows(player));
                    } else {

                        // Show an error
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use private Ender Chests.");
                    }
                }
            } else { // unprotected Ender chest
                if (!player.getItemInHand().getType().equals(Material.SIGN) || !protectionBridge.getBridgeName().equals("Lockette")) {
                    if (plugin.hasPermission(player, "betterenderchest.use.publicchest", true)) {
                        if (BetterEnderChest.PublicChest.openOnOpeningUnprotectedChest) {
                            // Show public chest
                            player.openInventory(chests.getInventory(BetterEnderChest.publicChestName, groupName));
                        } else {
                            // Show player's chest
                            String inventoryName = player.getName();
                            player.openInventory(chests.getInventory(inventoryName, groupName));
                            
                            // DEBUG
                            plugin.logThis("Rows: " + plugin.getPlayerRows(player));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "You do not have permissions to use public Ender Chests.");
                    }
                }
            }
        }
    }

    // show warning message for public chests on close
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (event.getInventory().getHolder() instanceof BetterEnderHolder) {
            BetterEnderHolder holder = (BetterEnderHolder) event.getInventory().getHolder();
            if (holder.getOwnerName().equals(BetterEnderChest.publicChestName)) {
                if (!BetterEnderChest.PublicChest.closeMessage.isEmpty()) {
                    player.sendMessage(BetterEnderChest.PublicChest.closeMessage);
                }
            }
        }
    }

    // change the drop
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;

        Block block = event.getBlock();
        Material material = block.getType();
        if (material.equals(plugin.getChestMaterial())) {
            // If an Ender Chest is being broken
            event.setCancelled(true);
            block.setData((byte) 0);
            block.setType(Material.AIR);

            // Get the right chest drop
            String chestDropString = plugin.chestDrop;

            if (event.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                // Silk touch
                chestDropString = plugin.chestDropSilkTouch;
            }
            
            if (event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                // Creative mode
                chestDropString = plugin.chestDropCreative;
            }
            
            // Drop it
            if (chestDropString.equals("OBSIDIAN") || chestDropString.equals("OBSIDIAN_WITH_EYE_OF_ENDER") || chestDropString.equals("OBSIDIAN_WITH_ENDER_PEARL")) {
                // Drop 8 obsidian
                event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.OBSIDIAN, 8));
            }

            if (chestDropString.equals("OBSIDIAN_WITH_EYE_OF_ENDER") || chestDropString.equals("EYE_OF_ENDER")) {
                // Drop Eye of Ender
                event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.EYE_OF_ENDER));
            }

            if (chestDropString.equals("OBSIDIAN_WITH_ENDER_PEARL") || chestDropString.equals("ENDER_PEARL")) {
                // Drop Ender Pearl
                event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.ENDER_PEARL));
            }

            if (chestDropString.equals("ITSELF")) {
                // Drop itself
                event.getPlayer().getWorld().dropItemNaturally(block.getLocation(), new ItemStack(material));
            }
        }
    }
}
