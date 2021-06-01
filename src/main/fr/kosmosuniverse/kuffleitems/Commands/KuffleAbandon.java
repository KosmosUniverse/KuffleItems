package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
		
		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-abandon>"));
		
		if (!player.hasPermission("ki-abandon")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.gameStarted) {
			if (!km.games.containsKey(player.getName())) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_PLAYING"));
				return true;
			}
			
			km.games.get(player.getName()).setLose(true);
		} else {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
		}

		return true;
	}
}
