package main.fr.kosmosuniverse.kuffleitems.commands;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Team;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleTeamRandomPlayer implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamRandomPlayer(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-team-random-player>"));
		
		if (!player.hasPermission("ki-team-random-player")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_LAUNCHED"));
			return true;
		}
		
		if (args.length != 0) {
			return false;
		}
		
		if (km.games.size() == 0) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "LIST_EMPTY"));
			return true;
		}
		
		if (calcMAxPlayers() < Utils.getPlayerList(km.games).size()) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_TOO_MANY_PLAYERS"));
			return true;
		}
		
		if (!checkEmptyTeams()) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_ALREADY_PLAYERS"));
			return true;
		}
		
		int cnt = 0;
		ArrayList<Player> players = Utils.getPlayerList(km.games);
		
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		while (players.size() > 0) {
			int idx = random.nextInt(players.size());
			
			km.teams.affectPlayer(km.teams.getTeams().get(cnt).name, players.get(idx));
			
			players.remove(idx);
			
			cnt++;
			
			if (cnt >= km.teams.getTeams().size()) {
				cnt = 0;
			}
		}
		
		km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "RANDOM").replace("%i", "" + Utils.getPlayerNames(km.games).size()).replace("%j", "" + km.teams.getTeams().size()));

		return true;
	}
	
	public int calcMAxPlayers() {
		return (km.config.getTeamSize() * km.teams.getTeams().size());
	}

	public boolean checkEmptyTeams() {
		for (Team item : km.teams.getTeams()) {
			if (item.players.size() != 0) {
				return false;
			}
		}
		
		return true;
	}
}
