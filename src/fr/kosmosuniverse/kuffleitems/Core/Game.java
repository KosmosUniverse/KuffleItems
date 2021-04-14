package fr.kosmosuniverse.kuffleitems.Core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.kosmosuniverse.kuffleitems.Core.LangManager;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.Core.RewardManager;
import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class Game {
	private KuffleMain km;
	private ArrayList<String> alreadyGot;
	
	private boolean finished;
	private boolean lose;
	
	private int time;
	private int itemCount = 1;
	private int age = 0;
	private int gameRank = 0;
	
	private long timeShuffle = -1;
	private long deathTime = -1;
	private long minTime;
	private long maxTime;
	private long interval = -1;
	private long deathInterval = -1;
	
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
	}
	
	public void setup() {
		time = km.config.getStartTime();
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
		
		global.put("age", age);
		global.put("maxAge", km.config.getMaxAges());
		global.put("current", currentItem);
		global.put("interval", System.currentTimeMillis() - timeShuffle);
		global.put("time", time);
		global.put("itemCount", itemCount);
		global.put("spawn", jsonSpawn);
		global.put("death", jsonDeath);
		global.put("teamName", teamName);
		//global.put("sameIdx", sameIdx);
		
		JSONArray got = new JSONArray();
		
		for (String block : alreadyGot) {
			got.add(block);
		}
		
		global.put("alreadyGot", got);

		return (global.toString());
	}
	
	public void load() {
		updateBar();
		reloadEffects();
		player.setPlayerListName(Utils.getColor(age) + player.getName());
		itemScore.setScore(itemCount);
	}
	
	public void pause() {
		interval = System.currentTimeMillis() - timeShuffle;
		
		if (deathTime != -1) {
			deathInterval = System.currentTimeMillis() - deathTime;
		}
	}
	
	public void resume() {
		timeShuffle = System.currentTimeMillis() - interval;
		interval = -1;
		
		if (deathInterval != -1) {
			deathTime = System.currentTimeMillis() - deathInterval;
			deathInterval = -1;
		}
	}
	
	private void updateBar() {
		double calc = ((double) itemCount) / km.config.getBlockPerAge();
		calc = calc > 1.0 ? 1.0 : calc;
		ageDisplay.setProgress(calc);
		ageDisplay.setTitle(km.ageNames.get(age).replace("_", " ") + ": " + itemCount);
	}
	
	public void resetBar() {
		if (ageDisplay != null && ageDisplay.getPlayers().size() != 0) {
			ageDisplay.removeAll();
			ageDisplay = null;
		}
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
				RewardManager.managePreviousEffects(km.allRewards.get(km.ageNames.get(age - 1)), km.effects, player, km.ageNames.get(age - 1));
			}
			
			RewardManager.givePlayerReward(km.allRewards.get(km.ageNames.get(age)), km.effects, player, km.ageNames.get(age));
		}
		
		itemCount = 1;
		age++;
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1f, 1f);
		player.setPlayerListName(Utils.getColor(age) + player.getName());
		itemScore.setScore(itemCount);
		updateBar();
	}
	
	public void finish(int _gameRank) {
		finished = true;
		
		gameRank = _gameRank;
		ageDisplay.setTitle("Game Done ! Rank : " + gameRank);
		player.setPlayerListName(Utils.getColor(age) + player.getName());
		km.playerRank.put(player.getName(), gameRank);
	}
	
	public void randomBarColor() {
		ageDisplay.setColor(getRandomColor());
	}
	
	public boolean skip(boolean malus) {
		if ((age + 1) < km.config.getSkipAge()) {
			km.logs.writeMsg(player, "You can't skip block this age.");
			
			return false;
		}
		
		if (itemCount == 1) {
			km.logs.writeMsg(player, "You can't skip the first block of the age.");
			
			return false;
		}
		
		if (malus) {
			itemCount--;
			km.logs.writeMsg(player, "Block [" + currentItem + "] was skipped.");
		}
		
		itemScore.setScore(itemCount);
		updateBar();
		currentItem = null;
		
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

			RewardManager.givePlayerRewardEffect(km.allRewards.get(km.ageNames.get(tmp)), km.effects, player, km.ageNames.get(tmp));
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
		if (System.currentTimeMillis() - deathTime > (maxTime * 1000)) {
			player.sendMessage("You waited too much to return to your death spot, your stuff is now unreachable.");
			deathInv.clear();
			deathInv = null;
			deathTime = -1;
			return;
		}
		
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
		deathTime = -1;
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
	
	public int getTime() {
		return time;
	}
	
	public int getItemCount() {
		return itemCount;
	}
	
	public int getAge() {
		return age;
	}
	
	public long getTimeShuffle() {
		return timeShuffle;
	}
	
	public long getDeathTime() {
		return deathTime;
	}
	
	public long getMinTime() {
		return minTime;
	}
	
	public long getMaxTime() {
		return maxTime;
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
	
	public void setTime(int _time) {
		time = _time;
	}
	
	public void setItemCount(int _itemCount) {
		itemCount = _itemCount;
	}
	
	public void setAge(int _age) {
		if (age == _age) {
			return;
		}
		
		age = _age;
		alreadyGot.clear();
	}
	
	public void setTimeShuffle(long _timeShuffle) {
		timeShuffle = _timeShuffle;
	}
	
	public void setDeathTime(long time, long _minTime, long _maxTime) {
		deathTime = time;
		minTime = _minTime;
		maxTime = _maxTime;
	}

	public void setCurrentItem(String _currentItem) {
		currentItem = _currentItem;
		alreadyGot.add(currentItem);
		timeShuffle = System.currentTimeMillis();
		itemDisplay = LangManager.findBlockDisplay(km.allLangs, currentItem, configLang);
		km.updatePlayersHead(player.getName(), itemDisplay);
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
		deathTime = System.currentTimeMillis();
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
	
	private BarColor getRandomColor() {
		Random r = new Random();
		
		return (BarColor.values()[r.nextInt(BarColor.values().length)]);
	}
}
