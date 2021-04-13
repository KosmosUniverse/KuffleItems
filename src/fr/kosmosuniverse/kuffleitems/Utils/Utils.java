package fr.kosmosuniverse.kuffleitems.Utils;

import java.io.BufferedReader;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
	
	public static ArrayList<String> getAges(String itemContent, String rewardContent) {
		ArrayList<String> itemAge = new ArrayList<String>();
		ArrayList<String> rewardAge = new ArrayList<String>();
		JSONObject tmpObj = new JSONObject();
		JSONParser parser = new JSONParser();
		
		try {
			tmpObj = (JSONObject) parser.parse(itemContent);
			
			for (Object key : tmpObj.keySet()) {
				itemAge.add((String) key);
			}
			
			tmpObj = (JSONObject) parser.parse(rewardContent);
			
			for (Object key : tmpObj.keySet()) {
				rewardAge.add((String) key);
			}
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		if (itemAge.size() != rewardAge.size() || checkAges(itemAge, rewardAge)) {
			return null;
		}
		
		return itemAge;
	}
	
	private static boolean checkAges(ArrayList<String> itemAge, ArrayList<String> rewardAge) {
		for (int cnt = 0; cnt < itemAge.size(); cnt++) {
			if (!itemAge.get(cnt).equals(rewardAge.get(cnt))) {
				return false;
			}
		}
		
		return true;
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
 