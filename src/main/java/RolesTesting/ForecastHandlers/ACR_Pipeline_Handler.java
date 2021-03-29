package RolesTesting.ForecastHandlers;

import RolesTesting.Constants.Constant;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.CustomExcelReader;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.FormulaHandler;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import RolesTesting.ExecutionHandlers.AutomationExecutionHandlers.ActionExecutors;
import Model.InputForm.AzureForecast.ACR_MOM;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import mmarquee.automation.AutomationException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//import org.junit.Assert;

import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;


public class ACR_Pipeline_Handler {
    private static Logger logger = Logger.getLogger(ACR_MOM_Handler.class);
    private static ExtentTest test;

//    public static void verifyACR_Pipeline(Row row, String workBookName, UIAutomation automation) throws Exception {
//        int bigTableRowReference = new CellAddress(row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2")).split(":")[0]).getRow() + 1;
//        int smallTableRowStart = new CellAddress(row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3")).split(":")[0]).getRow();
//        int mom_row = smallTableRowStart - bigTableRowReference + 1;
//
//        Table rowHeadersTable = SheetHandler.getTableInCellRangeFromSheet(workBookName, row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1")), row.getString(ConfigProperties.getProperty("testCaseFlow.parameter2")));
//        String fieldSegment = rowHeadersTable.get(mom_row, 0).toString();
//        String fiscalYear = rowHeadersTable.get(mom_row, 1).toString();
//
//        Table initialValuesTable = SheetHandler.getTableInCellRangeFromSheet(workBookName,
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1")),
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3")));
//
//        SheetHandler.runMacro("RUN_ACR_PIPE_NUS", workBookName, automation, row);
//
//        // SheetHandler.runMacro("Save", workBookName, automation, row);
//
//
//
//        ActionExecutors.waitForWindowWithTitle(FilenameUtils.getBaseName(workBookName)+ ".xlsm - Excel");
//        //TODO: Look at processes currently running
//        Thread.sleep(35000);
//        logger.info("Sync ended and close started");
//        Utils.closeProcess(automation.getDesktopWindow(FilenameUtils.getBaseName(workBookName) + ".xlsm - Excel").getNativeWindowHandle());
//        logger.info("waiting for close ");
//        ActionExecutors.waitForWindowWithTitle("Analysis");
//        automation.getDesktopWindow("Analysis").getButton("No").click();
//        ActionExecutors.waitForWindowWithTitle("Microsoft Excel");
//        logger.info("clicking on save");
//        automation.getDesktopWindow("Microsoft Excel").getButton("Save").click();
//
//        Thread.sleep(3000);
//
//        Table convertedValuesTable = SheetHandler.getTableInCellRangeFromSheet(workBookName,
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1")),
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter4")));
//
//        Table getConversionRate = SheetHandler.getTableInCellRangeFromSheet(workBookName,
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1")),
//                row.getString(ConfigProperties.getProperty("testCaseFlow.parameter3")));
//
//        Double conversionRate = Double.parseDouble(getConversionRate.get(0, 3).toString());
//
//        Double conversionRatePercent = conversionRate;
//
//        String initialValueString1 = initialValuesTable.get(0, 0).toString();
//        String initialValueString2 = initialValuesTable.get(0, 1).toString();
//        String initialValueString3 = initialValuesTable.get(0, 2).toString();
//
//        Double initialValue1 = Double.parseDouble(initialValueString1);
//        Double initialValue2 = Double.parseDouble(initialValueString2);
//        Double initialValue3 = Double.parseDouble(initialValueString3);
//
//        String convertedValueString1 = convertedValuesTable.get(0, 0).toString();
//        String convertedValueString2 = convertedValuesTable.get(0, 1).toString();
//        String convertedValueString3 = convertedValuesTable.get(0, 2).toString();
//
//        Double convertedValue1 = Double.parseDouble(convertedValueString1);
//        Double convertedValue2 = Double.parseDouble(convertedValueString2);
//        Double convertedValue3 = Double.parseDouble(convertedValueString3);
//
//        Double checkValue1 = initialValue1 * conversionRatePercent;
//        Double checkValue2 = initialValue2 * conversionRatePercent;
//        Double checkValue3 = initialValue3 * conversionRatePercent;
//        String value1Result;
//        String value2Result;
//        String value3Result;
//        if (checkValue1.equals(convertedValue1)) {
//            value1Result = "Pass";
//        } else {
//            value1Result = "Fail";
//        }
//        if (checkValue1.equals(convertedValue1)) {
//            value2Result = "Pass";
//        } else {
//            value2Result = "Fail";
//        }
//        if (checkValue1.equals(convertedValue1)) {
//            value3Result = "Pass";
//        } else {
//            value3Result = "Fail";
//        }
//        String[] rowHeaders = {"Automation Value", "SAP Value", "Result"};
//        Table acrPipelineReport = Table.create("ACR_Pipeline Report");
//        acrPipelineReport.addColumns(StringColumn.create("Field Segment: " + fieldSegment + " | " + "Fiscal Year/Period: " + fiscalYear, rowHeaders));
//        String[] column1 = {String.valueOf(new DecimalFormat("#.####").format(Math.round(checkValue1 * 1000000.0 )/ 1000000.0)), String.valueOf(new DecimalFormat("#.####").format(Math.round(convertedValue1 * 1000000.0 )/ 1000000.0)), value1Result};
//        acrPipelineReport.addColumns(StringColumn.create("1st Month of Quarter", column1));
//        String[] column2 = {String.valueOf(new DecimalFormat("#.####").format(Math.round(checkValue2 * 1000000.0 )/ 1000000.0)), String.valueOf(new DecimalFormat("#.####").format(Math.round(convertedValue2 * 1000000.0 )/ 1000000.0)), value2Result};
//        acrPipelineReport.addColumns(StringColumn.create("2nd Month of Quarter", column2));
//        String[] column3 = {String.valueOf(new DecimalFormat("#.####").format(Math.round(checkValue3 * 1000000.0 )/ 1000000.0)), String.valueOf(new DecimalFormat("#.####").format(Math.round(convertedValue3 * 1000000.0 )/ 1000000.0)), value3Result};
//        acrPipelineReport.addColumns(StringColumn.create("3rd Month of Quarter", column3));
//        System.out.println(acrPipelineReport);
//
//        try {
//            Files.write(Paths.get("log/logFile.txt"), acrPipelineReport.toString().getBytes(), StandardOpenOption.APPEND);
//        }catch (IOException e) {
//            //exception handling left as an exercise for the reader
//        }
//    }


    public static LinkedHashMap TestVerifyACR_Pipeline(String workbookPath, String sheetName, String tableHeaderName, String secondTableHeaderName, ExtentReports extent) throws Exception {
        test = extent.createTest("Calculate_Field_Segments_Test");
        test.log(Status.INFO, MarkupHelper.createLabel("Started execution for Test " + "Calculate_Field_Segments_Test" +
                " with description " + "Description_Test" +
                " with run flag " + "Run_Flag_Test", ExtentColor.BLUE));


        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        //ToDo Get values from config
        int numberOfColumns = 10;
        int numberOfRowsFieldSegment = 2;
        final int numberOfRowsConstant = numberOfRowsFieldSegment;

        int numberOfColumns2 = 10;
        int numberOfRowsFieldSegment2 = 5;
        final int numberOfRowsConstant2 = numberOfRowsFieldSegment2;

        //Gets the cell with the String input
        XSSFCell tableHeader = CustomExcelReader.getCellByString(sheet, tableHeaderName.toUpperCase());

        //Gets the last cell in the table based on the number of columns
        XSSFCell lastColumnHeader = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex(), tableHeader.getColumnIndex() + numberOfColumns - 1);

        //Cell Reference of the first cell in the table
        String fieldSegmentReference = tableHeader.getReference();

        XSSFCell fieldSegmentHeader = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex() + 1, tableHeader.getColumnIndex());

        //Calls getArrayListFieldSegments method to create ArrayList of Field Segment Cells
        ArrayList<XSSFCell> fieldSegments = FormulaHandler.getArrayListFieldSegments(workbookPath, sheetName, fieldSegmentHeader, numberOfRowsFieldSegment, numberOfRowsConstant);

        //Get last cell in the ArrayList (Last Field Segment in Table)
        XSSFCell lastFieldSegment = fieldSegments.get(fieldSegments.size() - 1);

        //Get Last Cell in the last column of the table
        XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, lastFieldSegment.getRowIndex() + numberOfRowsConstant - 1, lastColumnHeader.getColumnIndex());

        //Cell Reference of the last cell in the sheet
        String lastCellReference = lastCell.getReference();

        //Create ACR Pipeline Table using Cell References
        Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, fieldSegmentReference + ":" + lastCellReference);


        //Gets the cell with the String input
        XSSFCell tableHeader2 = CustomExcelReader.getCellByString(sheet, secondTableHeaderName.toUpperCase());

        //Gets the last cell in the table based on the number of columns
        XSSFCell lastColumnHeader2 = CustomExcelReader.getCellContentByIndex(sheet, tableHeader2.getRowIndex(), tableHeader2.getColumnIndex() + numberOfColumns - 1);

        //Cell Reference of the first cell in the table
        String fieldSegmentReference2 = tableHeader2.getReference();

        XSSFCell fieldSegmentHeader2 = CustomExcelReader.getCellContentByIndex(sheet, tableHeader2.getRowIndex() + 1, tableHeader2.getColumnIndex());

        //Calls getArrayListFieldSegments method to create ArrayList of Field Segment Cells
        ArrayList<XSSFCell> fieldSegments2 = FormulaHandler.getArrayListFieldSegments(workbookPath, sheetName, fieldSegmentHeader2, numberOfRowsFieldSegment2, numberOfRowsConstant2);

        //Get last cell in the ArrayList (Last Field Segment in Table)
        XSSFCell lastFieldSegment2 = fieldSegments2.get(fieldSegments2.size() - 1);

        //Get Last Cell in the last column of the table
        XSSFCell lastCell2 = CustomExcelReader.getCellContentByIndex(sheet, lastFieldSegment2.getRowIndex() + numberOfRowsConstant2 - 1, lastColumnHeader2.getColumnIndex());

        //Cell Reference of the last cell in the sheet
        String lastCellReference2 = lastCell2.getReference();

        //Create ACR Pipeline Table using Cell References
        Table acr_pipeline_forecast = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, fieldSegmentReference2 + ":" + lastCellReference2);


        //Remove Baseline, Baseline Adj, and Total Forecast from ACR Pipeline Forecast Table
        Table modifiedTable = FormulaHandler.removeThreeRowsFromFieldSegment(acr_pipeline_forecast);

        LinkedHashMap<String, Table> pipelineReports = new LinkedHashMap<String, Table>();

        Table pipelineReport = null;
        //j is row index
        for (int j = 2; j < fieldSegmentTable.rowCount(); j += 2) {
            String[] rowNames = {"Committed Forecast: Expected", "Committed Forecast: Actual", "Uncommitted Forecast: Expected", "Uncommitted Forecast: Actual", "Committed Forecast: Result", "Uncommitted Forecast: Result"};
            pipelineReport = Table.create(modifiedTable.column(0).get(j).toString());
            pipelineReport.addColumns(StringColumn.create("Field Segment: ", rowNames));


        for (int i = 2; i < 8; i++) {
            double committed;
            double committedConversionRate;
            double expectedCommittedForecast;
            double uncommitted;
            double uncommittedConversionRate;
            double expectedUncommittedForecast;
            double actualCommittedForecast;
            double actualUncommittedForecast;

            if(i < 5) {
                if(fieldSegmentTable.get(j, i).toString().equals("")){
                    committed = 0.0;
                    committedConversionRate = 0.0;
                    expectedCommittedForecast = 0.0;
                }
                else {
                    committed = Math.round(Double.parseDouble((fieldSegmentTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                    if(fieldSegmentTable.get(j, 5).toString().equals("")){
                        committedConversionRate = 0.0;
                        expectedCommittedForecast = 0.0;
                    }
                    else {
                        committedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j, 5)).toString()) * 10000.0) / 10000.0;
                        expectedCommittedForecast = Math.round((committed * committedConversionRate) * 10000.0) / 10000.0;
                    }
                }
                    if(fieldSegmentTable.get(j + 1, i).toString().equals("")) {
                        uncommitted = 0.0;
                        uncommittedConversionRate = 0.0;
                        expectedUncommittedForecast = 0.0;
                    }
                    else {
                        uncommitted = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, i)).toString()) * 10000.0) / 10000.0;
                        if(fieldSegmentTable.get(j + 1, 5).toString().equals("")){
                            committedConversionRate = 0.0;
                            expectedUncommittedForecast = 0.0;
                        }
                        else {
                            uncommittedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, 5)).toString()) * 10000.0) / 10000.0;
                            expectedUncommittedForecast = Math.round((uncommitted * uncommittedConversionRate) * 10000.0) / 10000.0;
                        }
                    }

                    if(modifiedTable.get(j, i).toString().equals("")){
                        actualCommittedForecast = 0.0;
                    }
                    else {
                        actualCommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                    }
                if (modifiedTable.get(j + 1, i).toString().equals("")) {
                    actualUncommittedForecast = 0.0;
                }
                else {
                    actualUncommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i)).toString()) * 10000.0) / 10000.0;
                }

            }
            else{
                if(fieldSegmentTable.get(j, i+1).toString().equals("")){
                    committed = 0.0;
                    committedConversionRate = 0.0;
                    expectedCommittedForecast = 0.0;
                }
                else {
                    committed = Math.round(Double.parseDouble((fieldSegmentTable.get(j, i+1)).toString()) * 10000.0) / 10000.0;
                    if(fieldSegmentTable.get(j, 9).toString().equals("")){
                        committedConversionRate = 0.0;
                        expectedCommittedForecast = 0.0;
                    }
                    else {
                        committedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j, 9)).toString()) * 10000.0) / 10000.0;
                        expectedCommittedForecast = Math.round((committed * committedConversionRate) * 10000.0) / 10000.0;
                    }
                }

                if(fieldSegmentTable.get(j + 1, i+1).toString().equals("")) {
                    uncommitted = 0.0;
                    uncommittedConversionRate = 0.0;
                    expectedUncommittedForecast = 0.0;
                }
                else {
                    uncommitted = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, i+1)).toString()) * 10000.0) / 10000.0;
                    if(fieldSegmentTable.get(j + 1, 9).toString().equals("")){
                        committedConversionRate = 0.0;
                        expectedUncommittedForecast = 0.0;
                    }
                    else {
                        uncommittedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, 9)).toString()) * 10000.0) / 10000.0;
                        expectedUncommittedForecast = Math.round((uncommitted * uncommittedConversionRate) * 10000.0) / 10000.0;
                    }
                }

                if(modifiedTable.get(j, i+1).toString().equals("")){
                    actualCommittedForecast = 0.0;
                }
                else {
                    actualCommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j, i + 1)).toString()) * 10000.0) / 10000.0;
                }
                if(modifiedTable.get(j + 1, i+1).toString().equals("")){
                    actualUncommittedForecast = 0.0;
                }
                else {
                    actualUncommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i + 1)).toString()) * 10000.0) / 10000.0;
                }

            }


            String fieldSegment = "Field Segment: " + modifiedTable.column(0).get(j);
            pipelineReport.column(0).setName(fieldSegment);
            String committedResult;
            String uncommittedResult;


            if (expectedCommittedForecast == actualCommittedForecast) {
                committedResult = "Passed";
                String status = committedResult;
                //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                test.log((Status.PASS), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment, (ExtentColor.GREEN)));
            } else {
                committedResult = "Failed";
                String status = committedResult;
                //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                test.log((Status.FAIL), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment, (ExtentColor.RED)));
            }
            if (expectedUncommittedForecast == actualUncommittedForecast) {
                uncommittedResult = "Passed";
                String status = uncommittedResult;
                //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                test.log((Status.PASS), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.GREEN)));
            } else {
                uncommittedResult = "Failed";
                String status = uncommittedResult;
                //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                test.log((Status.FAIL), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.RED)));
            }
            String month;
            if(i<5) {
                month = modifiedTable.get(1, i).toString();
            }
            else {
                month = modifiedTable.get(1, i+1).toString();
            }
            String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(expectedCommittedForecast)),
                    String.valueOf(new DecimalFormat("#.####").format(actualCommittedForecast)),
                    String.valueOf(new DecimalFormat("#.####").format(expectedUncommittedForecast)),
                    String.valueOf(new DecimalFormat("#.####").format(expectedUncommittedForecast)),
                    committedResult,
                    uncommittedResult};
            pipelineReport.addColumns(StringColumn.create(month, columnData));
        }
        //logger.info(String.valueOf(pipelineReport));
        System.out.println(pipelineReport);
//        logger.info("Results for Sheet: ACR_Pipeline");
//        logger.info(String.valueOf(pipelineReport));
        pipelineReports.put(pipelineReport.name(),pipelineReport);
    }
        return pipelineReports;
    }
}