package de.opentiming.feigWS.reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderID;

public class ReaderPower {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FedmIscReader fedm;
	private FedmConnect con;

	public ReaderPower(FedmConnect con) {
		this.con = con;
		this.fedm = con.getFedmIscReader();
	}

	public synchronized boolean setPower(String powerStr) {
		
		byte powerAdr1 = 3;
		byte powerAdr2 = 20;

		int power = (Integer.parseInt(powerStr) / 100) + 15;
		
		try {
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_ADR, powerAdr1);
			fedm.sendProtocol((byte)0x80);
			
			// schreiben
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_ADR, powerAdr1);
			fedm.setConfigPara(de.feig.ReaderConfig.AirInterface.Antenna.UHF.No1.OutputPower, power, true);
			
			fedm.sendProtocol((byte)0x81);

			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_ADR, powerAdr2);
			fedm.sendProtocol((byte)0x80);
			
			// schreiben
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_ADR, powerAdr2);
			fedm.setConfigPara(de.feig.ReaderConfig.AirInterface.Antenna.UHF.No2.OutputPower, power, true);
			fedm.setConfigPara(de.feig.ReaderConfig.AirInterface.Antenna.UHF.No3.OutputPower, power, true);
			fedm.setConfigPara(de.feig.ReaderConfig.AirInterface.Antenna.UHF.No4.OutputPower, power, true);
			
			fedm.sendProtocol((byte)0x81);			
			fedm.sendProtocol((byte)0x63);

		} catch (FePortDriverException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		} catch (FeReaderDriverException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		}
		
		return true;

	}
	
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }
	
}
