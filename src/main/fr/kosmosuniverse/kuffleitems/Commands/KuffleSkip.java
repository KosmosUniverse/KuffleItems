package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleSkip implements CommandExecutor {
	private KuffleMain km;

	public KuffleSkip(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-skip>");
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			
			return false;
		}

		if (!km.config.getSkip()) {
			km.logs.writeMsg(player, "This command is disabled in config.");
			
			return false;
		}
		
		if (msg.equals("ki-skip")) {
			if (!player.hasPermission("ki-skip")) {
				km.logs.writeMsg(player, "You are not allowed to do this command.");
			} else {
				if (args.length != 0) {
					return false;
				}
				
				for (String playerName : km.games.keySet()) {
					if (km.games.get(playerName).getPlayer().equals(player)) {
						km.games.get(playerName).skip(true);
						
						return true;
					}
				}
				
				km.logs.writeMsg(player, "You are not in the game.");
			}
			
			return false;
		} else if (msg.equals("ki-adminskip")) {
			if (!player.hasPermission("ki-adminskip")) {
				km.logs.writeMsg(player, "You are not allowed to do this command.");
			} else {
				if (args.length != 1) {
					return false;
				}
				
				if (args.length == 1) {
					for (String playerName : km.games.keySet()) {
						if (playerName.equals(args[0])) {
							String tmp = km.games.get(playerName).getCurrentItem();
							km.games.get(playerName).skip(false);
							km.logs.writeMsg(player, "Block [" + tmp + "] was skipped for player <" + playerName + ">.");
							
							return true;
						}
					}
					
					km.logs.writeMsg(player, "Can't find player to skip his block.");
				}
				
				return false;
			}
		}
		
		return false;
	}
}
