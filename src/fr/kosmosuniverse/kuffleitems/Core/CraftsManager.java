package fr.kosmosuniverse.kuffleitems.Core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.kosmosuniverse.kuffleitems.KuffleMain;
import fr.kosmosuniverse.kuffleitems.Crafts.ACrafts;
import fr.kosmosuniverse.kuffleitems.Crafts.Bell;
import fr.kosmosuniverse.kuffleitems.Crafts.Coal;
import fr.kosmosuniverse.kuffleitems.Crafts.CoalOre;
import fr.kosmosuniverse.kuffleitems.Crafts.Diamond;
import fr.kosmosuniverse.kuffleitems.Crafts.DiamondHorseArmor;
import fr.kosmosuniverse.kuffleitems.Crafts.DiamondOre;
import fr.kosmosuniverse.kuffleitems.Crafts.Emerald;
import fr.kosmosuniverse.kuffleitems.Crafts.EmeraldOre;
import fr.kosmosuniverse.kuffleitems.Crafts.EndPortalFrame;
import fr.kosmosuniverse.kuffleitems.Crafts.EndTeleporter;
import fr.kosmosuniverse.kuffleitems.Crafts.GoldHorseArmor;
import fr.kosmosuniverse.kuffleitems.Crafts.IronHorseArmor;
import fr.kosmosuniverse.kuffleitems.Crafts.Lapis;
import fr.kosmosuniverse.kuffleitems.Crafts.LapisOre;
import fr.kosmosuniverse.kuffleitems.Crafts.MossyCobblestone;
import fr.kosmosuniverse.kuffleitems.Crafts.MossyStoneBrick;
import fr.kosmosuniverse.kuffleitems.Crafts.OverworldTeleporter;
import fr.kosmosuniverse.kuffleitems.Crafts.Quartz;
import fr.kosmosuniverse.kuffleitems.Crafts.QuartzOre;
import fr.kosmosuniverse.kuffleitems.Crafts.RedNetherBrick;
import fr.kosmosuniverse.kuffleitems.Crafts.RedSand;
import fr.kosmosuniverse.kuffleitems.Crafts.Redstone;
import fr.kosmosuniverse.kuffleitems.Crafts.RedstoneOre;
import fr.kosmosuniverse.kuffleitems.Crafts.Saddle;

public class CraftsManager {
	private ArrayList<ACrafts> recipes = new ArrayList<ACrafts>();
	
	public CraftsManager(KuffleMain _km) {
		if (!_km.config.getCrafts()) {
			return;
		}
		
		recipes.add(new RedSand(_km));
		recipes.add(new MossyCobblestone(_km));
		recipes.add(new MossyStoneBrick(_km));
		
		recipes.add(new Coal(_km));
		recipes.add(new Lapis(_km));
		recipes.add(new Redstone(_km));
		recipes.add(new Diamond(_km));
		recipes.add(new Emerald(_km));
		recipes.add(new Quartz(_km));
		
		recipes.add(new CoalOre(_km));
		recipes.add(new LapisOre(_km));
		recipes.add(new RedstoneOre(_km));
		recipes.add(new DiamondOre(_km));
		recipes.add(new EmeraldOre(_km));
		recipes.add(new QuartzOre(_km));
		
		recipes.add(new RedNetherBrick(_km));
		
		recipes.add(new EndPortalFrame(_km));
		recipes.add(new Bell(_km));
		recipes.add(new EndTeleporter(_km));
		recipes.add(new OverworldTeleporter(_km));
		
		recipes.add(new Saddle(_km));
		recipes.add(new IronHorseArmor(_km));
		recipes.add(new GoldHorseArmor(_km));
		recipes.add(new DiamondHorseArmor(_km));
	}
	
	public ArrayList<ACrafts> getRecipeList() {
		return (recipes);
	}
	
	public Inventory getAllCraftsInventory() {
		Inventory inv = Bukkit.createInventory(null, getNbRows(), "�8AllCustomCrafts");
		int i = 0;
		
		for (ACrafts item : recipes) {
			inv.setItem(i, item.getItem());
			i++;
		}
		
		return (inv);
	}
	
	private int getNbRows() {
		int rows = recipes.size() / 9;
		
		if (recipes.size() % 9 == 0) {
			return rows * 9;
		} else {
			return (rows + 1) * 9;
		}
	}
	
	public ACrafts findCraftInventoryByItem(ItemStack item) {
		for (ACrafts craft : recipes) {
			if (craft.getItem().getType() == item.getType()) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ACrafts findCraftInventoryByName(String name) {
		for (ACrafts craft : recipes) {
			if (name.contains(craft.getName())) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ACrafts findCraftByInventoryName(String invName) {
		for (ACrafts craft : recipes) {
			if (invName.contains(craft.getName())) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ItemStack findItemByName(String itemName) {
		for (ACrafts craft : recipes) {
			if (itemName.equals(craft.getName())) {
				return (craft.getItem());
			}
		}
		
		return null;
	}
}