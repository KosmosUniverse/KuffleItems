 package main.fr.kosmosuniverse.kuffleitems.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.simple.JSONObject;

import main.fr.kosmosuniverse.kuffleitems.KuffleMain;
import main.fr.kosmosuniverse.kuffleitems.utils.Utils;

public class Config {
	private static final String CONFIG_DEFAULT = "CONFIG_DEFAULT";
	
	private boolean saturation;
	private boolean spread;
	private boolean rewards;
	private boolean skip;
	private boolean crafts;
	private boolean team;
	private boolean same;
	private boolean duoMode;
	private boolean sbttMode;
	private boolean gameEnd;
	private boolean endOne;
	private boolean passive;
	private int sbttAmount;
	private int teamSize;
	private int spreadDistance;
	private int spreadRadius;
	private int itemPerAge;
	private int skipAge;
	private int maxAges;
	private int startTime;
	private int addedTime;
	private int level;
	private String lang;

	private List<String> ret = new ArrayList<>();

	public Map<String, String> booleanElems = new HashMap<>();
	public Map<String, String> intElems = new HashMap<>();
	public Map<String, String> stringElems = new HashMap<>();

	public Map<String, List<String>> booleanRet = new HashMap<>();
	public Map<String, List<String>> intRet = new HashMap<>();
	public Map<String, List<String>> stringRet = new HashMap<>();

	public Config() {
		ret.add("TRUE");
		ret.add("FALSE");

		booleanElems.put("SATURATION", "setSaturation");
		booleanElems.put("SPREADPLAYERS", "setSpreadplayers");
		booleanElems.put("REWARDS", "setRewards");
		booleanElems.put("SKIP", "setSkip");
		booleanElems.put("CUSTOM_CRAFTS", "setCrafts");
		booleanElems.put("TEAM", "setTeam");
		booleanElems.put("SAME_MODE", "setSame");
		booleanElems.put("DOUBLE_MODE", "setDoubleMode");
		booleanElems.put("SBTT_MODE", "setSbttMode");
		booleanElems.put("AUTO_DETECT_END", "setGameEnd");
		booleanElems.put("END_WHEN_ONE", "setEndOne");
		booleanElems.put("PASSIVE", "setPassive");

		booleanRet.put("SATURATION", ret);
		booleanRet.put("SPREADPLAYERS", ret);
		booleanRet.put("REWARDS", ret);
		booleanRet.put("SKIP", ret);
		booleanRet.put("CUSTOM_CRAFTS", ret);
		booleanRet.put("TEAM", ret);
		booleanRet.put("SAME_MODE", ret);
		booleanRet.put("DOUBLE_MODE", ret);
		booleanRet.put("SBTT_MODE", ret);
		booleanRet.put("AUTO_DETECT_END", ret);
		booleanRet.put("END_WHEN_ONE", ret);
		booleanRet.put("PASSIVE", ret);

		intElems.put("SPREAD_MIN_DISTANCE", "setSpreadDistance");
		intElems.put("SPREAD_MIN_RADIUS", "setSpreadRadius");
		intElems.put("ITEM_PER_AGE", "setItemAge");
		intElems.put("FIRST_AGE_SKIP", "setFirstSkip");
		intElems.put("NB_AGE", "setMaxAge");
		intElems.put("START_DURATION", "setStartTime");
		intElems.put("ADDED_DURATION", "setAddedTime");
		intElems.put("TEAMSIZE", "setTeamSize");
		intElems.put("SBTT_AMOUNT", "setSbttAmount");
		
		ret = new ArrayList<>();

		for (int i = 1; i < 11; i++) {
			ret.add("" + i);
		}

		intRet.put("ITEM_PER_AGE", ret);

		ret = new ArrayList<>();

		for (int i = 2; i < 11; i++) {
			ret.add("" + i);
		}

		intRet.put("TEAMSIZE", ret);

		ret = new ArrayList<>();

		ret.add("100");

		for (int i = 1; i < 11; i++) {
			ret.add("" + (i + 100));
		}

		intRet.put("SPREAD_MIN_DISTANCE", ret);

		ret = new ArrayList<>();

		ret.add("500");

		for (int i = 1; i < 11; i++) {
			ret.add("" + (i + 100));
		}

		intRet.put("SPREAD_MIN_RADIUS", ret);
		intRet.put("FIRST_AGE_SKIP", null);

		ret = new ArrayList<>();

		for (int i = 1; i <= AgeManager.getAgeMaxNumber(KuffleMain.ages) + 1; i++) {
			ret.add("" + i);
		}

		intRet.put("NB_AGE", ret);

		ret = new ArrayList<>();

		for (int i = 1; i < 7; i++) {
			ret.add("" + i);
		}
		
		intRet.put("START_DURATION", ret);

		ret = new ArrayList<>();

		for (int i = 1; i < 6; i++) {
			ret.add("" + i);
		}

		intRet.put("ADDED_DURATION", ret);

		ret = new ArrayList<>();

		for (int i = 1; i < 10; i++) {
			ret.add("" + i);
		}
		
		intRet.put("SBTT_AMOUNT", ret);
		
		ret = new ArrayList<>();

		for (Level l : KuffleMain.levels) {
			ret.add(l.name);
		}

		stringElems.put("LANG", "setLang");
		stringElems.put("LEVEL", "setLevel");

		stringRet.put("LANG", KuffleMain.langs);
		stringRet.put("LEVEL", ret);
	}

	public void setupConfig(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.lang")
				|| !KuffleMain.langs.contains(configFile.getString("game_settings.lang"))) {
			lang = "en";
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "lang"));
			configFile.set("game_settings.lang", "en");
		} else {
			lang = configFile.getString("game_settings.lang");
		}
		
		setupSpread(configFile);
		setupModes(configFile);
		setupStart(configFile);
		setupOther(configFile);
		setupEnd(configFile);
		
		setValues(configFile);

		ret = new ArrayList<>();

		for (int i = 1; i < maxAges + 1; i++) {
			ret.add("" + i);
		}

		intRet.put("FIRST_AGE_SKIP", ret);
	}
	
	private void setupSpread(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.spreadplayers.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling spreadplayers"));
			configFile.set("game_settings.spreadplayers.enable", false);
		}
		
		if (!configFile.contains("game_settings.spreadplayers.minimum_distance")
				|| configFile.getInt("game_settings.spreadplayers.minimum_distance") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "spreadplayers minimum distance"));
			configFile.set("game_settings.spreadplayers.minimum_distance", 500);
		}

		if (!configFile.contains("game_settings.spreadplayers.minimum_radius")
				|| configFile.getInt("game_settings.spreadplayers.minimum_radius") < configFile
						.getInt("game_settings.spreadplayers.minimum_distance")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "spreadplayers minimum radius"));
			configFile.set("game_settings.spreadplayers.minimum_radius", 1000);
		}
	}
	
	private void setupModes(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.team.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling team"));
			configFile.set("game_settings.team.enable", false);
		}

		if (!configFile.contains("game_settings.team.size") || configFile.getInt("game_settings.team.size") < 2
				|| configFile.getInt("game_settings.team.size") > 10) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "max team size"));
			configFile.set("game_settings.team.size", 2);
		}
		
		if (!configFile.contains("game_settings.same_mode")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling same mode"));
			configFile.set("game_settings.same_mode", false);
		}
		
		if (!configFile.contains("game_settings.sbtt_mode.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "SBTT mode"));
			configFile.set("game_settings.sbtt_mode.enable", false);
		}
		
		if (!configFile.contains("game_settings.sbtt_mode.amount") ||
				configFile.getInt("game_settings.sbtt_mode.amount") < 1 ||
				configFile.getInt("game_settings.sbtt_mode.amount") > 9) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "SBTT item amount"));
			configFile.set("game_settings.sbtt_mode.amount", 4);
		}
		
		if (!configFile.contains("game_settings.double_mode")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "Double mode"));
			configFile.set("game_settings.double_mode", false);
		}
		
		if (!configFile.contains("game_settings.passive")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "Passive mode"));
			configFile.set("game_settings.passive", true);
		}
	}
	
	private void setupStart(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.item_per_age")
				|| configFile.getInt("game_settings.item_per_age") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "item per age"));
			configFile.set("game_settings.item_per_age", 5);
		}
		
		if (!configFile.contains("game_settings.start_time") || configFile.getInt("game_settings.start_time") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "start time"));
			configFile.set("game_settings.start_time", 4);
		}

		if (!configFile.contains("game_settings.time_added") || configFile.getInt("game_settings.time_added") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "time added"));
			configFile.set("game_settings.time_added", 2);
		}

		if (!configFile.contains("game_settings.max_age") || configFile.getInt("game_settings.max_age") < 1 || configFile.getInt("game_settings.max_age") > (AgeManager.getAgeMaxNumber(KuffleMain.ages) + 1)) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "max ages"));
			configFile.set("game_settings.max_age", AgeManager.getAgeMaxNumber(KuffleMain.ages) + 1);
		}

		if (!configFile.contains("game_settings.level") || configFile.getInt("game_settings.level") < 0
				|| configFile.getInt("game_settings.level") > 3) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "level"));
			configFile.set("game_settings.level", 1);
		}
	}
	
	private void setupOther(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.skip.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling skip"));
			configFile.set("game_settings.skip.enable", true);
		}

		if (!configFile.contains("game_settings.skip.age") || configFile.getInt("game_settings.skip.age") < 1) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "skip age"));
			configFile.set("game_settings.skip.age", 2);
		}

		if (!configFile.contains("game_settings.custom_crafts")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling custom crafts"));
			configFile.set("game_settings.custom_crafts", true);
		}
	}
	
	private void setupEnd(FileConfiguration configFile) {
		if (!configFile.contains("game_settings.auto_detect_game_end.enable")) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "enabling auto detect game end"));
			configFile.set("game_settings.auto_detect_game_end.enable", false);
		}
		
		gameEnd = configFile.getBoolean("game_settings.auto_detect_game_end.enable");
		
		if (!configFile.contains("game_settings.auto_detect_game_end.end_when_one") ||
				(!gameEnd && configFile.getBoolean("game_settings.auto_detect_game_end.end_when_one"))) {
			KuffleMain.systemLogs.logSystemMsg(Utils.getLangString(null, CONFIG_DEFAULT).replace("<#>", "game end when one"));
			configFile.set("game_settings.auto_detect_game_end.end_when_one", false);
		}
	}

	private void setValues(FileConfiguration configFile) {
		saturation = configFile.getBoolean("game_settings.saturation");
		spread = configFile.getBoolean("game_settings.spreadplayers.enable");
		rewards = configFile.getBoolean("game_settings.rewards");
		skip = configFile.getBoolean("game_settings.skip.enable");
		crafts = configFile.getBoolean("game_settings.custom_crafts");
		team = configFile.getBoolean("game_settings.team.enable");
		same = configFile.getBoolean("game_settings.same_mode");
		endOne = configFile.getBoolean("game_settings.auto_detect_game_end.end_when_one");
		duoMode = configFile.getBoolean("game_settings.double_mode");
		sbttMode = configFile.getBoolean("game_settings.sbtt_mode.enable");
		passive = configFile.getBoolean("game_settings.passive");
		
		spreadDistance = configFile.getInt("game_settings.spreadplayers.minimum_distance");
		spreadRadius = configFile.getInt("game_settings.spreadplayers.minimum_radius");
		itemPerAge = configFile.getInt("game_settings.item_per_age");
		skipAge = configFile.getInt("game_settings.skip.age");
		maxAges = configFile.getInt("game_settings.max_age");
		startTime = configFile.getInt("game_settings.start_time");
		addedTime = configFile.getInt("game_settings.time_added");
		level = configFile.getInt("game_settings.level");
		teamSize = configFile.getInt("game_settings.team.size");
		sbttAmount = configFile.getInt("game_settings.sbtt_mode.amount");
	}
	
	public String displayConfig() {
		StringBuilder sb = new StringBuilder();

		sb.append("Configuration:").append("\n");
		sb.append("  " + ChatColor.BLUE).append("Saturation: " + ChatColor.GOLD).append(saturation).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Spreadplayers: " + ChatColor.GOLD).append(spread).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Spreadplayer min distance: " + ChatColor.GOLD).append(spreadDistance).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Spreadplayer min radius: " + ChatColor.GOLD).append(spreadRadius).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Rewards: " + ChatColor.GOLD).append(rewards).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Skip: " + ChatColor.GOLD).append(skip).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Crafts: " + ChatColor.GOLD).append(crafts).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Nb item per age: " + ChatColor.GOLD).append(itemPerAge).append("\n");
		sb.append("  " + ChatColor.BLUE).append("First Age for Skipping: " + ChatColor.GOLD).append(skipAge).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Max age: " + ChatColor.GOLD).append(maxAges).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Start duration: " + ChatColor.GOLD).append(startTime).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Added duration: " + ChatColor.GOLD).append(addedTime).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Lang: " + ChatColor.GOLD).append(lang).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Level: " + ChatColor.GOLD).append(LevelManager.getLevelByNumber(KuffleMain.levels, level).name).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Detect Game End: " + ChatColor.GOLD).append(gameEnd).append("\n");
		sb.append("  " + ChatColor.BLUE).append("End game at one: " + ChatColor.GOLD).append(endOne).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Passive: " + ChatColor.GOLD).append(passive).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Team: " + ChatColor.GOLD).append(team).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Team Size: " + ChatColor.GOLD).append(teamSize).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Same mode: " + ChatColor.GOLD).append(same).append("\n");
		sb.append("  " + ChatColor.BLUE).append("Double mode: " + ChatColor.GOLD).append(duoMode).append("\n");
		sb.append("  " + ChatColor.BLUE).append("SBTT mode: " + ChatColor.GOLD).append(sbttMode).append("\n");
		sb.append("  " + ChatColor.BLUE).append("SBTT amount: " + ChatColor.GOLD).append(sbttAmount).append("\n");

		return sb.toString();
	}
	
	public void clear() {
		ret.clear();
		
		booleanElems.clear();
		intElems.clear();
		stringElems.clear();
		
		booleanRet.forEach((k, v) -> v.clear());
		intRet.forEach((k, v) -> v.clear());
		stringRet.forEach((k, v) -> v.clear());
		
		booleanRet.clear();
		intRet.clear();
		stringRet.clear();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject saveConfig() {
		JSONObject configObj = new JSONObject();
		
		configObj.put("saturation", saturation);
		configObj.put("spread", spread);
		configObj.put("rewards", rewards);
		configObj.put("skip", skip);
		configObj.put("crafts", crafts);
		configObj.put("team", team);
		configObj.put("same", same);
		configObj.put("duoMode", duoMode);
		configObj.put("sbttMode", sbttMode);
		configObj.put("gameEnd", gameEnd);
		configObj.put("endOne", endOne);
		configObj.put("sbttAmount", sbttAmount);
		configObj.put("teamSize", teamSize);
		configObj.put("spreadMin", spreadDistance);
		configObj.put("spreadMax", spreadRadius);
		configObj.put("itemPerAge", itemPerAge);
		configObj.put("skipAge", skipAge);
		configObj.put("maxAges", maxAges);
		configObj.put("startTime", startTime);
		configObj.put("addedTime", addedTime);
		configObj.put("level", level);
		configObj.put("lang", lang);
		
		return configObj;
	}
	
	public void loadConfig(JSONObject configObj) {
		saturation = (boolean) configObj.get("saturation");
		spread = (boolean) configObj.get("spread");
		rewards = (boolean) configObj.get("rewards");
		skip = (boolean) configObj.get("skip");
		crafts = (boolean) configObj.get("crafts");
		team = (boolean) configObj.get("team");
		same = (boolean) configObj.get("same");
		duoMode = (boolean) configObj.get("duoMode");
		sbttMode = (boolean) configObj.get("sbttMode");
		gameEnd = (boolean) configObj.get("gameEnd");
		endOne = (boolean) configObj.get("endOne");
		
		sbttAmount = Integer.parseInt(configObj.get("sbttAmount").toString());
		teamSize = Integer.parseInt(configObj.get("teamSize").toString());
		spreadDistance = Integer.parseInt(configObj.get("spreadMin").toString());
		spreadRadius = Integer.parseInt(configObj.get("spreadMax").toString());
		itemPerAge = Integer.parseInt(configObj.get("itemPerAge").toString());
		skipAge = Integer.parseInt(configObj.get("skipAge").toString());
		maxAges = Integer.parseInt(configObj.get("maxAges").toString());
		startTime = Integer.parseInt(configObj.get("startTime").toString());
		addedTime = Integer.parseInt(configObj.get("addedTime").toString());
		level = Integer.parseInt(configObj.get("level").toString());
		
		lang = configObj.get("lang").toString();
	}

	public boolean getSaturation() {
		return saturation;
	}

	public boolean getSpread() {
		return spread;
	}

	public boolean getRewards() {
		return rewards;
	}

	public boolean getSkip() {
		return skip;
	}

	public boolean getCrafts() {
		return crafts;
	}

	public boolean getTeam() {
		return team;
	}
	
	public boolean getSame() {
		return same;
	}
	
	public boolean getDouble() {
		return duoMode;
	}
	
	public boolean getSBTT() {
		return sbttMode;
	}
	
	public boolean getGameEnd() {
		return gameEnd;
	}
	
	public boolean getEndOne() {
		return endOne;
	}
	
	public boolean getPassive() {
		return passive;
	}

	public int getTeamSize() {
		return teamSize;
	}

	public int getItemPerAge() {
		return itemPerAge;
	}

	public int getSkipAge() {
		return skipAge;
	}

	public int getMaxAges() {
		return maxAges;
	}

	public int getStartTime() {
		return startTime;
	}

	public int getAddedTime() {
		return addedTime;
	}

	public int getSpreadDistance() {
		return spreadDistance;
	}

	public int getSpreadRadius() {
		return spreadRadius;
	}
	
	public int getSBTTAmount() {
		return sbttAmount;
	}

	public Level getLevel() {
		return LevelManager.getLevelByNumber(KuffleMain.levels, level);
	}

	public String getLang() {
		return lang;
	}

	public boolean setSaturation(boolean configSaturation) {
		saturation = configSaturation;
		return true;
	}

	public boolean setSpreadplayers(boolean configSpread) {
		spread = configSpread;
		return true;
	}

	public boolean setRewards(boolean configRewards) {
		rewards = configRewards;
		return true;
	}

	public boolean setSkip(boolean configSkip) {
		skip = configSkip;
		return true;
	}

	public boolean setCrafts(boolean configCrafts) {
		crafts = configCrafts;
		return true;
	}

	public boolean setTeam(boolean configTeam) {
		if (KuffleMain.gameStarted) {
			return false;
		}

		team = configTeam;
		
		return true;
	}
	
	public boolean setSame(boolean configSame) {
		if (KuffleMain.gameStarted) {
			return false;
		}
		
		same = configSame;
		
		return true;
	}
	
	public boolean setDoubleMode(boolean configDuoMode) {
		duoMode = configDuoMode;
		
		return true;
	}
	
	public boolean setSbttMode(boolean configSbttMode) {
		sbttMode = configSbttMode;
		
		return true;
	}
	
	public boolean setGameEnd(boolean configGameEnd) {
		gameEnd = configGameEnd;
		
		if (!gameEnd) {
			endOne = false;
		}
		
		return true;
	}
	
	public boolean setEndOne(boolean configEndOne) {
		if (!gameEnd) {
			return false;
		}
		
		endOne = configEndOne;
		
		return true;
	}
	
	public boolean setPassive(boolean configPassive) {
		passive = configPassive;
		
		return true;
	}

 	public boolean setTeamSize(int configTeamSize) {
 		if (KuffleMain.gameStarted) {
			return false;
		}
 		
		if (KuffleMain.teams.getTeams().size() > 0 && KuffleMain.teams.getMaxTeamSize() > configTeamSize) {
			return false;
		}

		teamSize = configTeamSize;
		return true;
	}

	public boolean setSpreadDistance(int configSpreadDistance) {
		spreadDistance = configSpreadDistance;
		return true;
	}

	public boolean setSpreadRadius(int configSpreadRadius) {
		if (configSpreadRadius < spreadDistance) {
			return false;
		}
		
		spreadRadius = configSpreadRadius;
		return true;
	}

	public boolean setItemAge(int configItemPerAge) {
		if (configItemPerAge < 1) {
			return false;
		}
		
		itemPerAge = configItemPerAge;
		return true;
	}

	public boolean setFirstSkip(int configSkipAge) {
		if (configSkipAge > maxAges) {
			return false;
		}
		
		skipAge = configSkipAge;
		return true;
	}

	public boolean setMaxAge(int configMaxAges) {
		if (configMaxAges > (AgeManager.getAgeMaxNumber(KuffleMain.ages) + 1)) {
			return false;
		}
		
		maxAges = configMaxAges;

		ret = new ArrayList<>();

		for (int i = 1; i < maxAges + 1; i++) {
			ret.add("" + i);
		}

		intRet.put("NB_AGE", ret);
		return true;
	}

	public boolean setStartTime(int configStartTime) {
		if (configStartTime < 1) {
			return false;
		}
		
		startTime = configStartTime;
		return true;
	}

	public boolean setAddedTime(int configAddedTime) {
		if (configAddedTime < 1) {
			return false;
		}
		
		addedTime = configAddedTime;
		return true;
	}
	
	public boolean setSbttAmount(int configSbttAmount) {
		if (configSbttAmount < 1 || configSbttAmount > 9) {
			return false;
		}
		
		sbttAmount = configSbttAmount;
		
		return true;
	}

	public boolean setLevel(String configLevel) {
		Level l;
		
		if ((l = LevelManager.getLevelByName(KuffleMain.levels, configLevel)) == null) {
			return false;
		}
		
		level = l.number;
		
		return true;
	}

	public boolean setLang(String configLang) {
		if (KuffleMain.langs.contains(configLang)) {
			lang = configLang;
			return true;
		}
		
		return false;
	}
}
