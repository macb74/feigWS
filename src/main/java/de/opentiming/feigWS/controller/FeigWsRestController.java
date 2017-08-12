package de.opentiming.feigWS.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.opentiming.feigWS.reader.ReaderAntenna;
import de.opentiming.feigWS.help.FileOutput;
import de.opentiming.feigWS.reader.FedmConnect;
import de.opentiming.feigWS.reader.ReaderInfo;
import de.opentiming.feigWS.reader.ReaderMode;
import de.opentiming.feigWS.reader.ReaderPower;
import de.opentiming.feigWS.reader.ReaderResultFiles;
import de.opentiming.feigWS.reader.ReaderValidTime;

@RestController
@RequestMapping(value="/api")
public class FeigWsRestController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private Environment env;
	
	@Resource(name = "connections")
	private Map<String, FedmConnect> connections;
	
    @RequestMapping(value="/{reader}/info", method=RequestMethod.GET)
    public Map<String, Object> getReaderInfo(@PathVariable String reader) {
    	FedmConnect con = connections.get(reader);
    	ReaderInfo ri = new ReaderInfo(con);
    	Map<String, Object> readerConfig = ri.getConfig();
    	
    	ReaderResultFiles rf = new ReaderResultFiles(env.getProperty("file.output"));
    	readerConfig.put("files", rf.getResultFiles(reader));
    	return readerConfig;
    }

    @RequestMapping(value="/{reader}/ant/{value}", method=RequestMethod.GET)
    public boolean setAntenna(@PathVariable String reader, @PathVariable String value) {
    	FedmConnect con = connections.get(reader);
    	ReaderAntenna a = new ReaderAntenna(con);
    	return a.setAntennas(value);
    }

    @RequestMapping(value="/{reader}/mode/{value}", method=RequestMethod.GET)
    public boolean setMode(@PathVariable String reader, @PathVariable String value) {
    	FedmConnect con = connections.get(reader);
    	ReaderMode m = new ReaderMode(con);
    	return m.setMode(value);
    }
    
    @RequestMapping(value="/{reader}/power/{value}", method=RequestMethod.GET)
    public boolean setPower(@PathVariable String reader, @PathVariable String value) {
    	FedmConnect con = connections.get(reader);
    	ReaderPower p = new ReaderPower(con);
    	return p.setPower(value);
    }
    
    @RequestMapping(value="/{reader}/validtime/{value}", method=RequestMethod.GET)
    public boolean setValidTime(@PathVariable String reader, @PathVariable String value) {
    	FedmConnect con = connections.get(reader);
    	ReaderValidTime v = new ReaderValidTime(con);
    	return v.setValidTime(value);
    }
    
    @RequestMapping(value="/{reader}/file/{value}", method=RequestMethod.GET)
    public List<String> getFileContent(@PathVariable String reader, @PathVariable String value) {
    	ReaderResultFiles rf = new ReaderResultFiles(env.getProperty("file.output"));
    	return rf.getFileContent(value);
    }
    
    @RequestMapping(value="/{reader}/resetReaderFile", method=RequestMethod.GET)
    public boolean resetReaderFile(@PathVariable String reader) {
    	FileOutput fo = new FileOutput(env.getProperty("file.output"));
    	fo.setHost(reader);
    	return fo.resetReaderFile();
    }

    @RequestMapping(value="/readers", method=RequestMethod.GET)
    public Set<String> getReaders() {
    	return connections.keySet();
    }
    
//    @RequestMapping(path = "/{reader}/download/{value}", method = RequestMethod.GET)
//    public ResponseEntity<Resource> download(@PathVariable String reader, @PathVariable String value) throws IOException {
//
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(param));
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentLength(file.length())
//                .contentType(MediaType.parseMediaType("application/octet-stream"))
//                .body(resource);
//    }
}
