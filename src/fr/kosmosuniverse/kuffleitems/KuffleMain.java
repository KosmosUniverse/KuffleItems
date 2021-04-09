package fr.kosmosuniverse.kuffleitems;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kosmosuniverse.kuffleitems.Core.Logs;
import fr.kosmosuniverse.kuffleitems.Core.ManageTeams;
import fr.kosmosuniverse.kuffleitems.Listeners.PlayerInteract;
import fr.kosmosuniverse.kuffleitems.Core.ItemManager;
import fr.kosmosuniverse.kuffleitems.Core.LangManager;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.Commands.*;
import fr.kosmosuniverse.kuffleitems.Core.Config;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.GameLoop;

public class KuffleMain extends JavaPlugin {
	public HashMap<String, ArrayList<String>> allItems = new HashMap<String, ArrayList<String>>();
	public HashMap<String, Game> games = new HashMap<String, Game>();
	public HashMap<String, HashMap<String, String>> allLangs;
	public GameLoop loop;
	public ArrayList<String> langs;
	public Config config;
	public Logs logs;
	public ManageTeams teams = new ManageTeams();
	public Inventory playersHeads;
	public boolean paused;
	
	public boolean gameStarted = false;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		
		try {
			InputStream in = getResource("items_" + Utils.getVersion() + ".json");
			String result = Utils.readFileContent(in);
			allItems = ItemManager.getAllItems(result, this.getDataFolder());
			
			in.close();
			
			/*in = getResource("rewards_" + Utils.getVersion() + ".json");
			result = Utils.readFileContent(in);
			allRewards = RewardManager.getAllRewards(result, this.getDataFolder());
			
			in.close();*/
			
			in = getResource("blocks_lang.json");
			result = Utils.readFileContent(in);
			allLangs = LangManager.getAllBlocksLang(result, this.getDataFolder());
			
			in.close();
			
			logs = new Logs(this.getDataFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		langs = new ArrayList<String>();
		
		langs.add("en");
		langs.add("fr");
		
		config = new Config(this);
		config.setupConfig(this, getConfig());
		
		logs = new Logs(this.getDataFolder());
		
		getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
		
		getCommand("ki-config").setExecutor(new KuffleConfig(this));
		getCommand("ki-list").setExecutor(new KuffleList(this));
		getCommand("ki-start").setExecutor(new KuffleStart(this));
		getCommand("ki-stop").setExecutor(new KuffleStop(this));
		
		getCommand("ki-team-create").setExecutor(new KuffleTeamCreate(this));
		getCommand("ki-team-delete").setExecutor(new KuffleTeamDelete(this));
		getCommand("ki-team-color").setExecutor(new KuffleTeamColor(this));
		getCommand("ki-team-show").setExecutor(new KuffleTeamShow(this));
		getCommand("ki-team-affect-player").setExecutor(new KuffleTeamAffectPlayer(this));
		getCommand("ki-team-remove-player").setExecutor(new KuffleTeamRemovePlayer(this));
		getCommand("ki-team-reset-players").setExecutor(new KuffleTeamResetPlayers(this));
		getCommand("ki-team-random-player").setExecutor(new KuffleTeamRandomPlayer(this));
		
		System.out.println("[Kuffle Items] : Plugin turned ON.");
	}
	
	@Override
	public void onDisable() {
		System.out.println("[Kuffle Items] : Plugin turned OFF.");
	}
}
