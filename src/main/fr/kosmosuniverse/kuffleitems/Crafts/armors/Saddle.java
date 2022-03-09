package main.fr.kosmosuniverse.kuffleitems.Crafts.armors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;

public class Saddle extends ACrafts {
	public Saddle(KuffleMain _km) {
		name = "Saddle";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.SADDLE));
		
		((ShapedRecipe) recipe).shape("LLL", "RLR", "TRT");
		((ShapedRecipe) recipe).setIngredient('L', Material.LEATHER);
		((ShapedRecipe) recipe).setIngredient('T', Material.TRIPWIRE_HOOK);
		
		item = new ItemStack(Material.SADDLE);
	}
	
	@Override
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
			} else if (i == 3 || i == 4 || i == 5 || i == 13) {
				inv.setItem(i, new ItemStack(Material.LEATHER));
			} else if (i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.TRIPWIRE_HOOK));
			} else if (i == 12 || i == 14 | i == 22) {
				inv.setItem(i, new ItemStack(grayPane));
			} else if (i == 16) {
				inv.setItem(i, new ItemStack(Material.SADDLE));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return (inv);
	}
	
}
