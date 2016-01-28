package com.solutionstar.swaftee.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.im4java.core.CompareCmd;
import org.im4java.core.CompositeCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;
import org.testng.asserts.SoftAssert;

import com.solutionstar.swaftee.constants.WebDriverConstants;


@SuppressWarnings("serial")
public class ImageDifference extends RecursiveAction
{
	public static SoftAssert sa=new SoftAssert();
	public static Map<String,Map<String,List<String>>> resultMap=Collections.synchronizedMap(new TreeMap<String,Map<String,List<String>>>());
	List<String> compareFiles;
	int threshold=1;
	public ImageDifference()
	{

	}
	public ImageDifference(List<String> compareFiles)
	{
		this.compareFiles=compareFiles;
	}

	/**
	 * 
	 * @param expexcted image url
	 * @param actual image url
	 * @param difference image url to be stored
	 * @return returns number of pixel difference between expected and actual images
	 */
	public String compareImages (String exp,String cur,String diff)
	{
		String myPath="D:\\ImageMagick-6.9.3-Q16";
		ProcessStarter.setGlobalSearchPath(myPath);
		CompareCmd compare = new CompareCmd();
		compare.setSearchPath(myPath);

		compare.setErrorConsumer(StandardStream.STDERR);
		IMOperation cmpOp = new IMOperation();

		cmpOp.fuzz((double) 5);
		cmpOp.metric("AE");
		//cmpOp.highlightColor("SeaGreen");
		cmpOp.compose("difference");
		// Add the expected image
		cmpOp.addImage(exp);

		// Add the current image
		cmpOp.addImage(cur);

		// This stores the difference
		cmpOp.addImage(diff);

		try 
		{

			return compare.run(cmpOp);
		}
		catch (Exception e)
		{
			if(!e.getMessage().equals("java.lang.NullPointerException"))
			{
				e.printStackTrace();
			}
		}
		return null; 
	}

	public String dissolveImages (String exp, String cur, String diff) 
	{
		CompositeCmd compare = new CompositeCmd();

		// For metric-output
		compare.setErrorConsumer(StandardStream.STDERR);

		IMOperation cmpOp = new IMOperation();
		cmpOp.dissolve(50);
		cmpOp.gravity("South");
		cmpOp.alpha("Set");
		// Add the expected image
		cmpOp.addImage(exp);

		// Add the current image
		cmpOp.addImage(cur);

		// This stores the difference
		cmpOp.addImage(diff);


		try {
			compare.run(cmpOp);
			return "";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
		return diff;
	}

	public void compareImagesAndStoreResult(String imagePath)
	{
		String imagePaths[]=imagePath.split(",");
		String baseFile=imagePaths[0];
		String compareFile=imagePaths[1];
		String storeTo=imagePaths[2];
		String baseMethodFolderName=imagePaths[3];
		String result="-1";
		if(!(storeTo.length()==0))
		{
			result=compareImages(baseFile,compareFile,storeTo);
			result=result.replaceAll("[^0-9.e+]", "");
			dissolveImages(baseFile,compareFile,storeTo);
		}
		sa.assertEquals(result, "0",compareFile+" differs from base file "+baseFile+" by "+result+" pixels");
		File f=new File(storeTo);

		if(f.exists())
		{
			result=compareFile+","+storeTo+","+result;
		}
		else
		{
			result=compareFile+","+storeTo+","+"error in difference generation";
		}
		addToResultMap(baseFile, result,baseMethodFolderName);
	}

	/***
	 * 
	 * @param key  -> base image file path
	 * @param val  -> compare image path, stored result image path, result of comparison
	 */
	public void addToResultMap(String key,String val,String baseMethodFolderName)
	{
		synchronized(this)
		{
			if(resultMap.containsKey(baseMethodFolderName))
			{
				//if(result!=null && result.equalsIgnoreCase("null"))
				if(resultMap.get(baseMethodFolderName).containsKey(key))
				{
					resultMap.get(baseMethodFolderName).get(key).add(val);
				}
				else
				{
					List<String> res=new ArrayList<String>();
					res.add(val);
					resultMap.get(baseMethodFolderName).put(key, res);
				}
			}
			else
			{
				Map<String,List<String>> resMap=new TreeMap<String,List<String>>();
				List<String> res=new ArrayList<String>();
				res.add(val);
				resMap.put(key, res);
				resultMap.put(baseMethodFolderName,resMap);
			}
		}
	}

	public static long compareImageList(List<String> imageList) {
		/*File file = new File(srcFile);

	        System.out.println("File size is " + file.length());
	        System.out.println("Threshold is " + sThreshold);
		 */
		int processors = Runtime.getRuntime().availableProcessors();
		System.out.println(Integer.toString(processors) + " processor"
				+ (processors != 1 ? "s are " : " is ") + "available");

		ImageDifference ce = new ImageDifference(imageList);

		ForkJoinPool pool = new ForkJoinPool();

		long startTime = System.currentTimeMillis();
		pool.invoke(ce);
		long endTime = System.currentTimeMillis();

		System.out.println("File copied in " + (endTime - startTime)
				+ " milliseconds.");
		return endTime - startTime;
	}

	@Override
	protected void compute() {
		if (compareFiles.size() == threshold) {
			compareImagesAndStoreResult(compareFiles.get(0));
			return;
		}

		int split = compareFiles.size() / 2;

		invokeAll(new ImageDifference(compareFiles.subList(0, split)),
				new ImageDifference(compareFiles.subList(split, compareFiles.size())));

	}
}