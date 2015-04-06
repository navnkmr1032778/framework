package com.solutionstar.swaftee.utils.dataarchive;

import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Sheet;

import com.solutionstar.swaftee.utils.CommonUtils;
        
/**
 * Data archive base class for archiving to XLS or XLSX (Excel) files.
 * 
 * @author Allen Godfrey
 */
public class XLSDataArchiveBase extends DataArchiveBase {
    
    /**
     *  logging object
     */
    private static Logger log = Logger.getLogger(XLSDataArchiveBase.class);
    
    /**
     * Save the data to a file.
     * 
     * @param filename
     * 
     * @throws Exception 
     */
    public void saveData(Workbook workbook, String filename) throws Exception {
        
        try {
            
        	CommonUtils utils = new CommonUtils();
            CreationHelper createHelper = workbook.getCreationHelper();  
            Sheet sheet = workbook.createSheet("Sheet1");

            for(int i = 0; i < list.size(); i++) 
            {
                Row row = sheet.createRow((short)i);   
                String[] data = list.get(i); 
    
                for (int j = 0; j < data.length; j++) 
                {
                    log.debug("For row: " + i + ", creating column: " + j + " with data: " + data[j]);
                    if(utils.isNumeric(data[j]))
                    {
                    	row.createCell(j, 0).setCellValue(createHelper.createRichTextString(data[j]));
                    }
                    else 
                    {
                    	row.createCell(j).setCellValue(createHelper.createRichTextString(data[j]));
                    }    
                }
            }
            
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
        
        }
        catch(Exception e) { log.error(e); throw e; }
        
    }
    
}


