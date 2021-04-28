package fr.kosmosuniverse.kuffleitems.Crafts;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class Template extends ACrafts {
	ArrayList<Material> compose;
	
	public Template(KuffleMain _km, String age, ArrayList<Material> _compose) {
		compose = _compose;
		name = age + "Template";
		item = new ItemStack(Material.EMERALD);
		
		ItemMeta itM = item.getItemMeta();
		itM.setDisplayName(ChatColor.DARK_RED + name);
		
		ArrayList<String> lore = new ArrayList<String>();
		
		lore.add("Single Use " + age + " Template.");
		lore.add("Right click to validate your item.");
		
		itM.setLore(lore);
		
		item.setItemMeta(itM);
		
		recipe = new ShapelessRecipe(new NamespacedKey(_km, name), item);
		
		for (int cnt = 0; cnt < 9; cnt++) {
			((ShapelessRecipe) recipe).addIngredient(compose.get(cnt));
		}
	}

	@Override
	public Inventory getInventoryRecipe() {
		Inventory inv = Bukkit.createInventory(null,  27, "§8" + name);
		ItemStack limePane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemMeta itM = limePane.getItemMeta();
		
		itM.setDisplayName(" ");
		limePane.setItemMeta(itM);
		itM = redPane.getItemMeta();
		itM.setDisplayName("<- Back");
		redPane.setItemMeta(itM);
		
		for (int i = 0; i < 27; i++) {
			if (i == 0) {
				inv.setItem(i, new ItemStack(redPane));
			} else if (i == 3) {
				inv.setItem(i, new ItemStack(compose.get(0)));
			} else if (i == 4) {
				inv.setItem(i, new ItemStack(compose.get(1)));
			} else if (i == 5) {
				inv.setItem(i, new ItemStack(compose.get(2)));
			} else if (i == 12) {
				inv.setItem(i, new ItemStack(compose.get(3)));
			} else if (i == 13) {
				inv.setItem(i, new ItemStack(compose.get(4)));
			} else if (i == 14) {
				inv.setItem(i, new ItemStack(compose.get(5)));
			} else if (i == 21) {
				inv.setItem(i, new ItemStack(compose.get(6)));
			} else if (i == 22) {
				inv.setItem(i, new ItemStack(compose.get(7)));
			} else if (i == 23) {
				inv.setItem(i, new ItemStack(compose.get(8)));
			}else if (i == 16) {
				inv.setItem(i, item);
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return (inv);
	}

}
