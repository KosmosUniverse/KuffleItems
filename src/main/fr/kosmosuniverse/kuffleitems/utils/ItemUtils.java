package main.fr.kosmosuniverse.kuffleitems.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * 
 * @author KosmosUniverse
 *
 */

public class ItemUtils {
	/**
	 * Private ItemUtils constructor
	 * @throws IllegalStateException
	 */
	private ItemUtils() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Make Item with custom name
	 * 
	 * @param material
	 * @param amount
	 * @param name
	 * @return Item
	 */
	public static ItemStack itemMakerName(Material material, int amount, String name) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setDisplayName(name);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Make item with custom lore
	 * 
	 * @param material
	 * @param amount
	 * @param rawLore array
	 * @return item
	 */
	public static ItemStack itemMakerLore(Material material, int amount, String... rawLore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		List<String> lore = new ArrayList<>();		
		
		Collections.addAll(lore, rawLore);
		
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Make item with custom lore
	 * 
	 * @param material
	 * @param amount
	 * @param rawLore list
	 * @return item
	 */
	public static ItemStack itemMakerLore(Material material, int amount, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Make item with custom name and lore
	 * 
	 * @param material
	 * @param amount
	 * @param rawLore array
	 * @return item
	 */
	public static ItemStack itemMakerFull(Material material, int amount, String name, String... rawLore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		List<String> lore = new ArrayList<>();		
		
		Collections.addAll(lore, rawLore);
		
		itM.setDisplayName(name);
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Make item with custom name and lore
	 * 
	 * @param material
	 * @param amount
	 * @param rawLore list
	 * @return item
	 */
	public static ItemStack itemMakerFull(Material material, int amount, String name, List<String> lore) {
		ItemStack item = new ItemStack(material, amount);
		ItemMeta itM = item.getItemMeta();
		
		itM.setDisplayName(name);
		itM.setLore(lore);
		item.setItemMeta(itM);
		
		return item;
	}
	
	/**
	 * Compare two item's lore
	 * 
	 * @param first
	 * @param second
	 * @return true is lore are same, false if not
	 */
	private static boolean compareLoreElements(ItemStack first, ItemStack second) {
		List<String> firstLore = (ArrayList<String>) first.getItemMeta().getLore();
		List<String> secondLore = (ArrayList<String>) second.getItemMeta().getLore();
		
		if (firstLore.size() != secondLore.size()) {
			return false;
		}
		
		for (int i = 0; i < firstLore.size(); i++) {
			if (!firstLore.get(i).equals(secondLore.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Compare first and second item's Names
	 * 
	 * @param first
	 * @param second
	 * @param hasItemMeta
	 * @param hasDisplayName
	 * @return true if first and second item's Names are same, false instead
	 */
	private static boolean compareName(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName) {
		boolean retValue = true;
		
		if (hasItemMeta && hasDisplayName) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue && (first.getItemMeta().hasDisplayName() == second.getItemMeta().hasDisplayName())) {
				retValue = first.getItemMeta().getDisplayName().equals(second.getItemMeta().getDisplayName());
			} else {
				retValue = false;
			}
		}
		
		return retValue;
	}
	
	/**
	 * Compare first and second item's Lores
	 * 
	 * @param first
	 * @param second
	 * @param hasItemMeta
	 * @param hasLore
	 * @return true if first and second item's Lore are same, false instead
	 */
	private static boolean compareLore(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasLore) {
		boolean retValue = true;
		
		if (hasItemMeta && hasLore) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue && (first.getItemMeta().hasLore() == second.getItemMeta().hasLore())) {
				retValue = compareLoreElements(first, second);
			} else {
				retValue = false;
			}			
		}
		
		return retValue;
	}
	
	/**
	 * Check if two item are same
	 * 
	 * @param first item
	 * @param second item
	 * @param hasItemMeta
	 * @param hasDisplayName
	 * @param hasLore
	 * @return true if items are same, false if not
	 */
	public static boolean itemComparison(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName, boolean hasLore) {
		boolean retValue = true;
		
		retValue = first.getType() == second.getType();
		
		if (retValue && hasItemMeta) {
			retValue = first.hasItemMeta() == second.hasItemMeta();

			if (retValue) {
				retValue = first.hasItemMeta() == second.hasItemMeta();
			}
		}
		
		if (retValue) {
			retValue = compareName(first, second, hasItemMeta, hasDisplayName);
		}
		
		if (retValue) {
			retValue = compareLore(first, second, hasItemMeta, hasLore);
		}

		return retValue;
	}
}
