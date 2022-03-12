package main.fr.kosmosuniverse.kuffleitems.core;

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
	
	public Logs(String pathFile) {
		path = pathFile;
		Path p_path = Paths.get(pathFile);
		
		try {
			if (!Files.exists(p_path)) {
				Files.createFile(p_path);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void logSystemMsg(String msg) {		
		try (FileWriter writer = new FileWriter(path, true)) { 
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [SYSTEM] -> " + msg + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
	
	public void logMsg(String name, String msg) {
		try (FileWriter writer = new FileWriter(path, true)) {
			LocalDateTime now = LocalDateTime.now();  
			
			writer.write(dtf.format(now) + " : [" + name + "] -> " + msg + "\n");
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
