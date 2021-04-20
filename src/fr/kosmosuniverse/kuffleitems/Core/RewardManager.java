package fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class RewardManager {
	public static synchronized HashMap<String, HashMap<String, RewardElem>> getAllRewards(ArrayList<Age> ages, String rewardsContent, File dataFolder) {
		HashMap<String, HashMap<String, RewardElem>> finalMap = new HashMap<String, HashMap<String, RewardElem>>();
		
		int max = Utils.getAgeMaxNumber(ages);
		
		for (int ageCnt = 0; ageCnt <= max; ageCnt++) {
			finalMap.put(Utils.getAgeByNumber(ages, ageCnt).name, getAgeRewards(Utils.getAgeByNumber(ages, ageCnt).name, rewardsContent, dataFolder));
		}
		
		return finalMap;
	}
	
	public static synchronized HashMap<String, RewardElem> getAgeRewards(String age, String rewardsContent, File dataFolder) {
		HashMap<String, RewardElem> ageRewards = new HashMap<String, RewardElem>();
		JSONObject rewards = new JSONObject();
		JSONParser jsonParser = new JSONParser();
		FileWriter writer = null;
		
		try {
			if (dataFolder.getPath().contains("\\")) {
				writer = new FileWriter(dataFolder.getPath() + "\\logs.txt", true);
			} else {
				writer = new FileWriter(dataFolder.getPath() + "/logs.txt", true);
			}
			
			rewards = (JSONObject) jsonParser.parse(rewardsContent);
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
		JSONObject ageObject = new JSONObject();
		
		ageObject = (JSONObject) rewards.get(age);
		
		for (Iterator<?> it = ageObject.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			JSONObject tmp = (JSONObject) ageObject.get(key);
			ageRewards.put(key, new RewardElem(key, Integer.parseInt(((Long) tmp.get("Amount")).toString()), (String) tmp.get("Enchant"), Integer.parseInt(((Long) tmp.get("Level")).toString()), (String) tmp.get("Effect")));
		}
		
		try {
			StringBuilder sb = new StringBuilder();

			for (String key : ageRewards.keySet()) {
				sb.append(ageRewards.get(key).toString()).append("\n");
			}
			
			writer.append(sb.toString());
			
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return ageRewards;
	}
	
	public static HashMap<String, PotionEffectType> getAllEffects() {
		HashMap<String, PotionEffectType> effectMap = new HashMap<String, PotionEffectType>();
		
		effectMap.put("absorption", PotionEffectType.ABSORPTION);
		effectMap.put("damage_resistance", PotionEffectType.DAMAGE_RESISTANCE);
		effectMap.put("dolphins_grace", PotionEffectType.DOLPHINS_GRACE);
		effectMap.put("fast_digging", PotionEffectType.FAST_DIGGING);
		effectMap.put("fire_resistance", PotionEffectType.FIRE_RESISTANCE);
		effectMap.put("heal", PotionEffectType.HEAL);
		effectMap.put("health_boost", PotionEffectType.HEALTH_BOOST);
		effectMap.put("invisibility", PotionEffectType.INVISIBILITY);
		effectMap.put("jump", PotionEffectType.JUMP);
		effectMap.put("luck", PotionEffectType.LUCK);
		effectMap.put("night_vision", PotionEffectType.NIGHT_VISION);
		effectMap.put("regeneration", PotionEffectType.REGENERATION);
		effectMap.put("speed", PotionEffectType.SPEED);
		effectMap.put("water_breathing", PotionEffectType.WATER_BREATHING);
		
		return effectMap;
	}
	
	public static synchronized void givePlayerRewardEffect(HashMap<String, RewardElem> ageReward, HashMap<String, PotionEffectType> effects, Player p, String age) {
		for (String k : ageReward.keySet()) {
			if (k.contains("potion")) {				
				p.addPotionEffect(new PotionEffect(findEffect(effects, ageReward.get(k).getEffect()), 999999, 1));
			}
		}
	}
	
	public static synchronized void givePlayerReward(HashMap<String, RewardElem> ageReward, HashMap<String, PotionEffectType> effects, Player p, ArrayList<Age> ages, int age) {
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		
		ItemStack container = new ItemStack(Utils.getAgeByNumber(ages, age).box);
		
		BlockStateMeta containerMeta = (BlockStateMeta) container.getItemMeta();
		ShulkerBox box = (ShulkerBox) containerMeta.getBlockState();
		Inventory inv = box.getInventory();
		
		for (String k : ageReward.keySet()) {
			ItemStack it;
			
			if (ageReward.get(k).enchant()) {
				it = new ItemStack(Material.matchMaterial(k), ageReward.get(k).getAmount());
				
				if (ageReward.get(k).getEnchant().contains(",")) {
					String[] tmp = ageReward.get(k).getEnchant().split(",");
					
					for (String enchant : tmp) {
						if (getEnchantment(enchant) != null) {
							it.addUnsafeEnchantment(getEnchantment(enchant), ageReward.get(k).getLevel());
						}
					}
				} else {
					if (getEnchantment(ageReward.get(k).getEnchant()) != null) {
						it.addUnsafeEnchantment(getEnchantment(ageReward.get(k).getEnchant()), ageReward.get(k).getLevel());		
					}
				}
				
				items.add(new ItemStack(it));
			} else if (k.contains("potion")) {				
				p.addPotionEffect(new PotionEffect(findEffect(effects, ageReward.get(k).getEffect()), 999999, 1));
			} else {
				it = new ItemStack(Material.matchMaterial(k), ageReward.get(k).getAmount());
				items.add(new ItemStack(it));
			}
		}

		for (ItemStack it : items) {
			inv.addItem(it);
		}

		box.update();
		containerMeta.setBlockState(box);
		container.setItemMeta(containerMeta);
		
		ItemMeta itM = container.getItemMeta();
		itM.setDisplayName(Utils.getAgeByNumber(ages, age).name.replace("_", " "));
		container.setItemMeta(itM);
		
		HashMap<Integer, ItemStack> ret = p.getInventory().addItem(container);
		
		if (ret != null) {
			for (Integer i : ret.keySet()) {
				p.getWorld().dropItem(p.getLocation(), ret.get(i));
			}
		}
	}
	
	public static void managePreviousEffects(HashMap<String, RewardElem> ageReward, HashMap<String, PotionEffectType> effects, Player p, String age) {
		for (String key : ageReward.keySet()) {
			if (key.contains("potion")) {
				p.removePotionEffect(findEffect(effects, ageReward.get(key).getEffect()));
			}
		}
	}
	
	private static Enchantment getEnchantment(String enchant) {
		for (Enchantment e : Enchantment.values()) {
			if (e.getKey().toString().split(":")[1].equals(enchant)) {
				return e;
			}
		}
		return null;
	}

	private static PotionEffectType findEffect(HashMap<String, PotionEffectType> effects, String effect) {
		for (String key : effects.keySet()) {
			if (key.contains(effect)){
				return (effects.get(key));
			}
		}
		
		return null;
	}
}
