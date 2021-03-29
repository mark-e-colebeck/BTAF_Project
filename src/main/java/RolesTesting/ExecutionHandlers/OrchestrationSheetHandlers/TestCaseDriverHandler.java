package RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers;

import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import Model.Execution.TestCaseDriverReportItem;
import RolesTesting.Util.ConfigProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TestCaseDriverHandler {

    public static List<String> getTestCasesToExecute(String workBookName) throws IOException {
        List<String> testCasesToRun = new ArrayList<>();

        List<Row> rowsToExecute = DriverSheetHandler.getRowsToExecute(workBookName,
                                    "0",
                                            ConfigProperties.getProperty("TestCaseDriver.runFlag"));
        rowsToExecute.forEach(row -> {
            try {
                testCasesToRun.add(SheetHandler.getTextFromColumn(row, ConfigProperties.getProperty("TestCaseDriver.testCaseName")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return testCasesToRun;
    }

    public static LinkedHashSet<String> getTestWithYFlagInTestCaseFlowSheet(Table testCaseFlowTable, List<String> tstCasWithYFlgInTstCasDriver, int[] rc){
        LinkedHashSet<String> testCaseNames = new LinkedHashSet<>();
        testCaseFlowTable.rows(rc).stream().iterator().forEachRemaining(row -> {
            try {
                if (!row.getString(ConfigProperties.getProperty("testCaseFlow.testCaseName")).equals("")){
                    if(tstCasWithYFlgInTstCasDriver.contains(row.getString(ConfigProperties.getProperty("testCaseFlow.testCaseName")))){
                        testCaseNames.add(row.getString(ConfigProperties.getProperty("testCaseFlow.testCaseName")));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return testCaseNames;
    }

    public static void writeToTestCaseDriverSheet(String testCaseName, String rowNumber, String status) throws IOException, InvalidFormatException {
        SheetHandler.writeToSheet("src/main/resources/Reports/AzureForecast.xlsx",
                ConfigProperties.getProperty("testCaseDriverReport.sheetName"),
                new TestCaseDriverReportItem(Integer.parseInt(rowNumber) + 1,
                                            status,
                                            testCaseName
                )
        );
    }



}
