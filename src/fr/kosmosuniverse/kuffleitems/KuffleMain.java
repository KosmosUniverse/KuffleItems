package fr.kosmosuniverse.kuffleitems;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import fr.kosmosuniverse.kuffleitems.Core.Logs;
import fr.kosmosuniverse.kuffleitems.Core.ManageTeams;
import fr.kosmosuniverse.kuffleitems.Core.Scores;
import fr.kosmosuniverse.kuffleitems.Listeners.*;
import fr.kosmosuniverse.kuffleitems.TabCmd.*;
import fr.kosmosuniverse.kuffleitems.Core.ItemManager;
import fr.kosmosuniverse.kuffleitems.Core.LangManager;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import fr.kosmosuniverse.kuffleitems.Core.RewardElem;
import fr.kosmosuniverse.kuffleitems.Core.RewardManager;
import fr.kosmosuniverse.kuffleitems.Commands.*;
import fr.kosmosuniverse.kuffleitems.Core.Config;
import fr.kosmosuniverse.kuffleitems.Core.CraftsManager;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.GameLoop;

public class KuffleMain extends JavaPlugin {
	public HashMap<String, HashMap<String, RewardElem>> allRewards;
	public HashMap<String, HashMap<String, String>> allLangs;
	
	public HashMap<String, ArrayList<String>> allItems = new HashMap<String, ArrayList<String>>();
	public HashMap<String, ArrayList<Inventory>> itemsInvs;
	
	public HashMap<String, PotionEffectType> effects;
	public HashMap<String, Game> games = new HashMap<String, Game>();
	public HashMap<String, Integer> playerRank = new HashMap<String, Integer>();
	
	public ArrayList<String> langs;
	public ArrayList<String> ageNames;
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
		
		try {
			InputStream in = getResource("items_" + Utils.getVersion() + ".json");
			String checkAges = Utils.readFileContent(in);
			in.close();
			
			if ((ageNames = Utils.getAges(checkAges)) == null) {
				this.getPluginLoader().disablePlugin(this);
				return ;
			}
			
			in = getResource("items_" + Utils.getVersion() + ".json");
			String result = Utils.readFileContent(in);
			allItems = ItemManager.getAllItems(ageNames, result, this.getDataFolder());
			
			in.close();
			
			in = getResource("rewards_" + Utils.getVersion() + ".json");
			result = Utils.readFileContent(in);
			allRewards = RewardManager.getAllRewards(ageNames, result, this.getDataFolder());
			
			in.close();
			
			in = getResource("items_lang.json");
			result = Utils.readFileContent(in);
			allLangs = LangManager.getAllBlocksLang(result, this.getDataFolder());
			
			in.close();
			
			logs = new Logs(this.getDataFolder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		langs = LangManager.findAllLangs(allLangs);
		effects = RewardManager.getAllEffects();
		
		config = new Config(this);
		config.setupConfig(this, getConfig());
		
		crafts = new CraftsManager(this);
		itemsInvs = ItemManager.getItemsInvs(allItems);
		scores = new Scores(this);
		logs = new Logs(this.getDataFolder());
		
		System.out.println("[KuffleItems] Add Custom Crafts.");
		for (ACrafts item : crafts.getRecipeList()) {
			getServer().addRecipe(item.getRecipe());
		}
		
		System.out.println("[KuffleItems] Add Game Listeners.");
		getServer().getPluginManager().registerEvents(new PlayerEvents(this, this.getDataFolder()), this);
		getServer().getPluginManager().registerEvents(new PlayerInteract(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
		
		System.out.println("[KuffleItems] Add Plugin Commands.");
		getCommand("ki-config").setExecutor(new KuffleConfig(this));
		getCommand("ki-list").setExecutor(new KuffleList(this));
		getCommand("ki-adminsave").setExecutor(new KuffleAdminSave(this, this.getDataFolder()));
		getCommand("ki-adminload").setExecutor(new KuffleAdminLoad(this, this.getDataFolder()));
		getCommand("ki-start").setExecutor(new KuffleStart(this));
		getCommand("ki-stop").setExecutor(new KuffleStop(this));
		getCommand("ki-pause").setExecutor(new KufflePause(this));
		getCommand("ki-resume").setExecutor(new KuffleResume(this));
		getCommand("ki-ageitems").setExecutor(new KuffleAgeItems(this));
		getCommand("ki-back").setExecutor(new KuffleBack(this));
		getCommand("ki-crafts").setExecutor(new KuffleCrafts(this));
		getCommand("ki-lang").setExecutor(new KuffleLang(this));
		getCommand("ki-skip").setExecutor(new KuffleSkip(this));
		getCommand("ki-validate").setExecutor(new KuffleValidate(this));
		getCommand("ki-players").setExecutor(new KufflePlayers(this));
		
		getCommand("ki-team-create").setExecutor(new KuffleTeamCreate(this));
		getCommand("ki-team-delete").setExecutor(new KuffleTeamDelete(this));
		getCommand("ki-team-color").setExecutor(new KuffleTeamColor(this));
		getCommand("ki-team-show").setExecutor(new KuffleTeamShow(this));
		getCommand("ki-team-affect-player").setExecutor(new KuffleTeamAffectPlayer(this));
		getCommand("ki-team-remove-player").setExecutor(new KuffleTeamRemovePlayer(this));
		getCommand("ki-team-reset-players").setExecutor(new KuffleTeamResetPlayers(this));
		getCommand("ki-team-random-player").setExecutor(new KuffleTeamRandomPlayer(this));
		
		System.out.println("[KuffleItems] Add Plugin Tab Completer.");
		getCommand("ki-config").setTabCompleter(new KuffleConfigTab(this));
		getCommand("ki-list").setTabCompleter(new KuffleListTab(this));
		getCommand("ki-lang").setTabCompleter(new KuffleLangTab(this));
		getCommand("ki-validate").setTabCompleter(new KuffleValidateTab(this));
		
		getCommand("ki-team-create").setTabCompleter(new KuffleTeamCreateTab(this));
		getCommand("ki-team-delete").setTabCompleter(new KuffleTeamDeleteTab(this));
		getCommand("ki-team-color").setTabCompleter(new KuffleTeamColorTab(this));
		getCommand("ki-team-show").setTabCompleter(new KuffleTeamShowTab(this));
		getCommand("ki-team-affect-player").setTabCompleter(new KuffleTeamAffectPlayerTab(this));
		getCommand("ki-team-remove-player").setTabCompleter(new KuffleTeamRemovePlayerTab(this));
		getCommand("ki-team-reset-players").setTabCompleter(new KuffleTeamResetPlayersTab(this));
		
		loaded = true;
		
		System.out.println("[KuffleItems] : Plugin turned ON.");
	}
	
	@Override
	public void onDisable() {
		if (loaded) {
			if (gameStarted) {
				Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "ki-stop");
			}
			
			killAll();
		}
		
		System.out.println("[KuffleItems] : Plugin turned OFF.");
	}
	
	private void killAll() {
		for (String key : allRewards.keySet()) {
			allRewards.get(key).clear();
		}
		
		allRewards.clear();
		
		for (String key : allItems.keySet()) {
			allItems.get(key).clear();
		}
		
		allItems.clear();

		for (String key : allLangs.keySet()) {
			allLangs.get(key).clear();
		}
		
		allLangs.clear();
		
		for (String key : itemsInvs.keySet()) {
			itemsInvs.get(key).clear();
		}
		
		itemsInvs.clear();
		
		
		effects.clear();
		playerRank.clear();
		games.clear();
		langs.clear();
		ageNames.clear();
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
