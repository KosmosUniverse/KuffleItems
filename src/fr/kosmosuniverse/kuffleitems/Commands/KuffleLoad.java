package fr.kosmosuniverse.kuffleitems.Commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import fr.kosmosuniverse.kuffleitems.Core.GameLoop;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleLoad implements CommandExecutor {
	private KuffleMain km;
	private File dataFolder;
	
	public KuffleLoad(KuffleMain _km, File _dataFolder) {
		km = _km;
		dataFolder = _dataFolder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, " achieved command <ki-load>");
		
		if (!player.hasPermission("ki-load")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.games.size() != 0) {
			if (km.gameStarted) {
				km.logs.logMsg(player, "A game is already launched.");
			} else {
				km.logs.logMsg(player, "There already are players in list.");
			}
			
			return false;
		}
		
		int invCnt = 0;
		
		km.playersHeads = Bukkit.createInventory(null, 54, "§8Players");
		
		for (String playerName : km.games.keySet()) {
			km.playersHeads.setItem(invCnt, Utils.getHead(km.games.get(playerName).getPlayer()));
			km.playerRank.put(playerName, 0);
			
			invCnt++;
		}
		
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		for (Player p : players) {
			if (Utils.fileExists(dataFolder.getPath(), player.getName() + ".ki")) {
				Utils.loadGame(km, p);
			}
		}
		
		if (km.config.getSaturation()) {
			for (String playerName : km.games.keySet()) {
				km.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}
		}
		
		FileReader reader = null;
		JSONParser parser = new JSONParser();
		JSONObject mainObject = new JSONObject();
		
		if (km.config.getTeam()) {
			try {
				if (dataFolder.getPath().contains("\\")) {
					reader = new FileReader(dataFolder.getPath() + "\\" + "Teams.ki");
				} else {
					reader = new FileReader(dataFolder.getPath() + "/" + "Teams.ki");
				}
				
				try {
					mainObject = (JSONObject) parser.parse(reader);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				km.teams.loadTeams(mainObject, km.games);
				
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		km.paused = true;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"5\",\"bold\":true,\"color\":\"red\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 20);
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"4\",\"bold\":true,\"color\":\"gold\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 40);
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"3\",\"bold\":true,\"color\":\"yellow\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 60);
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"2\",\"bold\":true,\"color\":\"green\"}", km.games.get(playerName).getPlayer());
				}
			}
		}, 80);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				km.scores.setupPlayerScores();
				
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"1\",\"bold\":true,\"color\":\"blue\"}", km.games.get(playerName).getPlayer());
					km.games.get(playerName).load();
				}
			}
		}, 100);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, new Runnable() {
			@Override
			public void run() {
				for (String playerName : km.games.keySet()) {
					ActionBar.sendRawTitle("{\"text\":\"GO!\",\"bold\":true,\"color\":\"dark_purple\"}", km.games.get(playerName).getPlayer());
				}
				
				km.loop = new GameLoop(km);
				km.loop.startRunnable();
				km.gameStarted = true;
				km.paused = false;
			}
		}, 120);
		
		return true;
	}
}
