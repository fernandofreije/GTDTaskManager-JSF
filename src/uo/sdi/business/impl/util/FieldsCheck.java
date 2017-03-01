package uo.sdi.business.impl.util;

public class FieldsCheck {
	
	/**
	 * 
	 * @param args
	 * @return True if fields are incorrect, false otherwise.
	 */
	public static boolean invalidFieldCheck(String... args){
		for (String field : args){
			if (field.trim().compareTo("") == 0)
				return true;
		}
		return false;
		
	}

}
