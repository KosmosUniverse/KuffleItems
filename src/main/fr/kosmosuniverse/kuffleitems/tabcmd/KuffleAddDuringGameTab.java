package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Team;

public class KuffleAddDuringGameTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleAddDuringGameTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		List<String> ret = new ArrayList<>();
		
		if (args.length == 1) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!km.games.containsKey(player.getName())) {
					ret.add(player.getName());
				}
			}
		} else if (args.length == 2 && km.config.getTeam()) {
			for (Team team : km.teams.getTeams()) {
				if (km.config.getTeamSize() > team.players.size()) {
					ret.add(team.name);
				}
			}
		}
		
		return ret;
	}
}
