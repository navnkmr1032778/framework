package com.solutionstar.swaftee.utils.ImageComparison;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import com.solutionstar.swaftee.utils.CommonUtils;


@SuppressWarnings("serial")
public class ImageCompareHelper extends RecursiveAction
{
	public static final Double DEFAULT_THRESHOLD=(double) 1350000;
	public static SoftAssert sa=new SoftAssert();
	public static CommonUtils utils=new CommonUtils();
	public static Map<String,Map<String,List<String>>> resultMap=Collections.synchronizedMap(new TreeMap<String,Map<String,List<String>>>());
	List<String> compareFiles;
	int threshold=1;
	public ImageCompareHelper()
	{

	}
	public ImageCompareHelper(List<String> compareFiles)
	{
		this.compareFiles=compareFiles;
	}

	/**
	 * 
	 * @param expexcted image url
	 * @param actual image url
	 * @param difference image url to be stored
	 * @return returns number of pixel difference between expected and actual images
	 * @throws UnsupportedEncodingException 
	 */
	public String compareImages (String exp,String cur,String diff) throws UnsupportedEncodingException
	{
		
		String myPath=utils.getSwafteeAbsolutePath()+WebDriverConstants.IMAGE_MAGICK_FOLDER_PATH;
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
			e.printStackTrace();
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

		try 
		{
			compare.run(cmpOp);
			return "";
		} catch (Exception e) 
		{
			e.printStackTrace();
		}
		return diff;
	}

	public void compareImagesAndStoreResult(String imagePath) throws UnsupportedEncodingException
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
		Double res;
		try
		{
			res=Double.parseDouble(result);
			sa.assertEquals(res.compareTo(DEFAULT_THRESHOLD),-1,compareFile+" differs from base file "+baseFile+" by "+result+" pixels");
			result=res.toString();
		}
		catch(Exception ex)
		{
			sa.assertFalse(true,"error in generating the comparison file");
		}
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

	public static void compareImageList(List<String> imageList)
	{
		ImageCompareHelper ce = new ImageCompareHelper(imageList);

		ForkJoinPool pool = new ForkJoinPool();
		pool.invoke(ce);
	}

	@Override
	protected void compute() {
		if (compareFiles.size() == threshold) {
			try {
				compareImagesAndStoreResult(compareFiles.get(0));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		int split = compareFiles.size() / 2;

		invokeAll(new ImageCompareHelper(compareFiles.subList(0, split)),
				new ImageCompareHelper(compareFiles.subList(split, compareFiles.size())));

	}
}