package main.fr.kosmosuniverse.kuffleitems.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Team;

public class KuffleTeamColorTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleTeamColorTab(KuffleMain _km) {
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
		} else if (args.length == 2) {
			ArrayList<String> colorList = new ArrayList<>();
			ArrayList<String> colorUsed = km.teams.getTeamColors();
			
			for (ChatColor item : ChatColor.values()) {
				if (!colorUsed.contains(item.name())) {
					colorList.add(item.name());	
				}
			}
			
			colorUsed.clear();
			
			return colorList;
		}

		return new ArrayList<>();
	}
}
