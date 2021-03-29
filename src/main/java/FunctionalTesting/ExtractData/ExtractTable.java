package FunctionalTesting.ExtractData;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.xlsx.XlsxReadOptions;
import tech.tablesaw.io.xlsx.XlsxReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtractTable {

    //Returns the Excel sheet of an Excel file
    public XSSFSheet readExcelSheet(File file, String sheetName) throws IOException {
        Workbook wb = WorkbookFactory.create(file);
        Sheet sheetToOpen = SheetHandler.getSheetFromWorkBook(wb, sheetName);
        /*FileInputStream inputStream = new FileInputStream(file);
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);*/
        return (XSSFSheet) sheetToOpen;
    }

    //Converts an Excel sheet into a TableSaw table
    public Table convertExcelToTable(String filePath, String sheetName) throws IOException {
        //Workbook workbook = new XSSFWorkbook(filePath);
        XlsxReadOptions options = XlsxReadOptions.builder(filePath)
                .sheetIndex(getNameAndIndexMap1(filePath,sheetName,false))
                .header(true)
                .build();
        XlsxReader xlsxReader = new XlsxReader();
        Table table=  xlsxReader.read(options);
        return table;
    }

    //Gets the index of the desired Excel sheet
    public int getNameAndIndexMap1(String workbookPath, String sheetName, Boolean inputFileFlag) throws IOException {
        Workbook workbook = new XSSFWorkbook(workbookPath);
        int index =0;
        for(int i=0; i<workbook.getNumberOfSheets(); i++){
            if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(sheetName)) {
                if(inputFileFlag) {
                    index = i+1;
                }
                else {
                    index = i;
                }
            }
        }
        return index;
    }

    //Gets the index of the desired Excel sheet
    public int getNameAndIndexMap(File file, String sheetName, Boolean inputFileFlag) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(file);
        int index =0;
        for(int i=0; i<workbook.getNumberOfSheets(); i++){
            if(workbook.getSheetAt(i).getSheetName().equalsIgnoreCase(sheetName)) {
                if(inputFileFlag) {
                    index = i+1;
                }
                else {
                    index = i;
                }
            }
        }
        return index;
    }

    //Returns the row index of the cell with the inputAnchorName
    public Map<String, Integer> getRowIndex(File inputFile, String inputSheetName, int startCol, int endCol, String inputAnchorName) throws IOException {
        XSSFSheet sheet = readExcelSheet(inputFile,inputSheetName);
        Map<String,Integer> rowIndex = new HashMap<>();
        String anchorName = null;
        int occurence = 0;
        if(inputAnchorName.contains("Occurence")) {
            anchorName = inputAnchorName.split("\\|")[0];
            occurence = Integer.parseInt(inputAnchorName.split("\\|")[1].split("_")[1]);
        } else {
            anchorName = inputAnchorName;
        }
        //String tableAnchor = (String) tableDefData.get("tableAnchor");
        int startIndex = 0;
        int endIndex =0;
        int anchorNameCount = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++){
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(startCol);
                if (cell != null) {
                    if (cell.getCellType()== CellType.STRING && cell.getStringCellValue().equals(anchorName)) {
                        anchorNameCount ++;
                        if(occurence > 0 && (occurence != anchorNameCount)) {
                                /*if(occurence != anchorNameCount) {
                                    continue;
                                }*/
                            continue;
                        } else {
                            startIndex = cell.getRowIndex();
                            rowIndex.put("startRow", startIndex);
                        }
                    } else {
                        if(startIndex > 0) {
                            Row row1 = sheet.getRow(i + 1);
                            //Cell cell1 = row1.getCell(1);
                            if (row1 == null) {
                                endIndex = cell.getRowIndex();
                                rowIndex.put("endRow", endIndex);
                                break;
                            }
                            else if(row1!=null) {
                                int count=0;
                                for(int j=0;j<row1.getLastCellNum();j++)
                                {
                                    if(row1.getCell(j)!=null){count++;}
                                }
                                if(count==0)
                                {
                                    endIndex = cell.getRowIndex();
                                    rowIndex.put("endRow", endIndex);
                                    break;
                                }
                            }
                        }
                    }
                }
                if(endIndex>0) {
                    break;
                }
            }
        }
        return rowIndex;
    }


    //Returns a table from the desired range of an Excel sheet
    public Table convertExcelToTableWithCellRange(File inputFile,String inputSheetName,Map<String, List<String>> tableDefData) throws Exception {
        List<String> columnIndex = new ArrayList<>();
        String anchorName = null;
        Table table = null;
        for (Map.Entry<String, List<String>> def : tableDefData.entrySet()) {
            anchorName = def.getKey();
            columnIndex = def.getValue();
            int startCol = (new CellReference(columnIndex.get(0))).getCol();
            int endCol = (new CellReference(columnIndex.get(1))).getCol();
            Map<String,Integer> rowIndex = getRowIndex(inputFile,inputSheetName,startCol,endCol,anchorName);
            int startRow = rowIndex.get("startRow");
            int endRow = rowIndex.get("endRow");

            XlsxReadOptions options = XlsxReadOptions.builder(inputFile)
                    .sheetIndex(getNameAndIndexMap(inputFile,inputSheetName,true))
                    .header(true)
                    .build();
            TablesawReader xlsxReader = new TablesawReader(inputFile, inputSheetName);

            table = table != null ? table.concat(xlsxReader.read(options, startRow, endRow, startCol, endCol, false)) : xlsxReader.read(options, startRow, endRow, startCol, endCol, false);
        }
        return table;
    }

    //Returns the table definitions data
    public Map<String, Map<String, List<String>>> getTableDefinitions(Table tableDef) {
        Map<String, Map<String, List<String>>> entireTableDefData = new HashMap<>();
        for (int i = 0; i < tableDef.rowCount(); i++) {
            int occurence =0;
            Map<String, List<String>> tableDefData = new HashMap<>();
            for (Column column : tableDef.columns()) {
                List<String> columnData = new ArrayList<>();

                if(column.name().startsWith("Occurence")) {
                    occurence = tableDef.row(i).getInt(tableDef.columnIndex(column.name()));
                }
                if (column.name().startsWith("Table Anchor")) {
                    columnData.add(tableDef.row(i).getString(tableDef.columnIndex(column.name()) + 1));
                    columnData.add(tableDef.row(i).getString(tableDef.columnIndex(column.name()) + 2));
                    String anchorName = tableDef.row(i).getString(tableDef.columnIndex(column.name()));
                    tableDefData.put(occurence > 0 ? anchorName.concat("|Occurence_"+String.valueOf(occurence)) : anchorName, columnData);
                } else {
                    continue;
                }
            }
            String tableName = tableDef.getString(i, "Table Name");
            entireTableDefData.put(tableName, tableDefData);
        }
        return entireTableDefData;
    }

    //Returns the worksheets from an Excel file
    public List<String> getWorksheetsFromWorkbook(File file) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(file);
        List<String> workbooks = new ArrayList<>();
        for(int i=0; i<workbook.getNumberOfSheets(); i++){
            if(! (workbook.isSheetHidden(i) || workbook.isSheetVeryHidden(i))) {
                workbooks.add(workbook.getSheetAt(i).getSheetName());
            }
        }
        return workbooks;
    }
}
