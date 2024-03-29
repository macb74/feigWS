package de.opentiming.feigWS.reader;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.feig.FeHexConvert;
import de.feig.FePortDriverException;
import de.feig.FeReaderDriverException;
import de.feig.FedmBrmTableItem;
import de.feig.FedmException;
import de.feig.FedmIscReader;
import de.feig.FedmIscReaderConst;
import de.feig.FedmIscReaderID;
import de.feig.FedmIscReaderInfo;
import de.feig.FedmIscRssiItem;
import de.opentiming.feigWS.help.FileOutput;


/**
 *
 * @author Martin Bussmann
 * 
 * Thread der den Buffer ausließt und die Inhalte in eine Textdatei schreibt.
 * 
 * - Nach einem Reconnect wird die Zeit automatisch neu gesetzt.
 * - Wenn der Reader nicht verbunden werden konnte wird dies alle 5 sec. erneut versucht
 * 
 */

public class BrmReadThread implements Runnable {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private FileOutput fo;
	private boolean firstConnect;
	private FedmConnect con;
	private int sleepTime;
	private FedmIscReader fedm;
	private int sets = 255;
	private boolean running;

	public BrmReadThread(FedmConnect con, String outputDir) {
		firstConnect = true;
		this.con = con;
		fedm = con.getFedmIscReader();
		fo = new FileOutput(outputDir);
		fo.setHost(con.getHost());
	}

	public synchronized void run() {
		try {			
			while (isRunning()) {

				con.fedmOpenConnection();

				if (con.isConnected()) {
					
					if (firstConnect) {

						firstConnect = false;
					}
					
					fedm.setTableSize(FedmIscReaderConst.BRM_TABLE, 256);
					readBuffer();

				} else {
					firstConnect = true;
					 // 5 sec. warten, wenn kein Reader verbunden werden konnte
					Thread.sleep(5000);
				}

				Thread.sleep(sleepTime);
			}
		} catch (InterruptedException e) {
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readBuffer() {

		if (fedm == null) {
			return;
		}

		FedmIscReaderInfo readerInfo = fedm.getReaderInfo();

		// read data from reader
		// read max. possible no. of data sets: request 255 data sets
		try {

			fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_ADV_BRM_SETS, sets);
			fedm.sendProtocol((byte) 0x22);

			FedmBrmTableItem[] brmItems = null;
			log.info("{} Anzahl Tags: {}", con.getHost(), fedm.getTableLength(FedmIscReaderConst.BRM_TABLE));

			if (fedm.getTableLength(FedmIscReaderConst.BRM_TABLE) > 0)
				brmItems = (FedmBrmTableItem[]) fedm.getTable(FedmIscReaderConst.BRM_TABLE);

			if (brmItems != null) {
				
				String[] serialNumberHex = new String[brmItems.length];
				// String[] serialNumber = new String[brmItems.length];
				int[] serialNumber = new int[brmItems.length];
				String[] uniqeNumber = new String[brmItems.length];
				String[] data = new String[brmItems.length];
				String[] date = new String[brmItems.length];
				String[] time = new String[brmItems.length];
				String[] type = new String[brmItems.length];
				String[] antNr = new String[brmItems.length];
				String[] rssi = new String[brmItems.length];

				String cTime = getComputerTime();
				String csvFileContent = "";

				for (int i = 0; i < brmItems.length; i++) {
										
					if (brmItems[i].isDataValid(FedmIscReaderConst.DATA_SNR)) {
						serialNumberHex[i] = brmItems[i].getStringData(FedmIscReaderConst.DATA_SNR);

						// zu kurze Seriennummern werden abgefangen
						while (serialNumberHex[i].length() < 8) {
							serialNumberHex[i] = "0" + serialNumberHex[i];
						}

						if (serialNumberHex[i].length() > 8) {
							serialNumberHex[i] = serialNumberHex[i].substring(0, 8);
						}

						// serialNumber[i] =
						// serialNumberHex[i].substring(serialNumberHex[i].length()-4,
						// serialNumberHex[i].length());
						serialNumber[i] = Integer.parseInt(serialNumberHex[i].substring(serialNumberHex[i].length() - 4,
								serialNumberHex[i].length()), 16);
						uniqeNumber[i] = serialNumberHex[i].substring(0, serialNumberHex[i].length() - 4);

					}

					if (brmItems[i].isDataValid(FedmIscReaderConst.DATA_RxDB)) { // data
																					// block
						byte[] b = brmItems[i].getByteArrayData(FedmIscReaderConst.DATA_RxDB,
								brmItems[i].getBlockAddress(), brmItems[i].getBlockCount());
						data[i] = FeHexConvert.byteArrayToHexString(b);
						System.out.println("DATA_RxDB: " + FeHexConvert.byteArrayToHexString(b));
					}

					if (brmItems[i].isDataValid(FedmIscReaderConst.DATA_TRTYPE)) { // tranponder
																					// type
						type[i] = brmItems[i].getStringData(FedmIscReaderConst.DATA_TRTYPE);
						// System.out.println("DATA_TRTYPE: "+
						// brmItems[i].getStringData(FedmIscReaderConst.DATA_TRTYPE));
					}

					rssi[i] = getAntData(brmItems[i], "RSSI");
					antNr[i] = getAntData(brmItems[i], "NR");

					if (brmItems[i].isDataValid(FedmIscReaderConst.DATA_TIMER)) { // Timer

						switch (readerInfo.readerType) {
						case de.feig.FedmIscReaderConst.TYPE_ISCLRU1002:
							date[i] = ReaderTime.getComputerDate();
							break;
						default:
							String year = Integer.toString(brmItems[i].getReaderTime().getYear());
							String month = Integer.toString(brmItems[i].getReaderTime().getMonth());
							String day = Integer.toString(brmItems[i].getReaderTime().getDay());
							date[i] = year + "-" + month + "-" + day;
							break;
						}

						String hour = Integer.toString(brmItems[i].getReaderTime().getHour());
						if (hour.length() == 1) {
							hour = "0" + hour;
						}
						String minute = Integer.toString(brmItems[i].getReaderTime().getMinute());
						if (minute.length() == 1) {
							minute = "0" + minute;
						}
						String second = Integer.toString(brmItems[i].getReaderTime().getMilliSecond() / 1000);
						if (second.length() == 1) {
							second = "0" + second;
						}
						String millisecond = Integer.toString(brmItems[i].getReaderTime().getMilliSecond() % 1000);
						if (millisecond.length() == 1) {
							millisecond = "0" + millisecond;
						}
						if (millisecond.length() == 2) {
							millisecond = "0" + millisecond;
						}

						time[i] = hour + ":" + minute + ":" + second + "." + millisecond;
					}

					csvFileContent = csvFileContent + "\n" + Integer.toString(serialNumber[i]) + ";" + date[i] + ";"
							+ time[i].substring(0, 8) + ";" + time[i].substring(9, 12) + ";" + con.getHost() + ";" + antNr[i]
							+ ";" + rssi[i] + ";" + uniqeNumber[i] + ";" + cTime;

					log.info("{} " + serialNumberHex[i] + " - " + antNr[i] + " - " + rssi[i] + " - " + serialNumber[i], con.getHost());

					/*
					 * //Senden der Daten an die serielle Schnittstelle
					 * if(ReadConfig.getConfig().getString("SERIAL_OUTPUT").
					 * equalsIgnoreCase("YES")) {
					 * LogWriter.write("Write to serial Port\n");
					 * SerialSendThread sSendThread = new SerialSendThread();
					 * Thread runner = new Thread(sSendThread);
					 * sSendThread.setMessage(time, serialNumber);
					 * runner.start(); }
					 */
				}

				try {
					fo.writeToFile(csvFileContent);
					if ((fedm.getLastError() >= 0)) {
						clearBuffer(this.fedm);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			//e.printStackTrace();
			log.error("{} reader connection brocken",  con.getHost());
		}
	}

	/**
	 * 
	 * Liefert den RSSI Wert und die Antennennummer
	 * 
	 * @param fedmBrmTableItem
	 * @param key
	 * @return
	 */
	private String getAntData(FedmBrmTableItem fedmBrmTableItem, String key) {

		String res = "0";
		byte b = 0;
		try {
			if (fedmBrmTableItem.getIntegerData(FedmIscReaderConst.DATA_ANT_NR) == 0) {
				HashMap<Integer, FedmIscRssiItem> item;

				item = fedmBrmTableItem.getRSSI();
				for (int i = 1; i < 5; i++) {
					if (item.get(i) != null) {
						FedmIscRssiItem fedmIscRssiItem = (item.get(i));
						if (key.equals("RSSI")) {
							b = fedmIscRssiItem.RSSI;
						}
						if (key.equals("NR")) {
							b = fedmIscRssiItem.antennaNumber;
						}
						res = b + "";
					}
				}

			} else {
				if (key.equals("NR")) {
					if (fedmBrmTableItem.isDataValid(FedmIscReaderConst.DATA_ANT_NR)) { // ant nr
						res = fedmBrmTableItem.getStringData(FedmIscReaderConst.DATA_ANT_NR);
						res = getDualValue(res);
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res + "";
	}

	/**
	 * Liefert die aktuelle Zeit des Hosts
	 * 
	 * @return
	 */
	
	public String getComputerTime() {
		Date now = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
		return sdf.format(now);
	}

	/**
	 * Liefert die Antennn an denen ein Tag erkannt wurde im Dual Format
	 * 
	 * @param antNr
	 * @return
	 */
	private String getDualValue(String antNr) {

		int r; // Rest r

		int dez = Integer.parseInt(antNr, 16);
		// int dez = Integer.parseInt(antNr);
		String dual = ""; // die Ausgabe wird in einer Zeichenkette (string) gesammelt

		do {
			r = dez % 2; // Rest berechnen
			dez = dez / 2; // neues n berechnen
			if (r == 0)
				dual = '0' + dual;
			else
				dual = '1' + dual; // Ausgabe konstruieren
		} while (dez > 0);

		while (dual.length() < 4) {
			dual = "0" + dual;
		}

		return dual;
	}

	private void clearBuffer(FedmIscReader fedm) {
		if (fedm == null) {
			return;
		}

		// clear all read data in reader
		try {
			fedm.sendProtocol((byte) 0x32);
		} catch (FedmException e) {
		} catch (FeReaderDriverException e) {
		} catch (FePortDriverException e) {
		}

	}

	public int getSets() {
		return sets;
	}

	public void setSets(int sets) {
		this.sets = sets;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
	    if(running) { log.info("{} start brmReadThread", con.getHost()); }
	    if(!running) { log.info("{} kill brmReadThread", con.getHost()); }
		this.running = running;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}
	
}