package de.opentiming.feigWS.reader;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderID;
import de.feig.FedmIscReaderInfo;


public class ReaderTime {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FedmIscReader fedm;
	private FedmConnect con;

	public synchronized String setTime() {
		
		String time = getComputerTime();
		String timeArr[] = time.split(":");
		String returnString = "";
		
		// Durch probleme beim setzen der Zeit wenn die Sekunde kleiner 10 ist
		// wird gewartet bis die Sekunden auf jeden Fall zweistellig sind
		if(timeArr[2].substring(0, 1).equals("0")) {
			try {
				log.info("{} sorry, warte 10 sec, bis die Sekunde zweistellig ist.");
				Thread.sleep(10000);
				time = getComputerTime();
				String timeArr2[] = time.split(":");
				for (int i = 0; i < timeArr2.length; i++) {
					timeArr[i] = timeArr2[i];
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		String date = getComputerDate();
		String dateArr[] = date.split("-");

//		String rTime = getReaderTime();
		
//		System.out.println("Computer Date:   " + date);
//		System.out.println("Computer Time:   " + time);
//		System.out.println("Reader Time:     " + rTime);
		

		byte century = Byte.parseByte(dateArr[0].substring(0,2));
		byte year    = Byte.parseByte(dateArr[0].substring(2,4));
		//System.out.println((int)Byte.parseByte(timeArr[2]) * 1000);
		//System.out.println((byte)Byte.parseByte(timeArr[2]) * 1000);
		
		// Set Time
		try {
			
			FedmIscReaderInfo readerInfo = fedm.getReaderInfo();
			switch(readerInfo.readerType)
            {
                case de.feig.FedmIscReaderConst.TYPE_ISCLRU1002:
                	fedm.setData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_H, (byte)Byte.parseByte(timeArr[0]));
                	fedm.setData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_M, (byte)Byte.parseByte(timeArr[1]));
                	fedm.setData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_MS, (int)Byte.parseByte(timeArr[2]) * 1000 + 500);
                	fedm.sendProtocol((byte)0x85);
                	break;                    
                default:
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_CENTURY, (byte)century); // 20. Jahrhundert
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_YEAR, (byte)year); // Jahr 04 im Jahrhundert
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MONTH, (byte)Byte.parseByte(dateArr[1])); // September
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_DAY, (byte)Byte.parseByte(dateArr[2])); // 15. September
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_TIMEZONE, (byte)0); // z.Zt. ungenutzt
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_HOUR, (byte)Byte.parseByte(timeArr[0])); // Stunden
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MINUTE, (byte)Byte.parseByte(timeArr[1])); // Minuten
        			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MILLISECOND, (int)Byte.parseByte(timeArr[2]) * 1000 + 500); // Millisekunden (inkl. Sekunden)
        			fedm.sendProtocol((byte)0x87);	// Datum und Uhrzeit setzen
        			break;
            }
									
		} catch (FePortDriverException e) {
			e.printStackTrace();
		} catch (FeReaderDriverException e) {
			e.printStackTrace();
		} catch (FedmException e) {
			e.printStackTrace();
		} 
		
		String nrTime = getReaderDateTime();
		log.info("{} set reader Time {}", con.getHost(), nrTime.substring(0, 19));
		returnString = nrTime.substring(0, 19);
		
		return returnString;

	}

	public String getReaderTime() {
		try
		{
			fedm.sendProtocol((byte)0x88);
		} catch (FePortDriverException e) {
			e.printStackTrace();
		} catch (FeReaderDriverException e) {
			e.printStackTrace();
		} catch (FedmException e) {
			e.printStackTrace();
		}
		
		int stunde = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_HOUR);
		String minute = Integer.toString(fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MINUTE));
		String sekunde = Integer.toString(fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MILLISECOND));
		
		while(minute.length() < 2) {
			minute = "0" + minute;
		}
		
		while(sekunde.length() < 5) {
			sekunde = "0" + sekunde;
		}
		
		String time = stunde + ":" + minute + ":" + sekunde;
		return(time);
	}

	public String getReaderDateTime() {

		int century = 0;
		int year    = 0;
		int month   = 0;
		int day     = 0;
		int std     = 0;
		int min     = 0;
		int sek     = 0;
		String date = "";
		
		try
		{
			FedmIscReaderInfo readerInfo = fedm.getReaderInfo();
			switch(readerInfo.readerType)
            {
                case de.feig.FedmIscReaderConst.TYPE_ISCLRU1002:
                	fedm.sendProtocol((byte)0x86);
                	std    = fedm.getIntegerData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_H);
                	min    = fedm.getIntegerData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_M);
                	sek    = fedm.getIntegerData(FedmIscReaderID.FEDM_ISCLR_TMP_TIME_MS);
                	date   = getComputerDate();
                	break;                    
                default:
                	fedm.sendProtocol((byte)0x88);
            		century = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_CENTURY);
            		year    = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_YEAR);
            		month   = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MONTH);
            		day     = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_DAY);
            		std     = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_HOUR);
            		min     = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MINUTE);
            		sek     = fedm.getIntegerData(FedmIscReaderID.FEDM_ISC_TMP_DATE_MILLISECOND);
                    date    = century + "" + year + "-" + month + "-" + day;
            		break;
            }
			
		} catch (FePortDriverException e) {
			e.printStackTrace();
		} catch (FeReaderDriverException e) {
			e.printStackTrace();
		} catch (FedmException e) {
			e.printStackTrace();
		}
				

		String stunde = Integer.toString(std);
		while(stunde.length() < 2) {
			stunde = "0" + stunde;
		}

		String minute = Integer.toString(min);
		while(minute.length() < 2) {
			minute = "0" + minute;
		}
		
		String sekunde = Integer.toString(sek);
		while(sekunde.length() < 5) {
			sekunde = "0" + sekunde;
		}
		
		String dateTime = date + " " + stunde + ":" + minute + ":" + sekunde;
		return(dateTime);
	}
	
	public static String getComputerDate() {
		Date now = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(now);
	}
	
	public String getComputerTime() {
		Date now = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
		return sdf.format(now);
	}
	
    public void setReaderCon(FedmConnect con) {
    	this.con = con;
		this.fedm = con.getFedmIscReader();
    }

    public void setFedmIscReader(FedmIscReader fedm) {
		this.fedm = fedm;
    }
}
