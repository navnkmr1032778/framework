package com.solutionstar.swaftee.jira;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Zephyr
{
	public static String testCycleId = System.getProperty("testCycleId");

	public static void resetTestCycle()
	{
		ZephyrUtils.initZephyr(testCycleId);
		ZephyrUtils.resetExecutionStatusForCycle();
	}

	public static void generateHtmlReport()
	{
		String writeToFile = System.getProperty("writeTo");
		boolean includeDescription = Boolean.valueOf(System.getProperty("includeDescription", "false"));
		StringBuilder output1 = new StringBuilder();
		output1.append(
				"<style>.PASS{color:green}.FAIL{color:red}.UNEXECUTED{color:orange}.BLOCKED{color:grey}</style><b>Test Case Status:</b><table border=1><tr><th>Jira Key</th><th>Name</th>");
		if (includeDescription)
			output1.append("<th>Description</th>");
		output1.append("<th>Result</th></tr>");
		String jiraServer = System.getProperty("jiraServer");

		HashMap<String, Integer> countHash = new HashMap<String, Integer>();

		ZephyrUtils.initZephyr(testCycleId);
		List<HashMap<String, String>> results = ZephyrUtils.getExecutionStatusFromCycle();

		Collections.sort(results, new Comparator<HashMap<String, String>>()
		{
			@Override
			public int compare(HashMap<String, String> o1, HashMap<String, String> o2)
			{
				int num1 = Integer.parseInt(o1.get("key").replaceAll("[^0-9]", ""));
				int num2 = Integer.parseInt(o2.get("key").replaceAll("[^0-9]", ""));
				return num1 - num2;
			}
		});

		for (HashMap<String, String> result : results)
		{
			output1.append("<tr><td><a href='" + jiraServer + "browse/" + result.get("key") + "'>" + result.get("key")
					+ "</a></td>");
			output1.append("<td>" + result.get("name") + "</td>");
			if (includeDescription)
				output1.append("<td>" + result.get("description") + "</td>");
			output1.append("<td class=" + result.get("result") + ">" + result.get("result") + "</td></tr>");
			int count = 0;
			if (countHash.containsKey(result.get("result")))
			{
				count = countHash.get(result.get("result"));
			}
			countHash.put(result.get("result"), ++count);
		}

		output1.append("</table>");

		// consolidated result:

		StringBuilder output2 = new StringBuilder();
		output2.append("<b>Execution Summary:</b><br /><table border=1>");

		if (countHash.containsKey("PASS"))
		{
			output2.append("<tr class='PASS'><td>PASS</td><td>" + countHash.get("PASS") + "</td>");
		}
		else
		{
			output2.append("<tr class='PASS'><td>PASS</td><td>" + 0 + "</td>");
		}

		if (countHash.containsKey("FAIL"))
		{
			output2.append("<tr class='FAIL'><td>FAIL</td><td>" + countHash.get("FAIL") + "</td>");
		}
		else
		{
			output2.append("<tr class='FAIL'><td>FAIL</td><td>" + 0 + "</td>");
		}

		if (countHash.containsKey("UNEXECUTED"))
		{
			output2.append("<tr class='UNEXECUTED'><td>UNEXECUTED</td><td>" + countHash.get("UNEXECUTED") + "</td>");
		}
		else
		{
			output2.append("<tr class='UNEXECUTED'><td>UNEXECUTED</td><td>" + 0 + "</td>");
		}

		if (countHash.containsKey("BLOCKED"))
		{
			output2.append("<tr class='BLOCKED'><td>BLOCKED</td><td>" + countHash.get("BLOCKED") + "</td>");
		}
		else
		{
			output2.append("<tr class='BLOCKED'><td>BLOCKED</td><td>" + 0 + "</td>");
		}

		output2.append("</table><br />");

		try
		{
			FileWriter fw = new FileWriter(writeToFile);
			fw.write(output2.toString() + output1.toString());
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		String mode = System.getProperty("zephyr");
		if (mode.equals("reset"))
		{
			resetTestCycle();
		}
		else if (mode.equals("generate-report"))
		{
			generateHtmlReport();
		}
	}

}
