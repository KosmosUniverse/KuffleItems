package fr.kosmosuniverse.kuffleitems.Core;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import fr.kosmosuniverse.kuffleitems.KuffleMain;
import net.md_5.bungee.api.ChatColor;

public class GameLoop {
	private KuffleMain km;
	
	private BukkitTask runnable;
	
	public GameLoop(KuffleMain _km) {
		km = _km;
	}
	
	public void startRunnable() {
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				int bestRank = getBestRank();
				int worseRank = getWorseRank();
				
				for (String playerName : km.games.keySet()) {
					Game tmpGame = km.games.get(playerName);
					
					if (tmpGame.getLose()) {
						if (!tmpGame.getFinished()) {
							tmpGame.finish(worseRank);
							worseRank = getWorseRank();
						}
					} else if (tmpGame.getFinished()) {
						tmpGame.randomBarColor();
					} else {
						if (tmpGame.getCurrentItem() == null) {
							if (tmpGame.getItemCount() >= (km.config.getBlockPerAge() + 1)) {
								if ((tmpGame.getAge() + 1) == km.config.getMaxAges()) {
									tmpGame.finish(bestRank);
									bestRank = getBestRank();
								} else {
									tmpGame.nextAge();	
								}
							}
							
							newItem(tmpGame);	
						} else {
							if (System.currentTimeMillis() - tmpGame.getTimeShuffle() > (tmpGame.getTime() * 60000)) {
								tmpGame.getPlayer().sendMessage("§4You didn't find your block. Let's give you another one.§r");
								newItem(tmpGame);
							}
						}
						
						long count = tmpGame.getTime() * 60000;
						String dispCuritem;
						
						count -= (System.currentTimeMillis() - tmpGame.getTimeShuffle());
						count /= 1000;
						
						if (tmpGame.getCurrentItem() == null) {
							dispCuritem = "Something New...";
						} else {
							dispCuritem = tmpGame.getItemDisplay();
						}
						
						ChatColor color = null;
						
						if (count < 30) {
							color = ChatColor.RED;
						} else if (count < 60) {
							color = ChatColor.YELLOW;
						} else {
							color = ChatColor.GREEN;
						}
						
						ActionBar.sendMessage(color + "Time Left: " + count + " seconds to find: " + dispCuritem + ".", tmpGame.getPlayer());	
					}
				}
			}
		}.runTaskTimer(km, 0, 20);
	}
	
	private int getBestRank() {
		int cntRank = 1;
		
		while (cntRank <= km.playerRank.size() && km.playerRank.containsValue(cntRank)) {
			cntRank++;
		}
		
		return cntRank;
	}
	
	private int getWorseRank() {
		int cntRank = km.playerRank.size();
		
		while (cntRank >= 1 && km.playerRank.containsValue(cntRank)) {
			cntRank--;
		}
		
		return cntRank;
	}
	
	private void newItem(Game tmpGame) {
		tmpGame.setCurrentItem(ItemManager.newBlock(tmpGame.getAlreadyGot(), km.allItems.get(km.ageNames.get(tmpGame.getAge()) + "_Age")));		
	}
	
	public void kill() {
		if (runnable != null && !runnable.isCancelled()) {
			runnable.cancel();
		}
	}
}
