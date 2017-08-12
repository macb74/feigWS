package de.opentiming.feigws.server;

import java.util.Map;

import javax.xml.ws.Endpoint;

import de.feig.FedmIscReader;
import de.opentiming.feigws.service.FeigWSService;

public class WebServer {

	private String hostname;
	private String port;
	private String context;
	private Map<String, FedmIscReader> readerConnectors;

	public Endpoint createWebServer() {
		
		Endpoint endpoint = null;
		try {
//			SSLContext ssl = SSLContext.getInstance("TLS");
//
//			KeyManagerFactory keyFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
//			KeyStore store = KeyStore.getInstance("JKS");
//		
//			store.load(new FileInputStream(keystoreFile), keyPass.toCharArray());
//			keyFactory.init(store, keyPass.toCharArray());
//		
//			TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//			trustFactory.init(store);
//		
//			ssl.init(keyFactory.getKeyManagers(), trustFactory.getTrustManagers(), new SecureRandom());
//			HttpsConfigurator configurator = new HttpsConfigurator(ssl);
//		
//			HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(hostname, port), 10);
//			httpsServer.setHttpsConfigurator(configurator);
//			
//			httpContext = httpsServer.createContext(context);

//			httpContext.setAuthenticator(new BasicAuthenticator("get") {
//		        @Override
//		        public boolean checkCredentials(String user, String pwd) {
//		            return user.equals("admin") && pwd.equals("password");
//		        }
//		    });
					
//			httpsServer.start();

		    endpoint = Endpoint.publish( "http://" + hostname + ":" + port + context, new FeigWSService(readerConnectors) );
			
		} catch (Exception e) {
			e.printStackTrace();
		}


		return endpoint;
	}


	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	public void setHttpContext(String context) {
		this.context = context;
	}

	public void setReaderConnectors(Map<String, FedmIscReader> readerConnectors) {
		this.readerConnectors = readerConnectors;
	}
}
