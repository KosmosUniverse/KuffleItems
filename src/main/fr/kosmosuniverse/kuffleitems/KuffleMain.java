package main.fr.kosmosuniverse.kuffleitems;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import main.fr.kosmosuniverse.kuffleitems.commands.*;
import main.fr.kosmosuniverse.kuffleitems.core.Age;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.core.Config;
import main.fr.kosmosuniverse.kuffleitems.core.CraftsManager;
import main.fr.kosmosuniverse.kuffleitems.core.Game;
import main.fr.kosmosuniverse.kuffleitems.core.GameLoop;
import main.fr.kosmosuniverse.kuffleitems.core.ItemManager;
import main.fr.kosmosuniverse.kuffleitems.core.LangManager;
import main.fr.kosmosuniverse.kuffleitems.core.Level;
import main.fr.kosmosuniverse.kuffleitems.core.LevelManager;
import main.fr.kosmosuniverse.kuffleitems.core.Logs;
import main.fr.kosmosuniverse.kuffleitems.core.ManageTeams;
import main.fr.kosmosuniverse.kuffleitems.core.RewardElem;
import main.fr.kosmosuniverse.kuffleitems.core.RewardManager;
import main.fr.kosmosuniverse.kuffleitems.core.Scores;
import main.fr.kosmosuniverse.kuffleitems.crafts.ACrafts;
import main.fr.kosmosuniverse.kuffleitems.listeners.*;
import main.fr.kosmosuniverse.kuffleitems.tabcmd.*;
import main.fr.kosmosuniverse.kuffleitems.utils.FilesConformity;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class KuffleMain extends JavaPlugin {
	public HashMap<String, HashMap<String, RewardElem>> allRewards;
	public HashMap<String, HashMap<String, String>> allItemsLangs;
	public HashMap<String, HashMap<String, String>> allLangs;

	public HashMap<String, ArrayList<String>> allItems = new HashMap<>();
	public HashMap<String, ArrayList<String>> allSbtts = new HashMap<>();
	public HashMap<String, ArrayList<Inventory>> itemsInvs;

	public HashMap<String, Game> games = new HashMap<>();
	public HashMap<String, Integer> playerRank = new HashMap<>();
	public HashMap<Integer, String> versions;

	public ArrayList<String> langs;
	public ArrayList<Age> ages;
	public ArrayList<Level> levels;

	public GameLoop loop;
	public Config config;
	public Logs gameLogs;
	public Logs systemLogs;
	public ManageTeams teams = new ManageTeams();
	public CraftsManager crafts = null;
	public Scores scores;
	public Inventory playersHeads;
	public PlayerInteract playerInteract;
	public PlayerEvents playerEvents;
	
	public boolean paused = false;
	public boolean loaded = false;

	public boolean gameStarted = false;

	public KuffleMain() {
		super();
	}

	public KuffleMain(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();

		gameLogs = new Logs(this.getDataFolder().getPath() + File.separator + "KuffleItemsGamelogs.txt");
		systemLogs = new Logs(this.getDataFolder().getPath() + File.separator + "KuffleItemsSystemlogs.txt");
		
		if (((versions = Utils.loadVersions(this, "versions.json")) == null) ||
				((ages = AgeManager.getAges(FilesConformity.getContent(this, "ages.json"))) == null) ||
				((allItems = ItemManager.getAllItems(ages, FilesConformity.getContent(this, "items_%v.json"), this.getDataFolder())) == null) ||
				((allSbtts = ItemManager.getAllItems(ages, FilesConformity.getContent(this, "sbtt_%v.json"), this.getDataFolder())) == null)) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}

		if ((allRewards = RewardManager.getAllRewards(ages, FilesConformity.getContent(this, "rewards_%v.json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}

		if ((allItemsLangs = LangManager.getAllItemsLang(FilesConformity.getContent(this, "items_lang.json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}

		if ((allLangs = LangManager.getAllItemsLang(FilesConformity.getContent(this, "langs.json"), this.getDataFolder())) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}

		if ((levels = LevelManager.getLevels(FilesConformity.getContent(this, "levels.json"))) == null) {
			this.getPluginLoader().disablePlugin(this);
			return ;
		}

		langs = LangManager.findAllLangs(allItemsLangs);

		config = new Config(this);
		config.setupConfig(this, getConfig());

		crafts = new CraftsManager(this);
		itemsInvs = ItemManager.getItemsInvs(allItems);
		scores = new Scores(this);

		int cnt = 0;

		for (ACrafts item : crafts.getRecipeList()) {
			getServer().addRecipe(item.getRecipe());
			cnt++;
		}

		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "ADD_CRAFTS").replace("%i", "" + cnt));

		playerInteract = new PlayerInteract(this);
		playerEvents = new PlayerEvents(this, this.getDataFolder());

		getServer().getPluginManager().registerEvents(playerEvents, this);
		getServer().getPluginManager().registerEvents(playerInteract, this);
		getServer().getPluginManager().registerEvents(new InventoryListeners(this), this);
		getServer().getPluginManager().registerEvents(new ItemEvent(this), this);
		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "ADD_LISTENERS").replace("%i", "4"));

		getCommand("ki-config").setExecutor(new KuffleConfig(this));
		getCommand("ki-list").setExecutor(new KuffleList(this));
		getCommand("ki-save").setExecutor(new KuffleSave(this, this.getDataFolder()));
		getCommand("ki-load").setExecutor(new KuffleLoad(this, this.getDataFolder()));
		getCommand("ki-start").setExecutor(new KuffleStart(this));
		getCommand("ki-stop").setExecutor(new KuffleStop(this));
		getCommand("ki-pause").setExecutor(new KufflePause(this));
		getCommand("ki-resume").setExecutor(new KuffleResume(this));
		getCommand("ki-ageitems").setExecutor(new KuffleAgeItems(this));
		getCommand("ki-crafts").setExecutor(new KuffleCrafts(this));
		getCommand("ki-lang").setExecutor(new KuffleLang(this));
		getCommand("ki-skip").setExecutor(new KuffleSkip(this));
		getCommand("ki-abandon").setExecutor(new KuffleAbandon(this));
		getCommand("ki-adminskip").setExecutor(new KuffleSkip(this));
		getCommand("ki-validate").setExecutor(new KuffleValidate(this));
		getCommand("ki-validate-age").setExecutor(new KuffleValidate(this));
		getCommand("ki-players").setExecutor(new KufflePlayers(this));
		getCommand("ki-add-during-game").setExecutor(new KuffleAddDuringGame(this));

		getCommand("ki-team-create").setExecutor(new KuffleTeamCreate(this));
		getCommand("ki-team-delete").setExecutor(new KuffleTeamDelete(this));
		getCommand("ki-team-color").setExecutor(new KuffleTeamColor(this));
		getCommand("ki-team-show").setExecutor(new KuffleTeamShow(this));
		getCommand("ki-team-affect-player").setExecutor(new KuffleTeamAffectPlayer(this));
		getCommand("ki-team-remove-player").setExecutor(new KuffleTeamRemovePlayer(this));
		getCommand("ki-team-reset-players").setExecutor(new KuffleTeamResetPlayers(this));
		getCommand("ki-team-random-player").setExecutor(new KuffleTeamRandomPlayer(this));
		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "ADD_CMD").replace("%i", "25"));

		getCommand("ki-config").setTabCompleter(new KuffleConfigTab(this));
		getCommand("ki-list").setTabCompleter(new KuffleListTab(this));
		getCommand("ki-lang").setTabCompleter(new KuffleLangTab(this));
		getCommand("ki-ageitems").setTabCompleter(new KuffleAgeItemsTab(this));
		getCommand("ki-validate").setTabCompleter(new KuffleValidateTab(this));
		getCommand("ki-validate-age").setTabCompleter(new KuffleValidateTab(this));
		getCommand("ki-add-during-game").setTabCompleter(new KuffleAddDuringGameTab(this));
		
		getCommand("ki-team-create").setTabCompleter(new KuffleTeamCreateTab(this));
		getCommand("ki-team-delete").setTabCompleter(new KuffleTeamDeleteTab(this));
		getCommand("ki-team-color").setTabCompleter(new KuffleTeamColorTab(this));
		getCommand("ki-team-show").setTabCompleter(new KuffleTeamShowTab(this));
		getCommand("ki-team-affect-player").setTabCompleter(new KuffleTeamAffectPlayerTab(this));
		getCommand("ki-team-remove-player").setTabCompleter(new KuffleTeamRemovePlayerTab(this));
		getCommand("ki-team-reset-players").setTabCompleter(new KuffleTeamResetPlayersTab(this));
		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "ADD_TAB").replace("%i", "13"));

		loaded = true;

		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "ON"));
	}

	@Override
	public void onDisable() {
		if (loaded) {
			killAll();
		}

		systemLogs.logMsg(this.getName(), Utils.getLangString(this, null, "OFF"));
	}

	public void addRecipe(Recipe recipe) {
		getServer().addRecipe(recipe);
	}

	public void removeRecipe(String name) {
		NamespacedKey n = new NamespacedKey(this, name);

		for (String playerName : games.keySet()) {
			games.get(playerName).getPlayer().undiscoverRecipe(n);
		}

		getServer().removeRecipe(n);
	}

	private void killAll() {
		if (allRewards != null) {
			allRewards.forEach((k, v) -> v.clear());

			allRewards.clear();
		}

		if (allItems != null) {
			allItems.forEach((k, v) -> v.clear());

			allItems.clear();
		}

		if (allItemsLangs != null) {
			allItemsLangs.forEach((k, v) -> v.clear());

			allItemsLangs.clear();
		}

		if (itemsInvs != null) {
			itemsInvs.forEach((k, v) -> v.clear());

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

		if (crafts != null) {
			for (ACrafts craft : crafts.getRecipeList()) {
				removeRecipe(craft.getName());
			}

			crafts.clear();
		}

		if (config != null) {
			config.clear();
		}
	}
	
	public void updatePlayersHead() {
		Inventory newInv = Bukkit.createInventory(null, Utils.getNbInventoryRows(games.size()), "§8Players");
		
		for (String playerName : games.keySet()) {
			newInv.addItem(Utils.getHead(games.get(playerName).getPlayer(), games.get(playerName).getItemDisplay()));
		}
		
		playersHeads.clear();
		playersHeads = newInv;
	}

	public void updatePlayersHeadData(String player, String currentItem) {
		ItemMeta itM;

		if (playersHeads == null) {
			return;
		}

		for (ItemStack item : playersHeads) {
			if (item != null) {
				itM = item.getItemMeta();

				if (itM.getDisplayName().equals(player)) {
					ArrayList<String> lore = new ArrayList<>();

					if (currentItem != null) {
						lore.add(currentItem);
					}

					itM.setLore(lore);
					item.setItemMeta(itM);
				}
			}
		}
	}
}
