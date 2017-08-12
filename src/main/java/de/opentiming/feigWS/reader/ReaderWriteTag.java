package de.opentiming.feigWS.reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderConst;
import de.feig.FedmIscReaderID;

public class ReaderWriteTag {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FedmIscReader fedm;
	boolean uniqeID = true;
	private String snr[];
	private FedmConnect con;
	private int tagsPerNumber = 1;

	
	public ReaderWriteTag(FedmConnect con) {
		this.con = con;
		this.fedm = con.getFedmIscReader();
	}
	
	public synchronized Map<String, String> writeTag(int intStNr) {

		Map<String,String> result = new HashMap<>();
		boolean success = false;
		boolean write = false;
		
		result.put("message","");
		result.put("success",Boolean.toString(success));
		
		try {

			if(intStNr > 65535) {
				log.info("{} die Startnummer darf nicht größer als 65535 sein", con.getHost());
				result.put("message","die Startnummer darf nicht größer als 65535 sein");
				return result;
			}
			
			String newSnr = stnrTo4DigitString(intStNr);
			
			con.fedmOpenConnection();

			if (con.isConnected()) {

				try {
					fedm.setTableSize(FedmIscReaderConst.ISO_TABLE, 128);
				} catch (FedmException e) {
					e.printStackTrace();
				}

				int tagsInZoneBefore = isoReadTag();
				log.info("{} alte sNr: {}", con.getHost(), toString(snr));
				result.put("oldSerialNumberBefore", toString(snr));
				sleep(200);

				/*
				 * Tags mit selber ID werden als 1 Tag erkannt
				 */
				if ((tagsInZoneBefore <= tagsPerNumber) && (tagsInZoneBefore != 0)) {
					write = isoTagWrite(newSnr);
					sleep(500);
				} else {
					log.info("{} es sind {} Tags im Lesebereich", con.getHost(), tagsInZoneBefore);
					result.put("message", "Es sind " + tagsInZoneBefore + " Tags im Lesebereich");
					success = false;
				}

				if (write) {
					isoReadTag();
					log.info("{} neue sNr: {}", con.getHost(), toString(snr));
					result.put("newSerialNumber", toString(snr));
					
					if (checkAllSNr(newSnr)) {
						success = true;
					} else {
						log.info("{} ACHTUNG: Neue Nummer ist FALSCH!!!", con.getHost());
						result.put("message", "ACHTUNG: Neue Nummer ist FALSCH!!!");
						success = false;
					}
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		result.put("success", Boolean.toString(success));
		return result;
	}

	private String toString(String[] nr) {
		return String.join(" ",nr);
	}

	private boolean checkAllSNr(String newSnr) {
		if(snr.length < tagsPerNumber) {
			return false;
		}
		for (int i = 0; i < snr.length; i++) {
			if (!snr[i].substring(snr[i].length()-4).equalsIgnoreCase(newSnr)) {
				return false;
			}
		}
		return true;
	}

	private boolean isoTagWrite(String newSnr) {

		String[] oldEpcLngs = new String[snr.length];
		String[] oldEPC = new String[snr.length];

		String hostCommand = "24";
		String mode = "31";
		String epcMemoryBank = "01";
		String setPassword = "00";
		String dbAdr = "01";
		String dbNoOfBlocks = "03";
		String dbBlockSize = "02";
		String dataBlock = "1000";
		int i = 0;
		
		/*
		 * Es wird sooft geschrieben wie Tags auf der Startnummer sind, da bei jedem Schreibvorgang
		 * nur ein Tag beschrieben wird. Tags mit gleicher ID werden als ein Tag erkannt - daher nicht
		 * die Anzahl der vorher gelesenen Tags
		 */
		for (int counter = 0; counter < tagsPerNumber; counter++) {
			
			/*
			 * Tags mit gleicher ID werden als ein Tag erkannt. Bei jedem Schreibvorgang wird nur ein
			 * Tag geschrieben - Bei Tags mit gleicher ID muss also immer wieder die erste Seriennummer
			 * übergeben werden.
			 */
			if(snr.length < tagsPerNumber) { i = 0; } else { i = counter; }
			
			
			/* 
			 * Eindeutige Seriennummer definieren
			 */
			String uniqeNr = "";
			if (uniqeID == true) {
				uniqeNr = getUniqeNumber();
			}

			String newUniqeSnr = uniqeNr + newSnr;
			while (newUniqeSnr.length() != 8) {
				newUniqeSnr = "0" + newUniqeSnr;
			}
			
			oldEpcLngs[i] = getSnrLenHex(snr[i]);
			oldEPC[i] = snr[i];

			String requestData = hostCommand + mode + oldEpcLngs[i] + oldEPC[i] + epcMemoryBank + setPassword + dbAdr
					+ dbNoOfBlocks + dbBlockSize + dataBlock + newUniqeSnr;

			try {
				// String requestData = pre + oldSnrLen + oldSnr + middle +
				// newSnr;
				// System.out.println("writ sNr: " + newSnr);
				fedm.sendProtocol((byte) 0xB0, requestData);
			} catch (FePortDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (FeReaderDriverException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (FedmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} // Kommunikation mit Leser/Transponder

			sleep(250);
		}

		return true;
	}

	private String getSnrLenHex(String snr) {
		int snrLenInt = snr.length() / 2;
		String snrLenHex = Integer.toHexString(snrLenInt);
		snrLenHex = 0 + snrLenHex;
		return snrLenHex;
	}

	private int isoReadTag() {

		int i = 0;

		try {
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_B0_CMD, 0x01);
			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_B0_MODE, 0x00);
			fedm.deleteTable(FedmIscReaderConst.ISO_TABLE);

			fedm.sendProtocol((byte) 0xB0);

			String[] serialNumber = new String[fedm.getTableLength(FedmIscReaderConst.ISO_TABLE)];
			String[] tagType = new String[fedm.getTableLength(FedmIscReaderConst.ISO_TABLE)];
			snr = new String[fedm.getTableLength(FedmIscReaderConst.ISO_TABLE)];
			
			// System.out.println(fedm.getTableLength(FedmIscReaderConst.ISO_TABLE)
			// + " Tag in der Zone");

			for (i = 0; i < fedm.getTableLength(FedmIscReaderConst.ISO_TABLE); ++i) {
				serialNumber[i] = fedm.getStringTableData(i, FedmIscReaderConst.ISO_TABLE, FedmIscReaderConst.DATA_SNR);
				tagType[i] = fedm.getStringTableData(i, FedmIscReaderConst.ISO_TABLE, FedmIscReaderConst.DATA_TRTYPE);

				if (tagType[i].equals("00"))
					tagType[i] = "Philips I-Code1";
				if (tagType[i].equals("01"))
					tagType[i] = "Texas Instruments Tag-it HF";
				if (tagType[i].equals("03"))
					tagType[i] = "ISO15693 Transponder";
				if (tagType[i].equals("04"))
					tagType[i] = "ISO14443-A";
				if (tagType[i].equals("05"))
					tagType[i] = "ISO14443-B";
				if (tagType[i].equals("06"))
					tagType[i] = "I-CODE EPC";
				if (tagType[i].equals("07"))
					tagType[i] = "I-CODE UID";
				if (tagType[i].equals("09"))
					tagType[i] = "EPC Class1 Gen2 HF";
				if (tagType[i].equals("81"))
					tagType[i] = "ISO18000-6-B";
				if (tagType[i].equals("84"))
					tagType[i] = "EPC Class1 Gen2 UHF";

				snr[i] = serialNumber[i];
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
		}

		return i;

	}

	private static String getUniqeNumber() {
		// Date now = new java.util.Date();
		// java.text.SimpleDateFormat df = new
		// java.text.SimpleDateFormat("yyyyMMddHHmmss");
		// return df.format(now);

		// generiert eine Zufallszahl zwischen 4096 (Hex: 1000) und 65535 (Hex:
		// FFFF)
		int randomInt = (int) Math.floor(Math.random() * (65535 - 4096)) + 4096;
		String randomHex = Integer.toHexString(randomInt);
		return randomHex;
	}

	private static String stnrTo4DigitString(int intStNr) {

		String stNr = Integer.toHexString(intStNr);
		while (stNr.length() != 4) {
			stNr = "0" + stNr;
		}

		return stNr;

	}

	public void onReceiveProtocol(FedmIscReader reader, String receiveProtocol) {
		// System.out.println(receiveProtocol);
		// protocollListener.setProtocoll(receiveProtocol);
	}

	public void onSendProtocol(FedmIscReader reader, String sendProtocol) {
		// System.out.println(sendProtocol);
		// protocollListener.setProtocoll(sendProtocol);
	}

	public void onReceiveProtocol(FedmIscReader reader, byte[] receiveProtocol) {
	}

	public void onSendProtocol(FedmIscReader reader, byte[] sendProtocol) {
	}

	private void sleep(int s) {
		try {
			Thread.sleep(s);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void setUniqeID(boolean uniqeID) {
		this.uniqeID = uniqeID;
	}
	
	public void setTagsPerNumber(int tagsPerNumber) {
		this.tagsPerNumber = tagsPerNumber;
	}
}
