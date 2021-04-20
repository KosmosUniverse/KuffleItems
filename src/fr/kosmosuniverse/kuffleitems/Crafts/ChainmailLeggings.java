package fr.kosmosuniverse.kuffleitems.Crafts;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class ChainmailLeggings extends ACrafts {
	public ChainmailLeggings(KuffleMain _km) {
		name = "ChainmailLeggings";
		
		recipe = new ShapedRecipe(new NamespacedKey(_km, name), new ItemStack(Material.CHAINMAIL_LEGGINGS));
		
		((ShapedRecipe) recipe).shape("CCC", "CRC", "CRC");
		((ShapedRecipe) recipe).setIngredient('C', Material.CHAIN);
		
		item = new ItemStack(Material.CHAINMAIL_LEGGINGS);
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
			} else if (i == 3 || i == 4 || i == 5 || i == 12 || i == 14 || i == 21 || i == 23) {
				inv.setItem(i, new ItemStack(Material.CHAIN));
			} else if (i == 16) {
				inv.setItem(i, new ItemStack(Material.CHAINMAIL_LEGGINGS));
			} else if (i == 13 || i == 22) {
				inv.setItem(i, new ItemStack(grayPane));
			} else {
				inv.setItem(i, new ItemStack(limePane));
			}
		}
		
		return (inv);
	}
	
}
