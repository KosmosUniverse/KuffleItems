package main.fr.kosmosuniverse.kuffleitems.Crafts;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public abstract class ACrafts {
	protected String name;
	protected ItemStack item;
	protected Recipe recipe;
	
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
