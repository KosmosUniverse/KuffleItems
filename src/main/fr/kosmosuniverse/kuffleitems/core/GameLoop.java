package main.fr.kosmosuniverse.kuffleitems.core;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Pair;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class GameLoop {
	private BukkitTask runnable;
	private boolean finished = false;

	public void startRunnable() {
		final ThreadLocalRandom random = ThreadLocalRandom.current();
		
		runnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (KuffleMain.paused) {
					return ;
				}

				int bestRank = getBestRank();
				int worstRank = getWorstRank();

				if (KuffleMain.config.getGameEnd() && !finished) {
					int lasts = Utils.playerLasts();

					if (lasts == 0) {
						Utils.printGameEnd();
						finished = true;
					} else if (lasts == 1 && KuffleMain.config.getEndOne()) {
						Utils.forceFinish(bestRank);
					}
				}

				for (String playerName : KuffleMain.games.keySet()) {
					Game tmpGame = KuffleMain.games.get(playerName);

					if (tmpGame.getLose()) {
						if (!tmpGame.getFinished()) {
							tmpGame.finish(worstRank);
							worstRank = getWorstRank();
						}
					} else if (tmpGame.getFinished()) {
						tmpGame.randomBarColor();
					} else {
						if (tmpGame.getCurrentItem() == null) {
							if (tmpGame.getItemCount() >= (KuffleMain.config.getItemPerAge() + 1)) {
								if (!KuffleMain.config.getTeam() || checkTeamMates(tmpGame)) {
									if ((tmpGame.getAge() + 1) == KuffleMain.config.getMaxAges()) {
										tmpGame.finish(bestRank);
										bestRank = getBestRank();
										KuffleMain.gameLogs.logSystemMsg(tmpGame.getPlayer().getName() + " complete this game !");

										for (String toSend : KuffleMain.games.keySet()) {
											KuffleMain.games.get(toSend).getPlayer().sendMessage(Utils.getLangString(toSend, "GAME_COMPLETE").replace("<#>", ChatColor.GOLD + "" + ChatColor.BOLD + tmpGame.getPlayer().getName() + ChatColor.BLUE));
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
								tmpGame.getPlayer().sendMessage(ChatColor.RED + Utils.getLangString(tmpGame.getPlayer().getName(), "ITEM_NOT_FOUND"));
								KuffleMain.gameLogs.logSystemMsg("Player : " + tmpGame.getPlayer().getName() + " did not found item : " + tmpGame.getCurrentItem());
								newItem(tmpGame);
							} else if (KuffleMain.config.getDouble() && !tmpGame.getCurrentItem().contains("/")) {
								String currentTmp = ItemManager.newItem(tmpGame.getAlreadyGot(), KuffleMain.allItems.get(AgeManager.getAgeByNumber(KuffleMain.ages, tmpGame.getAge()).name));

								tmpGame.addToAlreadyGot(currentTmp);
								tmpGame.setCurrentItem(tmpGame.getCurrentItem() + "/" + currentTmp);
							} else if (!KuffleMain.config.getDouble() && tmpGame.getCurrentItem().contains("/")) {
								String[] array = tmpGame.getCurrentItem().split("/");

								tmpGame.setCurrentItem(array[random.nextInt(2)]);
								tmpGame.removeFromList(array);
							}
						}

						printTimerItem(tmpGame);
					}
				}
			}
		}.runTaskTimer(KuffleMain.current, 0, 20);
	}

	private void printTimerItem(Game tmpGame) {
		if (KuffleMain.config.getTeam() && tmpGame.getItemCount() >= (KuffleMain.config.getItemPerAge() + 1)) {
			ActionBar.sendMessage(ChatColor.LIGHT_PURPLE + Utils.getLangString(tmpGame.getPlayer().getName(), "TEAM_WAIT"), tmpGame.getPlayer());
			return ;
		}

		long count = tmpGame.getTime() * 60000;
		String dispCuritem;

		count -= (System.currentTimeMillis() - tmpGame.getTimeShuffle());
		count /= 1000;

		if (tmpGame.getCurrentItem() == null) {
			dispCuritem = Utils.getLangString(tmpGame.getPlayer().getName(), "SOMETHING_NEW");
		} else {
			if (tmpGame.getItemDisplay().contains("/")) {
				dispCuritem = Utils.getLangString(tmpGame.getPlayer().getName(), "ITEM_DOUBLE").replace("[#]", tmpGame.getItemDisplay().split("/")[0]).replace("[##]", tmpGame.getItemDisplay().split("/")[1]);
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

		ActionBar.sendMessage(color + Utils.getLangString(tmpGame.getPlayer().getName(), "COUNTDOWN").replace("%i", "" + count).replace("%s", dispCuritem), tmpGame.getPlayer());

	}

	private boolean checkTeamMates(Game tmpGame) {
		for (String playerName : KuffleMain.games.keySet()) {
			if (KuffleMain.games.get(playerName).getTeamName().equals(tmpGame.getTeamName()) &&
					KuffleMain.games.get(playerName).getAge() <= tmpGame.getAge() &&
					KuffleMain.games.get(playerName).getItemCount() < (KuffleMain.config.getItemPerAge() + 1)) {
				return false;
			}
		}

		return true;
	}

	private int getBestRank() {
		int cntRank = 1;

		while (cntRank <= KuffleMain.playerRank.size() && KuffleMain.playerRank.containsValue(cntRank)) {
			cntRank++;
		}

		return cntRank;
	}

	private int getWorstRank() {
		int cntRank = KuffleMain.playerRank.size();

		while (cntRank >= 1 && KuffleMain.playerRank.containsValue(cntRank)) {
			cntRank--;
		}

		return cntRank;
	}

	private void newItem(Game tmpGame) {
		if (KuffleMain.config.getDouble()) {
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
		if (tmpGame.getAlreadyGot().size() >= KuffleMain.allItems.get(AgeManager.getAgeByNumber(KuffleMain.ages, tmpGame.getAge()).name).size()) {
			tmpGame.resetList();
		}

		String ret;

		if (KuffleMain.config.getSame()) {
			Pair tmpPair = ItemManager.nextItem(tmpGame.getAlreadyGot(), KuffleMain.allItems.get(AgeManager.getAgeByNumber(KuffleMain.ages, tmpGame.getAge()).name), tmpGame.getSameIdx());

			tmpGame.setSameIdx(tmpPair.key);
			ret = tmpPair.value;
		} else {
			ret = ItemManager.newItem(tmpGame.getAlreadyGot(), KuffleMain.allItems.get(AgeManager.getAgeByNumber(KuffleMain.ages, tmpGame.getAge()).name));
		}

		return ret;
	}

	public void kill() {
		if (runnable != null) {
			runnable.cancel();
		}
	}
}
