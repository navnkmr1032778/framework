package com.solutionstar.swaftee.utils.dataarchive;
import org.apache.log4j.Logger;

/**
 * Data archive interface for archiving data in CSV (comma separated value) 
 * output. 
 * 
 * This class always writes to a pre-existing file.
 * 
 * @author Allen Godfrey
 */
public class CSVAppendDataArchive extends CSVDataArchive {
    
    /**
     *  logging object
     */
    private static Logger log = Logger.getLogger(CSVAppendDataArchive.class);
    
    /**
     * Default constructor.
     */
    public CSVAppendDataArchive() { }
    

    /**
     * Save the data to a file. Append to a pre-existing file.
     * 
     * @param filename
     * 
     * @throws Exception 
     */
    @Override
    public void saveData(String filename) throws Exception 
    {
        
        try 
        { 
        	saveDataAppendToFile(filename, DELIMITER); 
        }
        catch(Exception e) 
        { 
        	throw e; 
        }
        
    }
    
}

