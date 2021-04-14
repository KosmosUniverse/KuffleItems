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
			tmpGame.setItemCount(Integer.parseInt(((Long) mainObject.get("itemCount")).toString()));
			//tmpGame.setSameIdx(Integer.parseInt(mainObject.get("sameIdx").toString()));
			tmpGame.setTeamName((String) mainObject.get("teamName"));
			tmpGame.setAlreadyGot((JSONArray) mainObject.get("alreadyGot"));
			tmpGame.setSpawnLoc((JSONObject) mainObject.get("spawn"));
			tmpGame.setDeathLoc((JSONObject) mainObject.get("death"));

			
			_km.games.put(player.getName(), tmpGame);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
		return ;
	}
	
	public static ArrayList<String> getAges(String itemContent) {
		HashMap<Integer, String> itemAge = new HashMap<Integer, String>();
		ArrayList<String> finalList = new ArrayList<String>();
		JSONObject tmpObj = new JSONObject();
		JSONParser parser = new JSONParser();
		
		try {
			tmpObj = (JSONObject) parser.parse(itemContent);
			tmpObj = (JSONObject) tmpObj.get("Order");
			
			for (Object key : tmpObj.keySet()) {
				itemAge.put(Integer.parseInt(key.toString()), (String) tmpObj.get(key));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		int max = getMaxValue(itemAge);
		
		for (int i = 1; i <= max; i++) {
			finalList.add(itemAge.get(i));
		}
		
		return finalList;
	}
	
	private static int getMaxValue(HashMap<Integer, String> itemAge) {
		int max = 0;
		
		for (int age : itemAge.keySet()) {
			if (age > max) {
				max = age;
			}
		}
		
		return max;
	}
	
	public static ItemStack getHead(Player player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        
        skull.setDisplayName(player.getName());
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);
        
        return item;
    }
	
	public static ChatColor getColor(int age) {
		switch (age) {
		case 0:
			return (ChatColor.RED);
		case 1:
			return (ChatColor.GOLD);
		case 2:
			return (ChatColor.YELLOW);
		case 3:
			return (ChatColor.GREEN);
		case 4:
			return (ChatColor.DARK_GREEN);
		case 5:
			return (ChatColor.DARK_BLUE);
		default:
			return (ChatColor.DARK_PURPLE);
		}
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
	
	/*public static ArrayList<Location> getAllPlayerLocation(ArrayList<GameTask> games) {
		ArrayList<Location> locs = new ArrayList<Location>();
		
		for (GameTask gt : games) {
			locs.add(gt.getSpawnLoc());
		}
		
		return locs;
	}*/
	
	/*public static Player getPlayerInList(ArrayList<GameTask> games, String name) {
		for (GameTask gt : games) {
			if (gt.getPlayer().getDisplayName().equals(name)) {
				return gt.getPlayer();
			}
		}
		
		return null;
	}*/
}
 