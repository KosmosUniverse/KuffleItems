package main.fr.kosmosuniverse.kuffleitems.crafts.ores;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.crafts.ACrafts;

public class DiamondOreDeepslate extends ACrafts{
	public DiamondOreDeepslate(KuffleMain _km) {
		name = "DiamondOreDeepslate";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.DEEPSLATE_DIAMOND_ORE));
		
		
		((ShapedRecipe) recipe).shape("SDR", "DSR", "RRR");
		((ShapedRecipe) recipe).setIngredient('S', Material.DEEPSLATE);
		((ShapedRecipe) recipe).setIngredient('D', Material.DIAMOND);
		
		item = new ItemStack(Material.DEEPSLATE_DIAMOND_ORE);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 13) {
				inv.setItem(i, new ItemStack(Material.DEEPSLATE));
			} else if (i == 4 || i == 12) {
				inv.setItem(i, new ItemStack(Material.DIAMOND));
			} else if (i == 5 || i == 14 || i == 21 || i == 22 || i == 23) {
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
