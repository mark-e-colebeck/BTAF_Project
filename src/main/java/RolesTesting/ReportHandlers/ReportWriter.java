package RolesTesting.ReportHandlers;

import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseFlowHandler;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import tech.tablesaw.api.Row;

import java.io.IOException;

public class ReportWriter {
    public static void assertStepAndPrint(String message, Object expected, Object actual, Row row) throws IOException, InvalidFormatException {
        if (expected.equals(actual)){
            TestCaseFlowHandler.writeToTestCaseFlowHandlerReport(row, message, "Passed");
        }
        else{
            TestCaseFlowHandler.writeToTestCaseFlowHandlerReport(row, message, "Failed");
        }
    }
}
