package fr.kosmosuniverse.kuffleitems.Commands;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleSave implements CommandExecutor {
	private KuffleMain km;
	private File dataFolder;
	
	public KuffleSave(KuffleMain _km, File _dataFolder) {
		km = _km;
		dataFolder = _dataFolder;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-save>");
		
		if (!player.hasPermission("ki-save")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			
			return false;
		}
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			return false;
		}
		
		FileWriter writer = null;
		
		km.paused = true;
		
		for (String playerName : km.games.keySet()) {
			km.games.get(playerName).stop();
			
			try {
				if (dataFolder.getPath().contains("\\")) {
					writer = new FileWriter(dataFolder.getPath() + "\\" + playerName + ".ki");
				} else {
					writer = new FileWriter(dataFolder.getPath() + "/" + playerName + ".ki");
				}
				
				writer.write(km.games.get(playerName).save());
				writer.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (km.config.getTeam()) {
			try {
				if (dataFolder.getPath().contains("\\")) {
					writer = new FileWriter(dataFolder.getPath() + "\\" + "Teams.ki");
				} else {
					writer = new FileWriter(dataFolder.getPath() + "/" + "Teams.ki");
				}
				
				writer.write(km.teams.saveTeams());
				writer.close();
							
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		km.scores.clear();
		km.games.clear();
		km.paused = false;
		km.gameStarted = false;
		km.logs.writeMsg(player, "Game Saved and Stopped.");
		
		return true;
	}
}
