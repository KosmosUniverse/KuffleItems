package main.fr.kosmosuniverse.kuffleitems.TabCmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;

public class KuffleLangTab implements TabCompleter {
	private KuffleMain km;
	
	public KuffleLangTab(KuffleMain _km) {
		km = _km;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			return km.langs;
		}

		return new ArrayList<>();
	}
}
