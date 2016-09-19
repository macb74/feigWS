package de.opentiming.feigws.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ReaderResultFiles {

	public String getResultFiles(String host) {
		ArrayList<String> files = new ArrayList<String>();
		String filter = "*" + host.replaceAll("\\.", "_") + "*";
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get("output/"), filter)) {
            for (Path path : directoryStream) {
            	files.add(path.toString().replaceAll("output", "").replaceAll("^.", ""));
            }
        } catch (IOException e) {
			e.printStackTrace();
        }
		
		return String.join(";", files);
	}

}
