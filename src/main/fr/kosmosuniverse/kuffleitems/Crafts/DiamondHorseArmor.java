package main.fr.kosmosuniverse.kuffleitems.Crafts;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class DiamondHorseArmor extends ACrafts {
	MaterialChoice mc;
	
	public DiamondHorseArmor(KuffleMain _km) {
		name = "DiamondHorseArmor";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.DIAMOND_HORSE_ARMOR));
		
		ArrayList<Material> wools = new ArrayList<Material>();
		
		for (Material m : Material.values()) {
			if (m.name().toLowerCase().contains("wool") && !m.name().toLowerCase().contains("legacy")) {
				wools.add(m);
			}
		}
		
		mc = new MaterialChoice(wools);
		
		((ShapedRecipe) recipe).shape("RRD", "DWD", "DDD");
		((ShapedRecipe) recipe).setIngredient('D', Material.DIAMOND);
		((ShapedRecipe) recipe).setIngredient('W', mc);
		
		item = new ItemStack(Material.DIAMOND_HORSE_ARMOR);
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
		
		ItemStack anyWool = new ItemStack(Material.LIGHT_GRAY_WOOL);
		ItemMeta woolM = anyWool.getItemMeta();
		woolM.setDisplayName(ChatColor.BLUE + "Any" + ChatColor.GREEN + " Wool " + ChatColor.RED + "Color");
		anyWool.setItemMeta(woolM);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(redPane));
			} else if (i == 5 || i == 12 || i == 14 || i == 21 || i == 22 || i == 23) {
				inv.setItem(i, new ItemStack(Material.DIAMOND));
			} else if (i == 13) {
				inv.setItem(i, anyWool);
			} else if (i == 16) {
				inv.setItem(i, new ItemStack(Material.DIAMOND_HORSE_ARMOR));
			} else if (i == 3 || i == 4) {
				inv.setItem(i, new ItemStack(grayPane));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return (inv);
	}
}
