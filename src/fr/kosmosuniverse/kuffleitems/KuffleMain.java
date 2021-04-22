package fr.kosmosuniverse.kuffleitems;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import fr.kosmosuniverse.kuffleitems.Core.Logs;
import fr.kosmosuniverse.kuffleitems.Core.ManageTeams;
import fr.kosmosuniverse.kuffleitems.Core.Scores;
import fr.kosmosuniverse.kuffleitems.Listeners.*;
import fr.kosmosuniverse.kuffleitems.TabCmd.*;
import fr.kosmosuniverse.kuffleitems.Core.ItemManager;
import fr.kosmosuniverse.kuffleitems.Core.LangManager;
import fr.kosmosuniverse.kuffleitems.Utils.FilesConformity;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import fr.kosmosuniverse.kuffleitems.Core.RewardElem;
import fr.kosmosuniverse.kuffleitems.Core.RewardManager;
import fr.kosmosuniverse.kuffleitems.Commands.*;
import fr.kosmosuniverse.kuffleitems.Core.Age;
import fr.kosmosuniverse.kuffleitems.Core.AgeManager;
import fr.kosmosuniverse.kuffleitems.Core.Config;
import fr.kosmosuniverse.kuffleitems.Core.CraftsManager;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.GameLoop;

public class KuffleMain extends JavaPlugin {
	public HashMap<String, HashMap<String, RewardElem>> allRewards;
	public HashMap<String, HashMap<String, String>> allLangs;
	
	public HashMap<String, ArrayList<String>> allItems = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<Inventory>> itemsInvs;
	
	public HashMap<String, Game> games = new HashMap<String, Game>();
	public HashMap<String, Integer> playerRank = new HashMap<String, Integer>();
	
	public ArrayList<String> langs;
	public ArrayList<Age> ages;
	public GameLoop loop;
	public Config config;
	public Logs logs;
	public ManageTeams teams = new ManageTeams();
	public CraftsManager crafts;
	public Scores scores;
	public Inventory playersHeads;
	
	public boolean paused = false;
	public boolean loaded = false;
	
	public boolean gameStarted = false;
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();

		if ((ages = AgeManager.getAges(FilesConformity.getContent(this, "ages.json"))) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}
		
		if ((allItems = ItemManager.getAllItems(ages, FilesConformity.getContent(this, "items_" + Utils.getVersion() + ".json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}
		
		if ((allRewards = RewardManager.getAllRewards(ages, FilesConformity.getContent(this, "rewards_" + Utils.getVersion() + ".json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}
		
		if ((allLangs = LangManager.getAllBlocksLang(FilesConformity.getContent(this, "items_lang.json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}
		
		logs = new Logs(this.getDataFolder());
		langs = LangManager.findAllLangs(allLangs);
		
		config = new Config(this);
		config.setupConfig(this, getConfig());
		
		crafts = new CraftsManager(this);
		itemsInvs = ItemManager.getItemsInvs(allItems);
		scores = new Scores(this);
		logs = new Logs(this.getDataFolder());
		
		int cnt = 0;
		
		for (ACrafts item : crafts.getRecipeList()) {
			getServer().addRecipe(item.getRecipe());
			cnt++;
		}
		
		System.out.println("[KuffleItems] Add " + cnt + " Custom Crafts.");
		

		getServer().getPluginManager().registerEvents(new PlayerEvents(this, this.getDataFolder()), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
		System.out.println("[KuffleItems] Add 3 Game Listeners.");
		
		getCommand("ki-config").setExecutor(new KuffleConfig(this));
		getCommand("ki-list").setExecutor(new KuffleList(this));
		getCommand("ki-save").setExecutor(new KuffleSave(this, this.getDataFolder()));
		getCommand("ki-load").setExecutor(new KuffleLoad(this, this.getDataFolder()));
		getCommand("ki-start").setExecutor(new KuffleStart(this));
		getCommand("ki-stop").setExecutor(new KuffleStop(this));
		getCommand("ki-pause").setExecutor(new KufflePause(this));
		getCommand("ki-resume").setExecutor(new KuffleResume(this));
		getCommand("ki-ageitems").setExecutor(new KuffleAgeItems(this));
		getCommand("ki-back").setExecutor(new KuffleBack(this));
		getCommand("ki-crafts").setExecutor(new KuffleCrafts(this));
		getCommand("ki-lang").setExecutor(new KuffleLang(this));
		getCommand("ki-skip").setExecutor(new KuffleSkip(this));
		getCommand("ki-adminskip").setExecutor(new KuffleSkip(this));
		getCommand("ki-validate").setExecutor(new KuffleValidate(this));
		getCommand("ki-validate-age").setExecutor(new KuffleValidate(this));
		getCommand("ki-players").setExecutor(new KufflePlayers(this));
		
		getCommand("ki-team-create").setExecutor(new KuffleTeamCreate(this));
		getCommand("ki-team-delete").setExecutor(new KuffleTeamDelete(this));
		getCommand("ki-team-color").setExecutor(new KuffleTeamColor(this));
		getCommand("ki-team-show").setExecutor(new KuffleTeamShow(this));
		getCommand("ki-team-affect-player").setExecutor(new KuffleTeamAffectPlayer(this));
		getCommand("ki-team-remove-player").setExecutor(new KuffleTeamRemovePlayer(this));
		getCommand("ki-team-reset-players").setExecutor(new KuffleTeamResetPlayers(this));
		getCommand("ki-team-random-player").setExecutor(new KuffleTeamRandomPlayer(this));
		System.out.println("[KuffleItems] Add 24 Plugin Commands.");

		getCommand("ki-config").setTabCompleter(new KuffleConfigTab(this));
		getCommand("ki-list").setTabCompleter(new KuffleListTab(this));
		getCommand("ki-lang").setTabCompleter(new KuffleLangTab(this));
		getCommand("ki-validate").setTabCompleter(new KuffleValidateTab(this));
		getCommand("ki-validate-age").setTabCompleter(new KuffleValidateTab(this));
		
		getCommand("ki-team-create").setTabCompleter(new KuffleTeamCreateTab(this));
		getCommand("ki-team-delete").setTabCompleter(new KuffleTeamDeleteTab(this));
		getCommand("ki-team-color").setTabCompleter(new KuffleTeamColorTab(this));
		getCommand("ki-team-show").setTabCompleter(new KuffleTeamShowTab(this));
		getCommand("ki-team-affect-player").setTabCompleter(new KuffleTeamAffectPlayerTab(this));
		getCommand("ki-team-remove-player").setTabCompleter(new KuffleTeamRemovePlayerTab(this));
		getCommand("ki-team-reset-players").setTabCompleter(new KuffleTeamResetPlayersTab(this));
		System.out.println("[KuffleItems] Add 11 Plugin Tab Completer.");
		
		loaded = true;
		
		System.out.println("[KuffleItems] Plugin turned ON.");
	}
	
	@Override
	public void onDisable() {
		if (loaded) {
			killAll();
		}
		
		System.out.println("[KuffleItems] Plugin turned OFF.");
	}
	
	private void killAll() {
		if (allRewards != null) {
			for (String key : allRewards.keySet()) {
				allRewards.get(key).clear();
			}
			
			allRewards.clear();
		}
		
		if (allItems != null) {
			for (String key : allItems.keySet()) {
				allItems.get(key).clear();
			}
			
			allItems.clear();
		}

		
		if (allLangs != null) {
			for (String key : allLangs.keySet()) {
				allLangs.get(key).clear();
			}
			
			allLangs.clear();
		}
		
		if (itemsInvs != null) {
			for (String key : itemsInvs.keySet()) {
				itemsInvs.get(key).clear();
			}
			
			itemsInvs.clear();
		}
		
		if (playerRank != null) {
			playerRank.clear();
		}
		
		if (games != null) {
			games.clear();	
		}
		
		if (langs != null) { 
			langs.clear();
		}
		
		if (ages != null) {
			ages.clear();	
		}
	}
	
	public void updatePlayersHead(String player, String currentItem) {
		ItemMeta itM;
		
		for (ItemStack item : playersHeads) {
			if (item != null) {
				itM = item.getItemMeta();
				
				if (itM.getDisplayName().equals(player)) {
					ArrayList<String> lore = new ArrayList<String>();
					
					lore.add(currentItem);
					
					itM.setLore(lore);
					item.setItemMeta(itM);
				}
			}
		}
	}
}
