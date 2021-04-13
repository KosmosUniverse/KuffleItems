package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

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
		
		if (!player.hasPermission("ki-skip")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			
			return false;
		}
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			
			return false;
		}

		if (!km.config.getSkip()) {
			km.logs.writeMsg(player, "This command is disabled in config.");
			
			return false;
		}
		
		for (String playerName : km.games.keySet()) {
			if (km.games.get(playerName).getPlayer().equals(player)) {
				km.games.get(playerName).skip();
				
				return true;
			}
		}
		
		km.logs.writeMsg(player, "You are not in the game.");
		
		return false;
	}
}
