package main.fr.kosmosuniverse.kuffleitems.Commands;

import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.AgeManager;

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
		
		km.logs.logMsg(player, "achieved command <ki-ageitems>");
		
		if (!player.hasPermission("ki-ageitems")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		if (km.gameStarted) {
			if (!km.games.containsKey(player.getName())) {
				km.logs.writeMsg(player, "You are not playing in this game.");
				return true;
			}
			
			String age;
			
			if (args.length == 0) {
				age = AgeManager.getAgeByNumber(km.ages, km.games.get(player.getName()).getAge()).name;	
			} else {
				age = args[0];
				
				if (!AgeManager.ageExists(km.ages, age)) {
					player.sendMessage("This age does not exist.");
					return false;
				}
			}
			
			ArrayList<Inventory> ageItems = km.itemsInvs.get(age);
			
			player.openInventory(ageItems.get(0));
		} else {
			km.logs.writeMsg(player, "The game has not launched yet.");			
		}

		return true;
	}
}
