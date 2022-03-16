package main.fr.kosmosuniverse.kuffleitems.commands;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleConfig implements CommandExecutor {
	private static final String CONFIG_CLASS = "main.fr.kosmosuniverse.kuffleitems.Core.Config";
	private static final String CONFIG_SET = "CONFIG_SET";
	private static final String CONFIG_NOT_SET = "CONFIG_NOT_SET";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length % 2 == 1) {
			return false;
		}
		
		Player player = (Player) sender;
		
		KuffleMain.systemLogs.logMsg(player.getName(), Utils.getLangString(player.getName(), "CMD_PERF").replace("<#>", "<ki-config>"));
		
		if (args.length == 0) {
			player.sendMessage(KuffleMain.config.displayConfig());
			return true;
		}
		
		if (!player.hasPermission("ki-op")) {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
			return true;
		}
		
		String before = "";
		
		for (int i = 0; i < args.length; i++) {
			if (i % 2 == 0) {
				before = args[i].toUpperCase();
			} else {
				try {
					boolean ret = invokeMethod(player, before, args[i]);
					
					if (ret) {
						KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));	
					} else {
						KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
						| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
					KuffleMain.systemLogs.logSystemMsg(e.getMessage());
				}
			}
		}
		
		return true;
	}
	
	private boolean invokeMethod(Player player, String before, String current)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		boolean ret;
		
		if (KuffleMain.config.stringElems.containsKey(before)) {
			ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.stringElems.get(before), String.class).invoke(KuffleMain.config, current);		
		} else if (KuffleMain.config.booleanElems.containsKey(before)) {
			String tmp = current.toLowerCase();
			
			if (!tmp.equals("true") && !tmp.equals("false")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "INVALID_BOOLEAN"));
				return true;
			}
			
			boolean boolValue = Boolean.parseBoolean(tmp);
			
			ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.booleanElems.get(before), boolean.class).invoke(KuffleMain.config, boolValue);
		} else if (KuffleMain.config.intElems.containsKey(before)) {
			int intValue = Integer.parseInt(current);
			
			ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.intElems.get(before), int.class).invoke(KuffleMain.config, intValue);
		} else {
			KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "KEY_NOT_REC").replace("[#]", "[" + before + "]"));
			ret = false;
		}
		
		return ret;
	}
}
