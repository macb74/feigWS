package de.opentiming.feigws.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import de.opentiming.feigws.server.FeigWSServer;


public class LogWriter {

	private static ReadProperties props;
	
	public static void write(String host, String logText) {
		props = FeigWSServer.getProps();
		
		if(props.getPropertie("reader.log").equals("true")) {
			try {
				String filename = "log/" + host.replaceAll("\\.", "_") + ".log";
				Path file = Paths.get(filename);
				if (Files.notExists(file)) {
					Files.write(file, "".getBytes());
				}
				Files.write(file, logText.getBytes(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}	
}
