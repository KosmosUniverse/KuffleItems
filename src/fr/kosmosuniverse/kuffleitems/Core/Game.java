package fr.kosmosuniverse.kuffleitems.Core;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kosmosuniverse.kuffleitems.Core.LangManager;
import fr.kosmosuniverse.kuffleitems.KuffleMain;

public class Game {
	private KuffleMain km;
	private ArrayList<String> alreadyGot;
	
	private boolean finished;
	
	private int time;
	private int itemCount = 1;
	private int age = 0;
	
	private long timeShuffle = -1;
	private long deathTime = 0;
	private long minTime;
	private long maxTime;
	
	private String currentItem;
	private String itemDisplay;
	private String configLang;
	private String teamName;
	
	private Location spawnLoc;
	private Location deathLoc;
	
	private Player player;
	private Inventory deathInv = null;
	
	public Game(KuffleMain _km, Player _player) {
		km = _km;
		player = _player;
		time = km.config.getStartTime();
		alreadyGot = new ArrayList<String>();
	}
	
	public void found() {
		currentItem = null;
		itemCount++;
		player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1f, 1f);
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

	public Location getSpawnLoc() {
		return spawnLoc;
	}
	
	public Location getDeathLoc() {
		return deathLoc;
	}

	public void setFinished(boolean _finished) {
		finished = _finished;
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

	public void setSpawnLoc(Location _spawnLoc) {
		spawnLoc = _spawnLoc;
	}
	
	public void setDeathLoc(Location _deathLoc) {
		deathLoc = _deathLoc;
		deathTime = System.currentTimeMillis();
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
	}
}
