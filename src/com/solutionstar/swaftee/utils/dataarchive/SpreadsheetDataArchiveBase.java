package com.solutionstar.swaftee.utils.dataarchive;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.solutionstar.swaftee.utils.CommonUtils;

/**
 * Data archive base class for archiving to XLS or XLSX (Excel) files.
 * 
 * @author Allen Godfrey
 */
public class SpreadsheetDataArchiveBase extends DataArchiveBase {

	/**
	 * logging object
	 */
	private static Logger log = Logger.getLogger(SpreadsheetDataArchiveBase.class);

	/**
	 * Save the data to a file.
	 * 
	 * @param filename
	 * 
	 * @throws Exception
	 */
	public void saveData(Workbook workbook, String filename) throws Exception {
		saveData(workbook, filename, false);
	}

	public void saveData(Workbook workbook, String filename, boolean forceNumbersAsString) throws Exception {

		CommonUtils utils = new CommonUtils();
		CreationHelper createHelper = workbook.getCreationHelper();
		Sheet sheet = workbook.createSheet("Sheet1");

		for (int i = 0; i < list.size(); i++) {
			Row row = sheet.createRow((short) i);
			String[] data = list.get(i);

			for (int j = 0; j < data.length; j++) {
				log.debug("For row: " + i + ", creating column: " + j + " with data: " + data[j]);
				if (forceNumbersAsString) {
					row.createCell(j).setCellValue(createHelper.createRichTextString(data[j]));
					continue;
				}
				if (utils.isNumeric(data[j])) {
					row.createCell(j).setCellValue(createHelper.createRichTextString(data[j]));// deprecated arg 0
				} else {
					row.createCell(j).setCellValue(createHelper.createRichTextString(data[j]));
				}
			}
		}

		FileOutputStream fileOut = new FileOutputStream(filename);
		workbook.write(fileOut);
		fileOut.close();
		clearData();
	}

	@SuppressWarnings("incomplete-switch")
	private List<String> getRowData(Row row, int... headerSize) throws Exception {
		List<String> rowData = new ArrayList<String>();
		int size = 0;
		if (headerSize.length > 0) {
			size = headerSize[0];
		} else {
			size = row.getLastCellNum();
		}
		for (int i = 0; i < size; i++) {
			Cell cell = row.getCell(i);
			if (cell != null) {
				switch (cell.getCellType()) {
				case NUMERIC:
					rowData.add("" + cell.getNumericCellValue());
					break;
				case STRING:
					rowData.add(cell.getStringCellValue());
					break;
				case BLANK:
					rowData.add("");
					break;
				}
			} else {
				rowData.add("");
			}
		}
		return rowData;
	}

	public List<HashMap<String, String>> retrieveData(Workbook workbook, boolean... value) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);
		List<String> header = getRowData(headerRow);
		List<HashMap<String, String>> outputData = new ArrayList<HashMap<String, String>>();
		int totalRows = sheet.getLastRowNum();
		for (int i = 1; i <= totalRows; i++) {
			List<String> rowData;
			if (value.length > 0) {
				rowData = getRowData(sheet.getRow(i), header.size());
			} else {
				rowData = getRowData(sheet.getRow(i));
			}
			int j = 0;
			HashMap<String, String> map = new HashMap<String, String>();
			for (String col : header) {
				map.put(col, rowData.get(j));
				j++;
			}
			outputData.add(map);
		}
		workbook.close();
		return outputData;
	}

	@SuppressWarnings("incomplete-switch")
	public String getCellValue(Cell cell) {
		String cellValue = "";
		DataFormatter formatter = new DataFormatter();
		if (cell != null) {
			switch (cell.getCellType()) {
			case NUMERIC:
				cellValue = "" + formatter.formatCellValue(cell);
				break;
			case STRING:
				cellValue = cell.getStringCellValue();
				break;
			case BLANK:
				cellValue = "";
				break;
			}
		} else {
			cellValue = "";
		}
		return cellValue;

	}

	public List<HashMap<String, String>> retrieveDataInverse(Workbook workbook, String... sheetName) throws Exception {

		Sheet sheet;
		if (sheetName.length > 0) {
			sheet = workbook.getSheet(sheetName[0]);
		} else {
			sheet = workbook.getSheetAt(0);
		}
		List<HashMap<String, String>> outputData = new ArrayList<HashMap<String, String>>();
		int totalRows = sheet.getLastRowNum();
		Row firstRow = sheet.getRow(0);
		int totalColumns = firstRow.getPhysicalNumberOfCells();
		String key = "", value = "";
		for (int k = 1; k <= totalColumns; k++) {
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i <= totalRows; i++) {
				Row row = sheet.getRow(i);
				if (row != null) {
					Cell cell = row.getCell(0);
					key = getCellValue(cell);

					Cell cell1 = row.getCell(k);
					value = getCellValue(cell1);

					map.put(key, value);
				}
			}
			outputData.add(map);
		}
		workbook.close();
		return outputData;
	}

	public List<String> retrieveHeaderData(Workbook workbook) throws Exception {
		Sheet sheet = workbook.getSheetAt(0);
		Row headerRow = sheet.getRow(0);
		List<String> header = getRowData(headerRow);
		workbook.close();
		return header;
	}

	public void writeDataToFile(Workbook workbook, String filename, List<HashMap<String, String>> data)
			throws Exception {
		if (data.size() == 0) {
			return;
		}

		HashMap<String, String> map = data.get(0);
		Set<String> header = map.keySet();
		writeDataToFile(workbook, filename, data, header.toArray(new String[header.size()]));
	}

	public void writeDataToFile(Workbook workbook, String filename, List<HashMap<String, String>> data, String[] header)
			throws Exception {
		writeDataToFile(workbook, filename, data, header, false);
	}

	public void writeDataToFile(Workbook workbook, String filename, List<HashMap<String, String>> data, String[] header,
			boolean forceNumbersAsString) throws Exception {
		addData(header);
		for (HashMap<String, String> map : data) {
			String[] row = new String[header.length];
			for (int i = 0; i < header.length; i++) {
				row[i] = map.get(header[i]);
			}
			addData(row);
		}
		saveData(workbook, filename, forceNumbersAsString);
	}

}
