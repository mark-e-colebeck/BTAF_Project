package RolesTesting.ForecastHandlers;

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

public class MACC_Pipeline_Handler {
    private static Logger logger = Logger.getLogger(MACC_Pipeline_Handler.class);
    private static ExtentTest test;

    public static LinkedHashMap TestVerifyMACC_Pipeline(String workbookPath, String sheetName, String tableHeaderName, String secondTableHeaderName, ExtentReports extent) throws Exception {
        test = extent.createTest("Calculate_Field_Segments_Test");
        test.log(Status.INFO, MarkupHelper.createLabel("Started execution for Test " + "Calculate_Field_Segments_Test" +
                " with description " + "Description_Test" +
                " with run flag " + "Run_Flag_Test", ExtentColor.BLUE));


        //file, workbook, and sheet to connect to
        FileInputStream inputStream = new FileInputStream(new File(workbookPath));
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheet(sheetName);

        //ToDo Get values from config
        int numberOfColumns = 9;
        int numberOfRowsFieldSegment = 2;
        final int numberOfRowsConstant = numberOfRowsFieldSegment;

        int numberOfColumns2 = 9;
        int numberOfRowsFieldSegment2 = 5;
        final int numberOfRowsConstant2 = numberOfRowsFieldSegment2;

        //Gets the cell with the String input
        XSSFCell tableHeader = CustomExcelReader.getCellByString(sheet, tableHeaderName.toUpperCase());

        String tableHeaderReference = tableHeader.getReference();

        XSSFCell lastCell = CustomExcelReader.getCellContentByIndex(sheet, tableHeader.getRowIndex() + 4, tableHeader.getColumnIndex() + 8);

        String lastCellReference = lastCell.getReference();

        //Create MACC Pipeline Table using Cell References
        Table fieldSegmentTable = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, tableHeaderReference + ":" + lastCellReference);


        //Gets the cell with the String input
        XSSFCell tableHeader2 = CustomExcelReader.getCellByString(sheet, secondTableHeaderName.toUpperCase());

        String tableHeaderReference2 = tableHeader2.getReference();

        XSSFCell lastCell2 = CustomExcelReader.getCellContentByIndex(sheet, tableHeader2.getRowIndex() + 6, tableHeader2.getColumnIndex() + 8);

        String lastCellReference2 = lastCell2.getReference();

        //Create MACC Pipeline Table using Cell References
        Table maccPipelineForecast = SheetHandler.getTableInCellRangeFromSheet(workbookPath, sheetName, tableHeaderReference2 + ":" + lastCellReference2);

        Table modifiedTable = maccPipelineForecast.dropRows(4);



        LinkedHashMap<String, Table> pipelineReports = new LinkedHashMap<String, Table>();

        Table pipelineReport = null;
        //j is row index
        for (int j = 2; j < fieldSegmentTable.rowCount() - 1 ; j += 3) {
            String[] rowNames = {"Committed Forecast: Expected", "Committed Forecast: Actual", "Uncommitted Forecast: Expected", "Uncommitted Forecast: Actual", "Uncommitted Upside: Expected", "Uncommitted Upside: Actual", "Committed Forecast: Result", "Uncommitted Forecast: Result", "Uncommitted Upside: Result"};
            pipelineReport = Table.create(modifiedTable.column(0).get(j).toString());
            pipelineReport.addColumns(StringColumn.create("Field Segment: ", rowNames));

            //i is column index
            for (int i = 1; i < 7; i++) {
                double committed;
                double committedConversionRate;
                double expectedCommittedForecast;
                double uncommitted;
                double uncommittedConversionRate;
                double expectedUncommittedForecast;
                double actualCommittedForecast;
                double actualUncommittedForecast;
                double uncommittedUpside;
                double uncommittedUpsideConversionRate;
                double expectedUncommittedUpside;
                double actualUncommittedUpside;


                if(i < 4) {
                    if(fieldSegmentTable.get(j, i).toString().equals("")){
                        committed = 0.0;
                        committedConversionRate = 0.0;
                        expectedCommittedForecast = 0.0;
                    }
                    else {
                        committed = Math.round(Double.parseDouble((fieldSegmentTable.get(j, i)).toString()) * 10000.0) / 10000.0;
                        if(fieldSegmentTable.get(j, 4).toString().equals("")){
                            committedConversionRate = 0.0;
                            expectedCommittedForecast = 0.0;
                        }
                        else {
                            committedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j, 4)).toString()) * 10000.0) / 10000.0;
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
                        if(fieldSegmentTable.get(j + 1, 4).toString().equals("")){
                            committedConversionRate = 0.0;
                            expectedUncommittedForecast = 0.0;
                        }
                        else {
                            uncommittedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, 4)).toString()) * 10000.0) / 10000.0;
                            expectedUncommittedForecast = Math.round((uncommitted * uncommittedConversionRate) * 10000.0) / 10000.0;
                        }
                    }

                    if(fieldSegmentTable.get(j + 2, i).toString().equals("")) {
                        uncommittedUpside = 0.0;
                        uncommittedUpsideConversionRate = 0.0;
                        expectedUncommittedUpside = 0.0;
                    }
                    else {
                        uncommittedUpside = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 2, i)).toString()) * 10000.0) / 10000.0;
                        if(fieldSegmentTable.get(j + 2, 4).toString().equals("")){
                            uncommittedUpsideConversionRate = 0.0;
                            expectedUncommittedUpside = 0.0;
                        }
                        else {
                            uncommittedUpsideConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 2, 4)).toString()) * 10000.0) / 10000.0;
                            expectedUncommittedUpside = Math.round((uncommittedUpside * uncommittedUpsideConversionRate) * 10000.0) / 10000.0;
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

                    if (modifiedTable.get(j + 2, i).toString().equals("")) {
                        actualUncommittedUpside = 0.0;
                    }
                    else {
                        actualUncommittedUpside = Math.round(Double.parseDouble((modifiedTable.get(j + 2, i)).toString()) * 10000.0) / 10000.0;
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
                        if(fieldSegmentTable.get(j, 8).toString().equals("")){
                            committedConversionRate = 0.0;
                            expectedCommittedForecast = 0.0;
                        }
                        else {
                            committedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j, 8)).toString()) * 10000.0) / 10000.0;
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
                        if(fieldSegmentTable.get(j + 1, 8).toString().equals("")){
                            committedConversionRate = 0.0;
                            expectedUncommittedForecast = 0.0;
                        }
                        else {
                            uncommittedConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 1, 8)).toString()) * 10000.0) / 10000.0;
                            expectedUncommittedForecast = Math.round((uncommitted * uncommittedConversionRate) * 10000.0) / 10000.0;
                        }
                    }

                    if(fieldSegmentTable.get(j + 2, i+1).toString().equals("")) {
                        uncommittedUpside = 0.0;
                        uncommittedUpsideConversionRate = 0.0;
                        expectedUncommittedUpside = 0.0;
                    }
                    else {
                        uncommittedUpside = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 2, i+1)).toString()) * 10000.0) / 10000.0;
                        if(fieldSegmentTable.get(j + 2, 8).toString().equals("")){
                            uncommittedUpsideConversionRate = 0.0;
                            expectedUncommittedUpside = 0.0;
                        }
                        else {
                            uncommittedUpsideConversionRate = Math.round(Double.parseDouble((fieldSegmentTable.get(j + 2, 8)).toString()) * 10000.0) / 10000.0;
                            expectedUncommittedUpside = Math.round((uncommittedUpside * uncommittedUpsideConversionRate) * 10000.0) / 10000.0;
                        }
                    }

                    if(modifiedTable.get(j, i+1).toString().equals("")){
                        actualCommittedForecast = 0.0;
                    }
                    else {
                        actualCommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j, i+1)).toString()) * 10000.0) / 10000.0;
                    }
                    if(modifiedTable.get(j + 1, i+1).toString().equals("")){
                        actualUncommittedForecast = 0.0;
                    }
                    else {
                        actualUncommittedForecast = Math.round(Double.parseDouble((modifiedTable.get(j + 1, i+1)).toString()) * 10000.0) / 10000.0;
                    }

                    if (modifiedTable.get(j + 2, i+1).toString().equals("")) {
                        actualUncommittedUpside = 0.0;
                    }
                    else {
                        actualUncommittedUpside = Math.round(Double.parseDouble((modifiedTable.get(j + 2, i+1)).toString()) * 10000.0) / 10000.0;
                    }

                }


                String fieldSegment = "Field Segment: " + modifiedTable.column(0).get(j);
                pipelineReport.column(0).setName(fieldSegment);
                String committedResult;
                String uncommittedResult;
                String uncommittedUpsideResult;


                if (expectedCommittedForecast == actualCommittedForecast) {
                    committedResult = "Passed";
                    String status = committedResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    //test.log((Status.PASS), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment, (ExtentColor.GREEN)));
                } else {
                    committedResult = "Failed";
                    String status = committedResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    //test.log((Status.FAIL), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "ACR$ " + status + " for " + fieldSegment, (ExtentColor.RED)));
                }
                if (expectedUncommittedForecast == actualUncommittedForecast) {
                    uncommittedResult = "Passed";
                    String status = uncommittedResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                   // test.log((Status.PASS), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.GREEN)));
                } else {
                    uncommittedResult = "Failed";
                    String status = uncommittedResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    //test.log((Status.FAIL), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.RED)));
                }
                if (expectedUncommittedUpside == actualUncommittedUpside) {
                    uncommittedUpsideResult = "Passed";
                    String status = uncommittedUpsideResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                    //test.log((Status.PASS), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.GREEN)));
                } else {
                    uncommittedUpsideResult = "Failed";
                    String status = uncommittedUpsideResult;
                    //logger.info("Test " + "Calculate_Field_Segments_Test" + " " + status);
                   // test.log((Status.FAIL), MarkupHelper.createLabel(modifiedTable.columnNames().get(i) + " : " + "MOM% " + status + " for " + fieldSegment, (ExtentColor.RED)));
                }
                String month;
                if(i<4) {
                    month = modifiedTable.get(1, i).toString();
                }
                else {
                    month = modifiedTable.get(1, i+1).toString();
                }
                String[] columnData = {String.valueOf(new DecimalFormat("#.####").format(expectedCommittedForecast)),
                        String.valueOf(new DecimalFormat("#.####").format(actualCommittedForecast)),
                        String.valueOf(new DecimalFormat("#.####").format(expectedUncommittedForecast)),
                        String.valueOf(new DecimalFormat("#.####").format(expectedUncommittedForecast)),
                        String.valueOf(new DecimalFormat("#.####").format(expectedUncommittedUpside)),
                        String.valueOf(new DecimalFormat("#.####").format(actualUncommittedUpside)),
                        committedResult,
                        uncommittedResult,
                        uncommittedUpsideResult};
                pipelineReport.addColumns(StringColumn.create(month, columnData));
            }
            //logger.info(String.valueOf(pipelineReport));
            System.out.println(pipelineReport);
            pipelineReports.put(pipelineReport.name(),pipelineReport);
        }
        return pipelineReports;
    }
}
