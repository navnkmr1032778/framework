package com.solutionstar.swaftee.restassured.responsevalidator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Every.everyItem;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.restassured.response.Response;
import com.solutionstar.swaftee.restassured.utils.Utils;

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
		assertThat(actual, isEmptyOrNullString());
	}

	public void emptyOrNullStringUtil(List<String> actual) {
		for (String item : actual)
			assertThat(item, isEmptyOrNullString());
	}

	public void containsJsonPathWithValue(String exceptionMsg, Response res, String jsonPath, Object value) {
		assertThat(exceptionMsg, utils.getJsonString(res), hasJsonPath(jsonPath, equalTo(value)));
	}

	public void containsJsonPath(String exceptionMsg, Response res, String jsonPath) {
		assertThat(exceptionMsg, utils.getJsonString(res), hasJsonPath(jsonPath));
	}

}
