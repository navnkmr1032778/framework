package com.solutionstar.swaftee.utils.ImageComparison;

import java.util.List;
import java.util.Map;

public interface ImageComparison
{

	/***
	 * gets all files and folders from compare->(given class name) folder
	 * stores the file and folder names in the structure:compareFilesMap
	 * Map  String				Map	String			List<String>
	 *     className_Instance ---> 	methodName--->	Screenshots File Name
	 *   
	 */
	
	public Map<String,Map<String,List<String>>> getAllCompareFilesByMethodNameAsMap();
	
	/***
	 * gets all files and folders from compare->(given class name/Device_browser) folder
	 * stores the file and folder names in the structure:baseFilesMap
	 * Map  	String			List<String>
	 *    	methodName--->	Screenshots File Name
	 * returns the map<method_name_folder,<base_image,List<compare_images>>
	 */
	
	public Map<String,List<String>> getAllBaseFilesByMethodNameAsMap();
	
	/**
	 * concatenates all the params with , separation and add it to ImageList
	 * @param compareFile
	 * @param baseFile
	 * @param storeTo
	 * @param baseMethodFolderName
	 */
	
	public void addToCompareImagesListByFile(String compareFile,String baseFile,String storeTo,String baseMethodFolderName);
	
	/**
	 * gets all the files from compare and base files lists using previous function
	 * @param compareFiles
	 * @param baseFiles
	 * @param filePath
	 */
	
	public void addToCompareImagesListByFolderName(List<String> compareFiles,List<String> baseFiles,Map<String,String> filePath);
	
	/**
	 * gets all files from base and compare folders as imageList<String>
	 */
	
	public void getImageCompareList();
	
	/**
	 * compares the files given in imageList
	 */
	
	public void compareAllImages();
	
	/**
	 * gets string array of sub files from given path
	 * @param path
	 * @return
	 */
	
	public String[] getListOfSubFiles(String path);
	
	/**
	 * get string array sub folders from given path
	 * @param path
	 * @return
	 */
	
	public String[] getListOfSubDirectories(String path);
	
	/**
	 * gets sub folders from given path with given folderName
	 * @param path
	 * @param folderName
	 * @return
	 */
	
	public String[] getListOfSubDirectories(String path,String folderName);
	
	/**
	 * generates html report with data provided using resultMap and stores in "testComparison.html"
	 * @param resultMap
	 */
	
	public void generateHTMLReport(Map<String,Map<String,List<String>>> resultMap);
}
