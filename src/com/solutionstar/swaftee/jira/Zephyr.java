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
		String output = "<style>.PASS{color:green}.FAIL{color:red}.UNEXECUTED{color:orange}.BLOCKED{color:grey}</style><table border=1><tr><th>Jira Key</th><th>Name</th>";
		if (includeDescription)
			output += "<th>Description</th>";
		output += "<th>Result</th></tr>";
		String jiraServer = System.getProperty("jiraServer");

		HashMap<String, Integer> countHash = new HashMap<String, Integer>();

		ZephyrUtils.initZephyr(testCycleId);
		List<HashMap<String, String>> results = ZephyrUtils.getExecutionStatusFromCycle();

		Collections.sort(results, new Comparator<HashMap<String,String>>()
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
			output += "<tr><td><a href='" + jiraServer + "browse/" + result.get("key") + "'>" + result.get("key")
					+ "</a></td>";
			output += "<td>" + result.get("name") + "</td>";
			if (includeDescription)
				output += "<td>" + result.get("description") + "</td>";
			output += "<td class=" + result.get("result") + ">" + result.get("result") + "</td></tr>";
			int count = 0;
			if (countHash.containsKey(result.get("result")))
			{
				count = countHash.get(result.get("result"));
			}
			countHash.put(result.get("result"), ++count);
		}

		output += "</table>";
		
		// consolidated result:
		
		output += "<br /><table border=1>";
		
		if(countHash.containsKey("PASS"))
		{
			output += "<tr class='PASS'><td>PASS</td><td>" + countHash.get("PASS") + "</td>";
		}
		else
		{
			output += "<tr class='PASS'><td>PASS</td><td>" + 0 + "</td>";
		}
		
		if(countHash.containsKey("FAIL"))
		{
			output += "<tr class='FAIL'><td>FAIL</td><td>" + countHash.get("FAIL") + "</td>";
		}
		else
		{
			output += "<tr class='FAIL'><td>FAIL</td><td>" + 0 + "</td>";
		}
		
		if(countHash.containsKey("UNEXECUTED"))
		{
			output += "<tr class='UNEXECUTED'><td>UNEXECUTED</td><td>" + countHash.get("UNEXECUTED") + "</td>";
		}
		else
		{
			output += "<tr class='UNEXECUTED'><td>UNEXECUTED</td><td>" + 0 + "</td>";
		}
		
		if(countHash.containsKey("BLOCKED"))
		{
			output += "<tr class='BLOCKED'><td>BLOCKED</td><td>" + countHash.get("BLOCKED") + "</td>";
		}
		else
		{
			output += "<tr class='BLOCKED'><td>BLOCKED</td><td>" + 0 + "</td>";
		}

		output += "</table>";
		
		try
		{
			FileWriter fw = new FileWriter(writeToFile);
			fw.write(output);
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
