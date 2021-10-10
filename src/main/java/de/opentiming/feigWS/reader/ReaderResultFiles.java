package de.opentiming.feigWS.reader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.opentiming.feigWS.help.FileOutput;

public class ReaderResultFiles {

	private String directory;

	public ReaderResultFiles(String directory) {
		this.directory = directory;
	}

	public ArrayList<Object> getResultFiles(String host) {
		ArrayList<Object> files = new ArrayList<Object>();
		String filter = "*" + host.replaceAll("\\.", "_") + "*";
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directory), filter)) {
            for (Path path : directoryStream) {
				int lines = FileOutput.countLines(path);
            	if(lines > 0) { lines--; }
            	
            	Map<String,String> file = new HashMap<>();
            	file.put("file",path.toString().replaceAll("output", "").replaceAll("^.", ""));
            	file.put("linecount", Integer.toString(lines));
            	files.add(file);
            }
        } catch (IOException e) {
			e.printStackTrace();
        }
		
		return files;
	}
	
	public List<String> getFileContent(String readerFile) {
		
		List<String> lines = null;
		try {
			Path file = Paths.get(directory + "/" + readerFile);
			lines = Files.readAllLines(file, Charset.forName("UTF-8"));
			if(lines.size() != 0) { lines.remove(0); }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lines;
	}

}
