package main.fr.kosmosuniverse.kuffleitems.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Team;

public class KuffleAddDuringGameTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleAddDuringGameTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return null;
		}
		
		if (!cmd.getName().equalsIgnoreCase("ki-add-during-game")) {
			return null;
		}

		List<String> ret = null;
		
		if (args.length == 1) {
			ret = new ArrayList<String>();
			
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!km.games.containsKey(player.getName())) {
					ret.add(player.getName());
				}
			}
		} else if (args.length == 2 && km.config.getTeam()) {
			ret = new ArrayList<String>();
			
			for (Team team : km.teams.getTeams()) {
				if (km.config.getTeamSize() > team.players.size()) {
					ret.add(team.name);
				}
			}
		}
		
		return ret;
	}
}
