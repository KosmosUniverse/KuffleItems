package main.fr.kosmosuniverse.kuffleitems.Crafts.resources;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import main.fr.kosmosuniverse.kuffleitems.Utils.ItemUtils;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class Redstone extends ACrafts {
	MaterialChoice mc;
	
	public Redstone(KuffleMain _km) {
		name = "Redstone";
		
		recipe = new ShapelessRecipe(new NamespacedKey(_km, name), new ItemStack(Material.REDSTONE, 5));
		
		ArrayList<Material> ores = new ArrayList<Material>();
		
		ores.add(Material.REDSTONE_ORE);
		
		if (Utils.findVersionNumber(_km, Utils.getVersion()) >= Utils.findVersionNumber(_km, "1.17")) {
			ores.add(Material.DEEPSLATE_REDSTONE_ORE);
		}
		
		mc = new MaterialChoice(ores);
		
		((ShapelessRecipe) recipe).addIngredient(mc);
		
		item = new ItemStack(Material.REDSTONE, 5);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		ItemStack customOre = mc.getChoices().size() > 1 ? ItemUtils.itemMakerName(Material.REDSTONE_ORE, 1, ChatColor.BLUE + "Any" + ChatColor.GREEN + " Redstone " + ChatColor.RED + "Ore") : new ItemStack(Material.REDSTONE_ORE);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3) {
				inv.setItem(i, customOre);
			} else if (i == 16) {
				inv.setItem(i, item);
			} else if (i == 4 || i == 5 || i == 12 ||
					i == 13 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, grayPane);
			} else {
				inv.setItem(i, limePane);
			}
		}
		
		return (inv);
	}
}
