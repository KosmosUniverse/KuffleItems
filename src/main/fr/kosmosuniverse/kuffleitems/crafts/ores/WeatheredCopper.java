package main.fr.kosmosuniverse.kuffleitems.crafts.ores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.crafts.ACrafts;

public class WeatheredCopper extends ACrafts {
	public WeatheredCopper(KuffleMain _km) {
		name = "WeatheredCopper";
		
		recipe = new ShapelessRecipe(new NamespacedKey(_km, name), new ItemStack(Material.WEATHERED_COPPER));
		
		((ShapelessRecipe) recipe).addIngredient(Material.EXPOSED_COPPER);
		((ShapelessRecipe) recipe).addIngredient(Material.WATER_BUCKET);
		((ShapelessRecipe) recipe).addIngredient(Material.WATER_BUCKET);
		
		item = new ItemStack(Material.WEATHERED_COPPER);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.EXPOSED_COPPER));
			} else if (i == 3 || i == 5) {
				inv.setItem(i, new ItemStack(Material.WATER_BUCKET));
			} else if (i == 12 || i == 13 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, grayPane);
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
}
