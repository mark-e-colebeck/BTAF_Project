package RolesTesting.ExecutionHandlers.AutomationExecutionHandlers;

import RolesTesting.ActionValidationHandlers.ActionVerifiers;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.FormulaHandler;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.JacobBase;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseDriverHandler;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseFlowHandler;
import RolesTesting.ForecastHandlers.ACR_MOM_Handler;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import mmarquee.automation.ItemNotFoundException;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

//import org.apache.tools.ant.input.InputHandler;
import tech.tablesaw.api.Row;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionExecutionDriver {
    private static Logger logger = Logger.getLogger(ActionExecutionDriver.class);
    private static ExtentTest test;
    private static ExtentReports extent;
    public static void actionPerformOrchestration(LinkedHashMap<String, List<Row>> testCaseFlowSheet, UIAutomation automation, ExtentReports extent){
        AtomicInteger iterationForRow = new AtomicInteger(0);
        testCaseFlowSheet.forEach((s, rows) -> {
            for (Row row1 : rows) {
                try {
                    performAction(row1.getString(ConfigProperties.getProperty("testCaseFlow.action")),
                            automation,
                            row1.getString(ConfigProperties.getProperty("testCaseFlow.parameter1")),
                            row1, extent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                TestCaseDriverHandler.writeToTestCaseDriverSheet(s, iterationForRow.toString(), "Passed");
            } catch (IOException | InvalidFormatException e) {
                e.printStackTrace();
            }
            iterationForRow.getAndIncrement();
        });
    }

    static String workBookName = "";
    public static void performAction(String actionInSheet, UIAutomation automation,
                                     String item, Row row, ExtentReports extent) throws Exception {
        logger.debug("Entered Perform Action");
        switch (actionInSheet){
            case "OpenWorkbook":
//                workBookName = row.getString(ConfigProperties.getProperty("testCaseFlow.openWorkBookInExecution.workbookName"));
                logger.info("Workbook opened started for " +  workBookName);
                workBookName = item + ".xlsm - Excel";
                workBookName = ActionExecutors.openWorkBookInExecution(workBookName, automation, row);
                logger.info("Workbook opened is " +  workBookName);
                break;
            case "OpenWorksheet":
                ActionExecutors.openCorrectWorkSheetInWindow(automation, item, row);
                break;
            case "ClickRefresh":
                logger.info("Clicked refresh start ");
                ActionExecutors.clickRefresh(row);

                //SheetHandler.runMacro("Refresh",workBookName,automation,row);

//                try {
//                    String username = ConfigProperties.getProperty("role.Username");
//                    String password = ConfigProperties.getProperty("role.Password");
//                    String connection = ConfigProperties.getProperty("role.Connection");
//
//                    RoleAndEnvironmentSetup.credentialsLogin(automation, username, password, connection);
//                }
//                finally {
                    logger.info("Clicked refresh done ");
//                }
                break;
            case "SelectRegion":
                logger.info("Select region start ");
                ActionExecutors.selectRegion(automation, row);
                logger.info("Select region end ");
                break;
            case "SyncAndCloseWorkbook":
                logger.info("Sync started ");
                ActionExecutors.waitForWindowWithTitle(FilenameUtils.getBaseName(workBookName) + " - Excel");
                //TODO: Look at processes currently running
                Thread.sleep(65000);
                logger.info("Sync ended and close started");
                Utils.closeProcess(automation.getDesktopWindow(FilenameUtils.getBaseName(workBookName) + " - Excel").getNativeWindowHandle());
                logger.info("waiting for close ");
                ActionExecutors.waitForWindowWithTitle("Microsoft Excel");
                logger.info("clicking on save");
                automation.getDesktopWindow("Microsoft Excel").getButton("Save").click();
                logger.info("workbook saved");
                Thread.sleep(25000);
                TestCaseFlowHandler.writeToTestCaseFlowHandlerReport(row, "Sync Complete", "Complete");
                break;
            case "VerifyAreaSelected":
                logger.info("verify area selected start");
                ActionVerifiers.verifyAreaSelected(row, workBookName);
                logger.info("verify area selected end");
                break;
            case "VerifyConnection":
                logger.info("verify connection start");
                ActionVerifiers.verifyConnection(row,workBookName);
                logger.info("verify connection end");
                break;
            case "VerifyPeriods":
                ActionVerifiers.verifyPeriods(row, workBookName);
                break;
            case "InputDataInCell":
                logger.info("Workbook opened started for " +  workBookName);
                try{
                    if(automation.getDesktopWindow(FilenameUtils.getBaseName(workBookName) + " - Excel")==null){
                        ActionExecutors.openWorkBookInExecution(workBookName, automation, row);
                    }
                }
                catch (ItemNotFoundException e){
                    Desktop.getDesktop().open(new File(workBookName));
                    ActionExecutors.waitForWindowWithTitle(FilenameUtils.getBaseName(workBookName) + " - Excel");
                    SheetHandler.runMacro("RUN_ACR_MOM_NUS",workBookName,automation,row);
                    //TODO - Below Needs to be uncommented once HTML Report object has been passed
                    //InputActionValidation.clickRefresh();
                }
                logger.info("Workbook opened is " +  workBookName);
                Thread.sleep(2000);
                ACR_MOM_Handler.inputDataInCell(row, workBookName);
                Thread.sleep(5000);
                SheetHandler.runMacro("RUN_ACR_MOM_NUS", workBookName, automation, row);
                Thread.sleep(4000);
                break;
//            case "InputData.ACR_MOM":
//                 logger.info("Entering data in the ACR_MOM sheet");
////                 ACR_MOM_Handler.inputDataACRMOM(row,workBookName);
//            case "VerifyACR_MOM":
//                 logger.info("Begin Calculations for ACR and MOM on ACR_MOM Workbook");
//                 ACR_MOM_Handler.verifyACR_MOM(row, workBookName);
//                logger.info("End Calculations for ACR and MOM on ACR_MOM Workbook");
//                break;
            case "VerifyACR_MOM":
                logger.info("Begin Calculations for ACR and MOM on ACR_MOM Workbook");
                FormulaHandler.calculateACR_MOM (row, workBookName, extent );
                logger.info("End Calculations for ACR and MOM on ACR_MOM Workbook");
                break;
            case "WriteToCell":
                logger.info("Begin Write to Cell");
                JacobBase.jacobWriteToCell(row, extent);
                logger.info("End Write to Cell");
                break;
            case "WriteToCellAndSave":
                logger.info("Begin Write to Cell and Save WorkBook");
                JacobBase.jacobWriteToCellAndSave(automation, row, extent);
                logger.info("End Write to Cell and Save WorkBook");
                break;
            case "SelectVariant":
                logger.info("Begin Select Variant");
                ActionExecutors.selectVariant(automation, row, extent);
                logger.info("End Select Variant");
                break;
            default:
        }
    }
}
