package de.opentiming.feigWS;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import de.opentiming.feigWS.reader.FedmConnect;
import de.opentiming.feigWS.help.FileOutput;
import de.opentiming.feigWS.reader.BrmReadThread;

@Component
public class FeigWsApplicationRunner implements ApplicationRunner {

	@Resource(name = "connections")
	private Map<String, FedmConnect> connections;
	
	@Autowired
	private Environment env;
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		String[] readers = env.getProperty("reader.ip").split(" ");
		
		for( String reader : readers) {
			
			FileOutput fo = new FileOutput(env.getProperty("file.output"));
			fo.setHost(reader);
			fo.resetReaderFile();
						
			FedmConnect con = new FedmConnect();
			con.logReaderProtocol(Boolean.valueOf(env.getProperty("reader.protocol")));
			con.setHost(reader);
			con.setPort(Integer.parseInt(env.getProperty("reader.port")));
			con.fedmOpenConnection();
			
			connections.put(reader, con);
			
			BrmReadThread brmReadThread = new BrmReadThread(con, env.getProperty("file.output"));
		    brmReadThread.setSleepTime(Integer.parseInt(env.getProperty("reader.sleep")));
		    brmReadThread.setSets(10);
		    Thread runner = new Thread(brmReadThread);
		    brmReadThread.setRunning(true);
		    runner.start();
			
		}
	}

}