package main.fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.Utils.Pair;

public class ItemManager {
	public static HashMap<String, ArrayList<String>> getAllItems(ArrayList<Age> ages, String itemsContent, File dataFolder) {
		HashMap<String, ArrayList<String>> finalMap = new HashMap<String, ArrayList<String>>();
		
		int max = AgeManager.getAgeMaxNumber(ages);
		
		for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
			finalMap.put(AgeManager.getAgeByNumber(ages, ageCnt).name, getAgeBlocks(AgeManager.getAgeByNumber(ages, ageCnt).name, itemsContent, dataFolder));
		}
		
		return finalMap;
	}
	
	public static synchronized ArrayList<String> getAgeBlocks(String age, String itemsContent, File dataFolder) {
		ArrayList<String> finalList = new ArrayList<String>();
		JSONObject blocks = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		FileWriter writer = null;
		
		try {
			if (dataFolder.getPath().contains("\\")) {
				writer = new FileWriter(dataFolder.getPath() + "\\logs.txt");
			} else {
				writer = new FileWriter(dataFolder.getPath() + "/logs.txt");
			}
			
			blocks = (JSONObject) jsonParser.parse(itemsContent);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject ageObject = new JSONObject();
		JSONArray agePart = new JSONArray();
		
		ageObject = (JSONObject) blocks.get(age);
					
		for (Object k : ageObject.keySet()) {
			agePart = (JSONArray) ageObject.get(k);
			for (int j = 0; j < agePart.size(); j++) {
				finalList.add((String) agePart.get(j));
				if (Material.matchMaterial((String) agePart.get(j)) == null) {
					try {
						writer.append((String) agePart.get(j));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return finalList;
	}

	public static synchronized String newItem(ArrayList<String> done, ArrayList<String> allAgeItems) {	
		ArrayList<String> finalList = new ArrayList<String>();
		
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
		
		Random r = new Random();
		
		return finalList.get(r.nextInt(finalList.size()));
	}
	
	public static synchronized Pair nextItem(ArrayList<String> done, ArrayList<String> allAgeBlocks, int sameIdx) {	
		String testBlock = allAgeBlocks.get(sameIdx);
		
		while (done.contains(testBlock)) {
			sameIdx++;
			testBlock = allAgeBlocks.get(sameIdx);
		}
		
		return (new Pair(sameIdx, testBlock));
	}
	
	public static HashMap<String, ArrayList<Inventory>> getItemsInvs(HashMap<String, ArrayList<String>> allItems) {
		HashMap<String, ArrayList<Inventory>> invs = new HashMap<String, ArrayList<Inventory>>();

		for (String age : allItems.keySet()) {
			invs.put(age, getAgeInvs(age, allItems.get(age)));
		}
		
		return invs;
	}

	public static ArrayList<Inventory> getAgeInvs(String age, ArrayList<String> ageItems) {
		ArrayList<Inventory> invs = new ArrayList<Inventory>();
		Inventory inv;
		int invCnt = 0;
		int nbInv = 1;
		
		ItemStack limePane = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
		ItemStack redPane = new ItemStack(Material.RED_STAINED_GLASS_PANE);
		ItemStack bluePane = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
		ItemMeta itM =  limePane.getItemMeta();
		
		itM = limePane.getItemMeta();
		itM.setDisplayName(" ");
		limePane.setItemMeta(itM);
		itM = redPane.getItemMeta();
		itM.setDisplayName("<- Previous");
		redPane.setItemMeta(itM);
		itM = bluePane.getItemMeta();
		itM.setDisplayName("Next ->");
		bluePane.setItemMeta(itM);
		
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
				retItem = new ItemStack(mat);

				ItemMeta itM = retItem.getItemMeta();
				
				itM.setDisplayName(item);
				retItem.setItemMeta(itM);
				
				return retItem;
			}
		}
		
		for (Material mat : Material.values()) {
			if (item.contains(mat.getKey().toString().split(":")[1]) && mat.isItem()) {
				retItem = new ItemStack(mat);

				ItemMeta itM = retItem.getItemMeta();
				
				itM.setDisplayName(item);
				retItem.setItemMeta(itM);
				
				return retItem;
			}
		}
		
		return null;
	}
}
