package main.fr.kosmosuniverse.kuffleitems.Core;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Utils.Pair;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class GameLoop {
	private KuffleMain km;
	private BukkitTask runnable;
	private boolean finished = false;

	public GameLoop(KuffleMain _km) {
		km = _km;
	}

	public void startRunnable() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (km.paused) {
					return ;
				}

				int bestRank = getBestRank();
				int worstRank = getWorstRank();

				if (km.config.getGameEnd() && !finished) {
					int lasts = Utils.playerLasts(km);

					if (lasts == 0) {
						Utils.printGameEnd(km);
						finished = true;
					} else if (lasts == 1 && km.config.getEndOne()) {
						Utils.forceFinish(km, bestRank);
					}
				}

				for (String playerName : km.games.keySet()) {
					Game tmpGame = km.games.get(playerName);

					if (tmpGame.getLose()) {
						if (!tmpGame.getFinished()) {
							tmpGame.finish(worstRank);
							worstRank = getWorstRank();
						}
					} else if (tmpGame.getFinished()) {
						tmpGame.randomBarColor();
					} else {
						if (tmpGame.getCurrentItem() == null) {
							if (tmpGame.getItemCount() >= (km.config.getItemPerAge() + 1)) {
								if (!km.config.getTeam() || checkTeamMates(tmpGame)) {
									if ((tmpGame.getAge() + 1) == km.config.getMaxAges()) {
										tmpGame.finish(bestRank);
										bestRank = getBestRank();
										km.gameLogs.logSystemMsg(tmpGame.getPlayer().getName() + " complete this game !");

										for (String toSend : km.games.keySet()) {
											km.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(km, toSend, "GAME_COMPLETE").replace("<#>", ChatColor.GOLD + "" + ChatColor.BOLD + tmpGame.getPlayer().getName() + ChatColor.BLUE));
										}
									} else {
										tmpGame.nextAge();
									}
								}
							} else {
								newItem(tmpGame);
							}
						} else {
							if (System.currentTimeMillis() - tmpGame.getTimeShuffle() > (tmpGame.getTime() * 60000)) {
								tmpGame.getPlayer().sendMessage(ChatColor.RED + Utils.getLangString(km, tmpGame.getPlayer().getName(), "ITEM_NOT_FOUND"));
								km.gameLogs.logSystemMsg("Player : " + tmpGame.getPlayer().getName() + " did not found item : " + tmpGame.getCurrentItem());
								newItem(tmpGame);
							} else if (km.config.getDouble() && !tmpGame.getCurrentItem().contains("/")) {
								String currentTmp = ItemManager.newItem(tmpGame.getAlreadyGot(), km.allItems.get(AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name));

								tmpGame.addToAlreadyGot(currentTmp);
								tmpGame.setCurrentItem(tmpGame.getCurrentItem() + "/" + currentTmp);
							} else if (!km.config.getDouble() && tmpGame.getCurrentItem().contains("/")) {
								String[] array = tmpGame.getCurrentItem().split("/");

								tmpGame.setCurrentItem(array[random.nextInt(2)]);
								tmpGame.removeFromList(array);
							}
						}

						printTimerItem(tmpGame);
					}
				}
			}
		}.runTaskTimer(km, 0, 20);
	}

	private void printTimerItem(Game tmpGame) {
		if (km.config.getTeam() && tmpGame.getItemCount() >= (km.config.getItemPerAge() + 1)) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + Utils.getLangString(km, tmpGame.getPlayer().getName(), "TEAM_WAIT"), tmpGame.getPlayer());
			return ;
		}

		long count = tmpGame.getTime() * 60000;
		String dispCuritem;

		count -= (System.currentTimeMillis() - tmpGame.getTimeShuffle());
		count /= 1000;

		if (tmpGame.getCurrentItem() == null) {
			dispCuritem = Utils.getLangString(km, tmpGame.getPlayer().getName(), "SOMETHING_NEW");
		} else {
			if (tmpGame.getItemDisplay().contains("/")) {
				dispCuritem = Utils.getLangString(km, tmpGame.getPlayer().getName(), "ITEM_DOUBLE").replace("[#]", tmpGame.getItemDisplay().split("/")[0]).replace("[##]", tmpGame.getItemDisplay().split("/")[1]);
			} else {
				dispCuritem = tmpGame.getItemDisplay();
			}
		}

		ChatColor color = null;

		if (count < 30) {
			color = ChatColor.RED;
		} else if (count < 60) {
			color = ChatColor.YELLOW;
		} else {
			color = ChatColor.GREEN;
		}

		ActionBar.sendMessage(color + Utils.getLangString(km, tmpGame.getPlayer().getName(), "COUNTDOWN").replace("%i", "" + count).replace("%s", dispCuritem), tmpGame.getPlayer());

	}

	private boolean checkTeamMates(Game tmpGame) {
		for (String playerName : km.games.keySet()) {
			if (km.games.get(playerName).getTeamName().equals(tmpGame.getTeamName()) &&
					km.games.get(playerName).getAge() <= tmpGame.getAge() &&
					km.games.get(playerName).getItemCount() < (km.config.getItemPerAge() + 1)) {
				return false;
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

	private int getWorstRank() {
		int cntRank = km.playerRank.size();

		while (cntRank >= 1 && km.playerRank.containsValue(cntRank)) {
			cntRank--;
		}

		return cntRank;
	}

	private void newItem(Game tmpGame) {
		if (km.config.getDouble()) {
			String currentItem = newItemSingle(tmpGame);
			tmpGame.addToAlreadyGot(currentItem);

			String currentItem2 = newItemSingle(tmpGame);
			tmpGame.addToAlreadyGot(currentItem2);

			tmpGame.setCurrentItem(currentItem + "/" + currentItem2);
		} else {
			tmpGame.setCurrentItem(newItemSingle(tmpGame));
		}
	}

	private String newItemSingle(Game tmpGame) {
		if (tmpGame.getAlreadyGot().size() >= km.allItems.get(AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name).size()) {
			tmpGame.resetList();
		}

		String ret;

		if (km.config.getSame()) {
			Pair tmpPair = ItemManager.nextItem(tmpGame.getAlreadyGot(), km.allItems.get(AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name), tmpGame.getSameIdx());

			tmpGame.setSameIdx(tmpPair.key);
			ret = tmpPair.value;
		} else {
			ret = ItemManager.newItem(tmpGame.getAlreadyGot(), km.allItems.get(AgeManager.getAgeByNumber(km.ages, tmpGame.getAge()).name));
		}

		return ret;
	}

	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
