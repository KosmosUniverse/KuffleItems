package main.fr.kosmosuniverse.kuffleitems.Crafts.armors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;

public class ChainmailBoots extends ACrafts {
	public ChainmailBoots(KuffleMain _km) {
		name = "ChainmailBoots";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.CHAINMAIL_BOOTS));
		
		((ShapedRecipe) recipe).shape("RRR", "CRC", "CRC");
		((ShapedRecipe) recipe).setIngredient('C', Material.CHAIN);
		
		item = new ItemStack(Material.CHAINMAIL_BOOTS);
	}
	
	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 || i == 13  || i == 22) {
				inv.setItem(i, new ItemStack(grayPane));
			} else if (i == 12 || i == 14 || i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.CHAIN));
			} else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
	
}
