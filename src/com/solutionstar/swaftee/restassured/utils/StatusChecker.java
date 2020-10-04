package com.solutionstar.swaftee.restassured.utils;

import static org.junit.Assert.assertEquals;

import com.jayway.restassured.response.Response;

public class StatusChecker {
	/*
	 * Below status codes indicates that the client's request was successfully
	 * received, understood, and accepted.
	 */

	public void checkStatusIs200_OK(Response res) {
		assertEquals("Status Check Failed!", 200, res.getStatusCode());
	}

	public void checkStatusIs201_Created(Response res) {
		assertEquals("Status Check Failed!", 201, res.getStatusCode());
	}

	public void checkStatusIs202_Accepted(Response res) {
		assertEquals("Status Check Failed!", 202, res.getStatusCode());
	}

	public void checkStatusIs203_Non_Authoritative_Information(Response res) {
		assertEquals("Status Check Failed!", 203, res.getStatusCode());

	}

	public void checkStatusIs204_NoContent(Response res) {
		assertEquals("Status Check Failed!", 204, res.getStatusCode());

	}

	public void checkStatusIs205_Reset_Content(Response res) {
		assertEquals("Status Check Failed!", 205, res.getStatusCode());

	}

	public void checkStatusIs206_Partial_Content(Response res) {
		assertEquals("Status Check Failed!", 206, res.getStatusCode());

	}

	public void checkStatusIs207_Multi_Status(Response res) {
		assertEquals("Status Check Failed!", 207, res.getStatusCode());

	}

	/*
	 * Below status codes indicates Client Error
	 */
	public void checkStatusIs400_Bad_Request(Response res) {
		assertEquals("Status Check Failed!", 400, res.getStatusCode());

	}

	public void checkStatusIs401_Unauthorized(Response res) {
		assertEquals("Status Check Failed!", 401, res.getStatusCode());

	}

	public void checkStatusIs403_Forbidden(Response res) {
		assertEquals("Status Check Failed!", 403, res.getStatusCode());

	}

	public void checkStatusIs404_Not_Found(Response res) {
		assertEquals("Status Check Failed!", 404, res.getStatusCode());

	}

	/*
	 * Response status codes - Server Errors
	 */
	public void checkStatusIs500_InternalServerError(Response res) {
		assertEquals("Status Check Failed!", 500, res.getStatusCode());

	}

	public void checkStatusIs502_BadGateway(Response res) {
		assertEquals("Status Check Failed!", 502, res.getStatusCode());

	}

	public void checkStatusIs503_ServiceUnavailable(Response res) {
		assertEquals("Status Check Failed!", 503, res.getStatusCode());

	}

	public void checkStatusIs504_GatewayTimeout(Response res) {
		assertEquals("Status Check Failed!", 504, res.getStatusCode());

	}

	public void checkStatusIs507_InsufficientStorage(Response res) {
		assertEquals("Status Check Failed!", 507, res.getStatusCode());

	}

}
