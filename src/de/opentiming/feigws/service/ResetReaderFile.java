package de.opentiming.feigws.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.opentiming.feigws.helper.FeigWsHelper;

public class ResetReaderFile {

	public String resetReaderFile(String ip) {

		String dir = "output/";
		String filename = ip.replaceAll("\\.", "_") + ".out";
		String now = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss_SSS").format(new Date());
		try {
			Path file = Paths.get(dir + filename);
			if (Files.notExists(file)) {
				Files.write(file, "".getBytes());
			} else {
				if(FeigWsHelper.countLines(file) != 0) {
					Files.move(file, Paths.get(dir + now + "_" + filename));
					Files.write(file, "".getBytes());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "false";
		}
		return "true";

	}

	
	private String getNewNum() {
		List<String> lines;
		String filename = "index.file";
		Path file;
		int id = 0;

		try {
			file = Paths.get(filename);

			if (Files.notExists(file)) {
				Files.write(file, "0".getBytes());
			}

			lines = Files.readAllLines(file, Charset.forName("UTF-8"));
			id = Integer.parseInt(lines.get(0)) + 1;
			lines.clear();
			lines.add(Integer.toString(id));
			Files.write(file, lines, Charset.forName("UTF-8"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Integer.toString(id - 1);
	}

}
