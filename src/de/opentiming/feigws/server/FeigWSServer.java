package de.opentiming.feigws.server;

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
		
		for( String r : reader) {
			startServer(r);
		}
	}


	private static void startServer(String host) {

		WebServer webServer = new WebServer();
		webServer.setHostname(props.getPropertie("webserver.host"));
		webServer.setPort(getPort(host));
		webServer.setHttpContext("/FeigWS");
		com.sun.net.httpserver.HttpContext httpContext = webServer.createWebServer();
	
		ResetReaderFile rrf = new ResetReaderFile();
		rrf.resetReaderFile(host);

		try {
			
			FedmIscReader fedm = new de.feig.FedmIscReader();
			FeigWSService fws = new FeigWSService();
			fws.setFedmIscReader(fedm);
			fws.setHost(host);
			
			BrmReadThread brmReadThread = new BrmReadThread();
		    brmReadThread.setFedmIscReader(fedm);
		    brmReadThread.setHost(host);
		    brmReadThread.setSleepTime(Integer.parseInt(props.getPropertie("reader.sleep")));
		    brmReadThread.setSets(10);
		    Thread runner = new Thread(brmReadThread);
		    brmReadThread.setRunning(true);
		    runner.start();
			
			Endpoint endpoint = Endpoint.create(fws);
			endpoint.publish(httpContext);
			
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	private static int getPort(String host) {
		String[] port = host.split("\\.");
		return Integer.parseInt(port[3]) * 10;
	}

	
	public static ReadProperties getProps() {
		return props;
	}
	
}
