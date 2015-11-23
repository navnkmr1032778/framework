package com.solutionstar.swaftee.jira;

import java.io.FileWriter;
import java.io.IOException;

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
		String output = "<table><tr><th>Jira Key</th><th>Name</th><th>Description</th><th>Result</th></tr>" +
		"<tr><td>CAS-123</td><td>Test 1</td><td>Description of the test</td><td>Unexecuted</td></tr>";
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
