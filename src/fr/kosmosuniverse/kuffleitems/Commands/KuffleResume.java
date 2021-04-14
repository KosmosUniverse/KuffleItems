package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.ActionBar;

public class KuffleResume implements CommandExecutor {
	private KuffleMain km;
	
	public KuffleResume(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-resume>");
		
		if (!player.hasPermission("ki-resume")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "You need to first add people with ki-list command, launch a game with ki-start command.");
			return false;
		}
		
		if (!km.paused) {
			km.logs.writeMsg(player, "Your game is already running, you can pause it with kpause command.");
			return false;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"3\",\"bold\":true,\"color\":\"red\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"2\",\"bold\":true,\"color\":\"yellow\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"1\",\"bold\":true,\"color\":\"green\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"Game Resumed!\",\"bold\":true,\"color\":\"dark_purple\"}", km.games.get(playerName).getPlayer());
				}

				km.paused = false;
				
				for (String playerName : km.games.keySet()) {
					km.games.get(playerName).resume();
					ActionBar.sendRawTitle("{\"text\":\"Game Resumed!\",\"bold\":true,\"color\":\"dark_purple\"}", km.games.get(playerName).getPlayer());
					km.games.get(playerName).getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
				}
			}
		}, 80);

		return true;
	}

}
