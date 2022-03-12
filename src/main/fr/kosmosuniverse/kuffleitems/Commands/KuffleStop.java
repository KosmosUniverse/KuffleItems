package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

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
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-stop>"));
		
		if (!player.hasPermission("ki-stop")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		km.games.forEach((playerName, game) -> {
			for (PotionEffect pe : game.getPlayer().getActivePotionEffects()) {
				game.getPlayer().removePotionEffect(pe.getType());
			}
			
			game.resetBar();
		});

		Utils.removeTemplates(km);
		km.scores.clear();
		
		km.teams.resetAll();
		
		km.games.clear();
		km.loop.kill();
		
		km.gameStarted = false;
		km.paused = false;
		km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_STOPPED"));
		
		return true;
	}

}
