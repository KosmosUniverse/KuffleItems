package fr.kosmosuniverse.kuffleitems.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.Age;
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.ItemManager;
import fr.kosmosuniverse.kuffleitems.Crafts.Template;

public class Utils {
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
		File tmp = null;
		
		if (path.contains("\\")) {
			tmp = new File(path + "\\" + fileName);
		} else {
			tmp = new File(path + "/" + fileName);
		}
		
		return tmp.exists();
	}
	
	public static boolean fileExists(String fileName) {
		File tmp = null;
		
		tmp = new File(fileName);
		
		return tmp.exists();
	}
	
	public static boolean fileDelete(String path, String fileName) {
		File tmp = null;
		
		if (path.contains("\\")) {
			tmp = new File(path + "\\" + fileName);
		} else {
			tmp = new File(path + "/" + fileName);
		}
		
		return tmp.delete();
	}
	
	public static int playeLasts(KuffleMain km) {
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
		FileReader reader = null;
		JSONParser parser = new JSONParser();
		Game tmpGame = new Game(_km, player);
		
		tmpGame.setup();
		
		try {
			if (_km.getDataFolder().getPath().contains("\\")) {
				reader = new FileReader(_km.getDataFolder().getPath() + "\\" + player.getName() + ".ki");
			} else {
				reader = new FileReader(_km.getDataFolder().getPath() + "/" + player.getName() + ".ki");
			}
			
			JSONObject mainObject = (JSONObject) parser.parse(reader);
			
			tmpGame.setAge(Integer.parseInt(((Long) mainObject.get("age")).toString()));
			tmpGame.setCurrentItem((String) mainObject.get("current"));
			tmpGame.setTimeShuffle(System.currentTimeMillis() - (Long) mainObject.get("interval"));
			tmpGame.setTime(Integer.parseInt(((Long) mainObject.get("time")).toString()));
			tmpGame.setDead((boolean) mainObject.get("isDead"));
			tmpGame.setItemCount(Integer.parseInt(((Long) mainObject.get("itemCount")).toString()));
			tmpGame.setTeamName((String) mainObject.get("teamName"));
			tmpGame.setAlreadyGot((JSONArray) mainObject.get("alreadyGot"));
			tmpGame.setSpawnLoc((JSONObject) mainObject.get("spawn"));
			tmpGame.setDeathLoc((JSONObject) mainObject.get("death"));
			tmpGame.setSameIdx(Integer.parseInt(((Long) mainObject.get("sameIdx")).toString()));
			
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
	
	public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
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
		ArrayList<Player> players = new ArrayList<Player>();
		
		for (String playerName : games.keySet()) {
			players.add(games.get(playerName).getPlayer());
		}
		
		return players;
	}
	
	public static ArrayList<String> getPlayerNames(HashMap<String, Game> games) {
		ArrayList<String> players = new ArrayList<String>();
		
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
		ArrayList<Template> templates = new ArrayList<Template>();
		
		for (Age age : km.ages) {
			if (age.number != -1) {
				String name = age.name;
				
				name = name.replace("_Age", "");
				templates.add(new Template(km, name, getNineMaterials(age.name, km)));
			}
		}
		
		for (Template t : templates) {
			km.crafts.addCraft(t);
			km.addRecipe(t.getRecipe());
		}
	}
	
	public static void removeTemplates(KuffleMain km) {
		for (Age age : km.ages) {
			if (age.number != -1) {
				String name = age.name;
				name = name.replace("_Age", "");
				name = name + "Template";
				
				km.removeRecipe(name);
				km.crafts.removeCraft(name);
			}
		}
	}
	
	public static ArrayList<Material> getNineMaterials(String age, KuffleMain km) {
		ArrayList<Material> compose = new ArrayList<Material>();
		ArrayList<String> done = new ArrayList<String>();
		
		for (int cnt = 0; cnt < 9; cnt++) {
			done.add(ItemManager.newItem(done, km.allItems.get(age)));
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
		
		Template t = new Template(km, age, getNineMaterials(age, km));
		
		km.crafts.addCraft(t);
		km.addRecipe(t.getRecipe());
	}
}
 