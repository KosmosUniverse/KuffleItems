package main.fr.kosmosuniverse.kuffleitems.commands;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleConfig implements CommandExecutor {
	private KuffleMain km;
	private static final String CONFIG_CLASS = "main.fr.kosmosuniverse.kuffleitems.Core.Config";
	private static final String CONFIG_SET = "CONFIG_SET";
	private static final String CONFIG_NOT_SET = "CONFIG_NOT_SET";
	
	public KuffleConfig(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;
		
		if (args.length % 2 == 1) {
			return false;
		}
		
		Player player = (Player) sender;
		
		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-config>"));
		
		if (args.length == 0) {
			player.sendMessage(km.config.displayConfig());
		} else {
			if (!player.hasPermission("ki-op")) {
				km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			String before = "";
			
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					before = args[i].toUpperCase();
				} else {
					if (km.config.stringElems.containsKey(before)) {
						try {
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(km.config.stringElems.get(before), String.class).invoke(km.config, args[i]);
							
							if (ret) {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));	
							} else {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (km.config.booleanElems.containsKey(before)) {
						try {
							String tmp = args[i].toLowerCase();
							
							if (!tmp.equals("true") && !tmp.equals("false")) {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "INVALID_BOOLEAN"));
								return true;
							}
							
							boolean boolValue = Boolean.parseBoolean(tmp);
							
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(km.config.booleanElems.get(before), boolean.class).invoke(km.config, boolValue);
							
							if (ret) {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + boolValue + "]"));	
							} else {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + boolValue + "]"));
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (km.config.intElems.containsKey(before)) {
						try {
							int intValue = Integer.parseInt(args[i]);
							
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(km.config.intElems.get(before), int.class).invoke(km.config, intValue);

							if (ret) {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + intValue + "]"));	
							} else {
								km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + intValue + "]"));
							}
							
						} catch (NumberFormatException e) {
							km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "INVALID_INT"));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						} 
					} else {
						km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "KEY_NOT_REC").replace("[#]", "[" + before + "]"));
					}
				}
			}
		}
		
		return true;
	}
}
