package de.opentiming.feigws.service;
import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderID;
import de.feig.FedmIscReaderInfo;


public class SetValidTime {
	
	private FedmIscReader fedm;

	public synchronized void setValidTime(String vtime) {
		
		byte cfgAdr = 12;
		int vt = Integer.parseInt(vtime) * 10;
		
		try {
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_READ_CFG_ADR, cfgAdr);
			fedm.sendProtocol((byte)0x80);
			
			// schreiben
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG, (byte)0);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_LOC, true); // aus dem EPROM lesen
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_WRITE_CFG_ADR, cfgAdr);
			fedm.setConfigPara(de.feig.ReaderConfig.OperatingMode.BufferedReadMode.Filter.TransponderValidTime, vt, true);
			fedm.sendProtocol((byte)0x81);
			
			FedmIscReaderInfo readerInfo = fedm.getReaderInfo();
			if (readerInfo.readerType != de.feig.FedmIscReaderConst.TYPE_ISCLRU1002) {
				fedm.sendProtocol((byte)0x64);				
			}

			//LogWriter.write("Reset...");

		} catch (FePortDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeReaderDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    public void setFedmIscReader(FedmIscReader fedm) {
        this.fedm = fedm;
    }
	
}
