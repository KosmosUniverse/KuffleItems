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
		} else {
			if (!player.hasPermission("ki-op")) {
				KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "NOT_ALLOWED"));
				return false;
			}
			
			String before = "";
			
			for (int i = 0; i < args.length; i++) {
				if (i % 2 == 0) {
					before = args[i].toUpperCase();
				} else {
					if (KuffleMain.config.stringElems.containsKey(before)) {
						try {
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.stringElems.get(before), String.class).invoke(KuffleMain.config, args[i]);
							
							if (ret) {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));	
							} else {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + args[i] + "]"));
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (KuffleMain.config.booleanElems.containsKey(before)) {
						try {
							String tmp = args[i].toLowerCase();
							
							if (!tmp.equals("true") && !tmp.equals("false")) {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "INVALID_BOOLEAN"));
								return true;
							}
							
							boolean boolValue = Boolean.parseBoolean(tmp);
							
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.booleanElems.get(before), boolean.class).invoke(KuffleMain.config, boolValue);
							
							if (ret) {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + boolValue + "]"));	
							} else {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + boolValue + "]"));
							}
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						}
					} else if (KuffleMain.config.intElems.containsKey(before)) {
						try {
							int intValue = Integer.parseInt(args[i]);
							
							boolean ret = (boolean) Class.forName(CONFIG_CLASS).getMethod(KuffleMain.config.intElems.get(before), int.class).invoke(KuffleMain.config, intValue);

							if (ret) {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_SET).replace("[#]", before).replace("[##]", "[" + intValue + "]"));	
							} else {
								KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), CONFIG_NOT_SET).replace("[#]", before).replace("[##]", "[" + intValue + "]"));
							}
							
						} catch (NumberFormatException e) {
							KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "INVALID_INT"));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
								| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
							e.printStackTrace();
						} 
					} else {
						KuffleMain.systemLogs.writeMsg(player, Utils.getLangString(player.getName(), "KEY_NOT_REC").replace("[#]", "[" + before + "]"));
					}
				}
			}
		}
		
		return true;
	}
}
