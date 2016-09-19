package de.opentiming.feigws.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ReaderResults {

	public String getResults(String rf) {
		
		List<String> lines = null;
		try {
			Path file = Paths.get("output/" + rf);
			lines = Files.readAllLines(file, Charset.forName("UTF-8"));
			if(lines.size() != 0) { lines.remove(0); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return String.join("|", lines);
	}

}
