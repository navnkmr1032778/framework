package com.solutionstar.swaftee.utils.dataarchive;
import org.apache.log4j.Logger;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Data archive interface for archiving to XLS (Excel) files. Do not use this 
 * class to save to XLSX format.
 * 
 * @author Allen Godfrey
 */
public class XLSDataArchive extends XLSDataArchiveBase implements DataArchive {
    
    /**
     *  logging object
     */
    private static Logger log = Logger.getLogger(XLSDataArchive.class);
    
    /**
     * Default constructor.
     */
    public XLSDataArchive() { }
    

    /**
     * Save the data to a file.
     * 
     * @param filename
     * 
     * @throws Exception 
     */
    public void saveData(String filename) throws Exception {
        
        try {
            
            log.debug("Attempting save data to filename: " + filename);
            
            saveData(new HSSFWorkbook(), filename);
            
        
        }
        catch(Exception e) { throw e; }
        
    }
    
}

