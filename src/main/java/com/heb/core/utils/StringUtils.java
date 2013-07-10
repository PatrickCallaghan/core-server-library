package com.heb.core.utils;

public class StringUtils {

	public static String concatStrings(String separator, String... values){
		
		StringBuffer buffer = new StringBuffer();
		
		for (String value : values){
			buffer.append(value);			
			buffer.append(separator);
		}
				
		return buffer.toString().substring(0, buffer.lastIndexOf(separator));		
	}
}
