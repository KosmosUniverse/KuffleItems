package fr.kosmosuniverse.kuffleitems.Listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class PlayerEvents implements Listener {
	private KuffleMain km;
	private File dataFolder;
	private ArrayList<Material> exceptions;
	
	public PlayerEvents(KuffleMain _km, File _dataFolder) {
		km = _km;
		dataFolder = _dataFolder;

		exceptions = new ArrayList<Material>();
		
		for (Material m : Material.values()) {
			if (m.name().contains("SHULKER_BOX")) {
				exceptions.add(m);
			}
		}
		
		exceptions.add(Material.CRAFTING_TABLE);
		exceptions.add(Material.FURNACE);
	}
	
	@EventHandler
	public void onPlayerConnectEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Game tmpGame;

		if (!player.hasPlayedBefore()) {
			for (ACrafts item : km.crafts.getRecipeList()) {
				player.discoverRecipe(new NamespacedKey(km, item.getName()));
			}
		}
	
		if (!km.gameStarted) {
			return;
		}
		
		if (Utils.fileExists(dataFolder.getPath(), player.getName() + ".ki")) {
			Utils.loadGame(km, player);
		} else {
			return;
		}
		
		tmpGame = km.games.get(player.getName());

		Inventory newInv = Bukkit.createInventory(null, 54, "§8Players");
		
		for (ItemStack item : km.playersHeads) {
			if (item != null) {
				newInv.addItem(item);
			}
		}
		
		newInv.addItem(Utils.getHead(tmpGame.getPlayer()));
		
		km.playersHeads = newInv;
		km.scores.setupPlayerScores(tmpGame);
		tmpGame.load();
		km.updatePlayersHead(player.getName(), tmpGame.getItemDisplay());
		
		player.sendMessage("[KuffleItems] : <" + player.getName() + "> game is reloaded !");
		
		return;
	}
	
	@EventHandler
	public void onPlayerDeconnectEvent(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FileWriter writer = null;
		Game tmpGame;
		
		if (!km.gameStarted || !km.games.containsKey(player.getName())) {
			return ;
		}
		
		tmpGame = km.games.remove(player.getName());

		try {
			if (dataFolder.getPath().contains("\\")) {
				writer = new FileWriter(dataFolder.getPath() + "\\" + player.getName() + ".ki");
			} else {
				writer = new FileWriter(dataFolder.getPath() + "/" + player.getName() + ".ki");
			}
			
			Inventory newInv = Bukkit.createInventory(null, 54, "§8Players");
			
			for (ItemStack item : km.playersHeads.getContents()) {
				if (item != null) {
					if (!item.getItemMeta().getDisplayName().equals(player.getName())) {
						newInv.addItem(item);
					}
				}
			}
			
			km.playersHeads = newInv;
			
			writer.write(tmpGame.save());
			writer.close();
			
			tmpGame.stop();
			
			event.setQuitMessage("[KuffleItems] : <" + player.getName() + "> game is saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		if (!km.gameStarted) {
			return ;
		}
		
		Player player = event.getEntity();
		Location deathLoc = player.getLocation();
		event.setKeepInventory(true);
		
		if (event.getDrops().size() > 0) {	
			event.getDrops().clear();
		}
		
		for (String playerName : km.games.keySet()) {
			if (playerName.equals(player.getName())) {
				if (km.config.getLevel().losable) {
					km.games.get(playerName).setLose(true);
				} else {
					km.games.get(playerName).setDeathLoc(deathLoc);
					km.games.get(playerName).savePlayerInv();
					km.games.get(playerName).setDead(true);
				}
				
				return ;
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		if (!km.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		
		for (String playerName : km.games.keySet()) {
			if (playerName.equals(player.getName())) {
				event.setRespawnLocation(km.games.get(playerName).getSpawnLoc());
				player.getInventory().clear();
			}
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					if (playerName.equals(player.getName())) {
						if (km.config.getLevel().losable) {
							player.sendMessage(ChatColor.RED + "YOU LOSE !");
						} else {
							km.games.get(playerName).reloadEffects();
							teleportAutoBack(km.games.get(playerName));
						}
						
						return;
					}
				}
			}
		}, 20);
	}
	
	public void teleportAutoBack(Game tmpGame) {
		tmpGame.getPlayer().sendMessage("You will be tp back to your death spot in " + km.config.getLevel().seconds + " seconds.");
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				Location loc = tmpGame.getDeathLoc();
				
				if (loc.getWorld().getName().contains("the_end") && loc.getY() < 0) {
					int tmp = loc.getWorld().getHighestBlockYAt(loc);
					
					if (tmp == -1) {
						loc.setY(59);
						
						for (double cntX = -2; cntX <= 2; cntX++) {
							for (double cntZ = -2; cntZ <= 2; cntZ++) {
								Location platform = loc.clone();
								
								platform.add(cntX, 0, cntZ);
								
								platform.getBlock().setType(Material.COBBLESTONE);
							}
						}
						
						loc.setY(61);
					} else {
						loc.setY(loc.getWorld().getHighestBlockYAt(loc) + 1);
					}
				} else {
					Location wall;
					
					for (double x = -2; x <= 2; x++) {
						for (double y = -2; y <= 2; y++) {
							for (double z = -2; z <= 2; z++) {
								wall = loc.clone();
								wall.add(x, y, z);
								
								if (x <= 1 && x >= -1 && y <= 1 && y >= -1 && z <= 1 && z >= -1) {
									replaceExeption(wall, Material.AIR);
								} else {
									replaceExeption(wall, Material.DIRT);
								}
							}
						}
					}
				}
				
				tmpGame.getPlayer().teleport(loc);
				tmpGame.restorePlayerInv();
			}
		}, (km.config.getLevel().seconds * 20));
	}
	
	private void replaceExeption(Location loc, Material m) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(m);
		}
	}
	
	@EventHandler
	public void onPauseEvent(PlayerMoveEvent event) {
		if (km.paused) {
			event.setCancelled(true);
			return ;
		}
	}
}
