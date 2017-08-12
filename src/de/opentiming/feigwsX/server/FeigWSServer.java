package de.opentiming.feigws.server;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.opentiming.feigws.helper.ReadProperties;
import de.opentiming.feigws.reader.BrmReadThread;
import de.opentiming.feigws.service.FeigWSService;
import de.opentiming.feigws.service.ResetReaderFile;


public class FeigWSServer {

	private static ReadProperties props = new ReadProperties("config.properties");
	
	public static void main(String args[]) {
		
		String readers = props.getPropertie("reader.ip");
		String[] reader = readers.split(" ");
		
		Map<String, FedmIscReader> readerConnectors = new HashMap<String, FedmIscReader>();
		
		for( String r : reader) {
			readerConnectors.put(r, startReaderThread(r));
		}
		
		startServer(readerConnectors);
	}


	private static void startServer(Map<String, FedmIscReader> readerConnectors) {
		Endpoint.publish( "http://" + props.getPropertie("webserver.host") + ":" + props.getPropertie("webserver.port") + "/FeigWS", 
				new FeigWSService(readerConnectors) );

	}
		
	private static FedmIscReader startReaderThread(String host) {

		FedmIscReader fedm = null;
		
		ResetReaderFile rrf = new ResetReaderFile();
		rrf.resetReaderFile(host);

		try {
			
			fedm = new de.feig.FedmIscReader();
			BrmReadThread brmReadThread = new BrmReadThread();
		    brmReadThread.setFedmIscReader(fedm);
		    brmReadThread.setHost(host);
		    brmReadThread.setSleepTime(Integer.parseInt(props.getPropertie("reader.sleep")));
		    brmReadThread.setSets(10);
		    brmReadThread.setSoundFile(props.getPropertie("sound.file"));;		    
		    Thread runner = new Thread(brmReadThread);
		    brmReadThread.setRunning(true);
		    runner.start();
			
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return fedm;
	}

	
	public static ReadProperties getProps() {
		return props;
	}
	
}
