package main.fr.kosmosuniverse.kuffleitems.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleAgeItems implements CommandExecutor  {
	@Override
	public boolean onCommand(CommandSender sender, Command cnd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-ageitems>"));
		
		if (!player.hasPermission("ki-ageitems")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		if (KuffleMain.gameStarted) {
			if (!KuffleMain.games.containsKey(player.getName())) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_PLAYING"));
				return true;
			}
			
			String age;
			
			if (args.length == 0) {
				age = AgeManager.getAgeByNumber(KuffleMain.ages, KuffleMain.games.get(player.getName()).getAge()).name;	
			} else {
				age = args[0];
				
				if (!AgeManager.ageExists(KuffleMain.ages, age)) {
					KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "AGE_NOT_EXISTS"));
					return false;
				}
			}
			
			List<Inventory> ageItems = KuffleMain.itemsInvs.get(age);
			
			player.openInventory(ageItems.get(0));
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "GAME_NOT_LAUNCHED"));			
		}

		return true;
	}
}
