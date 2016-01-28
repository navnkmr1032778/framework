package com.solutionstar.swaftee.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

import org.testng.asserts.SoftAssert;
import org.im4java.core.CompareCmd;
import org.im4java.core.CompositeCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.process.ProcessStarter;
import org.im4java.process.StandardStream;

import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.webdriverhelpers.BaseDriverHelper;

public class ImageComparisonUtils  {

	public Map<String,Map<String,List<String>>> compareFilesMap;
	public Map<String,List<String>> baseFilesMap;
	// method names -> methods
	public List<String> imageList;
	String folderName;
	BaseDriverHelper helper;
	CommonUtils utils;
	String baseDirLocation;
	String currentDirLocation;
	String path;
	
	public ImageComparisonUtils(String className)
	{
		this.folderName=className;
		this.compareFilesMap=new TreeMap<String,Map<String,List<String>>>();
		this.baseFilesMap=new TreeMap<String,List<String>>();
		this.imageList=new ArrayList<String>();
		helper=new BaseDriverHelper();
		utils=new CommonUtils();
		this.baseDirLocation=helper.getBaseDirLocation()+"/"+className;
		this.currentDirLocation=helper.getCurrentDirLocation();
		path=utils.getCurrentWorkingDirectory();
		getAllCompareFilesByMethodNameAsMap();
		getAllBaseFilesByMethodNameAsMap();
	}
	
	public void getAllCompareFilesByMethodNameAsMap()
	{
		String[] compareFoldersWithClassNames=getListOfSubDirectories(currentDirLocation,folderName);
		Map<String,List<String>> methodMap = null;
		
		for(int instance=0;instance<compareFoldersWithClassNames.length;instance++)
		{
			methodMap=new TreeMap<String,List<String>>();
			String comparePathWithClassName=currentDirLocation+"/"+compareFoldersWithClassNames[instance];
			String[] compareFoldersWithMethodNames=getListOfSubDirectories(comparePathWithClassName);
			for(int methods=0;methods<compareFoldersWithMethodNames.length;methods++)
			{
				String comparePathWithMethodName=comparePathWithClassName+"/"+compareFoldersWithMethodNames[methods];
				String[] screenshotFiles=getListOfSubFiles(comparePathWithMethodName);

				for(int image=0;image<screenshotFiles.length;image++)
				{
					if(methodMap.containsKey(compareFoldersWithMethodNames[methods]))
					{
						methodMap.get(compareFoldersWithMethodNames[methods]).add(screenshotFiles[image]);
					}
					else
					{
						List<String> fileList=new ArrayList<String>();
						fileList.add(screenshotFiles[image]);
						methodMap.put(compareFoldersWithMethodNames[methods], fileList);
					}
				}
			}	
			compareFilesMap.put(compareFoldersWithClassNames[instance],methodMap);
		}	
	}
	
	public void getAllBaseFilesByMethodNameAsMap()
	{
		String basePathWithClassName=baseDirLocation;
		String[] baseFoldersWithMethodNames=getListOfSubDirectories(basePathWithClassName);
		for(int methods=0;methods<baseFoldersWithMethodNames.length;methods++)
		{
			String comparePathWithMethodName=basePathWithClassName+"/"+baseFoldersWithMethodNames[methods];
			String[] screenshotFiles=getListOfSubFiles(comparePathWithMethodName);

			for(int image=0;image<screenshotFiles.length;image++)
			{
				if(baseFilesMap.containsKey(baseFoldersWithMethodNames[methods]))
				{
					baseFilesMap.get(baseFoldersWithMethodNames[methods]).add(screenshotFiles[image]);
				}
				else
				{
					List<String> fileList=new ArrayList<String>();
					fileList.add(screenshotFiles[image]);
					baseFilesMap.put(baseFoldersWithMethodNames[methods], fileList);
				}
			}
		}		
	}
	
	
	public void getCompareImagesListByFile(String compareFile,String baseFile,String storeTo,String baseMethodFolderName)
	{
		//String result;
		if(baseFile.length()==0)
		{
			compareFile=path+"/"+compareFile;
			//result=compareFile+",no comparison,"+"no base image found";
			imageList.add(","+compareFile+",,"+baseMethodFolderName);
		}
		else if(compareFile.length()==0)
		{
			baseFile=path+"/"+baseFile;
			//result="null,no comparison,no compare image found";
			imageList.add(baseFile+",,,"+baseMethodFolderName);
		}
		else
		{
			baseFile=path+"/"+baseFile;
			compareFile=path+"/"+compareFile;
			storeTo=path+"/"+storeTo;
			imageList.add(baseFile+","+compareFile+","+storeTo+","+baseMethodFolderName);
			//result=compareImages(baseFile, compareFile, storeTo);
			/*sa.assertEquals(result, "0",compareFile+" differs from base file "+baseFile+" by "+result+" pixels");
			dissolveImages(baseFile,compareFile,storeTo);
			File f=new File(storeTo);

			if(f.exists())
			{
				result=compareFile+","+storeTo+","+result;
			}
			else
			{
				result=compareFile+","+storeTo+","+"error in difference generation";
			}*/
		}
		//addToResultMap(baseFile, result,baseMethodFolderName);
	}
	
	public void getCompareImagesListByFolderName(List<String> compareFiles,List<String> baseFiles,Map<String,String> filePath)
	{
		String storePath=WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_COMPARE_RESULT+"/"+filePath.get("compareClassFolder")+"/"+filePath.get("baseMethodFolder");
		File storeFile=new File(storePath);
		if(!storeFile.exists())
		{
			storeFile.mkdirs();
		}
		String comparePath=currentDirLocation+"/"+filePath.get("compareClassFolder")+"/"+filePath.get("baseMethodFolder");
		String basePath=baseDirLocation+"/"+filePath.get("baseMethodFolder");
		
		int compareIndex=0;
		while(compareFiles.size()>0 && baseFiles.size()>0)
		{
			String compImage=compareFiles.get(compareIndex);
			if(baseFiles.contains(compImage))
			{
				getCompareImagesListByFile(comparePath+"/"+compImage, basePath+"/"+compImage,storePath+"/"+compImage,basePath);
				compareFiles.remove(0);
				baseFiles.remove(compImage);
			}
			else 
			{
				String compActualImage=compImage;
				int delStartIndex= compImage.lastIndexOf('_');
				int delEndIndex =  compImage.lastIndexOf('.');
				compImage=(compImage.substring(0, delStartIndex)+compImage.substring(delEndIndex, compImage.length()));
				if(baseFiles.contains(compImage))
				{
					getCompareImagesListByFile(comparePath+"/"+compActualImage, basePath+"/"+compImage,storePath+"/"+compActualImage,basePath);
					compareFiles.remove(0);
					baseFiles.remove(compImage);
				}
				else
				{
					getCompareImagesListByFile(comparePath+"/"+compActualImage,"","",basePath);
					compareFiles.remove(0);
				}
			}
		}
		while(baseFiles.size()>0)
		{
			getCompareImagesListByFile("",basePath+"/"+baseFiles.get(0),"",basePath);
			baseFiles.remove(0);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void getImageCompareList()
	{
		Iterator baseEntries = baseFilesMap.entrySet().iterator();
		Map<String,String> filePath=new TreeMap<String,String>();
		while (baseEntries.hasNext())
		{
			Entry baseEntry = (Entry) baseEntries.next();
			String baseKey = (String)baseEntry.getKey();
			@SuppressWarnings("unchecked")
			List<String> baseVal=(List<String>)baseEntry.getValue();
			filePath.put("baseMethodFolder", baseKey);

			Iterator compareEntries = compareFilesMap.entrySet().iterator();

			while (compareEntries.hasNext())
			{
				Entry compareEntry = (Entry) compareEntries.next();
				List<String> baseImages=new ArrayList<String>();
				baseImages.addAll(baseVal);
				String compareKey=(String) compareEntry.getKey();//imageComparisonTest_355769689846711
				@SuppressWarnings("unchecked")
				Map<String,List<String>> compareVal=(Map<String,List<String>>)compareEntry.getValue();
				filePath.put("compareClassFolder", compareKey);
				getCompareImagesListByFolderName(compareVal.get(baseKey),baseImages,filePath);
			}
		}
	}
	
	public void compareAllImages()
	{
		getImageCompareList();
		ImageDifference.compareImageList(imageList);
		generateHTMLReport(ImageDifference.resultMap);
	}
	
	/**
	 * get all files in give path (folder)
	 */
	
	public String[] getListOfSubFiles(String path)
	{
		File file = new File(path);
	String[] files = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isFile();
		  }
		});
	return files;
	}
	
	/**
	 * 
	 * @param path
	 * @return get string array of all folders present within path (folder)
	 */
	
	public String[] getListOfSubDirectories(String path)
	{
		File file = new File(path);
	String[] files = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
	return files;
	}
	
	public String[] getListOfSubDirectories(String path,String folderName)
	{
		File file = new File(path);
	String[] files = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return (new File(current, name).isDirectory() && 
		    		name.split("_").length==2 &&
		    		name.split("_")[0].equals(folderName));
		  }
		});
	return files;
	}
	
	public void generateHTMLReport(Map<String,Map<String,List<String>>> resultMap)
	{
		String pattern = Pattern.quote(System.getProperty("file.separator"));
		String[] swafteePaths=path.split(pattern);
		String swafteePath=swafteePaths[0]+"/"+swafteePaths[1]+"/"+swafteePaths[2];
		String compareFolder=this.currentDirLocation;
		String resultFolder=WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_COMPARE_RESULT;
		String writeToFile = "testComparisonOutput.html";
		StringBuilder output1 = new StringBuilder();
		String start="<!DOCTYPE html>"+
				"<html lang=\"en\">"+
				"<head>"+
				"  <title>Bootstrap Example</title>"+
				  "<meta charset=\"utf-8\">"+
				  "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"+
				  "<link rel=\"stylesheet\" href=\"http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css\">"+
				  "<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js\"></script>"+
				  "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\""+swafteePath+"/swaftee/resources/ImgCmpCss.css\" />"+
				  "<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"http://cdnjs.cloudflare.com/ajax/libs/fancybox/1.3.4/jquery.fancybox-1.3.4.css\" />"
				+"</head>"+
				"<body>"
				+ "<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-1.11.0.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"http://code.jquery.com/jquery-migrate-1.2.1.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\"http://cdnjs.cloudflare.com/ajax/libs/fancybox/1.3.4/jquery.fancybox-1.3.4.pack.min.js\"></script>"
				+ "<script type=\"text/javascript\" src=\""+swafteePath+"/swaftee/resources/ImgCmpScript.js\"></script>";
		output1.append(start);
		output1.append("<b>Image Comparison Test Status: "+folderName+"</b>");
		/*String style="<style> img:hover { position:relative; top:-25px; left:-35px; width:1000px; height:auto; display:block; z-index:999; </style>";
		output1.append(style);*/
		output1.append( "<div class=\"table-responsive\"><table class=\"table\"><tr><th>Base Image</th>");

				int i=1;
		while (i<=compareFilesMap.size()) 
		{
			output1.append("<th>Compare_Folder_"+i+"<div><input type='radio' name='all' value='all"+i+"' data-toggle='unchecked'></div></th>");
			++i;
		}
		output1.append("<th><button>UPDATE BASE IMAGE SCRIPT</button><th></tr><tbody>");
		
		Iterator entries = resultMap.entrySet().iterator();
		while (entries.hasNext()) 
		{
			@SuppressWarnings("rawtypes")
			Entry thisEntry = (Entry) entries.next();
			String key = (String)thisEntry.getKey();
			Map<String,List<String>> result1 =(Map<String,List<String>>) thisEntry.getValue();
			@SuppressWarnings("unchecked")
			Iterator entries2 = result1.entrySet().iterator();
			output1.append("<tr><td colspan=\"6\">"+key+"</tr>");
			while(entries2.hasNext())
			{
				Entry Entry = (Entry) entries2.next();
				String key2 = (String)Entry.getKey();
				List<String> result2 =(List<String>) Entry.getValue();
				output1.append("<tr><td><img src=\""+key2+"\"  class=\"fancybox\" height=\"200\" width=\"200\" title=\""+key2+"\"/></td>");
				
				for(String value:result2)
				{ 
					String res[]=value.split(",");
					String actual=res[1].replace(resultFolder, compareFolder);
					String borderStyle="style=\"border:5px solid green\"";
					if(!res[2].equals("0"))
					{
						borderStyle="style=\"border:5px solid red\"";
					}
					output1.append("<td><img src=\""+res[1]+"\"  class=\"fancybox\" height=\"200\" width=\"200\" "+borderStyle+" title=\""+actual+"\"/>"
							+ "<div><input type='radio' name='"+ key2 + "' value='"+actual+"' data-toggle='unchecked'>"+res[2]+"</div></td>");
				}
				output1.append("</tr>");
			}
		}
		output1.append("</tbody></table></div><br />");
		output1.append("</body></html>");
		
		try
		{
			FileWriter fw = new FileWriter(writeToFile);
			fw.write(output1.toString());
			fw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		ImageDifference.sa.assertAll();
	}
}
