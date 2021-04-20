package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			return false;
		}
		
		if (args.length != 1) {
			km.logs.writeMsg(player, "This command takes 1 argument: the name of the player to validate his block.");
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate")) {
			km.logs.logMsg(player, "achieved command <ki-validate>");
			
			if (!player.hasPermission("ki-validate")) {
				km.logs.writeMsg(player, "You are not allowed to do this command.");
				return false;
			}
			
			for (String playerName : km.games.keySet()) {
				if (playerName.equals(args[0])) {
					String tmp = km.games.get(playerName).getCurrentItem();
					
					km.games.get(playerName).found();
					km.logs.writeMsg(player, "Block [" + tmp + "] was validated for player <" + playerName + ">.");
					
					return true;
				}
			}
			
			km.logs.writeMsg(player, "Can't find player to validate his block.");
	
			return false;
		}
		
		if (msg.equalsIgnoreCase("ki-validate-age")) {
			km.logs.logMsg(player, "achieved command <ki-validate-age>");
			
			if (!player.hasPermission("ki-validate-age")) {
				km.logs.writeMsg(player, "You are not allowed to do this command.");
				return false;
			}
			
			for (String playerName : km.games.keySet()) {
				if (playerName.equals(args[0])) {
					String tmp = Utils.getAgeByNumber(km.ages, km.games.get(playerName).getAge()).name;
					
					km.games.get(playerName).setItemCount(km.config.getBlockPerAge() + 1);
					km.games.get(playerName).setCurrentItem(null);
					km.logs.writeMsg(player, "Age [" + tmp + "] was validated for player <" + playerName + ">.");
					
					return true;
				}
			}
			
			km.logs.writeMsg(player, "Can't find player to validate his block.");
		}
		
		return false;
	}

}
