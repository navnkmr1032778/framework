package com.solutionstar.swaftee.jira;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.LinkedTreeMap;
import com.solutionstar.swaftee.utils.CommonProperties;
import com.solutionstar.swaftee.utils.CommonUtils;

public class ZephyrUtils {
	
	
	static String jiraServer,jiraAuth,cycleId;
	protected static Logger logger = LoggerFactory
			.getLogger(ZephyrUtils.class.getName()); 
	
	CommonUtils utils;
	
	public ZephyrUtils()
	{
		CommonProperties props = CommonProperties.getInstance();
		utils = new CommonUtils();
		cycleId = System.getProperty("testCycleId");
		try {
			props.load("./conf/jiraconfiguration.properties");
			jiraServer = props.get("jira_server");
			jiraAuth = props.get("jira_auth_token");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 * @param testCases - JIRA test cases whose execution statuses are to be updated
	 * @param status - Execution status
	 * 
	 */
	public void updateExecutionStatusOfTests(String[] testCases, String status)
	{
		try
		{
			logger.info("Updating execution status for test cases "+StringUtils.join(testCases,", ")+" in JIRA..");

			Client client = ClientBuilder.newClient();

			/*
			 * Get project id
			 */
			Response response = client.target(jiraServer+"rest/zapi/latest/cycle/"+cycleId)
					.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
					.get();

			logger.info("Status : "+String.valueOf(response.getStatus())+" - "+response.toString());
			Map<String, Object> responseMap = utils.convertJSONToMap(response.readEntity(String.class));
			Object projectId = responseMap.get("projectId");

			/*
			 * Add test to cycle - request will return appropriate message if test is already added to the cycle
			 */
			HashMap<String,Object> hm = new HashMap<String, Object>();
			hm.put("issues", testCases);
			hm.put("cycleId",cycleId);
			hm.put("projectId", String.valueOf(Double.valueOf(projectId.toString()).intValue()));
			hm.put("methodId", String.valueOf(1));

			response = client.target(jiraServer+"rest/zapi/latest/execution/addTestsToCycle")
					.request(MediaType.APPLICATION_JSON_TYPE)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
					.post(Entity.json(utils.objToJson(hm)));

			logger.info("Status : "+String.valueOf(response.getStatus())+" - "+response.toString());

			/*
			 * Create & perform execution for all test cases
			 */

			for(String s : testCases)
			{
				/*
				 * Get JIRA issue id
				 */
				response = client.target(jiraServer+"rest/api/latest/search")
						.queryParam("jql","%20id%20in%20("+s+")")
						.request(MediaType.APPLICATION_JSON_TYPE)
						.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
						.get();

				logger.info("Status : "+String.valueOf(response.getStatus())+" - "+response.toString());

				responseMap = (HashMap<String,Object>)utils.constructObjfromJson(response.readEntity(String.class),responseMap.getClass());
				ArrayList<?> issues= (ArrayList<?>)responseMap.get("issues");
				LinkedTreeMap<String, Object> ltm = (LinkedTreeMap<String, Object>)issues.get(0);

				/*
				 * Create execution
				 */
				hm.clear();
				hm.put("issueId", ltm.get("id").toString());
				hm.put("cycleId",cycleId);
				hm.put("projectId", String.valueOf(Double.valueOf(projectId.toString()).intValue()));

				response = client.target(jiraServer+"rest/zapi/latest/execution")
						.request(MediaType.APPLICATION_JSON_TYPE)
						.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
						.post(Entity.json(utils.objToJson(hm)));

				logger.info("Status : "+String.valueOf(response.getStatus())+" - "+response.toString());

				responseMap = (HashMap<String,Object>)utils.constructObjfromJson(response.readEntity(String.class),responseMap.getClass());

				hm.clear();
				hm.put("status", status);

				/*
				 * Perform execution
				 */
				response = client.target(jiraServer+"rest/zapi/latest/execution/"+responseMap.keySet().toArray()[0].toString()+"/quickExecute")
						.request(MediaType.APPLICATION_JSON_TYPE)
						.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
						.post(Entity.json(utils.objToJson(hm)));

				logger.info("Status : "+String.valueOf(response.getStatus())+" - "+response.toString());	
			}
			client.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			logger.error("Error updating JIRA - "+e.getMessage());
		}
	}	
	
}

