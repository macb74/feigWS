package de.opentiming.feigws.helper;

public class ValidateInputString {

	public static boolean validateInput(String key, String value) {
		
		if((key.equalsIgnoreCase("time") || (key.equalsIgnoreCase("resetReaderFile"))) && value.equalsIgnoreCase("true")) {
			return true;
		}
		

		if(key.equalsIgnoreCase("power") || key.equalsIgnoreCase("antenna") || key.equalsIgnoreCase("trValidTime")) {
		    try {
		        Integer.parseInt( value );
		        return true;
		    } catch( Exception e ) {
		        return false;
		    }
		}
		
		if(key.equalsIgnoreCase("mode") && (value.equalsIgnoreCase("BRM") || value.equalsIgnoreCase("ISO"))) {
			return true;
		}
		
		return false;
	}

}
