package main.fr.kosmosuniverse.kuffleitems.crafts.resources;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffleitems.utils.ItemUtils;

public class HornCoralBlock extends ACrafts {
	public HornCoralBlock(KuffleMain _km) {
		name = "HornCoralBlock";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.HORN_CORAL_BLOCK));
		
		ArrayList<Material> corals = new ArrayList<Material>();
		corals.add(Material.HORN_CORAL);
		corals.add(Material.HORN_CORAL_FAN);
		MaterialChoice mc = new MaterialChoice(corals);
		
		((ShapedRecipe) recipe).shape("CCC", "CRC", "CCC");
		((ShapedRecipe) recipe).setIngredient('C', mc);
		
		item = new ItemStack(Material.HORN_CORAL_BLOCK);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 || i == 12) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.HORN_CORAL, 1, "Horn Coral or Horn Coral Fan"));
			} else if (i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.HORN_CORAL_FAN, 1, "Horn Coral or Horn Coral Fan"));
			} else if (i == 13) {
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
