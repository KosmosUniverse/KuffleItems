package main.fr.kosmosuniverse.kuffleitems.Utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.Core.RewardManager;

public class FilesConformity {
	private FilesConformity() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String getContent(KuffleMain km, String file) {
		String content;
		
		if (file.contains("%v")) {
			file = Utils.findFileExistVersion(km, file);
			
			if (file == null) {
				return null;
			}
		}
		
		content = getFromFile(km, file);
		
		if (content == null || !checkContent(km, file, content)) {
			content = getFromResource(km, file);
			km.systemLogs.logMsg(km.getName(), "Load " + file + " from Resource");
		} else {
			km.systemLogs.logMsg(km.getName(), "Load " + file + " from File");
		}
		
		return content;
	}
	
	private static String getFromFile(KuffleMain km, String file) {
		if (fileExists(km, km.getDataFolder().getPath(), file)) {
			try (FileReader reader = new FileReader(km.getDataFolder().getPath() + File.separator + km.getDescription().getVersion() + File.separator  + file)) {
				JSONParser parser = new JSONParser();
				
				String result = ((JSONObject) parser.parse(reader)).toString();

				return result;
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		} else {
			createFromResource(km, file);
		}
		
		return null;
	}
	
	private static void createFromResource(KuffleMain km, String fileName) {
		String path = km.getDataFolder().getPath() + File.separator + km.getDescription().getVersion();
		directoryExists(path);		
		
		try (FileWriter writer = new FileWriter(path + File.separator + fileName)) {
			InputStream in = km.getResource(fileName);

			writer.write(Utils.readFileContent(in));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getFromResource(KuffleMain km, String file) {
		try {
			InputStream in = km.getResource(file);
			String result = Utils.readFileContent(in);
			
			JSONParser parser = new JSONParser();
			
			JSONObject mainObject = (JSONObject) parser.parse(result);
			
			result = mainObject.toString();
			
			in.close();
			mainObject.clear();
			
			return result;
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static boolean checkContent(KuffleMain km, String file, String content) {
		if (content.equals(getFromResource(km, file))) {
			return false;
		}
		
		if (file.equals("ages.json")) {
			return ageConformity(km, content);
		} else if (file.equals("items_lang.json")) {
			return itemLangConformity(km, content);
		} else if (file.equals("items_" + Utils.getVersion() + ".json") ||
				file.equals("sbtt_" + Utils.getVersion() + ".json")) {
			return itemsConformity(km, content);
		} else if (file.equals("rewards_" + Utils.getVersion() + ".json")) {
			return rewardsConformity(km, content);
		} else if (file.equals("levels.json")) {
			return levelsConformity(km, content);
		} else if (file.equals("langs.json")) {
			return langConformity(km, content);
		}
		
		return false;
	}
	
	private static boolean ageConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				
				if (!((String) key).endsWith("_Age")) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does not finish by '_Age'.");
					jsonObj.clear();
					return false;
				}
				
				JSONObject ageObj = (JSONObject) jsonObj.get(key);
				
				if (!ageObj.containsKey("Number")) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'Number' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				} else if (!ageObj.containsKey("TextColor")) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'TextColor' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				} else if (!ageObj.containsKey("BoxColor")) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'BoxColor' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				}
				
				@SuppressWarnings("unused")
				int number = Integer.parseInt(ageObj.get("Number").toString());
				String color = (String) ageObj.get("TextColor");
				String box = (String) ageObj.get("BoxColor") + "_SHULKER_BOX";
				
				if (ChatColor.valueOf(color) == null) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] color [" + color + "] is not in ChatColor Enum.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				}
				
				if (Material.matchMaterial(box) == null) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] box [" + box + "] is not in Material Enum.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				}
				
				ageObj.clear();
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean itemLangConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			ArrayList<String> langs = null;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				
				if (Material.matchMaterial((String) key) == null) {
					km.systemLogs.logSystemMsg("Material [" + (String) key + "] does not exist.");
					jsonObj.clear();
					return false;
				}
				
				JSONObject materialObj = (JSONObject) jsonObj.get(key);
				
				if (langs == null) {
					langs = new ArrayList<>();
					
					for (Object keyLang : materialObj.keySet()) {
						langs.add((String) keyLang);
					}
				} else {
					for (Object keyLang : materialObj.keySet()) {
						if (!langs.contains((String) keyLang)) {
							km.systemLogs.logSystemMsg("Lang [" + (String) keyLang + "] is not everywhere in lang file.");
							langs.clear();
							materialObj.clear();
							return false;
						}
					}
				}
				
				materialObj.clear();
			}
			
			if (langs != null) {
				langs.clear();
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean langConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			ArrayList<String> langs = null;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject phraseObj = (JSONObject) jsonObj.get(key);
				
				if (langs == null) {
					langs = new ArrayList<>();
					
					for (Object keyLang : phraseObj.keySet()) {
						langs.add((String) keyLang);
					}
				} else {
					for (Object keyLang : phraseObj.keySet()) {
						if (!langs.contains((String) keyLang)) {
							km.systemLogs.logSystemMsg("Lang [" + (String) keyLang + "] is not everywhere in lang file.");
							langs.clear();
							phraseObj.clear();
							return false;
						}
					}
				}
				
				phraseObj.clear();
			}
			
			if (langs != null) {
				langs.clear();
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean itemsConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (AgeManager.ageExists(km.ages, (String) key)) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does exist in ages.json.");
					return false;
				}
				
				JSONObject categories = (JSONObject) jsonObj.get(key);
				
				for (Object category : categories.keySet()) {
					JSONArray array = (JSONArray) categories.get(category);
					array.clear();
				}
				
				categories.clear();
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean rewardsConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (AgeManager.ageExists(km.ages, (String) key)) {
					km.systemLogs.logSystemMsg("Age [" + (String) key + "] does exist in ages.json.");
					return false;
				}
				
				JSONObject rewards = (JSONObject) jsonObj.get(key);
				
				for (Object reward : rewards.keySet()) {
					if (Material.matchMaterial((String) reward) == null) {
						km.systemLogs.logSystemMsg("Material [" + (String) reward + "] is not in Material Enum.");
						rewards.clear();
						jsonObj.clear();
						
						return false;
					}
					
					JSONObject itemObj = (JSONObject) rewards.get(reward);

					if (!itemObj.containsKey("Amount")) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Amount' Object.");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					} else if (!itemObj.containsKey("Enchant")) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Enchant' Object.");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					} else if (!itemObj.containsKey("Level")) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Level' Object.");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					} else if (!itemObj.containsKey("Effect")) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Effect' Object.");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					}
					
					@SuppressWarnings("unused")
					int number = Integer.parseInt(itemObj.get("Amount").toString());
					number = Integer.parseInt(itemObj.get("Level").toString());
					
					String enchants = (String) itemObj.get("Enchant");
					String effects = (String) itemObj.get("Effect");
					
					if (enchants.contains(",")) {
						for (String enchant : enchants.split(",")) {
							if (RewardManager.getEnchantment(enchant) == null) {
								km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + enchant + "].");
								itemObj.clear();
								rewards.clear();
								jsonObj.clear();
								return false;
							}
						}
					} else if (RewardManager.getEnchantment(enchants) == null) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + enchants + "].");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					}
					
					if (effects.contains(",")) {
						for (String effect : effects.split(",")) {
							if (!Utils.checkEffect(effect)) {
								km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown effect : [" + effect + "].");
								
								itemObj.clear();
								rewards.clear();
								jsonObj.clear();
								return false;
							}
						}
					} else if (!Utils.checkEffect(effects)) {
						km.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + effects + "].");
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					}
					
					itemObj.clear();
				}
				
				rewards.clear();
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private static boolean levelsConformity(KuffleMain km, String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject levelObj = (JSONObject) jsonObj.get(key);
				
				if (!levelObj.containsKey("Number")) {
					km.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Number' Object.");
					levelObj.clear();
					jsonObj.clear();
					return false;
				} else if (!levelObj.containsKey("Seconds")) {
					km.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Seconds' Object.");
					levelObj.clear();
					jsonObj.clear();
					return false;
				} else if (!levelObj.containsKey("Lose")) {
					km.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Lose' Object.");
					levelObj.clear();
					jsonObj.clear();
					return false;
				}
				
				@SuppressWarnings("unused")
				int number = Integer.parseInt(levelObj.get("Number").toString());
				number = Integer.parseInt(levelObj.get("Seconds").toString());

				String lose = levelObj.get("Lose").toString();

				if (!lose.equalsIgnoreCase("true") && !lose.equalsIgnoreCase("false")) {
					return false;
				}
			}
			
			jsonObj.clear();
			
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public static boolean fileExists(KuffleMain km, String path, String fileName) {
		File tmp = new File(path + File.separator + km.getDescription().getVersion() + File.separator + fileName);
		
		return tmp.exists();
	}
	
	public static void directoryExists(String path) {
		File file = new File(path);
		 
        if (!file.isDirectory()) {
        	file.mkdir();
        }
	}
}
