package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Team;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleTeamAffectPlayerTab implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			List<String> ret = new ArrayList<>();
			
			for (Team item : KuffleMain.teams.getTeams()) {
				ret.add(item.name);
			}
			
			return ret;
		} else if (args.length == 2) {
			List<String> ret = new ArrayList<>();
			
			for (String item : Utils.getPlayerNames(KuffleMain.games)) {
				if (!KuffleMain.teams.isInTeam(item)) {
					ret.add(item);
				}
			}
			
			return ret;
		}

		return new ArrayList<>();
	}
}
