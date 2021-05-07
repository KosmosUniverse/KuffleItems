package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleAbandon implements CommandExecutor  {
	private KuffleMain km;

	public KuffleAbandon(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-abandon>");
		
		if (!player.hasPermission("ki-abandon")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.gameStarted) {
			if (!km.games.containsKey(player.getName())) {
				km.logs.writeMsg(player, "You are not playing in this game.");
				return true;
			}
			
			km.games.get(player.getName()).setLose(true);
		} else {
			km.logs.writeMsg(player, "The game has not launched yet.");			
		}

		return true;
	}
}
