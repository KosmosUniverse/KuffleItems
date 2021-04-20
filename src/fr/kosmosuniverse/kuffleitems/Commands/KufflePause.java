package fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.ActionBar;

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
		
		km.logs.logMsg(player, "achieved command <ki-pause>");
		
		if (!player.hasPermission("ki-pause")) {
			km.logs.writeMsg(player, "You are not allowed to do this command.");
			return false;
		}
		
		if (!km.gameStarted) {
			km.logs.writeMsg(player, "You need to first add people with ki-list command and launch a game with ki-start command.");
			return false;
		}
		
		if (km.paused) {
			km.logs.writeMsg(player, "Your game is already paused, you can resume it with ki-resume command.");
			return false;
		}
		
		km.paused = true;
		
		for (String playerName : km.games.keySet()) {
			km.games.get(playerName).pause();
			ActionBar.sendRawTitle("{\"text\":\"Game Paused..\",\"bold\":true,\"color\":\"dark_purple\"}", km.games.get(playerName).getPlayer());
			km.games.get(playerName).getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 10, false, false, false));
		}
		
		return true;
	}

}
