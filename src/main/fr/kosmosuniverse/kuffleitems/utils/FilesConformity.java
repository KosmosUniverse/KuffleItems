package main.fr.kosmosuniverse.kuffleitems.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.core.AgeManager;
import main.fr.kosmosuniverse.kuffleitems.core.RewardManager;

public class FilesConformity {
	private FilesConformity() {
		throw new IllegalStateException("Utility class");
	}
	
	public static String getContent(String file) {
		String content;
		
		if (file.contains("%v")) {
			file = Utils.findFileExistVersion(file);
			
			if (file == null) {
				return null;
			}
		}
		
		content = getFromFile(file);
		
		if (content == null || !checkContent(file, content)) {
			content = getFromResource(file);
			KuffleMain.systemLogs.logMsg(KuffleMain.current.getName(), "Load " + file + " from Resource.");
		} else {
			KuffleMain.systemLogs.logMsg(KuffleMain.current.getName(), "Load " + file + " from File.");
		}
		
		return content;
	}
	
	private static String getFromFile(String file) {
		if (fileExists(KuffleMain.current.getDataFolder().getPath(), file)) {
			try (FileReader reader = new FileReader(KuffleMain.current.getDataFolder().getPath() + File.separator + KuffleMain.current.getDescription().getVersion() + File.separator  + file)) {
				JSONParser parser = new JSONParser();
				
				return ((JSONObject) parser.parse(reader)).toString();
			} catch (IOException | ParseException e) {
				e.printStackTrace();
			}
		} else {
			createFromResource(file);
		}
		
		return null;
	}
	
	private static void createFromResource(String fileName) {
		String path = KuffleMain.current.getDataFolder().getPath() + File.separator + KuffleMain.current.getDescription().getVersion();
		directoryExists(path);		
		
		try (FileWriter writer = new FileWriter(path + File.separator + fileName)) {
			InputStream in = KuffleMain.current.getResource(fileName);

			writer.write(Utils.readFileContent(in));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String getFromResource(String file) {
		try {
			InputStream in = KuffleMain.current.getResource(file);
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
	
	private static boolean checkContent(String file, String content) {
		if (content.equals(getFromResource(file))) {
			return false;
		}
		
		if (file.equals("ages.json")) {
			return ageConformity(content);
		} else if (file.equals("items_lang.json")) {
			return itemLangConformity(content);
		} else if (file.equals("items_" + Utils.getVersion() + ".json") ||
				file.equals("sbtt_" + Utils.getVersion() + ".json")) {
			return itemsConformity(content);
		} else if (file.equals("rewards_" + Utils.getVersion() + ".json")) {
			return rewardsConformity(content);
		} else if (file.equals("levels.json")) {
			return levelsConformity(content);
		} else if (file.equals("langs.json")) {
			return langConformity(content);
		}
		
		return false;
	}
	
	private static boolean ageConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				
				if (!((String) key).endsWith("_Age")) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does not finish by '_Age'.");
					jsonObj.clear();
					return false;
				}
				
				JSONObject ageObj = (JSONObject) jsonObj.get(key);
				
				if (!ageObj.containsKey("Number")) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'Number' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				} else if (!ageObj.containsKey("TextColor")) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'TextColor' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				} else if (!ageObj.containsKey("BoxColor")) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does not contain 'BoxColor' Object.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				}
				
				@SuppressWarnings("unused")
				int number = Integer.parseInt(ageObj.get("Number").toString());
				String color = (String) ageObj.get("TextColor");
				String box = (String) ageObj.get("BoxColor") + "_SHULKER_BOX";
				
				if (ChatColor.valueOf(color) == null) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] color [" + color + "] is not in ChatColor Enum.");
					ageObj.clear();
					jsonObj.clear();
					return false;
				}
				
				if (Material.matchMaterial(box) == null) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] box [" + box + "] is not in Material Enum.");
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
	
	private static boolean itemLangConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			List<String> langs = null;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				
				if (Material.matchMaterial((String) key) == null) {
					KuffleMain.systemLogs.logSystemMsg("Material [" + (String) key + "] does not exist.");
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
						if (!langs.contains(keyLang)) {
							KuffleMain.systemLogs.logSystemMsg("Lang [" + (String) keyLang + "] is not everywhere in lang file.");
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
	
	private static boolean langConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			List<String> langs = null;
			
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
						if (!langs.contains(keyLang)) {
							KuffleMain.systemLogs.logSystemMsg("Lang [" + (String) keyLang + "] is not everywhere in lang file.");
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
	
	private static boolean itemsConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (AgeManager.ageExists(KuffleMain.ages, (String) key)) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does exist in ages.json.");
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
	
	private static boolean rewardsConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				if (AgeManager.ageExists(KuffleMain.ages, (String) key)) {
					KuffleMain.systemLogs.logSystemMsg("Age [" + (String) key + "] does exist in ages.json.");
					return false;
				}
				
				JSONObject rewards = (JSONObject) jsonObj.get(key);
				
				for (Object reward : rewards.keySet()) {
					if (Material.matchMaterial((String) reward) == null) {
						KuffleMain.systemLogs.logSystemMsg("Material [" + (String) reward + "] is not in Material Enum.");
						rewards.clear();
						jsonObj.clear();
						
						return false;
					}
					
					JSONObject itemObj = (JSONObject) rewards.get(reward);
					boolean containKey = true;
					
					if (!itemObj.containsKey("Amount")) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Amount' Object.");
						containKey = false;
					} else if (!itemObj.containsKey("Enchant")) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Enchant' Object.");
						containKey = false;
					} else if (!itemObj.containsKey("Level")) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Level' Object.");
						containKey = false;
					} else if (!itemObj.containsKey("Effect")) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] does not contain 'Effect' Object.");
						containKey = false;
					}
					
					if (!containKey) {
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
					
					containKey = true;
					
					if (enchants.contains(",")) {
						for (String enchant : enchants.split(",")) {
							if (RewardManager.getEnchantment(enchant) == null) {
								KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + enchant + "].");
								containKey = false;
								break;
							}
						}
					} else if (RewardManager.getEnchantment(enchants) == null) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + enchants + "].");
						containKey = false;
					}
					
					if (!containKey) {
						itemObj.clear();
						rewards.clear();
						jsonObj.clear();
						return false;
					}
					
					if (effects.contains(",")) {
						for (String effect : effects.split(",")) {
							if (!Utils.checkEffect(effect)) {
								KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown effect : [" + effect + "].");
								itemObj.clear();
								rewards.clear();
								jsonObj.clear();
								return false;
							}
						}
					} else if (!Utils.checkEffect(effects)) {
						KuffleMain.systemLogs.logSystemMsg("Reward [" + (String) reward + "] contains unknown enchant : [" + effects + "].");
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
	
	private static boolean levelsConformity(String content) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObj;
			
			jsonObj = (JSONObject) parser.parse(content);
			
			for (Object key : jsonObj.keySet()) {
				JSONObject levelObj = (JSONObject) jsonObj.get(key);
				
				boolean containKey = true;
				
				if (!levelObj.containsKey("Number")) {
					KuffleMain.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Number' Object.");
					containKey = false;
				} else if (!levelObj.containsKey("Seconds")) {
					KuffleMain.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Seconds' Object.");
					containKey = false;
				} else if (!levelObj.containsKey("Lose")) {
					KuffleMain.systemLogs.logSystemMsg("Level [" + (String) key + "] does not contain 'Lose' Object.");
					containKey = false;
				}
				
				if (!containKey) {
					levelObj.clear();
					jsonObj.clear();
					return false;
				}
				
				@SuppressWarnings("unused")
				int number = Integer.parseInt(levelObj.get("Number").toString());
				number = Integer.parseInt(levelObj.get("Seconds").toString());

				String lose = levelObj.get("Lose").toString();

				if (!lose.equalsIgnoreCase("true") && !lose.equalsIgnoreCase("false")) {
					levelObj.clear();
					jsonObj.clear();
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
	
	public static boolean fileExists(String path, String fileName) {
		File tmp = new File(path + File.separator + KuffleMain.current.getDescription().getVersion() + File.separator + fileName);
		
		return tmp.exists();
	}
	
	public static void directoryExists(String path) {
		File file = new File(path);
		 
        if (!file.isDirectory()) {
        	file.mkdir();
        }
	}
}
