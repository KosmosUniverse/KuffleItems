package fr.kosmosuniverse.kuffleitems;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.ManageTeams;

public class KuffleMain extends JavaPlugin {
	public HashMap<String, Game> games;
	public ArrayList<String> langs;
	public ManageTeams teams;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		System.out.println("[Kuffle Items] Plugin turned ON.");
	}
	
	@Override
	public void onDisable() {
		System.out.println("[Kuffle Items] : Plugin turned OFF.");
	}
}
