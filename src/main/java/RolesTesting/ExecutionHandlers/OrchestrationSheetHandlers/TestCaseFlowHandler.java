package RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers;

import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import Model.Execution.InitialExecutionKeys;
import Model.Execution.TestCaseFlowReportItem;
import RolesTesting.Util.ConfigProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class TestCaseFlowHandler {


    public static String getConnectionToRun(Row row, String columnNameWithConnection){
        return SheetHandler.getTextFromColumn(row, columnNameWithConnection);
    }

    public static List<String> getRoleFolderAndWorkbookFromConnection(Row row, String columnNameWithRole){
        String roleFolderPathAndWorkbook = SheetHandler.getTextFromColumn(row, columnNameWithRole);
        return  Arrays.asList(roleFolderPathAndWorkbook.split("->"));
    }

    private static String workbookToSelect;

    public static InitialExecutionKeys getInitialExecution(Row row, String connectionColumn,
                                                             String roleColumn) {
        List<String> roleFoldersToClick = new ArrayList<>();
        String connectionToClick = getConnectionToRun(row, connectionColumn);
        for (int i=0; i<= getRoleFolderAndWorkbookFromConnection(row, roleColumn).size()-1; i++){
            if (i!=getRoleFolderAndWorkbookFromConnection(row, roleColumn).size()-1){
                roleFoldersToClick.add(getRoleFolderAndWorkbookFromConnection(row, roleColumn).get(i));
            }
            else {
                workbookToSelect = getRoleFolderAndWorkbookFromConnection(row, roleColumn).get(i);
            }
        }

        return new InitialExecutionKeys(connectionToClick, roleFoldersToClick, workbookToSelect);
    }

    public static LinkedHashMap<String, List<Row>> getMapWithStepsForTestCase(Table testCaseFlowTable) throws IOException {
        LinkedHashMap<String, List<Row>> testCaseNameWithDetails = new LinkedHashMap<>();
        int[] rc = new int[testCaseFlowTable.rowCount()];
        for (int i=1; i<testCaseFlowTable.rowCount(); i++){
            rc[i] = i;
        }

        TestCaseDriverHandler.getTestWithYFlagInTestCaseFlowSheet(testCaseFlowTable,
                TestCaseDriverHandler.getTestCasesToExecute(
                        ConfigProperties.getProperty(
                                "TestCaseDriver.workBookName"
                        )
                ),
                rc
        ).forEach(s -> {
            List<Row> rowsWithSameTestCase = new ArrayList<>();
            AtomicReference<String> correctTestCaseName = new AtomicReference<>("");
            testCaseFlowTable.stream().forEach(row -> {
                Row row1 = testCaseFlowTable.row(row.getRowNumber());
                try {
                    if(!row1.getText(ConfigProperties.getProperty("testCaseFlow.testCaseName")).equals("")){
                        correctTestCaseName.set(row1.getText(ConfigProperties.getProperty("testCaseFlow.testCaseName")));
                    }
                    if (correctTestCaseName.toString().equals(s)){
                        rowsWithSameTestCase.add(row1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            testCaseNameWithDetails.put(s, rowsWithSameTestCase);
        });
        return testCaseNameWithDetails;
    }

    public static void writeToTestCaseFlowHandlerReport(Row row, String descriptionToWrite,
                                                        String resultToReport) throws IOException, InvalidFormatException {
        SheetHandler.writeToSheet("src/main/resources/Reports/AzureForecast.xlsx",
                ConfigProperties.getProperty("testCaseFlowReport.sheetName"),
                new TestCaseFlowReportItem(row.getRowNumber() + 1,
                        row.getString(ConfigProperties.getProperty("testCaseFlow.testCaseName")),
                        row.getString(ConfigProperties.getProperty("testCaseFlow.action")),
                        descriptionToWrite,
                        resultToReport));
    }


}
