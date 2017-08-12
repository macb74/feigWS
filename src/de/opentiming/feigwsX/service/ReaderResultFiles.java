package de.opentiming.feigws.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.tomcat.jni.File;

import de.opentiming.feigws.helper.FeigWsHelper;

public class ReaderResultFiles {

	public String getResultFiles(String host) {
		ArrayList<Object> files = new ArrayList<Object>();
		String filter = "*" + host.replaceAll("\\.", "_") + "*";
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("output/"), filter)) {
            for (Path path : directoryStream) {
            	int lines = FeigWsHelper.countLines(path);
            	if(lines > 0) { lines--; }
            	
            	Map<String><String> file = new HashMap<>();
            	file.put("file",path.toString().replaceAll("output", "").replaceAll("^.", ""));
            	file.put("linecount", lines);
            	files.add(file);
            }
        } catch (IOException e) {
			e.printStackTrace();
        }
		
		return String.join("|", files);
	}

}
