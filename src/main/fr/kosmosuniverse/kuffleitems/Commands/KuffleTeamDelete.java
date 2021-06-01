package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
		
		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-team-delete>"));
		
		if (!player.hasPermission("ki-team-delete")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_LAUNCHED"));
			return true;
		}
				
		if (args.length != 1) {
			return false;
		}
		
		if (!km.teams.hasTeam(args[0])) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
		} else {
			km.teams.deleteTeam(args[0]);
			
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_DELETED").replace("<#>", "<" + args[0] + ">"));
		}
		
		return true;
	}

}
