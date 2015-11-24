package com.solutionstar.swaftee.jira;

import java.io.FileWriter;
import java.io.IOException;
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
		String output = "<table border=1><tr><th>Jira Key</th><th>Name</th><th>Description</th><th>Result</th></tr>";
		
		ZephyrUtils.initZephyr(testCycleId);
		List<HashMap<String, String>> results = ZephyrUtils.getExecutionStatusFromCycle();
		
		for(HashMap<String, String> result : results)
		{
			output += "<tr><td>" + result.get("key") + "</td>";
			output += "<td>" + result.get("name") + "</td>";
			output += "<td>" + result.get("description") + "</td>";
			output += "<td>" + result.get("result") + "</td></tr>";
		}
		
		output += "</table>";
		
		try
		{
			FileWriter fw = new FileWriter(writeToFile);
			System.out.println("[User log] Output: " + output);
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
		System.out.println("[User log] Zephyr: " + mode);
		String writeToFile = System.getProperty("writeTo");
		System.out.println("[User log] Write To: " + writeToFile);
		if(mode.equals("reset"))
		{
			resetTestCycle();
		}
		else if(mode.equals("generate-report"))
		{
			generateHtmlReport();
		}
	}

}
