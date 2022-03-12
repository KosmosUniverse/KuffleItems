package main.fr.kosmosuniverse.kuffleitems.Commands;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.ActionBar;
import main.fr.kosmosuniverse.kuffleitems.Core.GameLoop;
import main.fr.kosmosuniverse.kuffleitems.Core.SpreadPlayer;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleStart implements CommandExecutor {
	private KuffleMain km;

	public KuffleStart(KuffleMain _km) {
		km = _km;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		km.systemLogs.logMsg(player.getName(), Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-start>"));

		if (!player.hasPermission("ki-start")) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}

		if (km.games.size() == 0) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "NO_PLAYERS"));

			return false;
		}

		if (km.gameStarted) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_LAUNCHED"));
			return false;
		}

		if (km.config.getSaturation()) {
			km.games.forEach((playerName, game) -> {
				game.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
			});
		}

		int spread = 0;

		if (km.config.getTeam() && !checkTeams()) {
			km.systemLogs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_NOT_IN_TEAM"));
			return true;
		}

		for (String key : km.allItems.keySet()) {
			Collections.shuffle(km.allItems.get(key));
		}

		km.games.forEach((playerName, game) -> {
			game.getPlayer().sendMessage(Utils.getLangString(km, player.getName(), "GAME_STARTED"));
		});

		km.systemLogs.logSystemMsg(Utils.getLangString(km, player.getName(), "GAME_STARTED"));

		if (km.config.getSpread()) {
			if (km.config.getTeam()) {
				SpreadPlayer.spreadPlayers(km, player, km.config.getSpreadDistance(), km.config.getSpreadRadius(), km.teams.getTeams(), Utils.getPlayerList(km.games));
			} else {
				SpreadPlayer.spreadPlayers(km, player, km.config.getSpreadDistance(), km.config.getSpreadRadius(), null, Utils.getPlayerList(km.games));
			}

			km.games.forEach((playerName, game) -> {
				if (km.config.getTeam()) {
					game.setTeamName(km.teams.findTeamByPlayer(playerName));
				}

				game.getPlayer().setBedSpawnLocation(game.getPlayer().getLocation(), true);
				game.setSpawnLoc(game.getPlayer().getLocation());
				game.getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			});

			spread = 20;
		} else {
			Location spawn = null;

			for (String playerName : km.games.keySet()) {
				if (km.config.getTeam()) {
					km.games.get(playerName).setTeamName(km.teams.findTeamByPlayer(playerName));
				}

				if (spawn == null) {
					spawn = km.games.get(playerName).getPlayer().getLocation().getWorld().getSpawnLocation();
				}

				km.games.get(playerName).setSpawnLoc(spawn);
			}

			if (spawn != null) {
				spawn.subtract(0, 1, 0).getBlock().setType(Material.BEDROCK);
			}
		}

		km.playersHeads = Bukkit.createInventory(null, Utils.getNbInventoryRows(km.games.size()), "§8Players");

		km.games.forEach((playerName, game) -> {
			km.playersHeads.addItem(Utils.getHead(game.getPlayer()));

			if (km.config.getTeam() && !km.playerRank.containsKey(game.getTeamName())) {
				km.playerRank.put(game.getTeamName(), 0);
			} else {
				km.playerRank.put(playerName, 0);
			}
		});

		km.paused = true;

		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.RED + "5" + ChatColor.RESET, game.getPlayer());
			});

			if (km.config.getSBTT()) {
				Utils.setupTemplates(km);
			}
		}, 20 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GOLD + "4" + ChatColor.RESET, game.getPlayer());
			});
		}, 40 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.YELLOW + "3" + ChatColor.RESET, game.getPlayer());
			});
		}, 60 + spread);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.GREEN + "2" + ChatColor.RESET, game.getPlayer());
			});
		}, 80 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.BLUE + "1" + ChatColor.RESET, game.getPlayer());
				game.setup();
			});

			km.scores.setupPlayerScores();
		}, 100 + spread);

		Bukkit.getScheduler().scheduleSyncDelayedTask(km, () -> {
			ItemStack box = getStartBox();

			km.games.forEach((playerName, game) -> {
				ActionBar.sendRawTitle(ChatColor.BOLD + "" + ChatColor.DARK_PURPLE + "GO!" + ChatColor.RESET, game.getPlayer());
				game.getPlayer().getInventory().addItem(box);
			});

			km.playerInteract.setXpSub(10);
			km.loop = new GameLoop(km);
			km.loop.startRunnable();
			km.gameStarted = true;
			km.paused = false;
		}, 120 + spread);

		return true;
	}

	static ItemStack getStartBox() {
		ItemStack item = new ItemStack(Material.WHITE_SHULKER_BOX);
		ItemMeta itM = item.getItemMeta();

		itM.setDisplayName("Start Box");
		item.setItemMeta(itM);

		return item;
	}

	public boolean checkTeams() {
		for (String playerName : km.games.keySet()) {
			if (!km.teams.isInTeam(playerName)) {
				return false;
			}
		}

		return true;
	}
}
