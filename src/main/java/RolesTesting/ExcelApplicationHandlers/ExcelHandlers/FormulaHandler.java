package RolesTesting.ExcelApplicationHandlers.ExcelHandlers;

import RolesTesting.Constants.Constant;
import RolesTesting.ExecutionHandlers.RoleBasedHandlers.RoleBasedDriver;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import org.apache.poi.ss.formula.FormulaParser;
import org.apache.poi.ss.formula.FormulaRenderer;
import org.apache.poi.ss.formula.FormulaType;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.io.*;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class FormulaHandler {
    private static final Logger logger = LoggerFactory.getLogger(FormulaHandler.class);
    private static ExtentTest test;


    public static double setFormula(String workbookPath, String formula, String cellAddress) throws IOException {

        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);

        XSSFSheet sheet = wb.getSheet("ACR_MOM2");

        CellReference cellReference = new CellReference(cellAddress);
        XSSFRow row = sheet.createRow(cellReference.getRow());
        XSSFCell formulaCell = (XSSFCell) ((XSSFRow) row).createCell(cellReference.getCol());

        formulaCell.setCellFormula(formula);

        XSSFFormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        formulaEvaluator.evaluateFormulaCell(formulaCell);

        FileOutputStream fileOut = new FileOutputStream(new File(workbookPath));
        wb.write(fileOut);
        wb.close();
        fileOut.close();

        return formulaCell.getNumericCellValue();
    }

    public static void copyFormula(Sheet sheet, Cell org, Cell dest) {
        if (org == null || dest == null || sheet == null || org.getCellType() != CellType.FORMULA)
            return;
        if (org.isPartOfArrayFormulaGroup())
            return;
        String formula = org.getCellFormula();
        int shiftRows = dest.getRowIndex() - org.getRowIndex();
        int shiftCols = dest.getColumnIndex() - org.getColumnIndex();
        XSSFEvaluationWorkbook workbookWrapper = XSSFEvaluationWorkbook.create((XSSFWorkbook) sheet.getWorkbook());
        Ptg[] ptgs = FormulaParser.parse(formula, workbookWrapper, FormulaType.CELL, sheet.getWorkbook().getSheetIndex(sheet));
        for (Ptg ptg : ptgs) {
            if (ptg instanceof RefPtgBase) // base class for cell references
            {
                RefPtgBase ref = (RefPtgBase) ptg;
                if (ref.isColRelative())
                    ref.setColumn(ref.getColumn() + shiftCols);
                if (ref.isRowRelative())
                    ref.setRow(ref.getRow() + shiftRows);
            } else if (ptg instanceof AreaPtg) // base class for range references
            {
                AreaPtg ref = (AreaPtg) ptg;
                if (ref.isFirstColRelative())
                    ref.setFirstColumn(ref.getFirstColumn() + shiftCols);
                if (ref.isLastColRelative())
                    ref.setLastColumn(ref.getLastColumn() + shiftCols);
                if (ref.isFirstRowRelative())
                    ref.setFirstRow(ref.getFirstRow() + shiftRows);
                if (ref.isLastRowRelative())
                    ref.setLastRow(ref.getLastRow() + shiftRows);
            }
        }
        formula = FormulaRenderer.toFormulaString(workbookWrapper, ptgs);
        dest.setCellFormula(formula);
    }

    public static void calculateFormulaRange(String workbookPath, String formula, String cellRange) throws IOException {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet("ACR_MOM2");

        //Parse range into the first cell and last cell in range
        String firstCellAddress = cellRange.split(":")[0];
        String lastCellAddress = cellRange.split(":")[1];

        //Create a formula cell at firstCellAddress
        CellReference firstCellReference = new CellReference(firstCellAddress);
        XSSFRow row = sheet.createRow(firstCellReference.getRow());
        XSSFCell formulaCell = (XSSFCell) row.createCell(firstCellReference.getCol());
        formulaCell.setCellFormula(formula);


        //Get the row and column number of the last cell in range
        CellReference lastCellReference = new CellReference(lastCellAddress);
        int lastCellRowNumber = lastCellReference.getRow();
        int lastCellColumnNumber = lastCellReference.getCol();

        //Number of cells in range
        int length = lastCellColumnNumber - formulaCell.getColumnIndex();

        // Create Table to store values
        Table values = Table.create("Values");

        //Dynamically update the formula reference and input into next cell in range
        for (int i = 0; i <= length; i++) {
            //Create a new formula cell
            XSSFCell newFormulaCell = (XSSFCell) row.createCell(firstCellReference.getCol() + i + 1);

            //Update the formula using relative reference
            copyFormula(sheet, CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i)
                    , CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i + 1));

            XSSFFormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();

            //Evaluate value of formula and add to table
            values.addColumns(StringColumn.create("Value " + (i + 1), String.valueOf(formulaEvaluator.evaluateInCell(CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i)))));

        }

        System.out.println(values);


    }

    public static void calculateFormulaRange(Row row, String workBookName) throws IOException {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workBookName));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SheetName"));
        XSSFSheet sheet = wb.getSheet(sheetName);

        String formula = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3"));
        String cellRange = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2"));

        //Parse range into the first cell and last cell in range
        String firstCellAddress = cellRange.split(":")[0];
        String lastCellAddress = cellRange.split(":")[1];

        //Create a formula cell at firstCellAddress
        CellReference firstCellReference = new CellReference(firstCellAddress);
        XSSFRow POIrow = sheet.createRow(firstCellReference.getRow());
        XSSFCell formulaCell = (XSSFCell) POIrow.createCell(firstCellReference.getCol());
        formulaCell.setCellFormula(formula);


        //Get the row and column number of the last cell in range
        CellReference lastCellReference = new CellReference(lastCellAddress);
        int lastCellRowNumber = lastCellReference.getRow();
        int lastCellColumnNumber = lastCellReference.getCol();

        //Number of cells in range
        int length = lastCellColumnNumber - formulaCell.getColumnIndex();

        // Create Table to store values
        Table values = Table.create("Values");

        //Dynamically update the formula reference and input into next cell in range
        for (int i = 0; i <= length; i++) {
            //Create a new formula cell
            XSSFCell newFormulaCell = (XSSFCell) ((XSSFRow) POIrow).createCell(firstCellReference.getCol() + i + 1);

            //Update the formula using relative reference
            copyFormula(sheet, CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i)
                    , CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i + 1));

            XSSFFormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();

            //Evaluate value of formula and add to table
            values.addColumns(StringColumn.create("Value " + (i + 1), String.valueOf(formulaEvaluator.evaluateInCell(CustomExcelReader.getCellContentByIndex(sheet, formulaCell.getRowIndex(), formulaCell.getColumnIndex() + i)))));

        }

        System.out.println(values);


    }


    public static Table getOneFieldSegment(String workbookPath, String sheetName, String fieldSegment, String lastColumnName, String columnsToDelete) throws Exception {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        XSSFCell segment = CustomExcelReader.getCellByString(sheet, fieldSegment);

        XSSFCell columnHeader = CustomExcelReader.getCellByString(sheet, lastColumnName);

        String fieldSegmentReference = segment.getReference();

        XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, segment.getRowIndex() + 1, columnHeader.getColumnIndex());

        String lastCellReference = lastCell.getReference();

        Table oneFieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, fieldSegmentReference + ":" + lastCellReference);


        System.out.println(oneFieldSegmentTable);

        return oneFieldSegmentTable;

    }

    public static Table removeColumnsInRange(String workbookPath, String sheetName, String cellRange, String columnsToDelete) throws Exception {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        String[] columnsArray = columnsToDelete.split(";");

        Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, cellRange);

        for (String column : columnsArray) {
            fieldSegmentTable.removeColumns(column);
        }

        // System.out.println(fieldSegmentTable);

        FormulaHandler.copyTableToSheet(fieldSegmentTable, workbookPath);

        return fieldSegmentTable;
    }

    public static void copyTableToSheet(Table table, String workbookPath) throws IOException {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet s = wb.createSheet();
        for (int i = 0; i < table.rowCount(); i++) {
            s.createRow(i);
            for (int j = 0; j < table.columnCount(); j++) {
                if (table.get(i, j) == null) {
                    s.getRow(i).createCell(j).setCellValue("");
                } else {
                    s.getRow(i).createCell(j).setCellValue(table.get(i, j).toString());
                }
            }
        }
        // Write the output to a file
        FileOutputStream fileOut = new FileOutputStream(workbookPath);
        wb.write(fileOut);
        fileOut.close();

        // Closing the workbook
        wb.close();
    }


    //Loop through Field Segments and returns an ArrayList with the Cells containing the Field Segment Name
    public static ArrayList<XSSFCell> getArrayListFieldSegments(String workbookPath, String sheetName, XSSFCell fieldSegmentHeader, int numberOfRowsFieldSegment, int numberOfRowsConstant) throws Exception {
        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        ArrayList<XSSFCell> fieldSegments = new ArrayList<>();
        for (int i = 0; i >= 0; i++) {
            try {
                if(CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()) == null){
                    return fieldSegments;
                }
            } catch (Exception e) {
                return fieldSegments;
            }
            if (CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + numberOfRowsFieldSegment + 1, fieldSegmentHeader.getColumnIndex()).getCellType() == CellType.STRING) {
                fieldSegments.add(CustomExcelReader.getCellContentByIndex(sheet, fieldSegmentHeader.getRowIndex() + 1 + numberOfRowsFieldSegment, fieldSegmentHeader.getColumnIndex()));
                numberOfRowsFieldSegment = numberOfRowsFieldSegment + numberOfRowsConstant;
            } else {
                i = -1;
                return fieldSegments;
            }

        }
        return null;
    }

    public static Table removeTwoRowsFromFieldSegment(Table table) {
        Table modifiedTable = table;

        //Remove Adj$ and ACR YoY% rows from table
        for (int i = 2; i < modifiedTable.rowCount(); i += 2) {
            modifiedTable = modifiedTable.dropRows(i, i + 1);
        }
        return modifiedTable;
    }

    public static Table removeThreeRowsFromFieldSegment(Table table) {
        Table modifiedTable = table;

        //Remove Baseline, Baseline Adj, and Total Forecast from ACR Pipeline Forecast Table
        for (int i = 2; i < modifiedTable.rowCount(); i += 2) {
            modifiedTable = modifiedTable.dropRows(i + 2, i + 3, i + 4);
        }
        return modifiedTable;
    }

    public static Table removeColumns(Table table, String columnNames) {
        //Split column names into list using ";" as delimiter
        String[] columnsArray = columnNames.split(";");

        //For each column name in list, remove column from table
        for (String column : columnsArray) {
            table.removeColumns(column);
        }
        return table;
    }

    public static LinkedHashMap testCalculateACR_MOM(String workbookPath, String sheetName, String tableHeaderName, String columnName, ExtentReports extent) throws Exception {
        test = extent.createTest("Calculate_Field_Segments_Test");
        test.log(Status.INFO, MarkupHelper.createLabel("Started execution for Test " + "Calculate_Field_Segments_Test" +
                " with description " + "Description_Test" +
                " with run flag " + "Run_Flag_Test", ExtentColor.BLUE));


        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        //ToDo Get values from config
        int numberOfColumns = 28;
        int numberOfRowsFieldSegment = 4;
        final int numberOfRowsConstant = numberOfRowsFieldSegment;

        //Gets the cell with the String input
        XSSFCell tableHeader = CustomExcelReader.getCellByString(sheet, tableHeaderName);

        //Gets the last cell in the table based on the number of columns
        XSSFCell lastColumnHeader = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex(), tableHeader.getColumnIndex() + numberOfColumns - 1);

        //Cell Reference of the first cell in the table
        String fieldSegmentReference = tableHeader.getReference();

        XSSFCell firstFieldSegment = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex() + 1, tableHeader.getColumnIndex());

        //Calls getArrayListFieldSegments method to create ArrayList of Field Segment Cells
        ArrayList<XSSFCell> fieldSegments = FormulaHandler.getArrayListFieldSegments(workbookPath, sheetName, tableHeader, numberOfRowsFieldSegment, numberOfRowsConstant);

        //Get last cell in the ArrayList (Last Field Segment in Table)
        XSSFCell lastFieldSegment = fieldSegments.get(fieldSegments.size() - 1);

        //Get Last Cell in the last column of the table
        XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, lastFieldSegment.getRowIndex() + numberOfRowsConstant - 1, lastColumnHeader.getColumnIndex());

        //Cell Reference of the last cell in the sheet
        String lastCellReference = lastCell.getReference();

        //Create Table using Cell References
        Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, fieldSegmentReference + ":" + lastCellReference);

        //Remove Adj$ and ACR YoY% rows from table
        Table modifiedTable = removeTwoRowsFromFieldSegment(fieldSegmentTable);

        //Remove columns from table
        removeColumns(modifiedTable, "FY 2020;FY 2021");

        //Calculate values for MOM% and ACR$
        int startingColumnIndex = modifiedTable.columnIndex(columnName);

        //String[] rowNames = {"ACR $: Expected", "ACR $: Actual", "MOM%: Expected", "MOM% Actual", "ACR $: Result", "MOM%: Result"};
        // Table acrReport = Table.create("acrReport");
        //acrReport.addColumns(StringColumn.create("Field Segment: ", rowNames));

        LinkedHashMap<String, Table> acrReports = new LinkedHashMap<String, Table>();
        //ArrayList<Table> acrReports = new ArrayList<>();

        Table acrReport = null;
        for (int j = 0; j < modifiedTable.rowCount(); j += 2) {
            String[] rowNames = {"ACR $: Expected", "ACR $: Actual", "MOM%: Expected", "MOM% Actual", "ACR $: Result", "MOM%: Result"};
            acrReport = Table.create(modifiedTable.column(0).get(j).toString());
            acrReport.addColumns(StringColumn.create("Field Segment: ", rowNames));


            for (int i = startingColumnIndex; i < modifiedTable.columnCount(); i++) {
                Integer pastMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i - 1).substring(0, 3));
                int pastYear = Integer.parseInt(modifiedTable.columnNames().get(i - 1).split(",")[1].trim());
                Integer past6Month = Constant.getMonths().get(modifiedTable.columnNames().get(i - 7).substring(0, 3));
                int past6Year = Integer.parseInt(modifiedTable.columnNames().get(i - 7).split(",")[1].trim());
                Integer currentMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i).substring(0, 3));
                int currentYear = Integer.parseInt(modifiedTable.columnNames().get(i).split(",")[1].trim());

                YearMonth yearMonthObject1 = YearMonth.of(pastYear, pastMonth);
                int daysInMonth1 = yearMonthObject1.lengthOfMonth();
                YearMonth yearMonthObject2 = YearMonth.of(past6Year, past6Month);
                int daysInMonth2 = yearMonthObject2.lengthOfMonth();
                YearMonth yearMonthObject3 = YearMonth.of(currentYear, currentMonth);
                int daysInMonth3 = yearMonthObject3.lengthOfMonth();


                double ACR$Past = Math.round(Double.parseDouble((modifiedTable.get(j, i - 1)).toString()) * 1000000.0) / 1000000.0;

                double ACR$Past6 = Math.round(Double.parseDouble((modifiedTable.get(j, i - 7)).toString()) * 1000000.0) / 1000000.0;

                double tempValue = Math.round(((ACR$Past / daysInMonth1) / (ACR$Past6 / daysInMonth2)) * 1000000.0) / 1000000.0;
                double expectedMOM_Per = Math.pow(tempValue, 0.16666666666) - 1;


                double actualMOM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 1000000.0) / 1000000.0;

                double tempRoundUntoSixDecimals = Math.round((1.00 + actualMOM_Per) * 1000000.0) / 1000000.0;
                double tempRoundSixMulLastValueRatio = Math.round(((ACR$Past / daysInMonth1) * tempRoundUntoSixDecimals) * 1000000.0) / 1000000.0;

                double tempRound = Math.round(tempRoundSixMulLastValueRatio * 1000000.0) / 1000000.0;
                double expectedAcr_Dol = tempRound * daysInMonth3;


                double momActual = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()));
                //double ACR$Actual = Double.parseDouble((modifiedTable.get(j, i)).toString());
                //double ACR$Actual = Math.round(Double.parseDouble((modifiedTable.get(j, i)).toString())* 10000.0) / 10000.0;


                String fieldSegment = "Field Segment: " + modifiedTable.column(0).get(j);
                acrReport.column(0).setName(fieldSegment);
                String acrResult;
                String momResult;

                double roundedExpectedACR$ = Math.round(expectedAcr_Dol * 10000.0) / 10000.0;
                double roundedActualACR$ = Math.round(Double.parseDouble((modifiedTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                double roundedExpectedMOM_Per = Math.round(expectedMOM_Per * 10000.0) / 10000.0;
                double roundedActualMoM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 10000.0) / 10000.0;


                if (roundedExpectedACR$ == roundedActualACR$) {
                    acrResult = "Passed";
                    String status = acrResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                } else {
                    acrResult = "Failed";
                    String status = acrResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment,(ExtentColor.RED)));
                }
                if (roundedExpectedMOM_Per == roundedActualMoM_Per) {
                    momResult = "Passed";
                    String status = momResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                } else {
                    momResult = "Failed";
                    String status = momResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment,(ExtentColor.RED)));
                }
                String month = modifiedTable.columnNames().get(i);
                String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(roundedExpectedACR$)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedActualACR$)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedExpectedMOM_Per)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedActualMoM_Per)),
                        acrResult,
                        momResult};
                acrReport.addColumns(StringColumn.create(month, columnData));
            }
            logger.info(String.valueOf(acrReport));
            acrReports.put(acrReport.name(),acrReport);
        }
        return acrReports;
    }

    //ToDo Maintenance: Formulas for calculating ACR$ and MOM% are in this method
    public static LinkedHashMap calculateACR_MOM(Row row, String workBookName, ExtentReports extent) throws Exception {
        //Create test in HTML report
        test = extent.createTest("Calculate_Field_Segments_Test");
        test.log(Status.INFO, MarkupHelper.createLabel("Started execution for Test " + "Calculate_Field_Segments_Test" +
                " with description " + "Description_Test" +
                " with run flag " + "Run_Flag_Test", ExtentColor.BLUE));

        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workBookName));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        String sheetName = row.getString(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_SheetName"));
        XSSFSheet sheet = wb.getSheet(sheetName);

        String tableHeaderName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2"));
        String columnName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3"));

        //Get number of rows for each field segment and columns from config.properties file
        int numberOfColumns = Integer.parseInt(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_Columns"));
        int numberOfRowsFieldSegment = Integer.parseInt(ConfigProperties.getProperty("testCaseFlow.ACR_MOM_Rows_Per_Field_Segment"));
        final int numberOfRowsConstant = numberOfRowsFieldSegment;

        //Gets the cell with the String input
        XSSFCell tableHeader = CustomExcelReader.getCellByString(sheet, tableHeaderName);

        //Gets the last cell in the table based on the number of columns
        XSSFCell lastColumnHeader = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex(), tableHeader.getColumnIndex() + numberOfColumns - 1);

        //Cell Reference of the first cell in the table
        String fieldSegmentReference = tableHeader.getReference();

        //Calls getArrayListFieldSegments method to create ArrayList of Field Segment Cells
        ArrayList<XSSFCell> fieldSegments = FormulaHandler.getArrayListFieldSegments(workBookName, sheetName, tableHeader, numberOfRowsFieldSegment, numberOfRowsConstant);

        //Get last cell in the ArrayList (Last Field Segment in Table)
        XSSFCell lastFieldSegment = fieldSegments.get(fieldSegments.size() - 1);

        //Get Last Cell in the last column of the table
        XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, lastFieldSegment.getRowIndex() + numberOfRowsConstant - 1, lastColumnHeader.getColumnIndex());

        //Cell Reference of the last cell in the sheet
        String lastCellReference = lastCell.getReference();

        //Create Table using Cell References
        Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workBookName, sheetName, fieldSegmentReference + ":" + lastCellReference);

        //Remove Adj$ and ACR YoY% rows from table
        Table modifiedTable = removeTwoRowsFromFieldSegment(fieldSegmentTable);

        //Remove columns from table
        removeColumns(modifiedTable, "FY 2020;FY 2021");

        //Calculate values for MOM% and ACR$
        int startingColumnIndex = modifiedTable.columnIndex(columnName);

        //Create LinkedHashMp to store field Segment calculation results tables
        LinkedHashMap<String, Table> acrReports = new LinkedHashMap<String, Table>();
        //ArrayList<Table> acrReports = new ArrayList<>();

        Table acrReport = null;
        for (int j = 0; j < modifiedTable.rowCount(); j += 2) {
            //Create TableSaw Table to store calculation results
            String[] rowNames = {"ACR $: Expected", "ACR $: Actual", "MOM%: Expected", "MOM% Actual", "ACR $: Result", "MOM%: Result"};
            acrReport = Table.create(modifiedTable.column(0).get(j).toString());
            acrReport.addColumns(StringColumn.create("Field Segment: ", rowNames));


            for (int i = startingColumnIndex; i < modifiedTable.columnCount(); i++) {
                //Get Days in month for current month and 6 months past
                Integer pastMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i - 1).substring(0, 3));
                int pastYear = Integer.parseInt(modifiedTable.columnNames().get(i - 1).split(",")[1].trim());
                Integer past6Month = Constant.getMonths().get(modifiedTable.columnNames().get(i - 7).substring(0, 3));
                int past6Year = Integer.parseInt(modifiedTable.columnNames().get(i - 7).split(",")[1].trim());
                Integer currentMonth = Constant.getMonths().get(modifiedTable.columnNames().get(i).substring(0, 3));
                int currentYear = Integer.parseInt(modifiedTable.columnNames().get(i).split(",")[1].trim());

                YearMonth yearMonthObject1 = YearMonth.of(pastYear, pastMonth);
                int daysInMonth1 = yearMonthObject1.lengthOfMonth();
                YearMonth yearMonthObject2 = YearMonth.of(past6Year, past6Month);
                int daysInMonth2 = yearMonthObject2.lengthOfMonth();
                YearMonth yearMonthObject3 = YearMonth.of(currentYear, currentMonth);
                int daysInMonth3 = yearMonthObject3.lengthOfMonth();

                //ToDo Maintenance: Calculations for expected MOM%
                //Get the ACR$ amount of previous month
                double ACR$Past = Math.round(Double.parseDouble((modifiedTable.get(j, i - 1)).toString()) * 1000000.0) / 1000000.0;

                //Get the ACR$ amount of previous 6 month
                double ACR$Past6 = Math.round(Double.parseDouble((modifiedTable.get(j, i - 7)).toString()) * 1000000.0) / 1000000.0;

                //Temporary value for rounding
                double tempValue = Math.round(((ACR$Past / daysInMonth1) / (ACR$Past6 / daysInMonth2)) * 1000000.0) / 1000000.0;

                //Calculate Expected MOM% value
                double expectedMOM_Per = Math.pow(tempValue, 0.16666666666) - 1;

                //Get the actual MOM% value for current month
                double actualMOM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 1000000.0) / 1000000.0;

                //ToDo Maintenance: Calculations for expected ACR$ amount
                double tempRoundUntoSixDecimals = Math.round((1.00 + actualMOM_Per) * 1000000.0) / 1000000.0;
                double tempRoundSixMulLastValueRatio = Math.round(((ACR$Past / daysInMonth1) * tempRoundUntoSixDecimals) * 1000000.0) / 1000000.0;
                double tempRound = Math.round(tempRoundSixMulLastValueRatio * 1000000.0) / 1000000.0;
                double expectedAcr_Dol = tempRound * daysInMonth3;

                String fieldSegment = "Field Segment: " + modifiedTable.column(0).get(j);
                acrReport.column(0).setName(fieldSegment);
                String acrResult;
                String momResult;

                //Round expected and actual values to 4 decimals
                double roundedExpectedACR$ = Math.round(expectedAcr_Dol * 10000.0) / 10000.0;
                double roundedActualACR$ = Math.round(Double.parseDouble((modifiedTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                double roundedExpectedMOM_Per = Math.round(expectedMOM_Per * 10000.0) / 10000.0;
                double roundedActualMoM_Per = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 10000.0) / 10000.0;

                //Compare expected and actual values
                if (roundedExpectedACR$ == roundedActualACR$) {
                    acrResult = "Passed";
                    String status = acrResult;
                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                } else {
                    acrResult = "Failed";
                    String status = acrResult;
                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment,(ExtentColor.RED)));
                }
                if (roundedExpectedMOM_Per == roundedActualMoM_Per) {
                    momResult = "Passed";
                    String status = momResult;
                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.PASS), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment,(ExtentColor.GREEN)));
                } else {
                    momResult = "Failed";
                    String status = momResult;
                    logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    test.log((Status.FAIL), MarkupHelper.createLabel( modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment,(ExtentColor.RED)));
                }
                //Store results in TableSaw Table
                String month = modifiedTable.columnNames().get(i);
                String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(roundedExpectedACR$)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedActualACR$)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedExpectedMOM_Per)),
                        String.valueOf(new DecimalFormat("#.####").format(roundedActualMoM_Per)),
                        acrResult,
                        momResult};
                acrReport.addColumns(StringColumn.create(month, columnData));
            }
            //Store results in LinkedHashMap
            acrReports.put(acrReport.name(),acrReport);
        }
        return acrReports;
    }
}


