package com.solutionstar.swaftee.jira;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.internal.LinkedTreeMap;
import com.solutionstar.swaftee.utils.CommonProperties;
import com.solutionstar.swaftee.utils.CommonUtils;

@SuppressWarnings("unchecked")
public class ZephyrUtils
{
	public final static String PASS = "1";
	public final static String FAIL = "2";
	public final static String UNEXECUTED = "-1";
	public final static String BLOCKED = "4";

	static String jiraServer, jiraAuth, cycleId;

	public static HashMap<String, String> testCaseStatus = new HashMap<String, String>();

	protected static Logger logger = LoggerFactory.getLogger(ZephyrUtils.class);

	static CommonUtils utils = new CommonUtils();

	public static void initZephyr(String testCycleId)
	{
		if (cycleId == null)
		{
			cycleId = testCycleId;
			jiraServer = System.getProperty("jiraServer");
			jiraAuth = System.getProperty("jiraAuth");
			if (jiraServer == null)
			{
				CommonProperties props = CommonProperties.getInstance();
				try
				{
					props.load("./conf/jiraconfiguration.properties");
					jiraServer = props.get("jira_server");
					jiraAuth = props.get("jira_auth_token");
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public static Map<String, Object> getCycleDetails()
	{
		Client client = ClientBuilder.newClient();
		Response response = client.target(jiraServer + "rest/zapi/latest/cycle/" + cycleId)
				.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth).get();
		logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());
		Map<String, Object> responseMap = utils.convertJSONToMap(response.readEntity(String.class));
		return responseMap;
	}
	
	public static String getTestCycleURL()
	{
		String url = jiraServer + "/secure/enav/#?query=cycleName%20in%20(\"" + getCycleDetails().get("name").toString() + "\")";
		return url;
	}

	public static HashMap<String, String> createNewTestExecution(String[] testCases)
	{
		HashMap<String, String> testExectionIdMap = new HashMap<String, String>();
		Client client = ClientBuilder.newClient();
		Map<String, Object> responseMap = getCycleDetails();
		Object projectId = responseMap.get("projectId");

		/*
		 * Add test to cycle - request will return appropriate message if test
		 * is already added to the cycle
		 */
		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("issues", testCases);
		hm.put("cycleId", cycleId);
		hm.put("projectId", String.valueOf(Double.valueOf(projectId.toString()).intValue()));
		hm.put("methodId", String.valueOf(1));

		Response response = client.target(jiraServer + "rest/zapi/latest/execution/addTestsToCycle")
				.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
				.post(Entity.json(utils.objToJson(hm)));

		logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());

		/*
		 * Create & perform execution for all test cases
		 */

		for (String s : testCases)
		{
			/*
			 * Get JIRA issue id
			 */
			response = client.target(jiraServer + "rest/api/latest/search")
					.queryParam("jql", "%20id%20in%20(" + s + ")").request(MediaType.APPLICATION_JSON_TYPE)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth).get();

			logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());

			responseMap = (HashMap<String, Object>) utils.constructObjfromJson(response.readEntity(String.class),
					responseMap.getClass());
			ArrayList<?> issues = (ArrayList<?>) responseMap.get("issues");
			LinkedTreeMap<String, Object> ltm = (LinkedTreeMap<String, Object>) issues.get(0);

			/*
			 * Create execution
			 */
			hm.clear();
			hm.put("issueId", ltm.get("id").toString());
			hm.put("cycleId", cycleId);
			hm.put("projectId", String.valueOf(Double.valueOf(projectId.toString()).intValue()));

			response = client.target(jiraServer + "rest/zapi/latest/execution").request(MediaType.APPLICATION_JSON_TYPE)
					.header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth).post(Entity.json(utils.objToJson(hm)));

			logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());

			responseMap = (HashMap<String, Object>) utils.constructObjfromJson(response.readEntity(String.class),
					responseMap.getClass());

			testExectionIdMap.put(s, responseMap.keySet().toArray()[0].toString());
		}
		client.close();

		return testExectionIdMap;

	}

	public static Map<String, Object> getInfoFromTestCycle()
	{
		Client client = ClientBuilder.newClient();

		Response response = client.target(jiraServer + "rest/zapi/latest/execution").queryParam("cycleId", cycleId)
				.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth).get();

		logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());

		Map<String, Object> responseMap = utils.convertJSONToMap(response.readEntity(String.class));

		client.close();

		return responseMap;
	}
	
	public static HashMap<String, String> getExecutionIdFromTestCycle()
	{
		HashMap<String, String> testExectionIdMap = new HashMap<String, String>();

		Map<String, Object> responseMap = getInfoFromTestCycle();

		List<LinkedTreeMap<String, Object>> executions = (List<LinkedTreeMap<String, Object>>) responseMap
				.get("executions");

		for (LinkedTreeMap<String, Object> execution : executions)
		{
			testExectionIdMap.put(execution.get("issueKey").toString(),
					"" + ((Double) execution.get("id")).longValue());
		}

		List<String> testCasesToBeAddded = new ArrayList<String>();

		for (String key : testCaseStatus.keySet())
		{
			if (!testExectionIdMap.containsKey(key))
			{
				testCasesToBeAddded.add(key);
			}
		}

		if (testCasesToBeAddded.size() > 0)
		{
			testExectionIdMap.putAll(
					createNewTestExecution(testCasesToBeAddded.toArray(new String[testCasesToBeAddded.size()])));
		}

		return testExectionIdMap;
	}

	public static void bulkUpdateStatus(Collection<String> executionIds, String status)
	{
		Client client = ClientBuilder.newClient();

		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("status", status);
		hm.put("stepStatus", "1");
		hm.put("executions", executionIds.toArray(new String[executionIds.size()]));

		String json = utils.objToJson(hm);

		Response response = client.target(jiraServer + "rest/zapi/latest/execution/updateBulkStatus")
				.request(MediaType.APPLICATION_JSON_TYPE).header(HttpHeaders.AUTHORIZATION, "Basic " + jiraAuth)
				.put(Entity.json(json));

		logger.info("Status : " + String.valueOf(response.getStatus()) + " - " + response.toString());

		client.close();
	}

	public static void resetExecutionStatusForCycle()
	{
		HashMap<String, String> testCaseExecutionMap = getExecutionIdFromTestCycle();
		bulkUpdateStatus(testCaseExecutionMap.values(), UNEXECUTED);
	}
	
	public static List<HashMap<String, String>> getExecutionStatusFromCycle()
	{

		List<HashMap<String, String>> output = new ArrayList<HashMap<String, String>>();

		Map<String, Object> responseMap = getInfoFromTestCycle();

		List<LinkedTreeMap<String, Object>> executions = (List<LinkedTreeMap<String, Object>>) responseMap
				.get("executions");

		for (LinkedTreeMap<String, Object> execution : executions)
		{
			HashMap<String, String> tmp = new HashMap<String, String>();
			tmp.put("key", execution.get("issueKey").toString());
			if (execution.containsKey("summary"))
			{
				tmp.put("name", execution.get("summary").toString());
			}
			else
			{
				tmp.put("name", "");
			}
			if (execution.containsKey("issueDescription"))
			{
				tmp.put("description", execution.get("issueDescription").toString());
			}
			else
			{
				tmp.put("description", "");
			}
			if (execution.containsKey("component"))
			{
				tmp.put("component", execution.get("component").toString());
			}
			else
			{
				tmp.put("component", "");
			}
			switch (execution.get("executionStatus").toString())
			{
			case UNEXECUTED:
				tmp.put("result", "UNEXECUTED");
				break;
			case PASS:
				tmp.put("result", "PASS");
				break;
			case FAIL:
				tmp.put("result", "FAIL");
				break;
			case BLOCKED:
				tmp.put("result", "BLOCKED");
				break;
			default:
				tmp.put("result", "");
			}
			output.add(tmp);
		}

		return output;
	}

	public static void updateExecutionStatusInJIRA()
	{
		try
		{
			// group test cases as per status
			HashMap<String, String> testCaseExecutionMap = getExecutionIdFromTestCycle();
			logger.info("Updating test cycle with test case ids - "+testCaseExecutionMap.keySet().toString());
			HashMap<String, Collection<String>> groupingMap = new HashMap<String, Collection<String>>();
			for (String testCase : testCaseStatus.keySet())
			{
				if (groupingMap.containsKey(testCaseStatus.get(testCase)))
				{
					groupingMap.get(testCaseStatus.get(testCase)).add(testCaseExecutionMap.get(testCase));
				}
				else
				{
					Collection<String> testCases = new ArrayList<String>();
					testCases.add(testCaseExecutionMap.get(testCase));
					groupingMap.put(testCaseStatus.get(testCase), testCases);
				}
			}
			for (String status : groupingMap.keySet())
			{
				bulkUpdateStatus(groupingMap.get(status), status);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
			logger.error("Error updating JIRA - " + e.getMessage());
		}
	}

	/**
	 * Updates the test case status in a static variable
	 * 
	 * @param testCases
	 *            - JIRA test cases whose execution statuses are to be updated
	 * @param status
	 *            - Execution status
	 * 
	 */
	public static void updateExecutionStatusOfTests(String[] testCases, String status)
	{
		for (String testCase : testCases)
		{
			if (testCaseStatus.containsKey(testCase))
			{
				if (status.equals(FAIL))
				{
					testCaseStatus.put(testCase, status);
				}
				else if (testCaseStatus.get(testCase).equals(FAIL))
				{
					continue;
				}
				else if (testCaseStatus.get(testCase).equals(PASS) && !testCaseStatus.get(testCase).equals(status))
				{
					continue;
				}
				else if (testCaseStatus.get(testCase).equals(BLOCKED) && !testCaseStatus.get(testCase).equals(status))
				{
					testCaseStatus.put(testCase, status);
				}
			}
			else
			{
				testCaseStatus.put(testCase, status);
			}
		}

	}

}
