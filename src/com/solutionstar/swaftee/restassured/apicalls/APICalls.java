package com.solutionstar.swaftee.restassured.apicalls;

import static io.restassured.RestAssured.*;

import java.io.*;
import java.util.*;

import com.solutionstar.swaftee.restassured.apiconstants.*;
import com.solutionstar.swaftee.restassured.utils.*;

import io.restassured.module.jsv.*;
import io.restassured.response.*;

public class APICalls {
	Utils utils = new Utils();

	public Response getRequest(String endpoint) {
		return (Response) given().headers(utils.getHeaders()).when().get(endpoint).then()
				.body(JsonSchemaValidator.matchesJsonSchemaInClasspath("products-schema.json"));
	}

	public Response getRequest(String endpoint, HashMap<String, String> param) {
		return given().params(param).headers(utils.getHeaders()).when().get(endpoint).thenReturn();
	}

	public Response getRequest(Response res, String endpoint) {
		return given().cookie(utils.getDetailedCookieUtil(res, APIConstants.RW_API_AUTH)).headers(utils.getHeaders())
				.when().get(endpoint).thenReturn();
	}

	public Response postRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().post(endpoint).then().extract().response();
	}

	public Response putRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().put(endpoint).then().extract().response();
	}

	public Response putRequest(Object body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().put(endpoint).then().extract().response();
	}

	public Response deleteRequest(String endpoint) {
		return given().headers(utils.getHeaders()).when().delete(endpoint).then().extract().response();
	}

	public Response deleteRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().delete(endpoint).then().extract().response();
	}

	public Response patchRequest(String body, String endpoint) {
		return given().headers(utils.getHeaders()).body(body).when().patch(endpoint).then().extract().response();
	}

	public Response postFileRequest(String fileName, String endpoint) {
		return given().headers(utils.getHeaders()).multiPart(new File(fileName)).log().all().when().post(endpoint)
				.then().log().all().extract().response();
	}

	public Response postFileRequest(String fileName, String endpoint, HashMap<String, String> map) {
		return given().headers(utils.getHeaders()).multiPart(new File(fileName)).formParams(map).log().all().when()
				.post(endpoint).then().log().all().extract().response();
	}

}