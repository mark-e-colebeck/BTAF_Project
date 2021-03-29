package RolesTesting.ExecutionHandlers;

import RolesTesting.ExecutionHandlers.AutomationExecutionHandlers.InitialExecutors;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.DriverSheetHandler;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseFlowHandler;
import Model.Execution.InitialExecutionKeys;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import mmarquee.automation.UIAutomation;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.util.LinkedHashMap;
import java.util.List;

import static RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler.getTableFromSheet;
import static RolesTesting.ExecutionHandlers.AutomationExecutionHandlers.ActionExecutionDriver.actionPerformOrchestration;

public class Executor {
    public static void executeUntilSheetLoad(ExtentReports extent) throws Exception {

        String driverWorkBookName = ConfigProperties.getProperty("driverSheet.workBookName");
        String driverSheetInWorkBook = ConfigProperties.getProperty("driverSheet.workBookName.workSheetIndex");
        String flagColumnName = ConfigProperties.getProperty("driverSheet.flagColumnName");
        String connectionColumnName = ConfigProperties.getProperty("driverSheet.connectionColumnName");
        String roleColumnName = ConfigProperties.getProperty("driverSheet.roleColumnName");
        DriverSheetHandler.getRowsToExecute(driverWorkBookName, driverSheetInWorkBook, flagColumnName).forEach(row ->{
            InitialExecutionKeys initialExecutionKeys = null;
            try {
                initialExecutionKeys = TestCaseFlowHandler.getInitialExecution(row,
                        connectionColumnName,
                        roleColumnName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            UIAutomation automation = UIAutomation.getInstance();
            assert initialExecutionKeys != null;
            Table openWorkBookWithSteps;
            LinkedHashMap<String, List<Row>> testCaseFlowSheet = null;
            try {
                InitialExecutors.connectionClickAndRoleFolderSelect(automation, initialExecutionKeys);
                openWorkBookWithSteps = getTableFromSheet(  "src/main/resources/"+ initialExecutionKeys.getWorkSheetToSelect() + ".xlsx", "1");
                assert openWorkBookWithSteps != null;
                testCaseFlowSheet = TestCaseFlowHandler.getMapWithStepsForTestCase(openWorkBookWithSteps);
                actionPerformOrchestration(testCaseFlowSheet, automation, extent);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }
}
