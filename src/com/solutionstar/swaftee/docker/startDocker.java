package com.solutionstar.swaftee.docker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;



public class startDocker {

	

	public void startFile() throws IOException, InterruptedException
	{
		
	boolean flag=false;
		Runtime runtime= Runtime.getRuntime();
		runtime.exec("cmd /c start dockerUp.bat",null,new File(new File(System.getProperty("user.dir")).getParent()+"/swaftee/resources/docker"));
		
	String f =new File(System.getProperty("user.dir")).getParent()+"/swaftee/resources/docker/output.txt";
	
	Calendar cal=Calendar.getInstance();//2:44 15th second
	cal.add(Calendar.SECOND, 45);//2:44   45seconds
	long stopnow=cal.getTimeInMillis();
	Thread.sleep(3000);
	
	while(System.currentTimeMillis()<stopnow)
	{
		if(flag)
		{
			break;
		}
		
		BufferedReader reader=new BufferedReader(new FileReader(f));
		String currentLine=reader.readLine();
	while(currentLine!=null && !flag)
		
	{
		
		if(currentLine.contains("registered to the hub and ready to use"))
		{
			System.out.println("found my text");
			flag=true;//14th seconds
			break;
		}
		
		 currentLine=reader.readLine();
	}
	reader.close();
	
	}
	
Assert.assertTrue(flag);
try {
	runtime.exec("cmd /c start scale.bat", null , new File(new File(System.getProperty("user.dir")).getParent()+"/swaftee/resources/docker"));
}
catch(Exception e) {}
Thread.sleep(15000);
	
	
	
			
	}
	
	
}
