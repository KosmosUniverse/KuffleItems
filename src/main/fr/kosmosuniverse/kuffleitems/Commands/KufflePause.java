package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KufflePause implements CommandExecutor {
	private KuffleMain km;
	
	public KufflePause(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-pause>"));
		
		if (!player.hasPermission("ki-pause")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		if (km.paused) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_PAUSED"));
			return false;
		}
		
		km.paused = true;
		
		km.games.forEach((playerName, game) -> {
			game.pause();
			ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + Utils.getLangString(km, player.getName(), "GAME_PAUSED") + ChatColor.RESET, game.getPlayer());
			game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));			
		});
		
		return true;
	}

}
