package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Team;

public class KuffleTeamDeleteTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleTeamDeleteTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			ArrayList<Team> teams = km.teams.getTeams();
			ArrayList<String> ret = new ArrayList<>();
			
			for (Team item : teams) {
				ret.add(item.name);
			}
			
			teams.clear();
			
			return ret;
		}

		return new ArrayList<>();
	}
}
