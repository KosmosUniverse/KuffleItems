package main.fr.kosmosuniverse.kuffleitems.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Game;

public class KuffleValidateTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleValidateTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender,  Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return null;
		
		if (cmd.getName().equalsIgnoreCase("ki-validate")) {
			if (args.length == 1) {
				ArrayList<String> list = new ArrayList<String>();
				
				for (String playerName : km.games.keySet()) {
					Game tmpGame = km.games.get(playerName);
					
					if (!tmpGame.getLose() && !tmpGame.getFinished()) {
						list.add(playerName);	
					}
				}
				
				return list;
			}
		} else if (cmd.getName().equalsIgnoreCase("ki-validate-age")) {
			if (args.length == 1) {
				ArrayList<String> list = new ArrayList<String>();
				
				for (String playerName : km.games.keySet()) {
					Game tmpGame = km.games.get(playerName);
					
					if (!tmpGame.getLose() && !tmpGame.getFinished()) {
						list.add(playerName);	
					}
				}
				
				return list;
			}
		}
		
		return new ArrayList<String>();
	}
}
