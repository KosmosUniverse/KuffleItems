package main.fr.kosmosuniverse.kuffleitems.Crafts.resources;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import main.fr.kosmosuniverse.kuffleitems.Utils.ItemUtils;

public class FireCoralBlock extends ACrafts {
	public FireCoralBlock(KuffleMain _km) {
		name = "FireCoralBlock";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.FIRE_CORAL_BLOCK));
		
		ArrayList<Material> corals = new ArrayList<Material>();
		corals.add(Material.FIRE_CORAL);
		corals.add(Material.FIRE_CORAL_FAN);
		MaterialChoice mc = new MaterialChoice(corals);
		
		((ShapedRecipe) recipe).shape("CCC", "CRC", "CCC");
		((ShapedRecipe) recipe).setIngredient('C', mc);
		
		item = new ItemStack(Material.FIRE_CORAL_BLOCK);
	}
	
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		ItemStack grayPane = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
		ItemStack limePane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta itM = grayPane.getItemMeta();
		
		itM.setDisplayName(" ");
		grayPane.setItemMeta(itM);
		itM = limePane.getItemMeta();
		itM.setDisplayName(" ");
		limePane.setItemMeta(itM);
		itM = redPane.getItemMeta();
		itM.setDisplayName("<- Back");
		redPane.setItemMeta(itM);
			
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(redPane));
			} else if (i == 3 || i == 4 || i == 5 || i == 12) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.FIRE_CORAL, 1, "Fire Coral or Fire Coral Fan"));
			} else if (i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, ItemUtils.itemMakerName(Material.FIRE_CORAL_FAN, 1, "Fire Coral or Fire Coral Fan"));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(grayPane));
			} else if (i == 16) {
				inv.setItem(i, new ItemStack(Material.FIRE_CORAL_BLOCK));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return (inv);
	}
}
