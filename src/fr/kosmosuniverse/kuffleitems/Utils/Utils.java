package fr.kosmosuniverse.kuffleitems.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
import fr.kosmosuniverse.kuffleitems.Core.Game;
import fr.kosmosuniverse.kuffleitems.Core.Level;

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
			tmpGame.setDeathTime((Long) mainObject.get("deathTime"), (Long) mainObject.get("minTime"), (Long) mainObject.get("maxTime"));
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
	
	public static long minSecondsWithLevel(Level level) {
		Random r = new Random();
		
		if (level == Level.EASY) {
			return 3;
		} else if (level == Level.NORMAL) {
			return 6;
		} else if (level == Level.EXPERT) {
			return (6 + (r.nextInt(9) + 1));
		}
		
		return -1;
	}
	
	public static long maxSecondsWithLevel(Level level) {
		if (level == Level.EASY) {
			return 40;
		} else if (level == Level.NORMAL) {
			return 30;
		} else if (level == Level.EXPERT) {
			return 20;
		}
		
		return -1;
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
}
 