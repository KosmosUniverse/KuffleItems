package fr.kosmosuniverse.kuffleitems.Core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import fr.kosmosuniverse.kuffleitems.KuffleMain;

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
				if (km.paused) {
					return ;
				}
				
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
									km.logs.logBroadcastMsg(tmpGame.getPlayer().getName() + " complete this game !");
									Bukkit.broadcastMessage("§1" + tmpGame.getPlayer().getName() + " §6§lcomplete this game !§r");
								} else {
									if (!km.config.getTeam() || checkTeamMates(tmpGame)) {
										tmpGame.nextAge();
									}
								}
							}
							
							newItem(tmpGame);	
						} else {
							if (System.currentTimeMillis() - tmpGame.getTimeShuffle() > (tmpGame.getTime() * 60000)) {
								tmpGame.getPlayer().sendMessage("§4You didn't find your block. Let's give you another one.§r");
								newItem(tmpGame);
							}
						}
						
						printTimerItem(tmpGame);
					}
				}
			}
		}.runTaskTimer(km, 0, 20);
	}
	
	private void printTimerItem(Game tmpGame) {
		if (km.config.getTeam() && tmpGame.getItemCount() >= (km.config.getBlockPerAge() + 1)) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + "Waiting for other players of the team.", tmpGame.getPlayer());
			return ;
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
	
	private boolean checkTeamMates(Game tmpGame) {
		Team team = km.teams.getTeam(tmpGame.getTeamName());
		
		for (String playerName : km.games.keySet()) {
			if (team.hasPlayer(km.games.get(playerName).getPlayer().getName())) {
				if (km.games.get(playerName).getItemCount() < (km.config.getBlockPerAge() + 1) && km.games.get(playerName).getAge() <= tmpGame.getAge()) {
					return false;
				}
			}
		}
		
		return true;
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
		tmpGame.setCurrentItem(ItemManager.newBlock(tmpGame.getAlreadyGot(), km.allItems.get(Utils.getAgeByNumber(km.ages, tmpGame.getAge()).name)));		
	}
	
	public void kill() {
		if (runnable != null && !runnable.isCancelled()) {
			runnable.cancel();
		}
	}
}
