package main.fr.kosmosuniverse.kuffleitems.Commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Core.Game;
import main.fr.kosmosuniverse.kuffleitems.Utils.Utils;

public class KuffleList implements CommandExecutor {
	private KuffleMain km;
	private static final String noPlayers = "NO_PLAYERS";

	public KuffleList(KuffleMain _km) {
		km = _km;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
		if (!(sender instanceof Player))
			return false;

		Player player = (Player) sender;

		km.logs.logMsg(player, Utils.getLangString(km, player.getName(), "CMD_PERF").replace("<#>", "<ki-list>"));

		if (!player.hasPermission("ki-list")) {
			km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "NOT_ALLOWED"));

			return false;
		}

		if (args.length == 0) {
			if (km.games.size() == 0) {
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), noPlayers));
			} else {
				StringBuilder sb = new StringBuilder();
				int i = 0;

				for (String playerName : km.games.keySet()) {
					if (i == 0) {
						sb.append(playerName);
					} else {
						sb.append(", ").append(playerName);
					}

					i++;
				}

				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_LIST") + " " + sb.toString());
			}

			return true;
		} else if (args.length == 1) {
			if (args[0].equals("reset")) {
				if (km.games.size() == 0) {
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), noPlayers));

					return false;
				}

				km.games.clear();
				km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "LIST_RESET"));

				return true;
			}
		} else if (args.length == 2) {
			if (args[0].equals("add")) {
				if (args[1].equals("@a")) {
					int cnt = 0;
					List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

					for (Player p : players) {
						if (!km.games.containsKey(p.getName())) {
							km.games.put(p.getName(), new Game(km, p));
							cnt++;
						}
					}

					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "ADDED_LIST").replace("%i", "" + cnt));

					return true;
				} else {
					Player retComp;

					if ((retComp = searchPlayerByName(args[1])) != null) {
						if (!km.games.containsKey(retComp.getName())) {
							km.games.put(retComp.getName(), new Game(km, retComp));
							km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "ADDED_ONE_LIST"));

							return true;
						} else {
							km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "PLAYER_ALREADY_LIST"));

							return false;
						}
					} else {
						return false;
					}
				}
			} else if (args[0].equals("remove")) {
				if (km.games.size() == 0) {
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), noPlayers));

					return false;
				}

				if (km.games.containsKey(args[1])) {
					km.games.remove(args[1]);
					km.logs.writeMsg(player, Utils.getLangString(km, player.getName(), "REMOVED_LIST"));

					return true;
				}
			}
		}

		return false;
	}

	static Player searchPlayerByName(String name) {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		Player retPlayer = null;

		for (Player player : players) {
			if (player.getName().contains(name)) {
				retPlayer = player;
			}
		}

		players.clear();

		return retPlayer;
	}

}
