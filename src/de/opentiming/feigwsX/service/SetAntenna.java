package de.opentiming.feigws.service;
import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderID;
import de.feig.FedmIscReaderInfo;


public class SetAntenna {
	
	private FedmIscReader fedm;
	
	/**
	 * setzt die Antennen.
	 * 
	 * 
	 * Der LRU2000 kann mit 0x76 die angeschlossenen Antennen automatisch erkennen
	 * 
	 * Dem LRU1002 muss man die Antennen als Binary Sting übergeben (erste antenne hinten)
	 * 
	 * @param antennas
	 */
	public void setAntennas(String antennas) {
		
		int ant;
		try {
			ant = Integer.parseInt(antennas, 2);
		} catch (Exception e) {
			ant = 1;
		}
			
		FedmIscReaderInfo readerInfo = fedm.getReaderInfo();
		try {
		
			switch(readerInfo.readerType)
	        {
	            case de.feig.FedmIscReaderConst.TYPE_ISCLRU1002:
	            	byte cfgAddr = 15;
	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG, (byte)0);
	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_LOC, true); // aus dem EPROM lesen
	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_ADR, cfgAddr);
	    			fedm.sendProtocol((byte)0x80);

	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG, (byte)0);
	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_LOC, true);
	    			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_ADR, cfgAddr);
	            	fedm.setConfigPara(de.feig.ReaderConfig.AirInterface.Multiplexer.UHF.Internal.SelectedAntennas, ant, true);
					fedm.sendProtocol((byte)0x81);	
	            	break;                    
	            default:
	            	fedm.sendProtocol((byte) 0x76);
					Thread.sleep(1000);
	    			break;
	        }
			
		} catch (FePortDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeReaderDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }
	    
}