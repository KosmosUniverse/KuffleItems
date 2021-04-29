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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.AgeManager;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class PlayerInteract implements Listener {
	private KuffleMain km;
	private int xpSub;
	
	public PlayerInteract(KuffleMain _km) {
		km = _km;
	}
	
	public void setXpSub(int _xpSub) {
		xpSub = _xpSub;
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
				
				if (event.getHand() == EquipmentSlot.HAND) {
					player.getInventory().setItemInMainHand(null);	
				} else if (event.getHand() == EquipmentSlot.OFF_HAND) {
					player.getInventory().setItemInOffHand(null);
				}
				
				return ;
			}
			
			if (compareItems(item, km.crafts.findItemByName("OverworldTeleporter"))) {
				overworldTeleporter(player);
				
				if (event.getHand() == EquipmentSlot.HAND) {
					player.getInventory().setItemInMainHand(null);	
				} else if (event.getHand() == EquipmentSlot.OFF_HAND) {
					player.getInventory().setItemInOffHand(null);
				}
				
				return ;
			}
			
			Game tmpGame = km.games.get(player.getName());

			if (item.getItemMeta().getDisplayName().contains("Template")) {
				String name = AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name;
				name = name.replace("_Age", "");
				name = name + "Template";

				if (compareItems(item, km.crafts.findItemByName(name))) {
					tmpGame.found();
					
					if (event.getHand() == EquipmentSlot.HAND) {
						player.getInventory().setItemInMainHand(null);	
					} else if (event.getHand() == EquipmentSlot.OFF_HAND) {
						player.getInventory().setItemInOffHand(null);
					}
					Utils.reloadTemplate(km, name, AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name);
					Bukkit.broadcastMessage(ChatColor.GOLD + player.getName() + ChatColor.DARK_BLUE + " just used Template !");
					
					return ;
				}
			}
			
			if (tmpGame != null && tmpGame.getCurrentItem() != null) {
				if (!km.config.getDouble() && tmpGame.getCurrentItem().equals(item.getType().name().toLowerCase())) {
					tmpGame.found();	
				} else if (km.config.getDouble() &&
						(tmpGame.getCurrentItem().split("/")[0].equals(item.getType().name().toLowerCase()) ||
						tmpGame.getCurrentItem().split("/")[1].equals(item.getType().name().toLowerCase()))) {
					tmpGame.found();
				}
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
			if (player.getLevel() < xpSub) {
				event.setCancelled(true);
				player.sendMessage("You need " + xpSub + " xp levels to craft this item.");
			} else {
				player.setLevel(player.getLevel() - xpSub);
			}
		}
	}
	
	@EventHandler
	public void onFireWorkThrow(PlayerInteractEvent event) {
		ItemStack item;
		Action action = event.getAction();
		Player player = event.getPlayer();

		if (!km.gameStarted || !km.games.containsKey(player.getName())) {
			return ;
		}
		
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
			return ;
		}
		
		if (event.getItem() != null && event.getItem().getType() == Material.FIREWORK_ROCKET) {
			item = event.getItem();
			
			if (item.getAmount() == 1) {
				item.setAmount(64);
				player.getInventory().setItemInMainHand(item);
			}
		} else if (player.getInventory().getItemInOffHand() != null && player.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET) {
			item = player.getInventory().getItemInOffHand();
			
			if (item.getAmount() == 1) {
				item.setAmount(64);
				player.getInventory().setItemInOffHand(item);
			}
		} else {
			return;
		}
	}
	
	private void endTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName() + "_the_end"), player.getLocation().getX() + 1000, 60.0, player.getLocation().getZ() + 1000);
		
		while (tmp.getBlock().getType() != Material.END_STONE) {
			tmp.add(10, 0, 10);
		}
		
		
		tmp.setY((double) tmp.getWorld().getHighestBlockAt(tmp).getY());

		if (km.config.getTeam()) {
			String teamName = km.games.get(player.getName()).getTeamName();
			
			for (String playerName : km.games.keySet()) {
				if (km.games.get(playerName).getTeamName().equals(teamName)) {
					km.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
					km.games.get(playerName).getPlayer().teleport(tmp);
					km.games.get(playerName).getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			player.teleport(tmp);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		}
	}
	
	private void overworldTeleporter(Player player) {
		Location tmp = new Location(Bukkit.getWorld(Utils.findNormalWorld().getName()), player.getLocation().getX() - 1000, 80.0, player.getLocation().getZ() - 1000);
		
		tmp.setY((double) tmp.getWorld().getHighestBlockAt(tmp).getY());
		
		if (km.config.getTeam()) {
			String teamName = km.games.get(player.getName()).getTeamName();
			
			for (String playerName : km.games.keySet()) {
				if (km.games.get(playerName).getTeamName().equals(teamName)) {
					km.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
					km.games.get(playerName).getPlayer().teleport(tmp);
					km.games.get(playerName).getPlayer().removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
				}
			}
		} else {
			player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 50, false, false, false));
			player.teleport(tmp);
			player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
		}
		
		xpSub = (xpSub - 2) < 2 ? 2 : (xpSub - 2);
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
