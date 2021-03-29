package RolesTesting.ExecutionHandlers.RoleBasedHandlers;

import RolesTesting.Roles_Based.Excel2Json.Excel2JsonConverter;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import RolesTesting.ExecutionHandlers.AutomationExecutionHandlers.ActionExecutors;
import RolesTesting.Roles_Based.FolderHandlers.RoleFolderValidation;
import RolesTesting.Roles_Based.InputHandlers.InputActionValidation;
import RolesTesting.Roles_Based.InputHandlers.RoleAndEnvironmentSetup;
import RolesTesting.Roles_Based.PoJos.RBTReport;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import RolesTesting.Roles_Based.ReportHandlers.CapturingScreenshot;
import RolesTesting.Util.ConfigProperties;
import RolesTesting.Roles_Based.WorkbookHandlers.RoleWorkbookValidation;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;

public class RoleBasedDriver {
    private static final Logger logger = LoggerFactory.getLogger(RoleBasedDriver.class);
    private static ExtentTest test;
    public static void performRoleBasedValidation(RoleBasedRow roleBasedRow, String whichValidation, ExtentReports extent) throws Exception {
//        logger.info("Started execution for Test " + roleBasedRow.getTest_Case_Name() + " with description " + roleBasedRow.getTest_Case_Description());
        UIAutomation automation = UIAutomation.getInstance();
        RBTReport report;
        if(roleBasedRow.getTest_Run_Flag().equals("Yes")){
            test = extent.createTest(roleBasedRow.getTest_Case_Name());
            logger.info("-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-");
            logger.info("Started execution for Test " + roleBasedRow.getTest_Case_Name() +
                    " with description " + roleBasedRow.getTest_Case_Description() +
                    " with run flag " + roleBasedRow.getTest_Run_Flag());
            Thread.sleep(3000);
            ActionExecutors.clickOnConnection(automation,
                    roleBasedRow.getConnection(),
                    roleBasedRow.getUsername(),
                    roleBasedRow.getPassword(),test);
            String status = null;

            switch (whichValidation){
                case "RBT_RoleFolder":
                    test.log(Status.INFO, MarkupHelper.createLabel("***** Beginning Role Folder Validations *****", ExtentColor.BLUE));
                    RoleAndEnvironmentSetup.usernameLogin(automation, roleBasedRow,test);
                    report = RoleFolderValidation.validateRoleFolders(automation, roleBasedRow,test);
                    status = report.isStatus() ? "Passed" : "Failed";
                    logger.info("Test " + roleBasedRow.getTest_Case_Name() + " " + status);
                    test.log((report.isStatus() ? Status.PASS : Status.FAIL), MarkupHelper.createLabel("Test " + roleBasedRow.getTest_Case_Name() + " " + status,(report.isStatus() ? ExtentColor.GREEN : ExtentColor.RED)));
                 if (status == "Failed"){
                       test.log(Status.WARNING,"Failure Screenshot", MediaEntityBuilder.createScreenCaptureFromPath
                             (CapturingScreenshot.capture("TestCaseFailure")).build());
                 }
//                    System.out.println("Status - " + report.isStatus() + " " + roleBasedRow.getTest_Case_Name());
                    Utils.closeProcess(automation.getDesktopWindow("Open Document").getNativeWindowHandle());
                    Utils.closeProcess(automation.getDesktopWindow("Book1 - Excel").getNativeWindowHandle());
                    break;
                case "RBT_RoleWorkbook":
                    test.log(Status.INFO, MarkupHelper.createLabel("***** Beginning Role Workbook Validations *****", ExtentColor.BLUE));
                    RoleAndEnvironmentSetup.usernameLogin(automation, roleBasedRow,test);
                    report = RoleWorkbookValidation.validateRoleWorkbooksForFolder(automation, roleBasedRow,test);
                    status = report.isStatus() ? "Passed" : "Failed";
                    logger.info("Test " + roleBasedRow.getTest_Case_Name() + " " + status);
                    test.log((report.isStatus() ? Status.PASS : Status.FAIL), MarkupHelper.createLabel("Test " + roleBasedRow.getTest_Case_Name() + " " + status,(report.isStatus() ? ExtentColor.GREEN : ExtentColor.RED)));
                    if (status == "Failed"){
                        test.log(Status.WARNING,"Failure Screenshot", MediaEntityBuilder.createScreenCaptureFromPath
                                (CapturingScreenshot.capture("TestCaseFailure")).build());
                    }
                    Utils.closeProcess(automation.getDesktopWindow("Open Document").getNativeWindowHandle());
                    Utils.closeProcess(automation.getDesktopWindow("Book1 - Excel").getNativeWindowHandle());
                    break;
                case "RBT":
                    test.log(Status.INFO, MarkupHelper.createLabel("***** Beginning Workbook Functionality Validations *****", ExtentColor.BLUE));
                    RoleAndEnvironmentSetup.usernameLogin(automation, roleBasedRow,test);
                    RoleAndEnvironmentSetup.clickOnRoleFolders(automation, roleBasedRow,test);
//                    TODO: CHANGE TO DYNAMIC WAIT
                    Thread.sleep(5000);
                    String userName = System.getProperty("user.name");
                    Optional<Path> lastFilePath = Files.walk(Paths.get("C:\\Users\\" + userName +"\\AppData\\Local\\Temp\\sapaocache"))
                            .filter(Files::isRegularFile)
                            .max(Comparator.comparingLong(value -> value.toFile().lastModified()));
                    //assert validateSheetPresence(roleBasedRow, lastFilePath);
                    //if (roleBasedRow.getCalculate().indexOf("Yes") != -1) {
                    assert validateSheetPresence(roleBasedRow, lastFilePath);
                    int sheetIndex = SheetHandler.getNameAndIndexMap(lastFilePath.get().toAbsolutePath().toString().replace("~$", ""))
                            .get(roleBasedRow.getCalculate()
                                    .split(";")[1]);
                    report = InputActionValidation.validateInputActions(automation, roleBasedRow, sheetIndex,test);
                    status = report.isStatus() ? "Passed" : "Failed";
                    logger.info("Test " + roleBasedRow.getTest_Case_Name() + " " + status);
                    /*} else {
                        logger.info("Calculate is a no");
                        report = InputActionValidation.validateRefreshActions(automation, roleBasedRow,test);
                        status = report.isStatus() ? "Passed" : "Failed";
                        logger.info("Test " + roleBasedRow.getTest_Case_Name() + " " + status);
                    }*/
//                    int sheetIndex = SheetHandler.getNameAndIndexMap(lastFilePath.get().toAbsolutePath().toString().replace("~$", ""))
//                            .get(roleBasedRow.getCalculate()
//                                    .split(";")[1]);
//                    report = InputActionValidation.validateInputActions(automation, roleBasedRow, sheetIndex);
//                    status = report.isStatus() ? "Passed" : "Failed";
//                    logger.info("Test " + roleBasedRow.getTest_Case_Name() + " " + "Passed");
                    // System.out.println("Status - " + report.isStatus() + " " + roleBasedRow.getTest_Case_Name());
                    Utils.closeProcess(automation.getDesktopWindow(getWindowName(roleBasedRow)).getNativeWindowHandle());

                    ActionExecutors.waitForWindowWithTitle("Microsoft Excel");
                    if(automation.getDesktopWindow("Microsoft Excel").getButton("OK").isEnabled()){
                        automation.getDesktopWindow("Microsoft Excel").getButton("OK").click();
                        Utils.closeProcess(automation.getDesktopWindow(getWindowName(roleBasedRow)).getNativeWindowHandle());
                    }
                    Thread.sleep(3000);
                    try{
                        automation.getDesktopWindow("Analysis").getButton("No").click();
                    }
                    catch (Exception i){
                        break;
                    }
                    Thread.sleep(5000);
                    try {
                        automation.getDesktopWindow("Microsoft Excel").getButton("Don't Save").click();
                    }
                    catch (Exception e){
                        break;
                    }
                    Utils.closeProcess(automation.getDesktopWindow(getWindowName(roleBasedRow)).getNativeWindowHandle());
                    break;
            }
        }

    }

    private static boolean validateSheetPresence(RoleBasedRow roleBasedRow, Optional<Path> lastFilePath){
        try{
            SheetHandler.getNameAndIndexMap(lastFilePath.get().toAbsolutePath().toString().replace("~$", ""))
                    .get(roleBasedRow.getCalculate()
                            .split(";")[1]);
            return true;
        }
        catch (Exception e){
            logger.info("Test " + roleBasedRow.getTest_Case_Name() + " Failed " + roleBasedRow.getCalculate()
                    .split(";")[1] + " is NOT in this role");
            return false;
        }
    }

    private static String getWindowName(RoleBasedRow roleBasedRow){
        String extension = roleBasedRow.getRole_Workbook().contains("Report") ? ".xlsm - Excel" : ".xlsm - Excel";
        return roleBasedRow.getRole_Workbook() + extension;
    }

    public static void executeEachRowInTestSheet(File workBook, String sheetName, ExtentReports extentReports) throws Exception {
        for (RoleBasedRow roleBasedRow : Excel2JsonConverter.getRoleBasedRows(workBook, sheetName)) {
            performRoleBasedValidation(roleBasedRow, sheetName, extentReports);
        }
    }
}
