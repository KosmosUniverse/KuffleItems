package main.fr.kosmosuniverse.kuffleitems.Crafts.armors;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import main.fr.kosmosuniverse.kuffleitems.Utils.ItemUtils;

public class GoldHorseArmor extends ACrafts {
	MaterialChoice mc;
	
	public GoldHorseArmor(KuffleMain _km) {
		name = "GoldHorseArmor";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.GOLDEN_HORSE_ARMOR));
		
		ArrayList<Material> wools = new ArrayList<Material>();
		
		for (Material m : Material.values()) {
			if (m.name().toLowerCase().contains("wool") && !m.name().toLowerCase().contains("legacy")) {
				wools.add(m);
			}
		}
		
		mc = new MaterialChoice(wools);
		
		((ShapedRecipe) recipe).shape("RRG", "GWG", "GGG");
		((ShapedRecipe) recipe).setIngredient('G', Material.GOLD_INGOT);
		((ShapedRecipe) recipe).setIngredient('W', mc);
		
		item = new ItemStack(Material.GOLDEN_HORSE_ARMOR);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);
		ItemStack anyWool = ItemUtils.itemMakerName(Material.GRAY_WOOL, 1, ChatColor.BLUE + "Any" + ChatColor.GREEN + " Wool " + ChatColor.RED + "Color");
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 5 || i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.GOLD_INGOT));
			} else if (i == 13) {
				inv.setItem(i, anyWool);
			} else if (i == 3 || i == 4) {
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
