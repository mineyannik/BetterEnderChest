package nl.rutgerkok.betterenderchest.nms;

import java.io.File;
import java.io.IOException;

import nl.rutgerkok.betterenderchest.WorldGroup;
import nl.rutgerkok.betterenderchest.chestowner.ChestOwner;
import nl.rutgerkok.betterenderchest.registry.Registration;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;

/**
 * Class to help with all NMS tasks.
 * 
 */
public abstract class NMSHandler implements Registration {

    /**
     * Decrements one of the player counter of the Ender Chest at the location.
     * This should play the close animation if no more players are viewing the
     * chest.
     * 
     * @param location
     *            The location of the Ender Chest.
     */
    public abstract void closeEnderChest(Location location);

    /**
     * Converts the given NBT bytes to a JSON-formatted string.
     * 
     * @param bytes
     *            The bytes to convert.
     * @throws IOException
     *             If the byte array is corrupted.
     * @return The JSON representation of the bytes.
     */
    public abstract String convertNBTBytesToJson(byte[] bytes) throws IOException;

    @Override
    public Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     * Loads a BetterEnderChest inventory from the NBT file. The inventory will
     * have the specified name and will be loaded from the specified child tag
     * (vanilla uses EnderItems and BeterEnderChest uses Inventory). It will
     * also search for size and name tags in the root of the file, but it should
     * guess them if they are not provided.
     * <p />
     * It is not permitted to use another load format than NBT, as for example
     * the vanilla importing process depends on it.
     * 
     * @param nbtFile
     *            The file to load from.
     * @param chestOwner
     *            The owner of the inventory.
     * @param worldGroup
     *            The group the inventory is in.
     * @param inventoryTagName
     *            The name of the tag in the file to load the items from.
     * @throws IOException
     *             If the file is not found or if the file is
     *             unreadable/corrupted.
     * @return The inventory. The holder of the inventory must be
     *         BetterEnderInventoryHolder.
     */
    public abstract Inventory loadNBTInventoryFromFile(File nbtFile, ChestOwner chestOwner, WorldGroup worldGroup, String inventoryTagName) throws IOException;

    /**
     * Loads a BetterEnderChest inventory from the JSON String, which represents
     * the NBT format of the inventory. The inventory will have the specified
     * name and will be loaded from the specified child tag (vanilla uses
     * EnderItems and BeterEnderChest uses Inventory). It will also search for
     * the chest size tag in the root of the file, but it should guess them if
     * they are not provided.
     * <p />
     * It is not permitted to use another load format than NBT, as for example
     * the vanilla importing process depends on it.
     * 
     * @param chestOwner
     *            The owner of the inventory.
     * @param worldGroup
     *            The world group the inventory is in.
     * @param nbtFile
     *            The array to load from.
     * 
     * @throws IOException
     *             If the byte array is corrupted.
     * @return The inventory. The holder of the inventory must be
     *         BetterEnderInventoryHolder.
     */
    public abstract Inventory loadNBTInventoryFromJson(String jsonString, ChestOwner chestOwner, WorldGroup worldGroup) throws IOException;

    /**
     * Increments one to the player counter of the Ender Chest at the location.
     * This should play the open animation of the Ender Chest if it wasn't
     * already opened by another player.
     * 
     * @param location
     *            The location of the Ender Chest.
     */
    public abstract void openEnderChest(Location location);

    /**
     * Saves a BetterEnderChest inventory to a NBT formatted file.
     * 
     * @param file
     *            The NBT file to save to. If the file does not exist, it is
     *            created.
     * @param inventory
     *            The inventory to save to. It must have
     *            BetterEnderInventoryHolder as it's holder.
     */
    public abstract void saveInventoryToFile(File file, Inventory inventory) throws IOException;

    /**
     * Saves a BetterEnderChest inventory to a JSON-formatted String, based on
     * the NBT representation of the inventory.
     * 
     * @param file
     *            The NBT file to save to. If the file does not exist, it is
     *            created.
     * @param inventory
     *            The inventory to save to. It must have
     *            BetterEnderInventoryHolder as it's holder.
     */
    public abstract String saveInventoryToJson(Inventory inventory) throws IOException;
}
