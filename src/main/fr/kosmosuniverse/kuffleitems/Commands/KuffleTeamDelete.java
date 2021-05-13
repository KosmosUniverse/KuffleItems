package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleTeamDelete implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamDelete(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-team-delete>");
		
		if (!player.hasPermission("ki-team-delete")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.logs.writeMsg(player, "Game is already launched, you cannot modify teams during the game.");
			return true;
		}
				
		if (args.length != 1) {
			return false;
		}
		
		if (!km.teams.hasTeam(args[0])) {
			km.logs.writeMsg(player, "Team <" + args[0] + "> does not exist, please choose another name.");
		} else {
			km.teams.deleteTeam(args[0]);
			
			km.logs.writeMsg(player, "Team <" + args[0] + "> was deleted.");
		}
		
		return true;
	}

}
