package main.fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LangManager {
	private LangManager() {
		throw new IllegalStateException("Utility class");
	}
	
	public static HashMap<String, HashMap<String, String>> getAllItemsLang(String JSONContent, File dataFolder) {
		HashMap<String, HashMap<String, String>> finalMap = new HashMap<String, HashMap<String, String>>();
		JSONObject langages = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		
		try (FileWriter writer = new FileWriter(dataFolder.getPath() + File.separator + "logs.txt", true)) {
			langages = (JSONObject) jsonParser.parse(JSONContent);

			for (Iterator<?> itItem = langages.keySet().iterator(); itItem.hasNext();) {
				String keyItem = (String) itItem.next();
				JSONObject item = (JSONObject) langages.get(keyItem);

				writer.append(keyItem);

				HashMap<String, String> itemLangs = new HashMap<String, String>();
				
				for (Iterator<?> itLang = item.keySet().iterator(); itLang.hasNext();) {
					String keyLang = (String) itLang.next();
					String value = (String) item.get(keyLang);
					
					writer.append("\t" + keyLang + " :\n" + value + "\n");
					itemLangs.put(keyLang, value);
				}
				
				finalMap.put(keyItem, itemLangs);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return finalMap;
	}
	
	public static String findDisplay(HashMap<String, HashMap<String, String>> allLangs, String item, String lang) {
		String res = null;
		
		if (allLangs.containsKey(item)) {
			HashMap<String, String> langs = allLangs.get(item);
			
			if (langs.containsKey(lang) && !langs.get(lang).isEmpty()) {
				return langs.get(lang);
			} else {
				return item;
			}
		} else {
			res = item;
		}
			
		return res;
	}
	
	public static ArrayList<String> findAllLangs(HashMap<String, HashMap<String, String>> allitemsLangs) {
		ArrayList<String> allLangs = new ArrayList<String>();
		
		HashMap<String, String> first = allitemsLangs.get(allitemsLangs.keySet().toArray()[0]);
		
		for (String key : first.keySet()) {
			allLangs.add(key);
		}
		
		return allLangs;
	}
}
