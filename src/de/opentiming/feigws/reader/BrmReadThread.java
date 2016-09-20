package de.opentiming.feigws.reader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.HashMap;

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
import de.opentiming.feigws.connector.FedmConnect;
import de.opentiming.feigws.helper.LogWriter;
import de.opentiming.feigws.service.SetTime;

/**
 *
 * @author Martin Bussmann
 */
public class BrmReadThread implements Runnable {

	private String filename;
	private boolean firstConnect;

	public BrmReadThread() {
		firstConnect = true;
	}

	public synchronized void run() {
		try {

			filename = "output/" + host.replaceAll("\\.", "_") + ".out";

			FedmConnect con = new FedmConnect();
			con.setFedmIscReader(fedm);
			con.setHost(host);

			SetTime t = new SetTime();
			t.setFedmIscReader(fedm);

			while (isRunning()) {

				con.fedmOpenConnection();

				if (con.isConnected()) {
					
					if (firstConnect) {
						t.setTime();
						LogWriter.write(host, "set Time\n");
						firstConnect = false;
					}
					
					fedm.setTableSize(FedmIscReaderConst.BRM_TABLE, 256);
					readBuffer(this.fedm, this.sets);
					con.fedmCloseConnection();

				} else {
					firstConnect = true;
				}

				Thread.sleep(sleepTime);
			}
		} catch (InterruptedException e) {
		} catch (FedmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void readBuffer(FedmIscReader fedm, int sets) {

		if (fedm == null) {
			return;
		}

		FedmIscReaderInfo readerInfo = fedm.getReaderInfo();
		// read data from reader
		// read max. possible no. of data sets: request 255 data sets
		try {
			switch (readerInfo.readerType) {
			case de.feig.FedmIscReaderConst.TYPE_ISCLR200:
				fedm.setData(FedmIscReaderID.FEDM_ISCLR_TMP_BRM_SETS, sets);
				fedm.sendProtocol((byte) 0x21);
				break;
			default:
				fedm.setData(FedmIscReaderID.FEDM_ISC_TMP_ADV_BRM_SETS, sets);
				fedm.sendProtocol((byte) 0x22);
				break;
			}

			FedmBrmTableItem[] brmItems = null;
			LogWriter.write(host,
					"* " + fedm.getTableLength(FedmIscReaderConst.BRM_TABLE) + " *********************\n");

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
							date[i] = SetTime.getComputerDate();
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
							+ time[i].substring(0, 8) + ";" + time[i].substring(9, 12) + ";" + host + ";" + antNr[i]
							+ ";" + rssi[i] + ";" + uniqeNumber[i] + ";" + cTime;

					LogWriter.write(host,
							serialNumberHex[i] + " - " + antNr[i] + " - " + rssi[i] + " - " + serialNumber[i] + "\n");

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
					Path file = Paths.get(filename);
					if (Files.notExists(file)) {
						Files.write(file, "".getBytes());
					}

					Files.write(file, csvFileContent.getBytes(), StandardOpenOption.APPEND);
					if ((fedm.getLastError() >= 0)) {
						clearBuffer(this.fedm);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
					if (fedmBrmTableItem.isDataValid(FedmIscReaderConst.DATA_ANT_NR)) { // ant
																						// nr
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

	public String getComputerTime() {
		Date now = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
		return sdf.format(now);
	}

	private String getDualValue(String antNr) {

		int r; // Rest r

		int dez = Integer.parseInt(antNr, 16);
		// int dez = Integer.parseInt(antNr);
		String dual = ""; // die Ausgabe wird in einer Zeichenkette (string)
							// gesammelt

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

	public void setFedmIscReader(FedmIscReader fedm) {
		this.fedm = fedm;
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
		this.running = running;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	private int sleepTime;
	private String host;
	private FedmIscReader fedm;
	private int sets = 255;
	private boolean running;
}