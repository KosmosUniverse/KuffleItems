package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleLang implements CommandExecutor {
	private KuffleMain km;
	
	public KuffleLang(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length > 1) {
			return false; 
		}
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-lang>"));
		
		if (!player.hasPermission("ki-lang")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_PLAYING"));
			return true;
		}
		
		km.games.forEach((playerName, game) -> {
			if (km.games.get(playerName).getPlayer().equals(player)) {
				if (args.length == 0) {
					km.systemLogs.writeMsg(player, km.games.get(playerName).getLang());
				} else if (args.length == 1) {
					String lang = args[0].toLowerCase();
					
					if (km.langs.contains(lang)) {
						km.games.get(playerName).setLang(lang);
						
						km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "LANG_SET").replace("[#]", " [" + lang + "]"));
					} else {
						km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "REQ_LANG_NOT_AVAIL"));
					}
				}
			}
		});
		
		return true;
	}

}
