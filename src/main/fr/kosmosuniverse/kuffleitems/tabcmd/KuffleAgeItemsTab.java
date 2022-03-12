package main.fr.kosmosuniverse.kuffleitems.tabcmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;

public class KuffleAgeItemsTab implements TabCompleter  {
	private KuffleMain km;
	private ArrayList<String> ages = new ArrayList<>();

	public KuffleAgeItemsTab(KuffleMain _km) {
		km = _km;
		
		int max = AgeManager.getAgeMaxNumber(km.ages);
		
		for (int cnt = 0; cnt <= max; cnt++) {
			String age = AgeManager.getAgeByNumber(km.ages, cnt).name;

			ages.add(age);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player)) {
			return new ArrayList<>();
		}
		
		if (args.length == 1) {
			return ages;
		}
		
		return new ArrayList<>();
	}
}
