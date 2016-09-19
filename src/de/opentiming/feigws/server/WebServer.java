package de.opentiming.feigws.server;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

public class WebServer {

	private String keystoreFile;
	private String keyPass;
	private String hostname;
	private int port;
	private String context;

	public HttpContext createWebServer() {
		
		HttpContext httpContext = null;
		
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

			HttpServer httpServer = HttpServer.create(new InetSocketAddress(hostname, port), 10);
			httpContext = httpServer.createContext(context);

			httpServer.start();

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return httpContext;
	}

	public void setKeystore(String keystore) {
		this.keystoreFile = keystore;
	}

	public void setKeyPass(String keyPass) {
		this.keyPass = keyPass;	
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public void setHttpContext(String context) {
		this.context = context;
	}
}
