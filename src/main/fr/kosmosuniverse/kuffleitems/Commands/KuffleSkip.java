package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-skip>"));
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			
			return false;
		}

		if (!km.config.getSkip()) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "CONFIG_DISABLED"));
			
			return false;
		}
		
		if (!km.games.containsKey(player.getName())) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_PLAYING"));					
			return true;
		}
		
		if (msg.equals("ki-skip")) {
			if (!player.hasPermission("ki-skip")) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return true;
			}
			
			if (args.length != 0) {
				return false;
			}
			
			String tmp = km.games.get(player.getName()).getCurrentItem();
			
			km.games.get(player.getName()).skip(false);
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + player.getName() + ">"));				
		} else if (msg.equals("ki-adminskip")) {
			if (!player.hasPermission("ki-adminskip")) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return true;
			}
			
			if (args.length != 1) {
				return false;
			}
			
			if (!km.games.containsKey(args[0])) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_NOT_FOUND"));
			}
			
			String tmp = km.games.get(args[0]).getCurrentItem();
			
			km.games.get(args[0]).skip(false);
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + args[0] + ">"));				
		}
		
		return true;
	}
}
