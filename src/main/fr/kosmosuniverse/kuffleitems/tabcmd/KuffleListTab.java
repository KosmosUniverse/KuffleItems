package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleListTab implements TabCompleter {
	private KuffleMain km;
	private ArrayList<String> list = new ArrayList<>();
	
	public KuffleListTab(KuffleMain _km) {
		km = _km;
		
		list.add("add");
		list.add("remove");
		list.add("reset");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			return list;
		} else if (args.length == 2) {
			ArrayList<String> nextList = new ArrayList<>();
			
			if (args[0].equals("add")) {
				nextList.add("@a");
				
				for (Player player : Bukkit.getOnlinePlayers()) {
					nextList.add(player.getName());
				}
				
				return nextList;
			} else if (args[0].equals("remove")) {
				for (String playerName : km.games.keySet()) {
					nextList.add(playerName);
				}
				return nextList;
			}
		}
		
		return new ArrayList<>();
	}
}
