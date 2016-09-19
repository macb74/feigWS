package de.opentiming.feigws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.ws.Holder;

import de.feig.FedmIscReader;
import de.opentiming.feigws.connector.FedmConnect;
import de.opentiming.feigws.helper.ReadProperties;
import de.opentiming.feigws.helper.ValidateInputString;
import de.opentiming.feigws.server.FeigWSServer;


@WebService(portName = "FeigWS", serviceName = "FeigWS")
@SOAPBinding(style = Style.RPC)

public class FeigWSService {

	private ReadProperties props;
	private FedmIscReader fedm;
	private FedmConnect con;
	private String host;

	public FeigWSService() {
		con = new FedmConnect();		
		props = FeigWSServer.getProps();
	}

	/*
	public void getReaderConfig(
			@WebParam(name = "antenna", mode = WebParam.Mode.OUT) Holder<String> antenna,
			@WebParam(name = "trValidTime", mode = WebParam.Mode.OUT) Holder<String> trValidTime,
			@WebParam(name = "power", mode = WebParam.Mode.OUT) Holder<String> power,
			@WebParam(name = "time", mode = WebParam.Mode.OUT) Holder<String> time,
			@WebParam(name = "mode", mode = WebParam.Mode.OUT) Holder<String> mode,
			@WebParam(name = "files", mode = WebParam.Mode.OUT) Holder<String> files,
			@WebParam(name = "error", mode = WebParam.Mode.OUT) Holder<String> error) {

		error.value       = "0";
		antenna.value     = "";
		trValidTime.value = "";
		mode.value        = "";
		power.value       = "";
		time.value        = "";
		files.value       = "";

		con.setFedmIscReader(fedm);
		con.setHost(host);
		con.fedmOpenConnection();

		if (con.isConnected()) {
			String[] readerInfo = getConfig();
			antenna.value     = readerInfo[3];
			trValidTime.value = readerInfo[1];
			mode.value        = readerInfo[0];
			power.value       = readerInfo[4];
			time.value        = readerInfo[2];
			con.fedmCloseConnection();
		} else {
			error.value = "Can not connect";
		}
		
		ReaderResultFiles rrf = new ReaderResultFiles();
		files.value = rrf.getResultFiles(host);
		
	}
	*/

	
	public void setReaderConfig(
			@WebParam(name = "power", mode = WebParam.Mode.IN) String np,
			@WebParam(name = "time", mode = WebParam.Mode.IN) String nt,
			@WebParam(name = "trValidTime", mode = WebParam.Mode.IN) String ntvt,
			@WebParam(name = "antenna", mode = WebParam.Mode.IN) String na,
			@WebParam(name = "mode", mode = WebParam.Mode.IN) String nm,
			@WebParam(name = "resetReaderFile", mode = WebParam.Mode.IN) String rf,
			@WebParam(name = "antenna", mode = WebParam.Mode.OUT) Holder<String> antenna,
			@WebParam(name = "trValidTime", mode = WebParam.Mode.OUT) Holder<String> trValidTime,
			@WebParam(name = "power", mode = WebParam.Mode.OUT) Holder<String> power,
			@WebParam(name = "time", mode = WebParam.Mode.OUT) Holder<String> time,
			@WebParam(name = "mode", mode = WebParam.Mode.OUT) Holder<String> mode,
			@WebParam(name = "files", mode = WebParam.Mode.OUT) Holder<String> files,
			@WebParam(name = "error", mode = WebParam.Mode.OUT) Holder<String> error) {

		error.value       = "0";
		antenna.value     = "";
		trValidTime.value = "";
		mode.value        = "";
		power.value       = "";
		time.value        = "";
		files.value       = "";

		con.setFedmIscReader(fedm);
		con.setHost(host);
		con.fedmOpenConnection();

		if (con.isConnected()) {
			
			if(ValidateInputString.validateInput("time", nt)) {
				SetTime t = new SetTime();
				t.setFedmIscReader(fedm);
				t.setTime();
			}
			
			if(ValidateInputString.validateInput("power", np)) {
				SetPower p = new SetPower();
				p.setFedmIscReader(fedm);
				p.setPower(np);
			}

			if(ValidateInputString.validateInput("antenna", na)) {
				SetAntenna a = new SetAntenna();
				a.setFedmIscReader(fedm);
				a.setAntennas(na);
			}
			
			if(ValidateInputString.validateInput("trValidTime", ntvt)) {
				SetValidTime vt = new SetValidTime();
				vt.setFedmIscReader(fedm);
				vt.setValidTime(ntvt);
			}
			
			if(ValidateInputString.validateInput("mode", nm)) {
				SetMode m = new SetMode();
				m.setFedmIscReader(fedm);
				m.setMode(nm);

			}
			
			if(ValidateInputString.validateInput("resetReaderFile", rf)) {
				ResetReaderFile r = new ResetReaderFile();
				r.resetReaderFile(host);
			}
			
			String[] readerInfo = getConfig();
			antenna.value     = readerInfo[3];
			trValidTime.value = readerInfo[1];
			mode.value        = readerInfo[0];
			power.value       = readerInfo[4];
			time.value        = readerInfo[2];
			
			con.fedmCloseConnection();
		} else {
			error.value = "Can not connect";
		}

		ReaderResultFiles rrf = new ReaderResultFiles();
		files.value = rrf.getResultFiles(host);

	}

	/*
	public void resetReaderFile(@WebParam(name = "status", mode = WebParam.Mode.OUT) Holder<String> status) {	
		ResetReaderFile r = new ResetReaderFile();
		status.value = r.resetReaderFile(host);
	}
	*/

	/*
	public void getReaderResultFiles(
			@WebParam(name = "result", mode = WebParam.Mode.OUT) Holder<String> result) {	
		ReaderResultFiles r = new ReaderResultFiles();
		result.value = r.getResultFiles(host);
	}
	*/
	
	public void getReaderResults(
			@WebParam(name = "resultFile", mode = WebParam.Mode.IN) String rf,
			@WebParam(name = "results", mode = WebParam.Mode.OUT) Holder<String> results) {	
		ReaderResults r = new ReaderResults();
		results.value = r.getResults(rf);
	}
	
	
	private String[] getConfig() {
		Info info = new Info();
		info.setFedmIscReader(fedm);
		String[] readerInfo = info.getConfig();
		return readerInfo;
	}

	@WebMethod(exclude = true)
	public void setFedmIscReader(FedmIscReader fedm) {
		this.fedm = fedm;
	}
	
	@WebMethod(exclude = true)
	public void setHost(String host) {
		this.host = host;
		
	}
}