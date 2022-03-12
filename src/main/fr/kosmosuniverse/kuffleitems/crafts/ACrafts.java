package main.fr.kosmosuniverse.kuffleitems.crafts;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import main.fr.kosmosuniverse.kuffleitems.utils.ItemUtils;

public abstract class ACrafts {
	protected String name;
	protected ItemStack item;
	protected Recipe recipe;
	
	protected ItemStack grayPane = ItemUtils.itemMakerName(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack limePane = ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " ");
	protected ItemStack redPane = ItemUtils.itemMakerName(Material.RED_STAINED_GLASS_PANE, 1, "<- Back");
	
	public abstract Inventory getInventoryRecipe();
	
	public String getName() {
		return (name);
	}
	
	public ItemStack getItem() {
		return (item);
	}
	
	public Recipe getRecipe() {
		return (recipe);
	}
}
