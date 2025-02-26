package com.solutionstar.swaftee.utils;

import org.slf4j.*;

public class StringUtils {

	protected static Logger logger = LoggerFactory.getLogger(CommonUtils.class.getName());

	/***
	 * 
	 * @param array
	 * @return This method removes the preceding/leading and trailing white spaces
	 *         from the array elements and returns the trimmed version of array
	 */
	public static String[] trimArrayElements(String[] array) {
		for (int i = 0; i < array.length; i++)
			array[i] = array[i].trim();
		return array;
	}
}
