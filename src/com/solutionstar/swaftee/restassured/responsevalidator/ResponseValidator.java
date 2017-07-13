package com.solutionstar.swaftee.restassured.responsevalidator;

import java.util.List;

import com.solutionstar.swaftee.restassured.apicalls.APICalls;
import com.solutionstar.swaftee.restassured.utils.Utils;



public class ResponseValidator {
	APICalls httpRequestSender = new APICalls();
	Utils utils = new Utils();

	/*
	 * Following utils gets a list of values from JSON path and stores in a
	 * List<String> and performs the validations accordingly
	 */

	// Validates list is in ascending order
	public boolean isSortedInAscendingOrder(List<String> list) {
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).compareTo(list.get(i)) > 0)
				return false;
		}

		return true;
	}

	// Validates list is in descending order
	public boolean isSortedInDescendingOrder(List<String> list) {
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i - 1).compareTo(list.get(i)) < 0)
				return false;
		}

		return true;
	}

	// Validates list is equal to a given value
	public boolean isEqualToGivenValue(List<String> list, String value) {

		for (String listItems : list) {
			if (listItems.equalsIgnoreCase(value) == false)
				return false;
		}
		return true;
	}

	// Validates list is equal to a boolean value
	public boolean isEqualToGivenBooleanValue(List<Boolean> list, boolean bool) {
		for (Boolean listItems : list) {
			if (listItems != bool)
				return false;
		}
		return true;
	}

	// Validates list is 'less than ' a given value
	public boolean isLessThanTheGivenValue(List<String> list, String value) {
		for (String listItems : list) {
			if (listItems.compareToIgnoreCase(value) > 0)
				return false;
		}
		return true;

	}

	// Validates list is 'greater than ' a given value
	public boolean isGreaterThanGivenValue(List<String> list, String value) {
		for (String listItems : list) {
			if (listItems.compareToIgnoreCase(value) < 0)
				return false;
		}
		return true;

	}

	// Validates list of values in in the given range
	public boolean isInTheRange(List<String> list, String min, String max) {
		boolean flag = false;
		for (String listItems : list) {
			if (listItems.compareToIgnoreCase(min) > 0 && listItems.compareToIgnoreCase(max) < 0)
				flag = true;
			else {
				flag = false;
				break;
			}
		}
		return flag;

	}

	// Validates list has float values
	public boolean isFloatValue(List<Float> list, float value) {
		for (Float listItems : list) {
			if (listItems.equals(value) == false)
				return false;
		}
		return true;
	}

	// Validates list has integer values
	public boolean isIntegerValue(List<Integer> list, int value) {
		for (Integer listItems : list) {
			if (listItems.equals(value) == false)
				return false;
		}
		return true;

	}

}
