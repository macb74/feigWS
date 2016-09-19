package de.opentiming.feigws.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import de.opentiming.feigws.helper.FeigWsHelper;

public class ReaderResultFiles {

	public String getResultFiles(String host) {
		ArrayList<String> files = new ArrayList<String>();
		String filter = "*" + host.replaceAll("\\.", "_") + "*";
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("output/"), filter)) {
            for (Path path : directoryStream) {
            	int lines = FeigWsHelper.countLines(path);
            	if(lines > 0) { lines--; }
            	files.add(path.toString().replaceAll("output", "").replaceAll("^.", "") + ";" + lines);
            }
        } catch (IOException e) {
			e.printStackTrace();
        }
		
		return String.join("|", files);
	}

}
