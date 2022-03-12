package main.fr.kosmosuniverse.kuffleitems.Commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleAgeItems implements CommandExecutor  {
	private KuffleMain km;

	public KuffleAgeItems(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-ageitems>"));
		
		if (!player.hasPermission("ki-ageitems")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		if (km.gameStarted) {
			if (!km.games.containsKey(player.getName())) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_PLAYING"));
				return true;
			}
			
			String age;
			
			if (args.length == 0) {
				age = AgeManager.getAgeByNumber(km.ages, km.games.get(player.getName()).getAge()).name;	
			} else {
				age = args[0];
				
				if (!AgeManager.ageExists(km.ages, age)) {
					km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "AGE_NOT_EXISTS"));
					return false;
				}
			}
			
			ArrayList<Inventory> ageItems = km.itemsInvs.get(age);
			
			player.openInventory(ageItems.get(0));
		} else {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));			
		}

		return true;
	}
}
