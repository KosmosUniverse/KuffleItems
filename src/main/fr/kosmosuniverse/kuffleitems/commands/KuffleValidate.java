package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleValidate implements CommandExecutor {
	private KuffleMain km;

	public KuffleValidate(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			return true;
		}
		
		if (args.length != 1) {
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate")) {
			km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-validate>"));
			
			if (!player.hasPermission("ki-validate")) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			if (!km.games.containsKey(args[0])) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "VALIDATE_PLAYER_ITEM"));	
			}

			String tmp = km.games.get(args[0]).getCurrentItem();
			
			km.games.get(args[0]).found();
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "ITEM_VALIDATED").replace("[#]", " [" + tmp + "] ").replace("<#>", "<" + args[0] + ">"));			
		}
		
		if (msg.equalsIgnoreCase("ki-validate-age")) {
			km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-validate-age>"));
			
			if (!player.hasPermission("ki-validate-age")) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			if (!km.games.containsKey(args[0])) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "VALIDATE_PLAYER_AGE"));	
			}
			
			if (km.games.get(args[0]).getAge() == -1) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_FINISHED").replace("<#>", "<" + args[0] + ">"));
				
				return true;
			}
			
			String tmp = AgeManager.getAgeByNumber(km.ages, km.games.get(args[0]).getAge()).name;
			
			km.games.get(args[0]).setItemCount(km.config.getItemPerAge() + 1);
			km.games.get(args[0]).setCurrentItem(null);
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "AGE_VALIDATED").replace("[#]", "[" + tmp + "]").replace("<#>", "<" + args[0] + ">"));
		}
		
		return false;
	}

}
