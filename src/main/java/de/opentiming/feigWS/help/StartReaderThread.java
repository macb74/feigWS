package de.opentiming.feigWS.help;

import de.opentiming.feigWS.reader.BrmReadThread;
import de.opentiming.feigWS.reader.FedmConnect;

public class StartReaderThread {

	private BrmReadThread brmReadThread;
	
	public StartReaderThread(FedmConnect con, String file, String sleep) {
		brmReadThread = new BrmReadThread(con, file);
	    brmReadThread.setSleepTime(Integer.parseInt(sleep));
	    brmReadThread.setSets(10);
	    Thread runner = new Thread(brmReadThread);
	    brmReadThread.setRunning(true);
	    runner.start();
	}

	public BrmReadThread getBrmReadThread() {
		return brmReadThread;
	}

}
