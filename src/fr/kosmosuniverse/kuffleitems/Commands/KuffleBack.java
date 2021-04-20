package fr.kosmosuniverse.kuffleitems.Commands;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleBack implements CommandExecutor {
	private KuffleMain km;
	private ArrayList<Material> exceptions;
	
	public KuffleBack(KuffleMain _km) {
		km = _km;
		
		exceptions = new ArrayList<Material>();
		
		for (Material m : Material.values()) {
			if (m.name().contains("SHULKER_BOX")) {
				exceptions.add(m);
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-back>");
		
		if (!player.hasPermission("ki-back")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.paused) {
			km.logs.writeMsg(player, "You cannot use this command when game is paused.");
			return true;
		}
		
		if (km.gameStarted) {
			for (String playerName : km.games.keySet()) {
				if (playerName.equals(player.getName())) {
					if (km.games.get(playerName).getDeathLoc() != null) {
						if (!compareLoc(player.getLocation().add(0, -1, 0), km.games.get(playerName).getSpawnLoc())) {
							km.logs.writeMsg(player, "You have to stand on your spawn point to make this command work.");
						} else if (System.currentTimeMillis() - km.games.get(playerName).getDeathTime() > (km.games.get(playerName).getMinTime() * 1000)) {
							Location loc = km.games.get(playerName).getDeathLoc();
							
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
							
							player.teleport(loc);
							km.games.get(playerName).restorePlayerInv();
							km.logs.writeMsg(player, "Here you are ! You can only reuse this command once you have died again.");	
						} else {
							km.logs.writeMsg(player, "You have to wait " + km.games.get(playerName).getMinTime() + " seconds after death to tp back");	
						}
						
						return true;
					} else {
						km.logs.writeMsg(player, "You need to die to use this command.");
						
						return false;
					}
				}
			}
			
			km.logs.writeMsg(player, "You are not playing in this game.");
			return false;
		}
		
		km.logs.writeMsg(player, "Game is not launched yet.");
		return false;
	}
	
	private boolean compareLoc(Location player, Location spawn) {
		return player.getBlockX() == spawn.getBlockX() && player.getBlockY() == spawn.getBlockY() && player.getBlockZ() == spawn.getBlockZ();
	}
	
	private void replaceExeption(Location loc, Material m) {
		if (!exceptions.contains(loc.getBlock().getType())) {
			loc.getBlock().setType(m);
		}
	}
}