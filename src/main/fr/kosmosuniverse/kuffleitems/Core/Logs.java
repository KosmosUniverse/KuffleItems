package main.fr.kosmosuniverse.kuffleitems.Core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.bukkit.entity.Player;

public class Logs {
	private String path = "";
	private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	public Logs(File dataFolder) {
		String s_path = dataFolder.getPath() + File.separator + "KuffleItemsGamelogs.txt";
		path = s_path;
		Path p_path = Paths.get(s_path);
		
		try {
			if (!Files.exists(p_path)) {
				Files.createFile(p_path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logBroadcastMsg(String msg) {		
		try (FileWriter writer = new FileWriter(path, true)) { 
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [SYSTEM] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void logMsg(Player to, String msg) {
		try (FileWriter writer = new FileWriter(path, true)) {
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [" + to.getName() + "] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void writeMsg(Player to, String msg) {
		to.sendMessage(msg);
		
		try (FileWriter writer = new FileWriter(path, true)) {
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [" + to.getName() + "] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
