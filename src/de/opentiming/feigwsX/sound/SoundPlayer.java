package de.opentiming.feigws.sound;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SoundPlayer implements Runnable {

	private Player mp3Player;
	private Thread playerThread;
	
	public SoundPlayer(String sfile){
    	FileInputStream fi;
		try {
			File f = new File("sound/" + sfile);
			if (f.isFile() && f.canRead()) {
				fi = new FileInputStream("sound/" + sfile);
				mp3Player = new Player(fi);
			    playerThread = new Thread(this);
			    playerThread.start();
			}
		} catch (FileNotFoundException | JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Override
	public void run(){

		try {
			mp3Player.play();
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public boolean getStatus() {
		return playerThread.isAlive();
	}

}