package main.fr.kosmosuniverse.kuffleitems.core;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.Crafts.ores.*;
import main.fr.kosmosuniverse.kuffleitems.Crafts.resources.*;
import main.fr.kosmosuniverse.kuffleitems.crafts.*;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.ChainmailBoots;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.ChainmailChestplate;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.ChainmailHelmet;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.ChainmailLeggings;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.DiamondHorseArmor;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.GoldHorseArmor;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.IronHorseArmor;
import main.fr.kosmosuniverse.kuffleitems.crafts.armors.Saddle;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.BuddingAmethyst;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.MossBlock;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.MossyCobblestone;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.MossyStoneBrick;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.Mycelium;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.PointedDripstone;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.PowderSnowBucket;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.RedNetherBrick;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.RedSand;
import main.fr.kosmosuniverse.kuffleitems.crafts.naturals.SmallDripleaf;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.CoalOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.CoalOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.CopperOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.CopperOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.DiamondOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.DiamondOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.EmeraldOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.EmeraldOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.ExposedCopper;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.GoldOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.GoldOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.IronOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.IronOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.LapisOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.LapisOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.OxidizedCopper;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.QuartzOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.RedstoneOre;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.RedstoneOreDeepslate;
import main.fr.kosmosuniverse.kuffleitems.crafts.ores.WeatheredCopper;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.BrainCoralBlock;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.BubbleCoralBlock;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Coal;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Diamond;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Emerald;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.FireCoralBlock;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.HornCoralBlock;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Lapis;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Quartz;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.RawCopper;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.RawGold;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.RawIron;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.Redstone;
import main.fr.kosmosuniverse.kuffleitems.crafts.resources.TubeCoralBlock;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;
import main.fr.kosmosuniverse.kuffleitems.Crafts.armors.*;
import main.fr.kosmosuniverse.kuffleitems.Crafts.naturals.*;

public class CraftsManager {
	private ArrayList<ACrafts> recipes = new ArrayList<ACrafts>();
	
	public CraftsManager(KuffleMain _km) {
		recipes.add(new EndPortalFrame(_km));
		recipes.add(new EndTeleporter(_km));
		recipes.add(new OverworldTeleporter(_km));
		
		if (Utils.findVersionNumber(_km, Utils.getVersion()) >= Utils.findVersionNumber(_km, "1.16")) {
			recipes.add(new ChainmailHelmet(_km));
			recipes.add(new ChainmailChestplate(_km));
			recipes.add(new ChainmailLeggings(_km));
			recipes.add(new ChainmailBoots(_km));
		}
		
		if (Utils.findVersionNumber(_km, Utils.getVersion()) >= Utils.findVersionNumber(_km, "1.17")) {
			recipes.add(new MossBlock(_km));
			recipes.add(new SmallDripleaf(_km));
			recipes.add(new PowderSnowBucket(_km));
			recipes.add(new BuddingAmethyst(_km));
		}
		
		if (!_km.config.getCrafts()) {
			return;
		}
		
		recipes.add(new RedSand(_km));
		recipes.add(new Mycelium(_km));
		recipes.add(new MossyCobblestone(_km));
		recipes.add(new MossyStoneBrick(_km));
		
		recipes.add(new TubeCoralBlock(_km));
		recipes.add(new BubbleCoralBlock(_km));
		recipes.add(new HornCoralBlock(_km));
		recipes.add(new FireCoralBlock(_km));
		recipes.add(new BrainCoralBlock(_km));
		
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
		
		recipes.add(new Bell(_km));
		
		recipes.add(new Saddle(_km));
		recipes.add(new IronHorseArmor(_km));
		recipes.add(new GoldHorseArmor(_km));
		recipes.add(new DiamondHorseArmor(_km));
		
		if (Utils.findVersionNumber(_km, Utils.getVersion()) >= Utils.findVersionNumber(_km, "1.17")) {
			recipes.add(new CoalOreDeepslate(_km));
			recipes.add(new CopperOreDeepslate(_km));
			recipes.add(new DiamondOreDeepslate(_km));
			recipes.add(new EmeraldOreDeepslate(_km));
			recipes.add(new GoldOreDeepslate(_km));
			recipes.add(new IronOreDeepslate(_km));
			recipes.add(new LapisOreDeepslate(_km));
			recipes.add(new RedstoneOreDeepslate(_km));
			recipes.add(new CopperOre(_km));
			recipes.add(new GoldOre(_km));
			recipes.add(new IronOre(_km));
			recipes.add(new RawCopper(_km));
			recipes.add(new RawGold(_km));
			recipes.add(new RawIron(_km));
			recipes.add(new PointedDripstone(_km));
			recipes.add(new ExposedCopper(_km));
			recipes.add(new WeatheredCopper(_km));
			recipes.add(new OxidizedCopper(_km));
		}
	}
	
	public void clear() {
		if (recipes != null) {
			recipes.clear();
		}
	}
	
	public void addCraft(ACrafts craft) {
		recipes.add(craft);
	}
	
	public void removeCraft(String name) {
		ACrafts craft = null;
		
		for (ACrafts tmp : recipes) {
			if (tmp.getName().equals(name)) {
				craft = tmp;
			}
		}
		
		if (craft != null) {
			recipes.remove(craft);
		}
	}
	
	public ArrayList<ACrafts> getRecipeList() {
		return (recipes);
	}
	
	public Inventory getAllCraftsInventory() {
		Inventory inv = Bukkit.createInventory(null, Utils.getNbInventoryRows(recipes.size()), "§8AllCustomCrafts");
		int i = 0;
		
		for (ACrafts item : recipes) {
			inv.setItem(i, item.getItem());
			i++;
		}
		
		return (inv);
	}
	
	
	
	public ACrafts findCraftInventoryByItem(ItemStack item) {
		for (ACrafts craft : recipes) {
			if (Utils.compareItems(craft.getItem(), item, item.hasItemMeta(),
					item.hasItemMeta() ? item.getItemMeta().hasDisplayName() : false,
					item.hasItemMeta() ? item.getItemMeta().hasLore() : false)) {
				return (craft);
			}
		}
		
		return null;
	}
	
	public ACrafts findCraftByInventoryName(String invName) {
		for (ACrafts craft : recipes) {
			String name = "§8" + craft.getName();
			
			if (invName.contains(name)) {
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
