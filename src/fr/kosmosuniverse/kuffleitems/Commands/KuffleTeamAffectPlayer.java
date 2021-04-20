package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
		
		km.logs.logMsg(player, "achieved command <ki-team-affect-player>");
		
		if (!player.hasPermission("ki-team-affect-player")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.games.size() > 0 && km.gameStarted) {
			km.logs.writeMsg(player, "Game is already launched, you cannot modify teams during the game.");
			return true;
		}
		
		if (args.length != 2) {
			return false;
		}
		
		if (!km.teams.hasTeam(args[0])) {
			km.logs.writeMsg(player, "Team :<" + args[0] + "> does not exist, please choose another name.");
			return true;
		}
		
		if (km.teams.getTeam(args[0]).players.size() == km.config.getTeamSize()) {
			km.logs.writeMsg(player, "This team is already full, please add him in another team or change team size with ki-config command.");
			return true;
		}
		
		if (!Utils.getPlayerNames(km.games).contains(args[1])) {
			km.logs.writeMsg(player, "This player is not in the game, please add him in the game list with ki-list command.");
			return true;
		}
		
		for (String playerName : km.games.keySet()) {
			if (km.games.get(playerName).getPlayer().getName().equals(args[1])) {
				km.teams.affectPlayer(args[0], km.games.get(playerName).getPlayer());
				
				km.logs.writeMsg(player, "Player <" + args[1] + "> added to team <" + args[0] + ">.");
				
				return true;
			}
		}
		
		return true;
	}

}
