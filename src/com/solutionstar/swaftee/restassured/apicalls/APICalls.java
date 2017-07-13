package com.solutionstar.swaftee.restassured.apicalls;

import static com.jayway.restassured.RestAssured.given;

import com.jayway.restassured.response.Cookie;
import com.jayway.restassured.response.Response;
import com.solutionstar.swaftee.restassured.*;
import com.solutionstar.swaftee.restassured.apiconstants.APIConstants;
import com.solutionstar.swaftee.restassured.utils.Utils;

public class APICalls {
	Utils utils = new Utils();

	public Response getRequest(String endpoint) {

		return given().headers(utils.getHeaders()).when().get(endpoint).thenReturn();

	}

	public Response getRequest(Response res, String endpoint) {

		return given().cookie(utils.getDetailedCookieUtil(res, APIConstants.RW_API_AUTH)).headers(utils.getHeaders1())
				.when().get(endpoint).thenReturn();

	}

	public Response postRequest(Object body, String endpoint) {
		return given().headers(utils.getHeaders1()).body(body).when().post(endpoint).then().extract().response();
	}

	public Response putRequest(Object body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().put(endpoint).then().extract().response();

	}

	public Response deleteRequest(Object body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().delete(endpoint).then().extract().response();
	}

}