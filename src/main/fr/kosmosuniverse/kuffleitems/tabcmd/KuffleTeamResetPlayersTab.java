package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Team;

public class KuffleTeamResetPlayersTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			List<Team> teams = KuffleMain.teams.getTeams();
			List<String> ret = new ArrayList<>();
			
			for (Team item : teams) {
				if (item.players.size() != 0) {
					ret.add(item.name);	
				}
			}
			
			teams.clear();
			
			return ret;
		}

		return new ArrayList<>();
	}
}
