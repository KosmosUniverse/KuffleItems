package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

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
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-players>"));
		
		if (!player.hasPermission("ki-players")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			
			return false;
		}
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			
			return false;
		}
		
		player.openInventory(km.playersHeads);
		
		return true;
	}
}
