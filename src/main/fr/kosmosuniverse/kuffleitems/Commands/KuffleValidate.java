package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			return true;
		}
		
		if (msg.equalsIgnoreCase("ki-validate")) {
			
			if (args.length != 1) {
				return false;
			}
			
			km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-validate>"));
			
			if (!player.hasPermission("ki-validate")) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			for (String playerName : km.games.keySet()) {
				if (playerName.equals(args[0])) {
					String tmp = km.games.get(playerName).getCurrentItem();
					
					km.games.get(playerName).found();
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "ITEM_VALIDATED").replace("[#]", " [" + tmp + "] ").replace("<#>", "<" + playerName + ">"));
					
					return true;
				}
			}
			
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "VALIDATE_PLAYER_ITEM"));
	
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate-age")) {
			
			if (args.length != 1) {
				return false;
			}
			
			km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-validate-age>"));
			
			if (!player.hasPermission("ki-validate-age")) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			for (String playerName : km.games.keySet()) {
				if (playerName.equals(args[0])) {
					if (km.games.get(playerName).getAge() == -1) {
						km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_FINISHED").replace("<#>", "<" + playerName + ">"));
						
						return true;
					}
					
					String tmp = AgeManager.getAgeByNumber(km.ages, km.games.get(playerName).getAge()).name;
					
					km.games.get(playerName).setItemCount(km.config.getItemPerAge() + 1);
					km.games.get(playerName).setCurrentItem(null);
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "AGE_VALIDATED").replace("[#]", "[" + tmp + "]").replace("<#>", "<" + playerName + ">"));
					
					return true;
				}
			}
			
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "VALIDATE_PLAYER_AGE"));
		}
		
		return false;
	}

}
