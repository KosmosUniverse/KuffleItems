package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleStop implements CommandExecutor {
	private KuffleMain km;

	public KuffleStop(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.logs.logMsg(player, "achieved command <ki-stop>");
		
		if (!player.hasPermission("ki-stop")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (km.games.size() == 0) {
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			return false;
		} else if (!km.gameStarted) {
			km.logs.writeMsg(player, "No game launched, you can launch a game with ki-start command.");
			return false;
		}
		
		for (String playerName : km.games.keySet()) {
			for (PotionEffect pe : km.games.get(playerName).getPlayer().getActivePotionEffects()) {
				km.games.get(playerName).getPlayer().removePotionEffect(pe.getType());
			}
		}
		
		//km.scores.clear();
		
		km.teams.resetAll();
		
		km.games.clear();
		km.loop.kill();
		
		km.gameStarted = false;
		km.logs.writeMsg(player, "Game Stopped.");
		
		return true;
	}

}
