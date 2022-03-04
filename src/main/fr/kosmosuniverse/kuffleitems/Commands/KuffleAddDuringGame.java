package main.fr.kosmosuniverse.kuffleitems.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Game;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleAddDuringGame implements CommandExecutor {
	private KuffleMain km;

	public KuffleAddDuringGame(KuffleMain _km) {
		km = _km;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-abandon>"));

		if (!player.hasPermission("ki-add-during-game")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));
			return false;
		}

		if (km.gameStarted) {
			if (args.length != 1 && args.length != 2) {
				return false;
			}

			Player retPlayer;

			if ((retPlayer = KuffleList.searchPlayerByName(args[0])) == null) {
				return true;
			}

			if (km.config.getTeam() && args.length == 2) {
				if (!km.teams.hasTeam(args[1])) {
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_NOT_EXISTS").replace("<#>", "<" + args[1] + ">"));
					return true;
				} else if (km.teams.getTeam(args[1]).players.size() == km.config.getTeamSize()) {
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "TEAM_FULL"));
					return true;
				}

				startPlayer(player, retPlayer, args[1]);
			} else if (args.length == 1 && !km.config.getTeam()) {
				startPlayer(player, retPlayer, null);
			} else {
				return false;
			}
		} else {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "GAME_NOT_LAUNCHED"));
		}

		return true;
	}

	private void startPlayer(Player sender, Player player, String team) {
		km.paused = true;

		km.games.put(player.getName(), new Game(km, player));
		km.logs.writeMsg(sender, Utils.getLangString(km, sender.getName(), "ADDED_ONE_LIST"));

		if (team != null) {
			km.teams.affectPlayer(team, player);
			km.logs.writeMsg(sender, Utils.getLangString(km, sender.getName(), "TEAM_ADD_PLAYER").replace("<#>", "<" + team + ">").replace("<##>", "<" + player.getName() + ">"));
			km.games.get(player.getName()).setTeamName(km.teams.findTeamByPlayer(player.getName()));
			km.games.get(player.getName()).setSpawnLoc(km.games.get(km.teams.getTeam(team).getPlayersName().get(0)).getSpawnLoc());
			km.playerRank.put(km.games.get(player.getName()).getTeamName(), 0);

			player.setBedSpawnLocation(km.games.get(km.teams.getTeam(team).getPlayersName().get(0)).getSpawnLoc(), true);
			player.teleport(km.games.get(km.teams.getTeam(team).getPlayersName().get(0)).getPlayer());
		} else {
			km.games.get(player.getName()).setSpawnLoc(player.getLocation());
			km.games.get(player.getName()).getSpawnLoc().add(0, -1, 0).getBlock().setType(Material.BEDROCK);
			km.playerRank.put(player.getName(), 0);

			player.setBedSpawnLocation(player.getLocation(), true);
		}

		player.sendMessage(Utils.getLangString(km, sender.getName(), "GAME_STARTED"));

		km.games.get(player.getName()).setup();
		km.scores.setupPlayerScores(km.games.get(player.getName()));
		km.updatePlayersHead();

		km.paused = false;

		player.getInventory().addItem(KuffleStart.getStartBox());
		km.games.get(player.getName()).updatePlayerListName();

		if (km.config.getSaturation()) {
			player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 999999, 10, false, false, false));
		}
	}
}
