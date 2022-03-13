package main.fr.kosmosuniverse.kuffleitems.crafts.resources;

import java.util.ArrayList;
import java.util.List;

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

public class TubeCoralBlock extends ACrafts {
	public TubeCoralBlock() {
		name = "TubeCoralBlock";
		
		recipe = new ShapedRecipe(new NamespacedKey(KuffleMain.current, name), new ItemStack(Material.TUBE_CORAL_BLOCK));
		
		List<Material> corals = new ArrayList<>();
		corals.add(Material.TUBE_CORAL);
		corals.add(Material.TUBE_CORAL_FAN);
		MaterialChoice mc = new MaterialChoice(corals);
		
		((ShapedRecipe) recipe).shape("CCC", "CRC", "CCC");
		((ShapedRecipe) recipe).setIngredient('C', mc);
		
		item = new ItemStack(Material.TUBE_CORAL_BLOCK);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "�8" + name);

		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, redPane);
			} else if (i == 3 || i == 4 || i == 5 || i == 12) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.TUBE_CORAL, 1, "Tube Coral or Tube Coral Fan"));
			} else if (i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.TUBE_CORAL_FAN, 1, "Tube Coral or Tube Coral Fan"));
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
