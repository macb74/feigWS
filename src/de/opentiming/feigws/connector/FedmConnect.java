package de.opentiming.feigws.connector;

import de.feig.FeIscListener;
import de.feig.Fedm;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderInfo;
import de.opentiming.feigws.helper.FeigWsHelper;
import de.opentiming.feigws.helper.LogWriter;
import de.opentiming.feigws.helper.ReadProperties;
import de.opentiming.feigws.server.FeigWSServer;


public class FedmConnect implements FeIscListener {

	private ReadProperties props;
	
	
	/**
	 * Verbindung mit dem Reader aufbauen.
	 * 
	 * Vorher wird geprüft, ob der Reader verfügbar und nicht bereits belegt ist.
	 */
	public void fedmOpenConnection() {

    	props = FeigWSServer.getProps();
				
		try {
        	//closeConnection();
        	boolean waited = false;
        	
        	/*
        	 * Prüfen ob der Reader überhaupt on line ist - vermeidet blockieren der feig lib.
        	 */
        	
    		if(!FeigWsHelper.portIsOpen(host,  props.getIntPropertie("reader.port"), 100) && !fedm.isConnected()) {
    			LogWriter.write(host, "host not available\n");
    			return;
    		}
        	
    		/*
    		 * Sleep nach potIsOpen, da der LRU2000 den Port sonst noch nicht freigegeben hat
    		 */
            Thread.sleep(200);
    		

    		/*
    		 * Warten bis der Reader frei ist
    		 */
        	while(fedm.isConnected()) {
        		LogWriter.write(host, "waiting\n");
        		Thread.sleep(200);
        		waited = true;
        	}
        	
        	
    		/*
    		 * Sleep nach dem warten, da der LRU2000 den Port sonst noch nicht freigegeben hat
    		 */
        	if(waited) {
        		Thread.sleep(100);
        	}
        	
        	
    		fedm.connectTCP(host, props.getIntPropertie("reader.port"));
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
            LogWriter.write(host, "open\n");

           	
        }
        catch (Exception e) {
            e.printStackTrace();
            LogWriter.write(host, "can not connect\n");
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
	            LogWriter.write(host, "close\n");
            }
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    }
        
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }
    
    public boolean isConnected() {
        return fedm.isConnected();
    }

	public void setHost(String host) {
		this.host = host;
		
	}
    
    private FedmIscReader fedm;
	private String host;


	@Override
	public void onReceiveProtocol(FedmIscReader arg0, String arg1) {
//		protocollListener.setProtocoll(arg1);
		LogWriter.write(host, arg1);
	}

	@Override
	public void onSendProtocol(FedmIscReader arg0, String arg1) {
//		protocollListener.setProtocoll(arg1);
		LogWriter.write(host, arg1);
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
