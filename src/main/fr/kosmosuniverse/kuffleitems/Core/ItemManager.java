package main.fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.Utils.ItemUtils;
import main.fr.kosmosuniverse.kuffleitems.Utils.Pair;

public class ItemManager {
	private ItemManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static HashMap<String, ArrayList<String>> getAllItems(ArrayList<Age> ages, String itemsContent, File dataFolder) {
		HashMap<String, ArrayList<String>> finalMap = new HashMap<>();
		
		int max = AgeManager.getAgeMaxNumber(ages);

		try {
			for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
				finalMap.put(AgeManager.getAgeByNumber(ages, ageCnt).name, getAgeItems(AgeManager.getAgeByNumber(ages, ageCnt).name, itemsContent, dataFolder));
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
			finalMap = null;
		}
		
		return finalMap;
	}
	
	public static synchronized ArrayList<String> getAgeItems(String age, String itemsContent, File dataFolder) throws IOException, ParseException {
		ArrayList<String> finalList = new ArrayList<>();
		JSONObject items = new JSONObject();
		JSONParser jsonParser = new JSONParser();

		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "logs.txt")) {
			items = (JSONObject) jsonParser.parse(itemsContent);
			
			JSONObject ageObject = new JSONObject();
			JSONArray agePart = new JSONArray();
			
			ageObject = (JSONObject) items.get(age);
						
			for (Object k : ageObject.keySet()) {
				agePart = (JSONArray) ageObject.get(k);
				for (int j = 0; j < agePart.size(); j++) {
					finalList.add((String) agePart.get(j));
					if (Material.matchMaterial((String) agePart.get(j)) == null) {
						writer.append((String) agePart.get(j));
					}
				}
			}
		
		}
		
		return finalList;
	}

	public static synchronized String newItem(ArrayList<String> done, ArrayList<String> allAgeItems) {	
		ArrayList<String> finalList = new ArrayList<>();
		
		if (allAgeItems == null) {
			return null;
		}
		
		for (String s : allAgeItems) {
			if (!done.contains(s)) {
				finalList.add(s);
			}
		}
		
		if (finalList.size() == 1) {
			return finalList.get(0);
		}
		
		return finalList.get(ThreadLocalRandom.current().nextInt(finalList.size()));
	}
	
	public static synchronized Pair nextItem(ArrayList<String> done, ArrayList<String> allAgeItems, int sameIdx) {	
		String testItem = allAgeItems.get(sameIdx);
		
		while (done.contains(testItem)) {
			sameIdx++;
			testItem = allAgeItems.get(sameIdx);
		}
		
		return (new Pair(sameIdx, testItem));
	}
	
	public static HashMap<String, ArrayList<Inventory>> getItemsInvs(HashMap<String, ArrayList<String>> allItems) {
		HashMap<String, ArrayList<Inventory>> invs = new HashMap<>();

		allItems.forEach((k, v) -> invs.put(k, getAgeInvs(k, v)));

		return invs;
	}

	public static ArrayList<Inventory> getAgeInvs(String age, ArrayList<String> ageItems) {
		ArrayList<Inventory> invs = new ArrayList<>();
		Inventory inv;
		int invCnt = 0;
		int nbInv = 1;
		
		ItemStack bluePane = ItemUtils.itemMakerName(Material.BLUE_STAINED_GLASS_PANE, 1, "Next ->");
		ItemStack limePane = ItemUtils.itemMakerName(Material.LIME_STAINED_GLASS_PANE, 1, " ");
		ItemStack redPane = ItemUtils.itemMakerName(Material.RED_STAINED_GLASS_PANE, 1, "<- Previous");
		
		if (ageItems.size() > 45) {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Items Tab 1");
		} else {
			inv = Bukkit.createInventory(null, 54, "§8" + age + " Items");
		}
		
		for (; invCnt < 9; invCnt++) {
			if (invCnt == 8) {
				inv.setItem(invCnt, bluePane);
			} else {
				inv.setItem(invCnt, limePane);
			}
		}
		
		for (String item : ageItems) {
			try {
				inv.setItem(invCnt, getMaterial(item));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
			
			if (invCnt == 53) {
				invCnt = 0;
				invs.add(inv);
				nbInv++;
				inv = Bukkit.createInventory(null, 54, "§8" + age + " Items Tab " + nbInv);
				
				for (; invCnt < 9; invCnt++) {
					if (invCnt == 0) {
						inv.setItem(invCnt, redPane);
					} else if (invCnt == 8) {
						inv.setItem(invCnt, bluePane);
					} else {
						inv.setItem(invCnt, limePane);
					}
				}
			} else {
				invCnt++;
			}
		}
		
		inv.setItem(8, limePane);
		
		invs.add(inv);
		
		return invs;
	}
	
	private static ItemStack getMaterial(String item) {
		for (Material mat : Material.values()) {
			if (mat.getKey().toString().split(":")[1].equals(item) && mat.isItem()) {
				return new ItemStack(mat);
			}
		}
		
		ItemStack retItem;
		
		for (Material mat : Material.values()) {
			if (mat.getKey().toString().split(":")[1].contains(item) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, item);

				return retItem;
			}
		}
		
		for (Material mat : Material.values()) {
			if (item.contains(mat.getKey().toString().split(":")[1]) && mat.isItem()) {
				retItem = ItemUtils.itemMakerName(mat, 1, item);

				return retItem;
			}
		}
		
		return null;
	}
}
