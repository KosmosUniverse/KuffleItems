package fr.kosmosuniverse.kuffleitems.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Core.Game;

public class PlayerInteract implements Listener {
	private KuffleMain km;
	
	public PlayerInteract(KuffleMain _km) {
		km = _km;
	}
	
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (!km.gameStarted) {
			return ;
		}
		
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack item = event.getItem();

		if (!event.hasItem()) {
			return ;
		}
		
		player.sendMessage("INTERACT !!!");
		
		if (action == Action.RIGHT_CLICK_AIR && item != null) {
			player.sendMessage("Left Click !!!");
			
			Game tmpGame = km.games.get(player.getName());
			
			if (tmpGame != null && tmpGame.getCurrentItem().equals(item.getType().name().toLowerCase())) {
				player.sendMessage("Found !!!");
				tmpGame.found();
			} else {
				if (tmpGame == null) {
					player.sendMessage("Player not found !!!");
				} else {
					player.sendMessage("playerItem : [" + tmpGame.getCurrentItem() + "], item: [" + item.getType().name() + "]");
				}
			}
		}
	}
}
