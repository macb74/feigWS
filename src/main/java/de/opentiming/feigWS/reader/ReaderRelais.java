package de.opentiming.feigWS.reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderID;


public class ReaderRelais {
		
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FedmIscReader fedm;
	private FedmConnect con;

	public ReaderRelais(FedmConnect con) {
		this.con = con;
		this.fedm = con.getFedmIscReader();
	}
	

	public synchronized boolean setNewMode(String mode) {
		
		byte modeAdr = 9;
		String stMode;
		
		if(mode.equals("on")) { stMode = "0F"; } else { stMode = "00"; }
		
		try {
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_ADR, modeAdr);
			fedm.sendProtocol((byte)0x80);
			
			// schreiben
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_ADR, modeAdr);
			fedm.setConfigPara(de.feig.ReaderConfig.DigitalIO.Relay.No1.ReadEventActivation.AntennaNo, stMode, true);
	//		fedm.setConfigPara(de.feig.ReaderConfig.DigitalIO.Relay.No2.ReadEventActivation.AntennaNo, intMode, true);
	//		fedm.setConfigPara(de.feig.ReaderConfig.DigitalIO.Relay.No3.ReadEventActivation.AntennaNo, intMode, true);
	//		fedm.setConfigPara(de.feig.ReaderConfig.DigitalIO.Relay.No4.ReadEventActivation.AntennaNo, intMode, true);
			fedm.sendProtocol((byte)0x81);
			///fedm.sendProtocol((byte)0x63);

			//System.out.println("Reset...");
			log.info("{} setting Relais to: {}",  con.getHost(), mode);

		} catch (FePortDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		} catch (FeReaderDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		}
		return true;

	}
	
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }

}
