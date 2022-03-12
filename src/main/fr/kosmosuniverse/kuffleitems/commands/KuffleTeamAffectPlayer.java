package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleTeamAffectPlayer implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamAffectPlayer(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-team-affect-player>"));
		
		if (!player.hasPermission("ki-team-affect-player")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_LAUNCHED"));
			return true;
		}
		
		if (args.length != 2) {
			return false;
		}
		
		if (!km.teams.hasTeam(args[0])) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			return true;
		}
		
		if (km.teams.getTeam(args[0]).players.size() == km.config.getTeamSize()) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_FULL"));
			return true;
		}
		
		if (!km.games.containsKey(args[1])) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_NOT_IN_GAME"));
			return true;
		}
		
		km.teams.affectPlayer(args[0], km.games.get(args[1]).getPlayer());
		km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_ADD_PLAYER").replace("<#>", "<" + args[1] + ">").replace("<##>", "<" + args[0] + ">"));
		
		return true;
	}

}
