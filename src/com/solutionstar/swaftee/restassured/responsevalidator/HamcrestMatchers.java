package com.solutionstar.swaftee.restassured.responsevalidator;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Every.everyItem;

import java.math.*;
import java.util.*;

import org.hamcrest.*;

import com.solutionstar.swaftee.restassured.utils.*;

import io.restassured.response.*;

public class HamcrestMatchers {
	Utils utils = new Utils();

	public void startsWith_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(startsWith(expected)));

	}

	public void endsWithList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(endsWith(expected)));

	}

	public void containsString_Hamcrest(String exceptionMsg, String actual, String expected) {
		assertThat(exceptionMsg, actual, containsString(expected));

	}

	public void containsStringList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(containsString(expected)));

	}

	public void equalToIgnoringCase_Hamcrest(String actual, String expected) {
		assertThat(actual, equalToIgnoringCase(expected));

	}

	public void equalToIgnoringCaseList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(equalToIgnoringCase(expected)));

	}

	public void greaterThan_Hamcrest(String actual, String expected) {
		assertThat(actual, greaterThan(expected));

	}

	public void greaterThanList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(greaterThan(expected)));

	}

	public void greaterThanOrEqualTo_Hamcrest(String actual, String expected) {
		assertThat(actual, greaterThanOrEqualTo(expected));

	}

	public void greaterThanOrEqualToList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(greaterThanOrEqualTo(expected)));
	}

	public void lessThan_Hamcrest(String actual, String expected) {
		assertThat(actual, lessThan(expected));

	}

	public void lessThanList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(lessThan(expected)));

	}

	public void lessThanOrEqualTo_Hamcrest(String actual, String expected) {
		assertThat(actual, lessThanOrEqualTo(expected));

	}

	public void lessThanOrEqualToList_Hamcrest(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(lessThanOrEqualTo(expected)));
	}

	//
	public void equalToSize(List<String> actual, int expected) {
		assertThat(actual, hasSize(expected));

	}

	public void compareIntegerValues(String exceptionMsg, int actual, int expected) {
		assertThat(exceptionMsg, actual, equalTo(expected));
	}

	public void isDoubleValue(Double actual, Double integerPart, Double decimalPart) {
		assertThat(actual, is(closeTo(integerPart, decimalPart)));

	}

	public void isBigDecimalValue(BigDecimal actual, BigDecimal integerPart, BigDecimal decimalPart) {
		assertThat(actual, is(closeTo(integerPart, decimalPart)));

	}

	public void comparesEqualToUtil(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(comparesEqualTo(expected)));
	}

	public void isEqualToGivenValue(Response res, String jsonPath, String expected) {
		assertThat(utils.getListFromJsonPath(res, jsonPath), everyItem(equalTo(expected)));
	}

	public void isEqualToIgnoringWhiteSpace(List<String> actual, String expected) {

		isEqualToIgnoringWhiteSpace(actual, expected);

	}

	public void hasEntryUtil(HashMap<String, String> hm, String key, String value) {
		assertThat(hm, hasEntry(key, value));
	}

	public void hasItemUtil(List<String> list, String item) {
		assertThat(list, hasItem(item));
	}

	public void hasItemAndGreateThan(List<String> list, String item) {
		assertThat(list, hasItem(greaterThan(item)));
	}

	public void hasItemsUtil(List<String> actual, List<String> expected) {
		assertThat(actual, hasItems("a"));
	}

	public void hasValueUtil(Map<String, String> hm, String value) {
		assertThat(hm, hasValue(value));
	}

	public void hasValueMatcher(Map<String, String> hm, String value) {
		assertThat(hm, hasValue(containsString(value)));
	}

	public void emptyOrNullStringUtil(String actual) {
		assertThat(actual, emptyString());
	}

	public void emptyOrNullStringUtil(List<String> actual) {
		for (String item : actual)
			assertThat(item, emptyString());
	}

	public void containsJsonPathWithValue(String exceptionMsg, Response res, String jsonPath, Object value) {
		assertThat(exceptionMsg, utils.getJsonString(res), Matchers.is(value));
	}

	public void containsJsonPath(String exceptionMsg, Response res, String jsonPath) {
		assertThat(exceptionMsg, utils.getJsonString(res), Matchers.is(jsonPath));
	}

}
