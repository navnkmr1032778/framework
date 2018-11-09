package com.solutionstar.swaftee.utils.dataarchive;

import java.util.HashMap;
import java.util.List;

/**
 * Data archive interface. All data archiving implementation classes 
 * need to implement this interface.
 * 
 * @author Allen Godfrey
 */
public interface DataArchive {
    
    /**
     * Add data to be archived.
     * 
     * @param data
     * 
     * @throws Exception 
     */
    public void addData(String[] data) throws Exception;

    /**
     * Save the data to a file.
     * 
     * @param filename
     * 
     * @throws Exception 
     */
    public void saveData(String filename) throws Exception;
    
    public void saveData(String filename, boolean forceNumbersAsString) throws Exception;
    
    /**
     * Clear/remove all data collected.
     * 
     * @throws Exception 
     */
    public void clearData() throws Exception;
    
    public List<HashMap<String, String>> retrieveData(String filename,boolean... val) throws Exception;
    
    public void writeDataToFile(String filename, List<HashMap<String, String>> data) throws Exception;
    
    public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header) throws Exception;
    
	public void writeDataToFile(String filename, List<HashMap<String, String>> data, String[] header, boolean forceNumbersAsString) throws Exception;

}

