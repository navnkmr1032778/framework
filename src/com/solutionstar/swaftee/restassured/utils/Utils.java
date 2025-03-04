package com.solutionstar.swaftee.restassured.utils;

import java.io.*;
import java.util.*;

import org.json.*;

import com.solutionstar.swaftee.restassured.apiconstants.*;
import com.solutionstar.swaftee.restassured.schemavalidator.*;

import io.restassured.http.*;
import io.restassured.http.Cookie;
import io.restassured.path.json.*;
import io.restassured.response.*;

public class Utils {
	public Cookie getDetailedCookieUtil(Response response, String cookieName) {
		return response.getDetailedCookie(cookieName);
	}

	public void createResponseFile(String methodName, Response response) throws FileNotFoundException {
		String res = response.asString();
		File myFile = new File(APIConstants.PATH_TO_RESPONSE + methodName + APIConstants.JSON_EXTENSION);
		PrintWriter log_file_writer = new PrintWriter(myFile);
		log_file_writer.println(res);
		log_file_writer.flush();
		log_file_writer.close();

	}

	public void schemaValidation_Utils(String schemaFileName, String responseFileName, Response res)
			throws FileNotFoundException {
		createResponseFile(responseFileName, res);

		File schemaFile = new File(APIConstants.PATH_TO_SCHEMA + schemaFileName + ".json");
		File jsonFile = new File(APIConstants.PATH_TO_RESPONSE + responseFileName + ".json");

		try {
			if (SchemaValidatorUtils.isJsonValid(schemaFile, jsonFile)) {
				System.out.println("Valid!");
			} else {
				System.out.println("NOT valid!");
			}
		} catch (Exception e) {
			e.getMessage();
		}

	}

	// Converting Response - > String - > JSON
	public JsonPath getJsonPath(Response res) {
		String json = res.asString();
		return new JsonPath(json);
	}

	// Get a single value from JSON path E.g. "items[0].listingKey"
	public String getValueFromJsonPath(Response res, String arrayPath) {
		JsonPath jp = getJsonPath(res);
		String value = jp.get(arrayPath).toString();
		return value;

	}

	/*
	 * Get list of values from JSON path "items.listingKey" - It returns a list of
	 * values
	 */

	public List<String> getListFromJsonPath(Response res, String arrayPath) {
		JsonPath jp = getJsonPath(res);
		List<String> list = jp.get(arrayPath);
		return list;
	}

	public List<HashMap<String, String>> getHashMapFromJsonPath(Response res, String arrayPath) {
		JsonPath jp = getJsonPath(res);
		List<HashMap<String, String>> hm = jp.get(arrayPath);
		return hm;
	}

	// Get response as jsonString
	public String getJsonString(Response res) {
		String jsonString = res.getBody().asString().replaceAll("\\[|\\]", "");
		return jsonString;
	}

	// Print response in JSON format
	public String prettyPrintUtil(Response res) {
		JsonPath jp = getJsonPath(res);
		return jp.prettyPrint();
	}

	// ****************
	// Print response by 'JSON Path' by storing it in list
	public void printResponseByJsonPath(Response res, String arrayPath) {
		JsonPath jp = getJsonPath(res);
		List<String> list = jp.get(arrayPath);
		for (String listItems : list) {
			System.out.println(listItems);
		}
	}

	public Headers getHeaders() {

		List<Header> listOfHeaders = new ArrayList<>();

		String clientToken = System.getProperty("clienttoken", "false");
		if (clientToken != null && !clientToken.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("clienttoken", clientToken));

		String apiKey = System.getProperty("ApiKey");
		String apiKeyProperty = System.getProperty("apiKeyProperty");
		if (apiKey != null && apiKeyProperty != null && !apiKey.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header(apiKeyProperty, apiKey));

		String accept = System.getProperty("Accept", "false");
		if (accept != null && !accept.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Accept", accept));

		String acceptCharset = System.getProperty("Accept-Charset", "false");
		if (acceptCharset != null && !acceptCharset.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Accept-Charset", acceptCharset));

		String acceptEncoding = System.getProperty("Accept-Encoding", "false");
		if (acceptEncoding != null && !acceptEncoding.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Accept-Encoding", acceptEncoding));

		String acceptLanguage = System.getProperty("Accept-Language", "false");
		if (acceptLanguage != null && !acceptLanguage.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Accept-Language", acceptLanguage));

		String accessControlRequestHeaders = System.getProperty("Access-Control-Request-Headers", "false");
		if (accessControlRequestHeaders != null && !accessControlRequestHeaders.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Access-Control-Request-Headers", accessControlRequestHeaders));

		String accessControlRequestMethod = System.getProperty("Access-Control-Request-Method", "false");
		if (accessControlRequestMethod != null && !accessControlRequestMethod.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Access-Control-Request-Method", accessControlRequestMethod));

		String authorization = System.getProperty("Authorization", "false");
		if (authorization != null && !authorization.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Authorization", authorization));

		String cacheControl = System.getProperty("Cache-Control", "false");
		if (cacheControl != null && !cacheControl.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Cache-Control", cacheControl));

		String contentMD5 = System.getProperty("Content-MD5", "false");
		if (contentMD5 != null && !contentMD5.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Content-MD5", contentMD5));

		String contentLength = System.getProperty("Content-Length", "false");
		if (contentLength != null && !contentLength.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Content-Length", contentLength));

		String contentTransferEncoding = System.getProperty("Content-Transfer-Encoding", "false");
		if (contentTransferEncoding != null && !contentTransferEncoding.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Content-Transfer-Encoding", contentTransferEncoding));

		String contentType = System.getProperty("Content-Type", "false");
		if (contentType != null && !contentType.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Content-Type", contentType));

		String cookie = System.getProperty("Cookie", "false");
		if (cookie != null && !cookie.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Cookie", cookie));

		String cookie2 = System.getProperty("Cookie2", "false");
		if (cookie2 != null && !cookie2.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Cookie2", cookie2));

		String date = System.getProperty("Date", "false");
		if (date != null && !date.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Date", date));

		String expect = System.getProperty("Expect", "false");
		if (expect != null && !expect.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Expect", expect));

		String from = System.getProperty("From", "false");
		if (from != null && !from.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("From", from));

		String host = System.getProperty("Host", "false");
		if (host != null && !host.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Host", host));

		String ifMatch = System.getProperty("If-Match", "false");
		if (ifMatch != null && !ifMatch.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("If-Match", ifMatch));

		String ifModifiedSince = System.getProperty("If-Modified-Since", "false");
		if (ifModifiedSince != null && !ifModifiedSince.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("If-Modified-Since", ifModifiedSince));

		String ifNoneMatch = System.getProperty("If-None-Match", "false");
		if (ifNoneMatch != null && !ifNoneMatch.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("If-None-Match", ifNoneMatch));

		String ifRange = System.getProperty("If-Range", "false");
		if (ifRange != null && !ifRange.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("If-Range", ifRange));

		String ifUnmodifiedSince = System.getProperty("If-Unmodified-Since", "false");
		if (ifUnmodifiedSince != null && !ifUnmodifiedSince.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("If-Unmodified-Since", ifUnmodifiedSince));

		String keepAlive = System.getProperty("Keep-Alive", "false");
		if (keepAlive != null && !keepAlive.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Keep-Alive", keepAlive));

		String maxForwards = System.getProperty("Max-Forwards", "false");
		if (maxForwards != null && !maxForwards.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Max-Forwards", maxForwards));

		String origin = System.getProperty("Origin", "false");
		if (origin != null && !origin.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Origin", origin));

		String pragma = System.getProperty("Pragma", "false");
		if (pragma != null && !pragma.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Pragma", pragma));

		String proxyAuthorization = System.getProperty("Proxy-Authorization", "false");
		if (proxyAuthorization != null && !proxyAuthorization.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Proxy-Authorization", proxyAuthorization));

		String range = System.getProperty("Range", "false");
		if (range != null && !range.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Range", range));

		String referer = System.getProperty("Referer", "false");
		if (referer != null && !referer.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Referer", referer));

		String te = System.getProperty("TE", "false");
		if (te != null && !te.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("TE", te));

		String trailer = System.getProperty("Trailer", "false");
		if (trailer != null && !trailer.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Trailer", trailer));

		String transferEncoding = System.getProperty("Transfer-Encoding", "false");
		if (transferEncoding != null && !transferEncoding.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Transfer-Encoding", transferEncoding));

		String upgrade = System.getProperty("Upgrade", "false");
		if (upgrade != null && !upgrade.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Upgrade", upgrade));

		String userAgent = System.getProperty("User-Agent", "false");
		if (userAgent != null && !userAgent.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("User-Agent", userAgent));

		String via = System.getProperty("Via", "false");
		if (via != null && !via.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Via", via));

		String warning = System.getProperty("Warning", "false");
		if (warning != null && !warning.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Warning", warning));

		String xRequestedWith = System.getProperty("X-Requested-With", "false");
		if (xRequestedWith != null && !xRequestedWith.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("X-Requested-With", xRequestedWith));

		String XDoNotTrack = System.getProperty("X-Do-Not-Track", "false");
		if (XDoNotTrack != null && !XDoNotTrack.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("X-Do-Not-Track", XDoNotTrack));

		String DNT = System.getProperty("DNT", "false");
		if (DNT != null && !DNT.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("DNT", DNT));

		String connection = System.getProperty("Connection", "false");
		if (connection != null && !connection.equalsIgnoreCase("false"))
			listOfHeaders.add(new Header("Connection", connection));

		return new Headers(listOfHeaders);
	}

	public void jsonString(String myJSONString) {
		try {
			JSONObject object = new JSONObject(myJSONString);
			String[] keys = JSONObject.getNames(object);

			for (String key : keys) {
				System.out.println(key);
				Object value = object.get(key);
				System.out.println(value.toString());
				// Determine type of value and do something with it...
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// Convert request body file to string
	public String convertFileToString(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}

}
