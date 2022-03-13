package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleSkip implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-skip>"));
		
		if (!KuffleMain.gameStarted) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));
			
			return false;
		}

		if (!KuffleMain.config.getSkip()) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "CONFIG_DISABLED"));
			
			return false;
		}
		
		if (!KuffleMain.games.containsKey(player.getName())) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_PLAYING"));					
			return true;
		}
		
		if (msg.equals("ki-skip")) {
			if (!player.hasPermission("ki-skip")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
				return true;
			}
			
			if (args.length != 0) {
				return false;
			}
			
			String tmp = KuffleMain.games.get(player.getName()).getCurrentItem();
			
			KuffleMain.games.get(player.getName()).skip(false);
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + player.getName() + ">"));				
		} else if (msg.equals("ki-adminskip")) {
			if (!player.hasPermission("ki-adminskip")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
				return true;
			}
			
			if (args.length != 1) {
				return false;
			}
			
			if (!KuffleMain.games.containsKey(args[0])) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "PLAYER_NOT_FOUND"));
			}
			
			String tmp = KuffleMain.games.get(args[0]).getCurrentItem();
			
			KuffleMain.games.get(args[0]).skip(false);
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + args[0] + ">"));				
		}
		
		return true;
	}
}
