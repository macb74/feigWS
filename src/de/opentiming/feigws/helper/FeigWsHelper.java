package de.opentiming.feigws.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FeigWsHelper {

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
	
	

}
