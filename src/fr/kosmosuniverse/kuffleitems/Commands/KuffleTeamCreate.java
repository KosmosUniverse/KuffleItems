package fr.kosmosuniverse.kuffleitems.Commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleTeamCreate implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamCreate(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-team-create>");
		
		if (!player.hasPermission("ki-team-create")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (!km.config.getTeam()) {
			km.logs.writeMsg(player, "Please enable Teams with ki-config command.");
			return true;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.logs.writeMsg(player, "Game is already launched, you cannot modify teams during the game.");
			return true;
		}
		
		if (args.length < 1 || args.length > 2) {
			return false;
		}
		
		if (km.teams.hasTeam(args[0])) {
			km.logs.writeMsg(player, "Team <" + args[0] + "> already exists, please choose another name.");
		} else {
			if (args.length == 1) {
				km.teams.createTeam(args[0]);
				km.logs.writeMsg(player, "Team <" + args[0] + "> was created.");
			} else if (args.length == 2) {
				ChatColor tmp;
				
				if ((tmp = Utils.findChatColor(args[1])) == null) {
					km.logs.writeMsg(player, "Color <" + args[1] + "> does not exist, please choose another name.");
				} else {
					ArrayList<String> colorUsed = km.teams.getTeamColors();
					
					if (!colorUsed.contains(tmp.name())) {
						km.teams.createTeam(args[0], tmp);
						
						km.logs.writeMsg(player, "Team <" + args[0] + "> was created.");
					} else {
						km.logs.writeMsg(player, "Color <" + tmp.name() + "> is already used, please choose another one.");
					}
				}
			}
		}
		
		return true;
	}

}
