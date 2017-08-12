package de.opentiming.feigWS.help;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FeigWsHelper {

	/**
	 * Zählt die Zeilen einer Textdatei
	 * 
	 * @param file
	 * @return
	 */
	public static int countLines(Path file) {
		int count = 0;
		try {
			List<String> lines = Files.readAllLines(file);
			count = lines.size();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	

	/**
	 * Prüft, ob der TCP Port zu öffnen ist
	 * 
	 * @param ip
	 * @param port
	 * @param timeout
	 * @return
	 */
	public static boolean portIsOpen(String ip, int port, int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

}
