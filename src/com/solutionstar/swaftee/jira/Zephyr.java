package com.solutionstar.swaftee.jira;

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
		
	}
	
	public static void main(String[] args)
	{
		
		String mode = System.getProperty("zephyr");
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
