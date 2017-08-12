package de.opentiming.feigWS.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FeIscListener;
import de.feig.Fedm;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderInfo;
import de.opentiming.feigWS.help.FeigWsHelper;

public class FedmConnect implements FeIscListener {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
    private FedmIscReader fedm;
    private String host;
	private int port;
	private boolean logReaderProtocol = false;
	
	public FedmConnect() throws FedmException, Exception {
		fedm = new de.feig.FedmIscReader();
	}
	
	/**
	 * Verbindung mit dem Reader aufbauen.
	 * 
	 * Vorher wird geprüft, ob der Reader verfügbar und nicht bereits belegt ist.
	 * @param port 
	 * @param host 
	 */
	public void fedmOpenConnection() {
				
		try {
        	
			/*
        	 * Prüfen ob der Reader überhaupt online ist - vermeidet blockieren der feig lib.
        	 */        	
    		if(!FeigWsHelper.portIsOpen(host,  port, 100) && !fedm.isConnected()) {
    			log.info("{} host not available", host);
    			return;
    		}
        	
    		/*
    		 * Sleep nach potIsOpen, da der LRU2000 den Port sonst noch nicht freigegeben hat
    		 */
            Thread.sleep(200);

            /*
             * Wenn der Reader noch nicht verbunden ist, oder es bei der letzten Kommunikaton einen Fehler gab
             */
        	if(!fedm.isConnected() || fedm.getLastError() != 0) {
    		
        		/*
        		 * Wenn der Reader noch verbunden ist wir derstmal die Verbindung geschlossen
        		 */
	        	if(fedm.isConnected()) {
	        		fedmCloseConnection();
	        		Thread.sleep(200);
	        		return;
	        	}
	        	
	        	fedm.connectTCP(host, port);
		        fedm.setPortPara("Timeout", "3000");
	            //System.out.println("connection opened");
		        
	            FedmIscReaderInfo readerInfo = fedm.readReaderInfo();
	            //fedm.readCompleteConfiguration(true);
	            
	        	fedm.addEventListener(this, FeIscListener.RECEIVE_STRING_EVENT);
	        	fedm.addEventListener(this, FeIscListener.SEND_STRING_EVENT);
	        		        	
	            switch(readerInfo.readerType)
	            {
	                case de.feig.FedmIscReaderConst.TYPE_ISCMR200:
	                case de.feig.FedmIscReaderConst.TYPE_ISCLR2000:
	                case de.feig.FedmIscReaderConst.TYPE_ISCMRU200:
	                case de.feig.FedmIscReaderConst.TYPE_ISCLRU1000:
	                case de.feig.FedmIscReaderConst.TYPE_ISCLRU1002:
	                case de.feig.FedmIscReaderConst.TYPE_ISCLRU2000:
	                case de.feig.FedmIscReaderConst.TYPE_ISCLRU3000:
	                    fedm.setProtocolFrameSupport(Fedm.PRT_FRAME_ADVANCED);
	                    break;                    
	                default:
	                    fedm.setProtocolFrameSupport(Fedm.PRT_FRAME_STANDARD);
	                    break;
	            }
	            log.info("{} open", host);
	            
	            //log.info("{} set Time", host);
				ReaderTime t = new ReaderTime();
				t.setReaderCon(this);
				t.setTime();

        	}
           	
        }
        catch (Exception e) {
            e.printStackTrace();
            log.info("{} can not connect", host);
        	//System.exit(1);
        }
    }

	
	/**
	 * Verbindung zum Reader schließen
	 */
    public void fedmCloseConnection() {
        try {
            if (fedm.isConnected()) {
	        	fedm.removeEventListener(this, FeIscListener.RECEIVE_STRING_EVENT);
	        	fedm.removeEventListener(this, FeIscListener.SEND_STRING_EVENT);
	        	fedm.disConnect();
	            
	        	log.info("{} close", host);
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
        
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }
    
	public FedmIscReader getFedmIscReader() {
		return fedm;
	}
    
    public boolean isConnected() {
        return fedm.isConnected();
    }
    
	public void setHost(String reader) {
		this.host = reader;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}
	
	public void logReaderProtocol(boolean lrp) {
		this.logReaderProtocol = lrp;
	}
	
	
	@Override
	public void onReceiveProtocol(FedmIscReader arg0, String arg1) {
//		protocollListener.setProtocoll(arg1);
		if(logReaderProtocol) { log.info(host + " " + arg1); }
	}

	@Override
	public void onSendProtocol(FedmIscReader arg0, String arg1) {
//		protocollListener.setProtocoll(arg1);
		if(logReaderProtocol) { log.info(host + " " + arg1); }
	}

	@Override
	public void onReceiveProtocol(FedmIscReader arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSendProtocol(FedmIscReader arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		
	}

}
