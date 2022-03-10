package main.fr.kosmosuniverse.kuffleitems.Crafts.naturals;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;

public class Mycelium extends ACrafts {
	MaterialChoice mc;
	
	public Mycelium(KuffleMain _km) {
		name = "Mycelium";
		
		recipe = new ShapelessRecipe(new NamespacedKey(_km, name), new ItemStack(Material.MYCELIUM));
		
		ArrayList<Material> champs = new ArrayList<Material>();
		
		champs.add(Material.BROWN_MUSHROOM);
		champs.add(Material.RED_MUSHROOM);
		champs.add(Material.CRIMSON_FUNGUS);
		champs.add(Material.WARPED_FUNGUS);
		
		mc = new MaterialChoice(champs);
		
		((ShapelessRecipe) recipe).addIngredient(Material.DIRT);
		((ShapelessRecipe) recipe).addIngredient(mc);
		
		item = new ItemStack(Material.MYCELIUM);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(Material.DIRT));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(Material.BROWN_MUSHROOM));
			} else if (i == 5 || i == 12 || i == 13 ||
					i == 14 || i == 21 || i == 22 || i == 23) {
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
