package main.fr.kosmosuniverse.kuffleitems.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.Age;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.core.Game;
import main.fr.kosmosuniverse.kuffleitems.core.ItemManager;
import main.fr.kosmosuniverse.kuffleitems.core.LangManager;
import main.fr.kosmosuniverse.kuffleitems.crafts.Template;

public final class Utils {
	private Utils() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String readFileContent(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + System.lineSeparator());
        }

        return sb.toString();
	}

	public static boolean fileExists(String path, String fileName) {
		File tmp = new File(path + File.separator + fileName);

		return tmp.exists();
	}

	public static boolean fileExists(String fileName) {
		File tmp = new File(fileName);;

		return tmp.exists();
	}

	public static boolean fileDelete(String path, String fileName) {
		try {
			Files.delete(Paths.get(path + File.separator + fileName));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static String findFileExistVersion(KuffleMain km, String fileName) {
		String version = getVersion();
		String file = fileName.replace("%v", version);
		int versionNb = findVersionNumber(km, version);

		if (versionNb == -1) {
			return null;
		}

		while (km.getResource(file)  == null && versionNb > 0) {
			versionNb -= 1;
			version = km.versions.get(versionNb);
			file = fileName.replace("%v", version);
		}

		if (km.getResource(file)  == null) {
			return null;
		}

		return file;
	}

	public static int playerLasts(KuffleMain km) {
		int notEnded = 0;

		for (String playerName : km.games.keySet()) {
			if (!km.games.get(playerName).getFinished()) {
				notEnded++;
			}
		}

		return notEnded;
	}

	public static void forceFinish(KuffleMain km, int gameRank) {
		for (String playerName : km.games.keySet()) {
			if (!km.games.get(playerName).getFinished()) {
				km.games.get(playerName).finish(gameRank);
			}
		}
	}

	public static void loadGame(KuffleMain _km, Player player) {
		JSONParser parser = new JSONParser();
		Game tmpGame = new Game(_km, player);

		tmpGame.setup();

		try (FileReader reader = new FileReader(_km.getDataFolder().getPath() + File.separator + player.getName() + ".ki")) {
			JSONObject mainObject = (JSONObject) parser.parse(reader);

			tmpGame.setAge(Integer.parseInt(((Long) mainObject.get("age")).toString()));
			tmpGame.setCurrentItem((String) mainObject.get("current"));
			tmpGame.setTimeShuffle(System.currentTimeMillis() - (Long) mainObject.get("interval"));
			tmpGame.setTime(Integer.parseInt(((Long) mainObject.get("time")).toString()));
			tmpGame.setDead((boolean) mainObject.get("isDead"));
			tmpGame.setFinished((boolean) mainObject.get("finished"));
			tmpGame.setLose((boolean) mainObject.get("lose"));
			tmpGame.setItemCount(Integer.parseInt(((Long) mainObject.get("itemCount")).toString()));
			tmpGame.setTeamName((String) mainObject.get("teamName"));
			tmpGame.setAlreadyGot((JSONArray) mainObject.get("alreadyGot"));
			tmpGame.setSpawnLoc((JSONObject) mainObject.get("spawn"));
			tmpGame.setDeathLoc((JSONObject) mainObject.get("death"));
			tmpGame.setSameIdx(Integer.parseInt(((Long) mainObject.get("sameIdx")).toString()));
			tmpGame.setTimes((JSONObject) mainObject.get("times"));
			tmpGame.setDeathCount(Integer.parseInt(mainObject.get("deathCount").toString()));
			tmpGame.setSkipCount(Integer.parseInt(mainObject.get("skipCount").toString()));

			if (fileExists(_km.getDataFolder().getPath(), player.getName() + ".yml")) {
				tmpGame.loadInventory();
				fileDelete(_km.getDataFolder().getPath(), player.getName() + ".yml");
			}

			if (tmpGame.getDead()) {
				_km.playerEvents.teleportAutoBack(tmpGame);
			}

			_km.games.put(player.getName(), tmpGame);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		return ;
	}
	
	public static int getNbInventoryRows(int quantity) {
		int rows = quantity / 9;
		
		if (quantity % 9 == 0) {
			rows = rows * 9;
		} else {
			rows = (rows + 1) * 9;
		}
		
		return rows;
	}

	public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();

        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        return item;
    }
	
	public static ItemStack getHead(Player player, String currentItem) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        
        if (currentItem != null) {        
	        ItemMeta itM = item.getItemMeta();
	        
	        ArrayList<String> lore = new ArrayList<>();
	        lore.add(currentItem);
	
			itM.setLore(lore);
			item.setItemMeta(itM);
		}
		
		SkullMeta skull = (SkullMeta) item.getItemMeta();

        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);

        return item;
    }

	public static String getVersion() {
		String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

		version = version.split("v")[1];
		version = version.split("_")[0] + "." + version.split("_")[1];

		return version;
	}

	public static ChatColor findChatColor(String color) {
		for (ChatColor item : ChatColor.values()) {
			if (item.name().equals(color)) {
				return item;
			}
		}

		return null;
	}

	public static ArrayList<Player> getPlayerList(HashMap<String, Game> games) {
		ArrayList<Player> players = new ArrayList<>();

		for (String playerName : games.keySet()) {
			players.add(games.get(playerName).getPlayer());
		}

		return players;
	}

	public static ArrayList<String> getPlayerNames(HashMap<String, Game> games) {
		ArrayList<String> players = new ArrayList<>();

		for (String playerName : games.keySet()) {
			players.add(playerName);
		}

		return players;
	}

	public static World findNormalWorld() {
		for (World w : Bukkit.getWorlds()) {
			if (!w.getName().contains("nether") && !w.getName().contains("the_end")) {
				return w;
			}
		}

		return null;
	}

	public static boolean checkEffect(String effect) {
		for (PotionEffectType potion : PotionEffectType.values()) {
			if (potion.getName().equalsIgnoreCase(effect)) {
				return true;
			}
		}

		return false;
	}

	public static void setupTemplates(KuffleMain km) {
		ArrayList<Template> templates = new ArrayList<>();

		for (int i = 0; i < km.config.getMaxAges(); i++)  {
			String name = AgeManager.getAgeByNumber(km.ages, i).name;

			name = name.replace("_Age", "");
			templates.add(new Template(km, name, getMaterials(AgeManager.getAgeByNumber(km.ages, i).name, km)));
		}

		for (Template t : templates) {
			km.crafts.addCraft(t);
			km.addRecipe(t.getRecipe());
		}
	}

	public static void removeTemplates(KuffleMain km) {
		for (int i = 0; i < km.config.getMaxAges(); i++)  {
			String name = AgeManager.getAgeByNumber(km.ages, i).name;
			name = name.replace("_Age", "");
			name = name + "Template";

			km.removeRecipe(name);
			km.crafts.removeCraft(name);
		}
	}

	public static ArrayList<Material> getMaterials(String age, KuffleMain km) {
		ArrayList<Material> compose = new ArrayList<>();
		ArrayList<String> done = new ArrayList<>();

		for (int cnt = 0; cnt < km.config.getSBTTAmount(); cnt++) {
			done.add(ItemManager.newItem(done, km.allSbtts.get(age)));
		}

		for (String item : done) {
			compose.add(Material.matchMaterial(item));
		}

		done.clear();
		return compose;
	}

	public static void reloadTemplate(KuffleMain km, String name, String age) {
		km.crafts.removeCraft(name);
		km.removeRecipe(name);

		String tmp = age;

		tmp = tmp.replace("_Age", "");

		Template t = new Template(km, tmp, getMaterials(age, km));

		km.crafts.addCraft(t);
		km.addRecipe(t.getRecipe());

		km.games.forEach((playerName, game) -> {
			game.getPlayer().discoverRecipe(new NamespacedKey(km, t.getName()));
		});
	}

	public static String getTimeFromSec(long sec) {
		StringBuilder sb = new StringBuilder();

		if (sec != 0 && sec >= 3600) {
			sb.append(sec / 3600);
			sb.append("h");

			sec = sec % 3600;
		}

		if (sec != 0 && sec >= 60) {
			sb.append(sec / 60);
			sb.append("m");

			sec = sec % 60;
		}

		sb.append(sec);
		sb.append("s");

		return sb.toString();
	}

	public static boolean compareItems(ItemStack first, ItemStack second, boolean hasItemMeta, boolean hasDisplayName, boolean hasLore) {
		if ((first.getType() != second.getType()) || (first.hasItemMeta() != second.hasItemMeta()) || (first.hasItemMeta() != hasItemMeta)) {
			return false;
		}

		if (!hasItemMeta) {
			return true;
		}

		ItemMeta firstMeta = first.getItemMeta();
		ItemMeta secondMeta = second.getItemMeta();

		if (firstMeta.hasDisplayName() != secondMeta.hasDisplayName()) {
			return false;
		}

		if (firstMeta.hasDisplayName() != hasDisplayName) {
			return false;
		}

		if (!hasDisplayName) {
			return true;
		}

		if (!firstMeta.getDisplayName().equals(secondMeta.getDisplayName())) {
			return false;
		}

		if (firstMeta.hasLore() != secondMeta.hasLore()) {
			return false;
		}

		if (firstMeta.hasLore() != hasLore) {
			return false;
		}

		if (!hasLore) {
			return true;
		}

		ArrayList<String> firstLore = (ArrayList<String>) firstMeta.getLore();
		ArrayList<String> secondLore = (ArrayList<String>) secondMeta.getLore();

		if (firstLore.size() != secondLore.size()) {
			return false;
		}

		for (int i = 0; i < firstLore.size(); i++) {
			if (!firstLore.get(i).equals(secondLore.get(i))) {
				return false;
			}
		}

		return true;
	}

	public static String getLangString(KuffleMain km, String player, String tag) {
		if (km.gameStarted && player != null && km.games.containsKey(player)) {
			return (LangManager.findDisplay(km.allLangs, tag, km.games.get(player).getLang()));
		} else {
			return (LangManager.findDisplay(km.allLangs, tag, km.config.getLang()));
		}
	}

	public static HashMap<Integer, String> loadVersions(KuffleMain km, String file) {
		HashMap<Integer, String> versions = null;

		try {
			InputStream in = km.getResource(file);
			String content = Utils.readFileContent(in);

			JSONParser parser = new JSONParser();
			JSONObject result = ((JSONObject) parser.parse(content));

			in.close();

			versions = new HashMap<>();

			for (Object key : result.keySet()) {
				versions.put(Integer.parseInt(result.get(key).toString()), (String) key);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

		return versions;
	}

	public static int findVersionNumber(KuffleMain km, String version) {
		for (int key : km.versions.keySet()) {
			if (km.versions.get(key).equals(version)) {
				return key;
			}
		}

		return -1;
	}

	public static void printGameEnd(KuffleMain km) {
		km.games.forEach((playerName, game) -> {
			logPlayer(km, playerName);

			km.games.forEach((playerToSend, gameToSend) -> {
				printPlayer(km, playerName, playerToSend);
			});
		});
	}

	public static void printPlayer(KuffleMain km, String playerName, String toSend) {
		long total = 0;
		Game game = km.games.get(playerName);

		km.games.get(toSend).getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + playerName + ChatColor.RESET + ":");
		km.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(km, toSend, "DEATH_COUNT").replace("%i", "" + ChatColor.RESET + game.getDeathCount()));
		km.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(km, toSend, "SKIP_COUNT").replace("%i", "" + ChatColor.RESET + game.getSkipCount()));
		km.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(km, toSend, "TEMPLATE_COUNT").replace("%i", "" + ChatColor.RESET + game.getSbttCount()));
		km.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(km, toSend, "TIME_TAB"));

		for (int i = 0; i < km.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(km.ages, i);

			if (game.getAgeTime(age.name) == -1) {
				km.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(km, toSend, "FINISH_ABANDON").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.RESET));
			} else {
				km.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(km, toSend, "FINISH_TIME").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(game.getAgeTime(age.name) / 1000)));
				total += game.getAgeTime(age.name) / 1000;
			}
		}

		km.games.get(toSend).getPlayer().sendMessage(ChatColor.BLUE + Utils.getLangString(km, toSend, "FINISH_TOTAL").replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)));

	}

	public static void logPlayer(KuffleMain km, String playerName) {
		long total = 0;
		Game game = km.games.get(playerName);
		StringBuilder sb = new StringBuilder();

		sb.append("\n");
		sb.append(ChatColor.GOLD + "" + ChatColor.BOLD + playerName + ChatColor.RESET + ":" + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(km, playerName, "DEATH_COUNT").replace("%i", "" + ChatColor.RESET + game.getDeathCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(km, playerName, "SKIP_COUNT").replace("%i", "" + ChatColor.RESET + game.getSkipCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(km, playerName, "TEMPLATE_COUNT").replace("%i", "" + ChatColor.RESET + game.getSbttCount()) + "\n");
		sb.append(ChatColor.BLUE + Utils.getLangString(km, playerName, "TIME_TAB") + "\n");

		for (int i = 0; i < km.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(km.ages, i);

			if (game.getAgeTime(age.name) == -1) {
				sb.append(Utils.getLangString(km, playerName, "FINISH_ABANDON").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.RESET) + "\n");
			} else {
				sb.append(Utils.getLangString(km, playerName, "FINISH_TIME").replace("%s", age.color + age.name.replace("_Age", "") + ChatColor.BLUE).replace("%t", ChatColor.RESET + Utils.getTimeFromSec(game.getAgeTime(age.name) / 1000)) + "\n");
				total += game.getAgeTime(age.name) / 1000;
			}
		}

		sb.append(ChatColor.BLUE + Utils.getLangString(km, playerName, "FINISH_TOTAL").replace("%t", ChatColor.RESET + Utils.getTimeFromSec(total)) + "\n");

		km.gameLogs.logSystemMsg(sb.toString());
	}
}
