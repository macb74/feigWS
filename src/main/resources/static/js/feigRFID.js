/*
 * 
 */
var t;
var isRunning = false;

function handleTabButtons(r) {
	clearTimeout(t);
	if($( '#reader-' + r + '-live').css('display') == 'block') {
		handleReaderResults(r , 'Aktuell_' + r + '.out', 'showlive'); 
		t = setTimeout(function() { 
			handleTabButtons(r);
			}, 5000);
	}
}

function handleWriteButtons(r, mode) {
	var error = true;
	var readerIp = r.replace(/_/g, '.');
	
	$('#faultstring-write-' + r).html('no reader connected');
	$('#faultstring-write-' + r).css("display","none");

	//$('#successstring-write-' + r).css("display","none");

	if( $('#mode-' + r).val() == 'BRM' ) {
		$('#faultstring-write-' + r).html('Enable ISO Mode');
		$('#faultstring-write-' + r).css("display","block");
		return;
	}
	
	if(!parseInt( $('#newnr-'+r).val() )) {
		$('#faultstring-write-' + r).html( 'Die Startnummer muss eine Zahl sein' );
		$('#faultstring-write-' + r).css("display","block");
		return;
	}
	
	if( mode == 0 ) {
		$('#stopWriteTag-' + r).css("display","inline-block");
		$('#writeTag-' + r).css("display","none");
	}
	
	if(!isRunning) {
		isRunning = true;
		
		var jqxhr = $.getJSON( '/api/' + readerIp + '/write/' + $('#newnr-'+r).val() );
		jqxhr.done(function( data ) {
			if(data != "") {
				$.each( data, function( key, val ) {
					if(key == 'success' && val == 'true') { 
						error = false;
					}
					
					if(key == 'message' && val != '') {
						$('#faultstring-write-' + r).html( val );
					}
				});
				
				if(!error) {
					if(mode == 0) {
						$('#newnr-' + r).val( parseInt(data['stnr'])+1 );
					}
					$('#successstring-write-' + r).html('Startnummer: ' + data['stnr'] + ' - Seriennummer: ' + data['newSerialNumber']);
					$('#successstring-write-' + r).css("display","block");
				}
				
				if(error) {
					$('#faultstring-write-' + r).css("display","block");
				}	
				
			}		
		});
		isRunning = false;
	}

	if(mode == 0) {
		t = setTimeout(function() { 
			handleWriteButtons(r, 0);
			}, $('#ConfigWriteSleepTime').val() * 1000);
	}

}

function handleStopWrite(r) {
	clearTimeout(t);
	$('#stopWriteTag-' + r).css("display","none");
	$('#writeTag-' + r).css("display","inline-block");
}


function getReaderData(r, a) {
	var action = 'info';
	var error = true;
	var readerIp = r.replace(/_/g, '.');
	
	switch(a) {
		case 0:
			action = 'ant/' + $('#antenna-'+r).val();
			break;
		case 1:
			action = 'time';
			break;
		case 2:
			action = 'validtime/' + $('#transponderValidTime-'+r).val();
			break;
		case 3:
			action = 'power/' + $('#power-'+r).val();
			break;
		case 4:
			action = 'resetReaderFile';
			break;
		case 5:
			var newmode = 'BRM';
			if($('#mode-'+r).val() == 'BRM') { newmode = 'ISO'; }
			action = 'mode/' + newmode;
			break;
		case 6:
			var newval = 'on';
			if($('#relais-'+r).val() == 'on') { newval = 'off'; }
			action = 'relais/' + newval;
			$('#relais-'+r).val(newval)
	}
	
	$('#faultstring-' + r).css("display","none");
	
	if(action != 'info') { 
		var x = $.getJSON( '/api/' + readerIp + '/' + action);
	}

	if(!isRunning) {
		isRunning = true;
		
		var jqxhr = $.getJSON( '/api/' + readerIp + '/info');
		jqxhr.done(function( data ) {
			if(data != "") {
				$.each( data, function( key, val ) {
					if(key == "mode") { 
						error = false;
					}
					
					if(key == "files") {
						setTableData(r, val, readerIp);
					}
					
					$('#' + key + '-' + r).val(val);
				});
			}
	
			if(error) {
				$('#faultstring-' + r).html('no reader connection');
				$('#faultstring-' + r).css("display","block");
			}
			
		});
		isRunning = false;
	}
}

function setTableData(r, val, readerIp) {
	table = "";

	val.sort(function(a, b) {
	    return a.file > b.file;
	});

	val.reverse(); 

	$.each(val, function(key, val) {
	    table = table + "<tr><td>" + val['file'] + "</td>" +
		"<td class='text-right'>" + val['linecount'] + "</td>" + 
    	"<td><a class='btn btn-success btn-sm' href='#' onclick=\"handleReaderResults('" + readerIp + "','" + val['file'] + "', 'show'); return false;\" role='button'>SHOW</a></td>" + 
		"<td><a class='btn btn-success btn-sm' href='#' onclick=\"handleReaderResults('" + readerIp + "','" + val['file'] + "', 'load'); return false;\" role='button'>DOWNLOAD</a></td>" + 
		"</tr>";
	});
	
	$('#tbody-'+r).html(table);
}


function handleReaderResults(r, file, mode) {
	if(mode == "show") {
		target = "#modal-body";
		$( '#modal' ).modal();
	}
	
	if(mode == "showlive") {
		target = "#showlive-" + r;
	}

	if(!isRunning) {
		isRunning = true;
		
		var jqxhr = $.getJSON( "/api/" + r + "/file/" + file);
	
		//$( target ).html( '' );
		jqxhr.done(function( data ) {
			$( target ).html( showReaderResults(data) );
		});
		
		isRunning = false;
	}
}

function showReaderResults(data) {

	var html = '';
	html = html + '<div class="table-responsive">' +
		'<table class="table table-striped table-vcenter">' +
		'<thead>' +
			'<tr>' +
				'<th>Startnummer</th>' +
				'<th>Datum</th>' +
				'<th>Uhrzeit</th>' +
				'<th>Milli</th>' +
				'<th>Reader</th>' +
				'<th>Antenne</th>' +
				'<th>RSSI</th>' +
				'<th>UID</th>' +
				'<th>Lesezeit</th>' +
			'</tr>' +
		'</thead>' +
		'<tbody>';
	

	data.reverse(); 
	
	var max = 100
	var count = 0;
	$.each(data, function(key, val) {
		var fields = val.split(";");
		html = html + '<tr>';
		$.each(fields, function(key, val) {
			html = html + '<td>' + val + '</td>';
		});
		html = html + '</tr>';
		count++;
		if(count > max) { return false; }
	});

		'</tbody>' +
	'</table>' +
	'</div>';
	
	return html;
}


function clearModal() {
	var data = '<span class="text-muted">loading...</span>';
	$( '#modal-body' ).html( data );
}