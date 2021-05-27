package main.fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.LangManager;
import main.fr.kosmosuniverse.kuffleitems.Core.RewardManager;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Game {
	private KuffleMain km;
	private ArrayList<String> alreadyGot;
	private HashMap<String, Long> times;
	
	private boolean finished;
	private boolean lose;
	private boolean dead;
	
	private int time;
	private int itemCount = 1;
	private int age = 0;
	private int gameRank = -1;
	private int sameIdx = 0;
	
	private int deathCount = 0;
	private int skipCount = 0;
	private int sbttCount = 0;
	
	private long timeShuffle = -1;
	private long interval = -1;
	private long timeBase;
	
	private String currentItem;
	private String itemDisplay;
	private String configLang;
	private String teamName;
	
	private Location spawnLoc;
	private Location deathLoc;
	
	private Player player;
	private Inventory deathInv = null;
	private Score itemScore;
	private BossBar ageDisplay;
	
	public Game(KuffleMain _km, Player _player) {
		km = _km;
		player = _player;
		finished = false;
		lose = false;
		dead = false;
	}
	
	public void setup() {
		time = km.config.getStartTime();
		timeBase = System.currentTimeMillis();
		times = new HashMap<String, Long>();
		alreadyGot = new ArrayList<String>();
		ageDisplay = Bukkit.createBossBar("STARTING...", BarColor.PURPLE, BarStyle.SOLID);
		ageDisplay.addPlayer(player);
		deathLoc = null;
		configLang = km.config.getLang();
		updateBar();
	}
	
	public void stop() {
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		
		resetBar();
		alreadyGot.clear();
	}
	
	@SuppressWarnings("unchecked")
	public String save() {
		JSONObject jsonSpawn = new JSONObject();
		
		jsonSpawn.put("World", spawnLoc.getWorld().getName());
		jsonSpawn.put("X", spawnLoc.getX());
		jsonSpawn.put("Y", spawnLoc.getY());
		jsonSpawn.put("Z", spawnLoc.getZ());
		
		JSONObject jsonDeath = new JSONObject();
		if (deathLoc == null) {
			jsonDeath = null;
		} else {
			jsonDeath.put("World", deathLoc.getWorld().getName());
			jsonDeath.put("X", deathLoc.getX());
			jsonDeath.put("Y", deathLoc.getY());
			jsonDeath.put("Z", deathLoc.getZ());
		}
		
		JSONObject global = new JSONObject();
		
		if (deathInv != null) {
			try {
				saveInventory();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		global.put("age", age);
		global.put("maxAge", km.config.getMaxAges());
		global.put("current", currentItem);
		global.put("interval", System.currentTimeMillis() - timeShuffle);
		global.put("time", time);
		global.put("isDead", dead);
		global.put("itemCount", itemCount);
		global.put("spawn", jsonSpawn);
		global.put("death", jsonDeath);
		global.put("teamName", teamName);
		global.put("sameIdx", sameIdx);
		global.put("deathCount", deathCount);
		global.put("skipCount", skipCount);
		global.put("finished", finished);
		global.put("lose", lose);
		
		JSONArray got = new JSONArray();
		
		for (String block : alreadyGot) {
			got.add(block);
		}
		
		global.put("alreadyGot", got);
		
		JSONObject saveTimes = new JSONObject();
		
		for (String time : times.keySet()) {
			saveTimes.put(time, times.get(time));
		}
		
		saveTimes.put("interval", System.currentTimeMillis() - timeBase);
		
		global.put("times", saveTimes);

		return (global.toString());
	}
	
	public void saveInventory() throws IOException {
        File f = new File(km.getDataFolder().getPath(), player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("inventory.content", deathInv.getContents());
        c.save(f);
    }
	
	@SuppressWarnings("unchecked")
	public void loadInventory() throws IOException {
        File f = new File(km.getDataFolder().getPath(), player.getName() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        
        deathInv = Bukkit.createInventory(null, 54);
        
        ItemStack[] content = ((List<ItemStack>) c.get("inventory.content")).toArray(new ItemStack[0]);
        deathInv.setContents(content);
    }
	
	public void load() {
		if (finished) {
			gameRank = km.playerRank.get(player.getName());
		}
		
		updateBar();
		reloadEffects();
		updatePlayerListName();
		itemScore.setScore(itemCount);
	}
	
	public void pause() {
		interval = System.currentTimeMillis() - timeShuffle;
	}
	
	public void resume() {
		timeShuffle = System.currentTimeMillis() - interval;
		interval = -1;
	}
	
	private void updateBar() {
		if (lose) {
			ageDisplay.setProgress(0.0);
			ageDisplay.setTitle("Game Done ! Rank : " + gameRank);
			
			return ;
		} 
		
		if (finished) {
			ageDisplay.setProgress(1.0);
			ageDisplay.setTitle("Game Done ! Rank : " + gameRank);
			
			return ;
		}
		
		double calc = ((double) itemCount) / km.config.getBlockPerAge();
		calc = calc > 1.0 ? 1.0 : calc;
		ageDisplay.setProgress(calc);
		ageDisplay.setTitle(AgeManager.getAgeByNumber(km.ages, age).name.replace("_", " ") + ": " + itemCount);
	}
	
	public void resetBar() {
		if (ageDisplay != null && ageDisplay.getPlayers().size() != 0) {
			ageDisplay.removeAll();
			ageDisplay = null;
		}
	}
	
	public void foundSBTT() {
		sbttCount++;
		
		found();
	}
	
	public void found() {
		currentItem = null;
		itemCount++;
		player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
		itemScore.setScore(itemCount);
		updateBar();
	}
	
	public void nextAge() {
		if (km.config.getRewards()) {
			if (age > 0) {
				RewardManager.managePreviousEffects(km.allRewards.get(AgeManager.getAgeByNumber(km.ages, age - 1).name), player, AgeManager.getAgeByNumber(km.ages, age - 1).name);
			}
			
			RewardManager.givePlayerReward(km.allRewards.get(AgeManager.getAgeByNumber(km.ages, age).name), player, km.ages,  AgeManager.getAgeByNumber(km.ages, age).number);
		}
		
		times.put(AgeManager.getAgeByNumber(km.ages, age).name, System.currentTimeMillis() - timeBase);
		
		timeBase = System.currentTimeMillis();
		alreadyGot.clear();
		currentItem = null;
		itemCount = 1;
		age++;
		time = time + km.config.getAddedTime();
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
		updatePlayerListName();
		itemScore.setScore(itemCount);
		updateBar();
		Bukkit.broadcastMessage("�6�l" + player.getName() + ChatColor.BLUE + " has moved to the " + AgeManager.getAgeByNumber(km.ages, age).color + AgeManager.getAgeByNumber(km.ages, age).name.replace("_", " ") + "�1.");
	}
	
	public void finish(int _gameRank) {
		finished = true;
		
		if (km.config.getTeam()) {
			int tmpRank;

			if ((tmpRank = checkTeamMateRank()) != -1) {
				_gameRank = tmpRank;
			}
		}
		
		gameRank = _gameRank;
		ageDisplay.setTitle("Game Done ! Rank : " + gameRank);
		
		if (lose) {
			ageDisplay.setProgress(0.0f);	
		} else {
			ageDisplay.setProgress(1.0f);	
		}
		
		km.playerRank.put(player.getName(), gameRank);
		km.updatePlayersHead(player.getName(), null);
		
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		
		if (lose) {
			for (int cnt = age; cnt < km.config.getMaxAges(); cnt++) {
				times.put(AgeManager.getAgeByNumber(km.ages, cnt).name, (long) -1);
			}
		} else {
			times.put(AgeManager.getAgeByNumber(km.ages, age).name, System.currentTimeMillis() - timeBase);
		}
		
		age = -1;
		
		updatePlayerListName();
		
		player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + player.getName());
		player.sendMessage(ChatColor.BLUE + " - Death Count: " + ChatColor.RESET + deathCount);
		player.sendMessage(ChatColor.BLUE + " - Skip Count: " + ChatColor.RESET + skipCount);
		player.sendMessage(ChatColor.BLUE + " - Template Count: " + ChatColor.RESET + sbttCount);
		player.sendMessage(ChatColor.BLUE + " - Times Tab:");
		
		for (int i = 0; i < km.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(km.ages, i);
			
			String tmp;
			
			if (times.get(age.name) == -1) {
				tmp = ChatColor.RESET + ": Abandon";
			} else {
				tmp = ChatColor.BLUE + " in: " + ChatColor.RESET + Utils.getTimeFromSec(times.get(age.name) / 1000);
			}
			
			player.sendMessage(ChatColor.BLUE + "   - Finished " + age.color + age.name + tmp);
		}
	}
	
	private int checkTeamMateRank() {
		int tmp = -1;
		
		for (String playerName : km.games.keySet()) {
			if (km.games.get(playerName).getTeamName().equals(teamName) &&
					km.games.get(playerName).getRank() != -1) {
				tmp = km.games.get(playerName).getRank();
			}
		}
		
		return tmp;
	}
	
	public void randomBarColor() {
		ageDisplay.setColor(getRandomColor());
	}
	
	public boolean skip(boolean malus) {
		skipCount++;
		
		if (malus) {
			if ((age + 1) < km.config.getSkipAge()) {
				km.logs.writeMsg(player, "You can't skip block this age.");
				
				return false;
			}
			
			if (itemCount == 1) {
				km.logs.writeMsg(player, "You can't skip the first block of the age.");
				
				return false;
			}
			
			itemCount--;
			km.logs.writeMsg(player, "Block [" + currentItem + "] was skipped.");
			
			itemScore.setScore(itemCount);
			updateBar();
			currentItem = null;
		} else {
			itemScore.setScore(itemCount);
			updateBar();
			currentItem = null;
		}
		
		return true;
	}
	
	public void reloadEffects() {
		if (km.config.getRewards()) {
			if (km.config.getSaturation()) {
				player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			}
			
			int tmp = age - 1;
			
			if (tmp < 0) 
				return;

			RewardManager.givePlayerRewardEffect(km.allRewards.get(AgeManager.getAgeByNumber(km.ages, tmp).name), player, AgeManager.getAgeByNumber(km.ages, tmp).name);
		}
	}
	
	public void savePlayerInv() {
		deathInv = Bukkit.createInventory(null, 54);
		
		for (ItemStack item : player.getInventory().getContents()) {
			if (item != null) {
				deathInv.addItem(item);
			}
		}
	}
	
	public void restorePlayerInv() {
		for (ItemStack item : deathInv.getContents()) {
			if (item != null) {
				HashMap<Integer, ItemStack> ret = player.getInventory().addItem(item);
				if (!ret.isEmpty()) {
					for (Integer cnt : ret.keySet()) {
						player.getWorld().dropItem(player.getLocation(), ret.get(cnt));
					}
				}
			}
		}
		
		deathInv.clear();
		deathInv = null;
		deathLoc = null;
		dead = false;
	}
	
	public void updatePlayerListName() {
		if (km.config.getTeam()) {
			player.setPlayerListName("[" + km.teams.getTeam(teamName).color + teamName + ChatColor.RESET + "] - " + AgeManager.getAgeByNumber(km.ages, age).color + player.getName());
		} else {
			player.setPlayerListName(AgeManager.getAgeByNumber(km.ages, age).color + player.getName());	
		}
	}
	
	public ArrayList<String> getAlreadyGot() {
		return alreadyGot;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public boolean getLose() {
		return lose;
	}
	
	public boolean getDead() {
		return dead;
	}
	
	public int getTime() {
		return time;
	}
	
	public int getItemCount() {
		return itemCount;
	}
	
	public int getAge() {
		return age;
	}
	
	public int getRank() {
		return gameRank;
	}
	
	public int getSameIdx() {
		return sameIdx;
	}
	
	public int getDeathCount() {
		return deathCount;
	}
	
	public int getSkipCount() {
		return skipCount;
	}
	
	public int getSbttCount() {
		return sbttCount;
	}
	
	public long getTimeShuffle() {
		return timeShuffle;
	}
	
	public long getAgeTime(String age) {
		return times.get(age);
	}
	
	public String getCurrentItem() {
		return currentItem;
	}
	
	public String getItemDisplay() {
		return itemDisplay;
	}
	
	public String getTeamName() {
		return teamName;
	}
	
	public String getLang() {
		return configLang;
	}
	
	public Score getItemScore() {
		return itemScore;
	}

	public Location getSpawnLoc() {
		return spawnLoc;
	}
	
	public Location getDeathLoc() {
		return deathLoc;
	}

	public void setAlreadyGot(JSONArray _alreadyGot) {
		for (int i = 0; i < _alreadyGot.size(); i++) {
			alreadyGot.add((String) _alreadyGot.get(i));			
		}
	}
	
	public void setFinished(boolean _finished) {
		finished = _finished;
	}
	
	public void setLose(boolean _lose) {
		lose = _lose;
	}
	
	public void setDead(boolean _dead) {
		dead = _dead;
		
		if (dead) {
			deathCount++;
		}
	}
	
	public void setTime(int _time) {
		time = _time;
	}
	
	public void setItemCount(int _itemCount) {
		itemCount = _itemCount;
		
		if (itemScore != null) {
			itemScore.setScore(itemCount);			
		}

		updateBar();
	}
	
	public void setAge(int _age) {
		if (age == _age) {
			return;
		}
		
		age = _age;
		alreadyGot.clear();
	}
	
	public void setSameIdx(int _sameIdx) {
		sameIdx = _sameIdx;
	}
	
	public void setDeathCount(int _deathCount) {
		deathCount = _deathCount;
	}
	
	public void setSkipCount(int _skipCount) {
		skipCount = _skipCount;
	}
	
	public void setTimeShuffle(long _timeShuffle) {
		timeShuffle = _timeShuffle;
	}

	public void setCurrentItem(String _currentItem) {
		currentItem = _currentItem;
		
		if (currentItem == null) {
			return ;
		}

		if (km.config.getDouble()) {
			timeShuffle = System.currentTimeMillis();
			
			itemDisplay = LangManager.findBlockDisplay(km.allLangs, currentItem.split("/")[0], configLang) + "/" + LangManager.findBlockDisplay(km.allLangs, currentItem.split("/")[1], configLang);
			km.updatePlayersHead(player.getName(), itemDisplay);
		} else {
			if (!alreadyGot.contains(currentItem)) {
				alreadyGot.add(currentItem);
			}
			
			timeShuffle = System.currentTimeMillis();
			itemDisplay = LangManager.findBlockDisplay(km.allLangs, currentItem, configLang);
			km.updatePlayersHead(player.getName(), itemDisplay);
		}
	}
	
	public void setTeamName(String _teamName) {
		teamName = _teamName;
	}
	
	public void setLang(String _configLang) {
		if (_configLang.equals(configLang)) {
			return ;
		}
		
		configLang = _configLang;
		
		if (currentItem != null) {
			itemDisplay = LangManager.findBlockDisplay(km.allLangs, currentItem, configLang);
		}
	}
	
	public void setItemScore(Score score) {
		itemScore = score;
	}
	
	public void setDeathInv(Inventory _deathInv) {
		deathInv = _deathInv;
	}

	public void setSpawnLoc(Location _spawnLoc) {
		spawnLoc = _spawnLoc;
	}
	
	public void setSpawnLoc(JSONObject _spawnloc) {
		spawnLoc = new Location(Bukkit.getWorld((String) _spawnloc.get("World")),
				(double) _spawnloc.get("X"),
				(double) _spawnloc.get("Y"),
				(double) _spawnloc.get("Z"));
	}
	
	public void setDeathLoc(Location _deathLoc) {
		deathLoc = _deathLoc;
	}
	
	public void setDeathLoc(JSONObject _deathLoc) {
		if (_deathLoc == null) {
			deathLoc = null;
		} else {
			deathLoc = new Location(Bukkit.getWorld((String) _deathLoc.get("World")),
					(double) _deathLoc.get("X"),
					(double) _deathLoc.get("Y"),
					(double) _deathLoc.get("Z"));	
		}
	}
	
	public void setTimes(JSONObject _times) {
		timeBase = System.currentTimeMillis() - (Long) _times.get("interval");
		
		for (int i = 0; i < km.config.getMaxAges(); i++) {
			Age age = AgeManager.getAgeByNumber(km.ages, i);
			
			if (_times.containsKey(age.name)) {
				times.put((String) age.name, (Long) _times.get(age.name));
			}
		}
	}
	
	public void addToAlreadyGot(String item) {
		alreadyGot.add(item);
	}
	
	public void removeFromList(String[] array) {
		alreadyGot.remove(currentItem.equals(array[0]) ? array[1] : array[0]);
	}
	
	public void resetList() {
		alreadyGot.clear();
	}
	
	private BarColor getRandomColor() {
		Random r = new Random();
		
		return (BarColor.values()[r.nextInt(BarColor.values().length)]);
	}
}
