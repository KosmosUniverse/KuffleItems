package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleTeamShow implements CommandExecutor {
	private KuffleMain km;

	public KuffleTeamShow(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-team-show>"));
		
		if (!player.hasPermission("ki-team-show")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (args.length > 1) {
			return false;
		}
		
		if (args.length == 0) {
			km.logs.writeMsg(player, km.teams.toString(km));
		} else if (args.length == 1) {
			if (km.teams.hasTeam(args[0])) {
				km.logs.writeMsg(player, km.teams.printTeam(args[0]));
			} else {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[0] + ">"));
			}
		}
		
		return true;
	}

}
