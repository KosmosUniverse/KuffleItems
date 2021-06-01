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
		
		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-skip>"));
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			
			return false;
		}

		if (!km.config.getSkip()) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "CONFIG_DISABLED"));
			
			return false;
		}
		
		if (msg.equals("ki-skip")) {
			if (!player.hasPermission("ki-skip")) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			} else {
				if (args.length != 0) {
					return false;
				}
				
				for (String playerName : km.games.keySet()) {
					if (km.games.get(playerName).getPlayer().equals(player)) {
						km.games.get(playerName).skip(true);
						
						return true;
					}
				}
				
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_PLAYING"));
			}
			
			return false;
		} else if (msg.equals("ki-adminskip")) {
			if (!player.hasPermission("ki-adminskip")) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			} else {
				if (args.length != 1) {
					return false;
				}
				
				if (args.length == 1) {
					for (String playerName : km.games.keySet()) {
						if (playerName.equals(args[0])) {
							String tmp = km.games.get(playerName).getCurrentItem();
							km.games.get(playerName).skip(false);
							km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "ITEM_SKIPPED").replace("[#]", " [" + tmp + "] ").replace("<#>", " <" + playerName + ">"));
							
							return true;
						}
					}
					
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_NOT_FOUND"));
				}
				
				return false;
			}
		}
		
		return false;
	}
}
