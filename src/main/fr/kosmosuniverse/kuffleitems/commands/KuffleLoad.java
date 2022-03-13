package main.fr.kosmosuniverse.kuffleitems.commands;

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
import main.fr.kosmosuniverse.kuffleitems.core.ActionBar;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleLoad implements CommandExecutor {
	private File dataFolder;
	private static final String GAME_FILE = "Game.ki";
	
	public KuffleLoad(File folder) {
		dataFolder = folder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-load>"));
		
		if (!player.hasPermission("ki-load")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (KuffleMain.games.size() != 0) {
			if (KuffleMain.gameStarted) {
				KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "GAME_LAUNCHED"));
			} else {
				KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "LIST_NOT_EMPTY") + ".");
			}
			
			return false;
		}
		
		JSONParser parser = new JSONParser();
		JSONObject mainObject = new JSONObject();
		
		if (Utils.fileExists(dataFolder.getPath(), GAME_FILE)) {
			try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + GAME_FILE)) {
				mainObject = (JSONObject) parser.parse(reader);
				KuffleMain.config.loadConfig((JSONObject) mainObject.get("config"));
				loadRanks((JSONObject) mainObject.get("ranks"));
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
			
			mainObject.clear();
		}
		
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		for (Player p : players) {
			if (Utils.fileExists(dataFolder.getPath(), player.getName() + ".ki")) {
				Utils.loadGame(p);
			}
		}
		
		KuffleMain.playersHeads = Bukkit.createInventory(null, Utils.getNbInventoryRows(KuffleMain.games.size()), "�8Players");
		
		KuffleMain.games.forEach((playerName, game) -> {
			KuffleMain.playersHeads.addItem(Utils.getHead(game.getPlayer()));
			
			if (KuffleMain.config.getTeam() && !KuffleMain.playerRank.containsKey(game.getTeamName())) {
				KuffleMain.playerRank.put(game.getTeamName(), 0);
			} else if (!KuffleMain.playerRank.containsKey(playerName)) {
				KuffleMain.playerRank.put(playerName, 0);
			}
		});
		
		if (KuffleMain.config.getSaturation()) {
			KuffleMain.games.forEach((playerName, game) ->
				game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false))
			);
		}
		
		if (KuffleMain.config.getTeam()) {
			try (FileReader reader = new FileReader(dataFolder.getPath() + File.separator + "Teams.ki")) {
				mainObject = (JSONObject) parser.parse(reader);
				KuffleMain.teams.loadTeams(mainObject, KuffleMain.games);
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		}
		
		KuffleMain.paused = true;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.getPlayer())
			);
			
			if (KuffleMain.config.getSBTT()) {
				Utils.setupTemplates();
			}
		}, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.getPlayer())
			)
		, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.getPlayer())
			)
		, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () ->
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.getPlayer())
			)
		, 80);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.scores.setupPlayerScores();
				
			KuffleMain.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.getPlayer());
				KuffleMain.games.get(playerName).load();
			});
		}, 100);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(KuffleMain.current, () -> {
			KuffleMain.games.forEach((playerName, game) ->
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.getPlayer())
			);
			
			KuffleMain.loop.startRunnable();
			KuffleMain.gameStarted = true;
			KuffleMain.paused = false;
		}, 120);
		
		return true;
	}
	
	private void loadRanks(JSONObject ranksObj) {
		for (Object key : ranksObj.keySet()) {
			String playerName = (String) key;
			int rank = Integer.parseInt(ranksObj.get(key).toString());
			
			KuffleMain.playerRank.put(playerName, rank);
		}
	}
}
