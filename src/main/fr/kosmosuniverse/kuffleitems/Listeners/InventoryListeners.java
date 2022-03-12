package main.fr.kosmosuniverse.kuffleitems.Listeners;

import java.util.ArrayList;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Game;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;

public class InventoryListeners implements Listener {
	private KuffleMain km;
	
	public InventoryListeners(KuffleMain _km) {
		km = _km;
	}
	
	@EventHandler
	public void onItemClick(InventoryClickEvent event) {	
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		Inventory current = event.getClickedInventory();
		ACrafts craft;
		Inventory inv;
		
		if (item == null) {
			return;
		}
		
		if (event.getView().getTitle() == "§8AllCustomCrafts") {
			event.setCancelled(true);
			if ((craft = km.crafts.findCraftInventoryByItem(item)) != null &&
					(inv = craft.getInventoryRecipe()) != null) {
				player.openInventory(inv);
			}
		} else if (km.crafts.findCraftByInventoryName(event.getView().getTitle()) != null) {
			event.setCancelled(true);
			
			if (item.getItemMeta().getDisplayName().equals("<- Back")) {
				player.openInventory(km.crafts.getAllCraftsInventory());
			}
		} else if (event.getView().getTitle() == "§8Players") {
			event.setCancelled(true);
			
			if (item.getType() == Material.PLAYER_HEAD &&
					!item.getItemMeta().getDisplayName().equals(player.getName())) {
				Game tmpGame = km.games.get(item.getItemMeta().getDisplayName());
				
				if (tmpGame != null && km.games.get(player.getName()).getFinished()) {
					if (player.getGameMode() != GameMode.SPECTATOR) {
						player.setGameMode(GameMode.SPECTATOR);
					}
					player.teleport(tmpGame.getPlayer());	
				}
			}
		} else if (event.getView().getTitle().contains(" Items ")) {
			for (String age : km.itemsInvs.keySet()) {
				if (event.getView().getTitle().contains(age)) {
					event.setCancelled(true);

					ArrayList<Inventory> tmpInvs = km.itemsInvs.get(age);
					
					if (tmpInvs.size() > 1) {
						int invIdx = tmpInvs.indexOf(current);
						
						if (item.getItemMeta().getDisplayName().equals("<- Previous")) {
							player.openInventory(tmpInvs.get(invIdx - 1));
						} else if (item.getItemMeta().getDisplayName().equals("Next ->")) {
							player.openInventory(tmpInvs.get(invIdx + 1));
						}
					}
				}
			}
		}
	}
}
