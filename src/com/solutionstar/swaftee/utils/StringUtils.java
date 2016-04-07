package com.solutionstar.swaftee.utils;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils {

	protected static Logger logger = LoggerFactory.getLogger(CommonUtils.class.getName());

	/***
	 * 
	 * @param array
	 * @return This method removes the preceding/leading and trailing white spaces from the array elements and returns the trimmed version of arrayList  
	 */
	public static ArrayList<String> trimArrayElements(String[] array)
	{
		String[] trimmedArray = new String[array.length];
		for (int i = 0; i < array.length; i++)
		    trimmedArray[i] = array[i].replaceAll("^\\s+|\\s+$", "");

		return new ArrayList<String>(Arrays.asList(trimmedArray));
	}
}
