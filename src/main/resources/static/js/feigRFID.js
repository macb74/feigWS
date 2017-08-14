/*
 * 
 */

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
	}
	
	$('#faultstring-'+r).addClass('hidden');
	
	if(action != 'info') { 
		var x = $.getJSON( "/api/" + readerIp + "/" + action);
	}

	var jqxhr = $.getJSON( "/api/" + readerIp + "/info");
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
			$('#faultstring-' + r).removeClass('hidden')
		}
		
	});
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
    	"<td><a class='btn btn-success btn-xs' href='#' onclick=\"handleReaderResults('" + readerIp + "','" + val['file'] + "', 'show'); return false;\" role='button'>SHOW</a></td>" + 
		"<td><a class='btn btn-success btn-xs' href='#' onclick=\"handleReaderResults('" + readerIp + "','" + val['file'] + "', 'load'); return false;\" role='button'>DOWNLOAD</a></td>" + 
		"</tr>";
	});
	
	$('#tbody-'+r).html(table);
}


function handleReaderResults(r, file, mode) {
	if(mode == "show") {
		modal = "#modal";
		method = "showReaderResults";
	}

	$( modal ).modal();
	var jqxhr = $.getJSON( "/api/" + r + "/file/" + file);

	jqxhr.done(function( data ) {
		$( modal + '-body' ).html( data );
	});
}

function clearModal() {
	var data = '<span class="text-muted">loading...</span>';
	$( '#modal-body' ).html( data );
}