package fr.kosmosuniverse.kuffleitems.Listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class PlayerInteract implements Listener {
	private KuffleMain km;
	
	public PlayerInteract(KuffleMain _km) {
		km = _km;
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (!km.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();

		if (!event.hasItem()) {
			return ;
		}
		
		if (action == Action.RIGHT_CLICK_AIR && item != null) {
			if (compareItems(item, km.crafts.findItemByName("EndTeleporter"))) {
				endTeleporter(player);
				return ;
			}
			
			if (compareItems(item, km.crafts.findItemByName("OverworldTeleporter"))) {
				overworldTeleporter(player);
				return ;
			}
			
			Game tmpGame = km.games.get(player.getName());
			
			if (tmpGame != null && tmpGame.getCurrentItem().equals(item.getType().name().toLowerCase())) {
				tmpGame.found();
			}
		}
	}
	
	@EventHandler
	public void onCraft(CraftItemEvent event) {
		ItemStack item = event.getInventory().getResult();
		Player player = (Player) event.getWhoClicked();
		
		if (compareItems(item, km.crafts.findItemByName("EndTeleporter"))) {
			if (player.getLevel() < 5) {
				event.setCancelled(true);
				player.sendMessage("You need 5 xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - 5);
			}
		} else if (compareItems(item, km.crafts.findItemByName("OverworldTeleporter"))) {
			if (player.getLevel() < 10) {
				event.setCancelled(true);
				player.sendMessage("You need 10 xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - 10);
			}
		}
	}
	
	private void endTeleporter(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		
		tmp.setY((double) tmp.getWorld().getHighestBlockAt(tmp).getY());
		player.teleport(tmp);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);	
	}
	
	private void overworldTeleporter(Player player) {
		player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
		Location tmp = new Location(Bukkit.getWorld("" + Utils.findNormalWorld()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		tmp.setY((double) tmp.getWorld().getHighestBlockAt(tmp).getY());
		player.teleport(tmp);
		player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
	}
	
	private boolean compareItems(ItemStack first, ItemStack second) {
		if (first.getType() != second.getType()) {
			return false;
		}
		
		ItemMeta firstMeta = first.getItemMeta();
		ItemMeta secondMeta = second.getItemMeta();
		
		if (!firstMeta.getDisplayName().equals(secondMeta.getDisplayName())) {
			return false;
		}
		
		if (firstMeta.hasLore() != secondMeta.hasLore()) {
			return false;
		}
		
		if (!firstMeta.hasLore()) {
			return true;
		}
		
		ArrayList<String> firstLore = (ArrayList<String>) firstMeta.getLore();
		ArrayList<String> secondLore = (ArrayList<String>) secondMeta.getLore();
		
		if (firstLore.size() != secondLore.size()) {
			return false;
		}
		
		for (int i = 0; i < firstLore.size(); i++) {
			if (!firstLore.get(i).equals(secondLore.get(i))) {
				return false;
			}
		}
		
		return true;
	}
}
