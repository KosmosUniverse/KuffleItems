package fr.kosmosuniverse.kuffleitems.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.Team;

public class KuffleTeamShowTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleTeamShowTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
		if (cmd.getName().equalsIgnoreCase("ki-team-show")) {
			if (args.length == 1) {				
				ArrayList<Team> teams = km.teams.getTeams();
				ArrayList<String> ret = new ArrayList<String>();
				
				for (Team item : teams) {
					ret.add(item.name);
				}
				
				return ret;
			}
		}

		return new ArrayList<String>();
	}
}
