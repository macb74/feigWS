package de.opentiming.feigWS.help;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileOutput {

	private String filename;
	private String directory;
	private String host;


	public FileOutput(String directory) {
		this.directory = directory;
	}


	public boolean resetReaderFile() {

		String newfilename = host.replaceAll("\\.", "_") + ".out";
		
		String now = new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss_SSS").format(new Date());
		try {
			Path file = Paths.get(directory + "/" + filename);
			if (Files.notExists(file)) {
				Files.write(file, "".getBytes());
			} else {
				if(countLines(file) != 0) {
					Files.move(file, Paths.get(directory + "/" + now + "_" + newfilename));
					Files.write(file, "".getBytes());
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;

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

	/**
	 * 
	 * Schreibt Textdatei
	 * 
	 * @param filename
	 * @param content
	 * @return
	 */
	public void writeToFile(String content) throws IOException {
		
		Path file = Paths.get(directory + '/' + filename);
		
		if (Files.notExists(file)) {
			Files.write(file, "".getBytes());
		}

		Files.write(file, content.getBytes(), StandardOpenOption.APPEND);
	}
	
	
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
	
	
	public String getFilename(String host) {
		String filename = "Aktuell_" + host.replaceAll("\\.", "_") + ".out";
		return filename;
	}
	
	public void setHost(String host) {
		this.host = host;
		this.filename = getFilename(host);
	}

}
