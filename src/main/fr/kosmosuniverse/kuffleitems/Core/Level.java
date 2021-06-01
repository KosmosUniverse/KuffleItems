package main.fr.kosmosuniverse.kuffleitems.Core;

public class Level {
	public String name;
	public int number;
	public int seconds;
	public boolean losable;
	
	public Level(String _name, int _number, int _seconds, boolean _losable) {
		name = _name;
		number = _number;
		seconds = _seconds;
		losable = _losable;
	}
}
