package com.solutionstar.swaftee.utils.ImageComparison;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import com.solutionstar.swaftee.constants.WebDriverConstants;
import com.solutionstar.swaftee.utils.CommonUtils;
import com.solutionstar.swaftee.utils.FileDownloader;

public class ImageComparisonUtils implements ImageComparison
{
	public Map<String,Map<String,Map<String,List<String>>>> compareFilesMap;
	public Map<String,Map<String,List<String>>> baseFilesMap;
	// method names -> methods
	public List<String> imageList;
	String folderName,className;
	CommonUtils utils;
	String baseDirLocation;
	String currentDirLocation;
	String path;
	
	public ImageComparisonUtils(String className,String baseDirLocation,String currentDirLocation)
	{
		this.className=this.folderName=className;
		this.compareFilesMap=new TreeMap<String,Map<String,Map<String,List<String>>>>();
		this.baseFilesMap=new TreeMap<String,Map<String,List<String>>>();
		this.imageList=new ArrayList<String>();
		utils=new CommonUtils();
		this.baseDirLocation=baseDirLocation+"/"+className;
		this.currentDirLocation=currentDirLocation;
		path=utils.getCurrentWorkingDirectory();
	}
	
	public void getCompareFilesByDeviceName()
	{
		String[] compareClassFolders=getListOfSubDirectories(currentDirLocation,folderName);
		
		for(int instance=0;instance<compareClassFolders.length;instance++)
		{
			String classDir=currentDirLocation;
			folderName=compareClassFolders[instance];
			currentDirLocation=currentDirLocation+"/"+folderName;
			Map<String,Map<String,List<String>>> deviceFoldersMapList=getAllCompareFilesByMethodNameAsMap();
			compareFilesMap.put(compareClassFolders[instance],deviceFoldersMapList);
			currentDirLocation=classDir;
		}
	}
	
	public Map<String,Map<String,List<String>>> getAllCompareFilesByMethodNameAsMap()
	{
		Map<String,Map<String,List<String>>> methodFilesMap=new TreeMap<String,Map<String,List<String>>>();
		String[] compareFoldersWithClassNames=getListOfSubDirectories(currentDirLocation);
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
			methodFilesMap.put(compareFoldersWithClassNames[instance],methodMap);
		}
		return methodFilesMap;
	}
	
	public void getBaseFilesByDeviceName()
	{
		String[] DeviceFoldersInsideClassFolder=getListOfSubDirectories(baseDirLocation);
		Map<String,List<String>> deviceMap = null;
		
		for(int instance=0;instance<DeviceFoldersInsideClassFolder.length;instance++)
		{
			deviceMap=new TreeMap<String,List<String>>();
			String dirLocation=baseDirLocation;
			baseDirLocation=baseDirLocation+"/"+DeviceFoldersInsideClassFolder[instance];
			Map<String,List<String>> deviceFilesMap=getAllBaseFilesByMethodNameAsMap();
			baseFilesMap.put(DeviceFoldersInsideClassFolder[instance],deviceFilesMap);
			baseDirLocation=dirLocation;
		}
	}
	
	public Map<String,List<String>> getAllBaseFilesByMethodNameAsMap()
	{
		Map<String,List<String>> baseMethodFiles=new TreeMap<String,List<String>>();
		String basePathWithClassName=baseDirLocation;
		String[] baseFoldersWithMethodNames=getListOfSubDirectories(basePathWithClassName);
		for(int methods=0;methods<baseFoldersWithMethodNames.length;methods++)
		{
			String comparePathWithMethodName=basePathWithClassName+"/"+baseFoldersWithMethodNames[methods];
			String[] screenshotFiles=getListOfSubFiles(comparePathWithMethodName);

			for(int image=0;image<screenshotFiles.length;image++)
			{
				if(baseMethodFiles.containsKey(baseFoldersWithMethodNames[methods]))
				{
					baseMethodFiles.get(baseFoldersWithMethodNames[methods]).add(screenshotFiles[image]);
				}
				else
				{
					List<String> fileList=new ArrayList<String>();
					fileList.add(screenshotFiles[image]);
					baseMethodFiles.put(baseFoldersWithMethodNames[methods], fileList);
				}
			}
		}
		return baseMethodFiles;
	}
	
	
	public void addToCompareImagesListByFile(String compareFile,String baseFile,String storeTo,String baseMethodFolderName)
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
		}
	}
	
	public void addToCompareImagesListByFolderName(List<String> compareFiles,List<String> baseFiles,Map<String,String> filePath)
	{
		String storePath=WebDriverConstants.PATH_TO_BROWSER_SCREENSHOT_COMPARE_RESULT+"/"+filePath.get("compareClassFolder")+"/"+filePath.get("deviceFolder")+"/"+filePath.get("baseMethodFolder");
		File storeFile=new File(storePath);
		if(!storeFile.exists())
		{
			storeFile.mkdirs();
		}
		String comparePath=currentDirLocation+"/"+filePath.get("compareClassFolder")+"/"+filePath.get("deviceFolder")+"/"+filePath.get("baseMethodFolder");
		String basePath=baseDirLocation+"/"+filePath.get("deviceFolder")+"/"+filePath.get("baseMethodFolder");
		
		int compareIndex=0;
		while(compareFiles.size()>0 && baseFiles.size()>0)
		{
			String compImage=compareFiles.get(compareIndex);
			if(baseFiles.contains(compImage))
			{
				addToCompareImagesListByFile(comparePath+"/"+compImage, basePath+"/"+compImage,storePath+"/"+compImage,basePath);
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
					addToCompareImagesListByFile(comparePath+"/"+compActualImage, basePath+"/"+compImage,storePath+"/"+compActualImage,basePath);
					compareFiles.remove(0);
					baseFiles.remove(compImage);
				}
				else
				{
					addToCompareImagesListByFile(comparePath+"/"+compActualImage,"","",basePath);
					compareFiles.remove(0);
				}
			}
		}
		while(baseFiles.size()>0)
		{
			addToCompareImagesListByFile("",basePath+"/"+baseFiles.get(0),"",basePath);
			baseFiles.remove(0);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getImageCompareList()
	{
		Iterator compareClassIter = compareFilesMap.entrySet().iterator();
		while(compareClassIter.hasNext())
		{
			Entry compareClassEntry = (Entry) compareClassIter.next();
			Map<String,String> filePath=new TreeMap<String,String>();
			String compareClassKey=(String)compareClassEntry.getKey();
			Map<String,Map<String,List<String>>> compareClassVal=(Map<String,Map<String,List<String>>>)compareClassEntry.getValue();
			filePath.put("compareClassFolder",compareClassKey);
			Iterator baseDeviceIter = baseFilesMap.entrySet().iterator();
			while(baseDeviceIter.hasNext())
			{
				Entry baseDeviceEntry = (Entry) baseDeviceIter.next();
				String baseDeviceKey = (String)baseDeviceEntry.getKey();
				filePath.put("deviceFolder", baseDeviceKey);
				Iterator baseMethodIter = (Iterator)((Map<String,List<String>>)(baseDeviceEntry.getValue())).entrySet().iterator();
				while (baseMethodIter.hasNext())
				{
					Entry baseMethodEntry = (Entry) baseMethodIter.next();
					String baseMethodKey = (String)baseMethodEntry.getKey();
					List<String> baseMethodValue = (List<String>)baseMethodEntry.getValue();
					List<String> baseImages=new ArrayList<String>();
					baseImages.addAll(baseMethodValue);
					filePath.put("baseMethodFolder", baseMethodKey);
					addToCompareImagesListByFolderName(compareClassVal.get(baseDeviceKey).get(baseMethodKey), baseImages,filePath);
				}
			}
			//while()
		}
	}

	public void compareAllImages()
	{
		try {
			downloadImageMagick();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getImageCompareList();
		ImageCompareHelper.compareImageList(imageList);
		generateHTMLReport(ImageCompareHelper.resultMap);
	}
	
	/**
	 * download image magick if not found in location
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	
	@SuppressWarnings("static-access")
	public void downloadImageMagick() throws MalformedURLException, IOException
	{
		FileDownloader fileDownloader=new FileDownloader();
		String swafteePath=utils.getSwafteeAbsolutePath();
		String zipPath=swafteePath+WebDriverConstants.IMAGE_MAGICK_ZIP_PATH;
		String imageMagickPath=swafteePath+WebDriverConstants.IMAGE_MAGICK_FOLDER_PATH;
		if(!new File(imageMagickPath).exists())
		{
			fileDownloader.saveFileFromUrlWithJavaIO(zipPath,WebDriverConstants.IMAGE_MAGICK_URL);
			fileDownloader.unZipIt(zipPath,imageMagickPath);
		}
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
	
	/**
	 * gets sub folders from given path with given folderName
	 * @param path
	 * @param folderName
	 * @return
	 */
	
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
		output1.append("<b>Image Comparison Test Status: "+className+"</b>");
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
			@SuppressWarnings("unchecked")
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
					if(res[2].toLowerCase().contains("error in difference generation"))
					{
						borderStyle="style=\"border:5px solid red\"";
					}
					else
					{
						Double resVal=Double.parseDouble(res[2]);
						if(resVal.compareTo(ImageCompareHelper.DEFAULT_THRESHOLD)!=-1)
						{
							borderStyle="style=\"border:5px solid red\"";
						}
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

		ImageCompareHelper.sa.assertAll();
	}
}
