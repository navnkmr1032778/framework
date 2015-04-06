package com.solutionstar.swaftee.utils.dataarchive;

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
    
    /**
     * Clear/remove all data collected.
     * 
     * @throws Exception 
     */
    public void clearData() throws Exception;
    
}

