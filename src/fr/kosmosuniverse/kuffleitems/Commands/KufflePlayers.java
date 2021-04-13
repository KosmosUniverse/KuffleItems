package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KufflePlayers implements CommandExecutor {

	private KuffleMain km;

	public KufflePlayers(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <kplayers>");
		
		if (!player.hasPermission("kplayers")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			
			return false;
		}
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "Game has not launched yet.");
			
			return false;
		}
		
		player.openInventory(km.playersHeads);
		
		return true;
	}
}
