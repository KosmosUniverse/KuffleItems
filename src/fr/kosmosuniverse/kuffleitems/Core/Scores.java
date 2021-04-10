package fr.kosmosuniverse.kuffleitems.Core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class Scores {
	private KuffleMain km;
	private Scoreboard scoreboard;
	private Objective age;
	private Objective items;
	private ArrayList<Score> S_ages = new ArrayList<Score>();
	
	public Scores(KuffleMain _km) {
		km = _km;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		age = scoreboard.registerNewObjective("ages", "dummy", ChatColor.LIGHT_PURPLE + "Ages");
		items = scoreboard.registerNewObjective("items", "dummy", "Items");
		
		int ageCnt = 0;
		
		for (String ageStr : km.ageNames) {
			S_ages.add(age.getScore(Utils.getColor(ageCnt) + ageStr + " Age"));
			ageCnt++;
		}
		
		ageCnt = 1;
		
		for (Score ageScore : S_ages) {
			ageScore.setScore(ageCnt);
			ageCnt++;
		}
	}
	
	public void setupPlayerScores() {
		items.setDisplaySlot(DisplaySlot.PLAYER_LIST);
		age.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		for (String playerName : km.games.keySet()) {
			km.games.get(playerName).setBlockScore(items.getScore(km.games.get(playerName).getPlayer().getDisplayName()));
			km.games.get(playerName).getItemScore().setScore(1);
			km.games.get(playerName).getPlayer().setScoreboard(scoreboard);
			
			if (km.config.getTeam()) {
				km.games.get(playerName).getPlayer().setPlayerListName("[" + km.teams.getTeam(km.games.get(playerName).getTeamName()).color + km.games.get(playerName).getTeamName() + ChatColor.RESET + "] - " + ChatColor.RED + km.games.get(playerName).getPlayer().getName());
			} else {
				km.games.get(playerName).getPlayer().setPlayerListName(ChatColor.RED + km.games.get(playerName).getPlayer().getName());	
			}
			
			
		}
	}
	
	public void clear() {
		scoreboard.clearSlot(age.getDisplaySlot());
		scoreboard.clearSlot(items.getDisplaySlot());
		
		for (String playerName : km.games.keySet()) {
			km.games.get(playerName).getPlayer().setPlayerListName(ChatColor.WHITE + km.games.get(playerName).getPlayer().getName());
		}
	}
	
	public void reset() {
		for (String playerName : km.games.keySet()) {
			km.games.get(playerName).getItemScore().setScore(1);;
			km.games.get(playerName).getPlayer().setPlayerListName(ChatColor.RED + km.games.get(playerName).getPlayer().getName());
		}
	}
}
