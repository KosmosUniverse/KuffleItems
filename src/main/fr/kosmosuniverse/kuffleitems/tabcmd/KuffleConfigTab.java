package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleConfigTab implements TabCompleter {
	private ArrayList<String> all = new ArrayList<>();
	
	public KuffleConfigTab() {
		all.addAll(KuffleMain.config.booleanElems.keySet());
		all.addAll(KuffleMain.config.intElems.keySet());
		all.addAll(KuffleMain.config.stringElems.keySet());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 0) {
			return all;
		} else if (args.length % 2 == 1) {
			ArrayList<String> ret = new ArrayList<>();
			ret.addAll(all);
			
			for (String arg : args) {
				if (ret.contains(arg)) {
					ret.remove(arg);
				}
			}
			
			return ret;
		} else {
			if (KuffleMain.config.booleanElems.keySet().contains(args[args.length - 2])) {
				return KuffleMain.config.booleanRet.get(args[args.length - 2]);
			} else if (KuffleMain.config.intElems.keySet().contains(args[args.length - 2])) {
				return KuffleMain.config.intRet.get(args[args.length - 2]);
			} else if (KuffleMain.config.stringElems.keySet().contains(args[args.length - 2])) {
				return KuffleMain.config.stringRet.get(args[args.length - 2]);
			} else {
				return new ArrayList<>();	
			}
		}
	}
}
