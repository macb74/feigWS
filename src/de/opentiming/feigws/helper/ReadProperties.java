package de.opentiming.feigws.helper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

 
public class ReadProperties {
	    
    private Properties props;
    
    public ReadProperties(String file) {
    	props = new Properties();
        InputStream input = null;
    
       	try {
       		input = new FileInputStream(file);
       		props.load(input);
       		     
       	} catch (IOException ex) {
       		ex.printStackTrace();
       	} finally {
       		if (input != null) {
       			try {
       				input.close();
       			} catch (IOException e) {
       				e.printStackTrace();
       			}
       		}
       	}
		
	}

    public void setPropertie(int allBytes, String app) {
		try {
			props.setProperty(app, Integer.toString(allBytes));
			File f = new File("status.properties");
			FileOutputStream out = new FileOutputStream( f );
			props.store(out, "Do not change manually");
			} catch (IOException e) {
				e.printStackTrace();
			} 
    }
    
    public String getPropertie(String prop) {
    	return props.getProperty(prop);
    }
    
    public int getIntPropertie(String prop) {
    	return Integer.parseInt(props.getProperty(prop));
    }
}