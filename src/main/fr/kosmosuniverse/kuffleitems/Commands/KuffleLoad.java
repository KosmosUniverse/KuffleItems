package main.fr.kosmosuniverse.kuffleitems.Commands;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import main.fr.kosmosuniverse.kuffleitems.Core.GameLoop;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleLoad implements CommandExecutor {
	private KuffleMain km;
	private File dataFolder;
	private static final String gameFile = "Game.ki";
	
	public KuffleLoad(KuffleMain _km, File _dataFolder) {
		km = _km;
		dataFolder = _dataFolder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-load>"));
		
		if (!player.hasPermission("ki-load")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.games.size() != 0) {
			if (km.gameStarted) {
				km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "GAME_LAUNCHED"));
			} else {
				km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "LIST_NOT_EMPTY") + ".");
			}
			
			return false;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject mainObject = new JSONObject();
		
		if (Utils.fileExists(dataFolder.getPath(), gameFile)) {
			try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + gameFile)) {
				mainObject = (JSONObject) parser.parse(reader);
				km.config.loadConfig((JSONObject) mainObject.get("config"));
				loadRanks((JSONObject) mainObject.get("ranks"));
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			
			mainObject.clear();
		}
		
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		for (Player p : players) {
			if (Utils.fileExists(dataFolder.getPath(), player.getName() + ".ki")) {
				Utils.loadGame(km, p);
			}
		}
		
		int invCnt = 0;
		
		km.playersHeads = Bukkit.createInventory(null, 54, "�8Players");
		
		for (String playerName : km.games.keySet()) {
			km.playersHeads.setItem(invCnt, Utils.getHead(km.games.get(playerName).getPlayer()));
			
			if (km.config.getTeam() && !km.playerRank.containsKey(km.games.get(playerName).getTeamName())) {
				km.playerRank.put(km.games.get(playerName).getTeamName(), 0);
			} else if (!km.playerRank.containsKey(playerName)) {
				km.playerRank.put(playerName, 0);
			}
			
			invCnt++;
		}
		
		if (km.config.getSaturation()) {
			for (String playerName : km.games.keySet()) {
				km.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}
		}
		
		if (km.config.getTeam()) {
			try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + "Teams.ki")) {
				mainObject = (JSONObject) parser.parse(reader);
				km.teams.loadTeams(km, mainObject, km.games);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
		
		km.paused = true;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, km.games.get(playerName).getPlayer());
			}
			
			if (km.config.getSBTT()) {
				Utils.setupTemplates(km);
			}
		}, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, km.games.get(playerName).getPlayer());
			}
		}, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, km.games.get(playerName).getPlayer());
			}
		}, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, km.games.get(playerName).getPlayer());
			}
		}, 80);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.scores.setupPlayerScores();
				
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, km.games.get(playerName).getPlayer());
				km.games.get(playerName).load();
			}
		}, 100);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			for (String playerName : km.games.keySet()) {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, km.games.get(playerName).getPlayer());
			}
			
			km.loop = new GameLoop(km);
			km.loop.startRunnable();
			km.gameStarted = true;
			km.paused = false;
		}, 120);
		
		return true;
	}
	
	private void loadRanks(JSONObject ranksObj) {
		for (Object key : ranksObj.keySet()) {
			String playerName = (String) key;
			int rank = Integer.parseInt(ranksObj.get(key).toString());
			
			km.playerRank.put(playerName, rank);
		}
	}
}
