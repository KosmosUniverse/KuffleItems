package main.fr.kosmosuniverse.kuffleitems.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleCrafts implements CommandExecutor {
	private KuffleMain km;

	public KuffleCrafts(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-crafts>"));
		
		if (!player.hasPermission("ki-crafts")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}
		
		player.openInventory(km.crafts.getAllCraftsInventory());
		
		return true;
	}
}
