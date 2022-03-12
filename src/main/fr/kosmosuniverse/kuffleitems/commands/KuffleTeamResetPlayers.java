package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleTeamResetPlayers implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamResetPlayers(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-team-reset-players>"));
		
		if (!player.hasPermission("ki-team-reset-players")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_LAUNCHED"));
			return true;
		}
				
		if (args.length != 1) {
			return false;
		}
		
		if (!km.teams.hasTeam(args[0])) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		km.teams.getTeam(args[0]).players.clear();
		km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_RESETED").replace("<#>", "<" + args[0] + ">"));
		
		return true;
	}

}
