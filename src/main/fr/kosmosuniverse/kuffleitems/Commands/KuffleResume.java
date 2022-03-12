package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleResume implements CommandExecutor {
	private KuffleMain km;
	
	public KuffleResume(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-resume>"));
		
		if (!player.hasPermission("ki-resume")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		if (!km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
			return false;
		}
		
		if (!km.paused) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_ALREADY_RUNNING"));
			return false;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "3" + ChatColor.RESET, game.getPlayer());
			});
		}, 20);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "2" + ChatColor.RESET, game.getPlayer());
			});
		}, 40);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "1" + ChatColor.RESET, game.getPlayer());
			});
		}, 60);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.paused = false;
			
			km.games.forEach((playerName, game) -> {
				game.resume();
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + Utils.getLangString(km, player.getName(), "GAME_RESUMED") + ChatColor.RESET, game.getPlayer());
				game.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
			});
		}, 80);

		return true;
	}

}
