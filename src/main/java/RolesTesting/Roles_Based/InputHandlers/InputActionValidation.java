package RolesTesting.Roles_Based.InputHandlers;

import FunctionalTesting.ExecutionHandlers.ActionExecutors;
import RolesTesting.Roles_Based.ApplicationStatus.WindowsSyncCheck;
import RolesTesting.Roles_Based.PoJos.RBTReport;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import RolesTesting.Roles_Based.ReportHandlers.CapturingScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Variant;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationListItem;
import mmarquee.automation.controls.AutomationWindow;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.apache.commons.io.FilenameUtils;
//import org.apache.tools.ant.input.InputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.util.CellAddress;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler.getTableFromSheet;
import static RolesTesting.Roles_Based.InputHandlers.RoleAndEnvironmentSetup.checkRegion;

public class InputActionValidation {
    private static Logger logger = LoggerFactory.getLogger(InputActionValidation.class);
    WindowsSyncCheck windowsSyncCheck = new WindowsSyncCheck();


    public static boolean checkForRefresh(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws PatternNotFoundException, AutomationException, IOException, AWTException {
//        logger.info("Check for refresh has started");
        if (roleBasedRow.getRefresh().split(";")[0].equals("Yes")){
            try {
                String workBookName = roleBasedRow.getRole_Workbook();
                WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, FilenameUtils.getBaseName(workBookName) + ".xlsm - Excel");
                ActionExecutors.openPromptsWindow(automation, workBookName + ".xlsm - Excel");
                String variantName = roleBasedRow.getRefresh().split(";")[1];
                ActionExecutors.selectVariantTest(automation, variantName);
                //clickRefresh(roleBasedRow.getRefresh().split(";")[2],test);
                // launchMessages(automation,roleBasedRow,test);
                // checkForRegion(automation,roleBasedRow,test);
                //selectRegion(automation, roleBasedRow,test);
//                logger.info("Check for refresh has been completed");
                return launchMessages(automation, roleBasedRow,test);
            }
            catch (Exception e){
//                logger.info("Check for refresh has been completed");
                return launchMessages(automation, roleBasedRow,test);
            }
        }
        else {
            try {
                clickRefresh(test);
                launchMessages(automation,roleBasedRow,test);
//                logger.info("Check for refresh has been completed");
                return false;
            }
            catch (Exception e){
//                logger.info("Check for refresh has been completed");
                return !launchMessages(automation, roleBasedRow,test);
            }
        }
    }

    public static boolean checkForRegion(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws PatternNotFoundException, AutomationException {
        try {
            checkRegion(automation, roleBasedRow,test);
            logger.info("Region "+roleBasedRow.getRefresh().split(";")[1]+" has been successfully selected");
            return true;
        }
        catch(Exception e){
            logger.info("Region "+roleBasedRow.getRefresh().split(";")[1
                    ]+" could not be selected");
            return false;

        }
    }
    public static boolean checkListOfRegions(UIAutomation automation, RoleBasedRow roleBasedRow) throws PatternNotFoundException, AutomationException {
        String[] regions = roleBasedRow.getListOfRegions().split(";");
        String[] regionsInPrompt = automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").toString().split(",");
        return true;
    }


    public static boolean checkForCalculate(UIAutomation automation, String macroName, RoleBasedRow roleBasedRow, int sheetIndex,ExtentTest test) throws InterruptedException, AutomationException, IOException, AWTException, InvalidFormatException, PatternNotFoundException {
//        logger.info("Check for calculate has started");

//        String sheetName = roleBasedRow.getCalculate().split(";")[1];
        // if(roleBasedRow.getCalculate().split(";")[0].equals("Yes")){
        try{
                /*if(roleBasedRow.getCalculate()
                        .split(";").length > 2){*/
                   /* if(roleBasedRow.getCalculate()
                            .split(";")[1] != null){
                    String[] locationsAndValues = roleBasedRow.getCalculate()
                            .split(";")[2]
                            .split(",");
                    for(String locationAndValue: locationsAndValues){
                        String location = locationAndValue.split("::")[0];
                        String value  = locationAndValue.split("::")[1];
                        writeToCell(roleBasedRow.getRole_Workbook(), sheetIndex, location, value);

                    }
                }*/
            runMacro(macroName, roleBasedRow.getRole_Workbook());
            test.log(Status.INFO, MarkupHelper.createLabel("Run Calculate functionality on ACR_MOM Worksheet", ExtentColor.BLUE));
//                logger.info("Check for calculate has been completed");
            return launchMessages(automation, roleBasedRow,test);
        }
        catch (Exception e){
//                logger.info("Check for calculate has been completed");
            return false;
        }
        //}
        /*else {
            try{
                runMacro("", roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Run Calculate functionality on ACR_MOM Worksheet", ExtentColor.BLUE));
//                logger.info("Check for calculate has been completed");
                return !launchMessages(automation, roleBasedRow,test);
            }
            catch (Exception e){
//                logger.info("Check for calculate has been completed");
                return true;
            }

        }*/
    }

    public static boolean checkForSubmit(UIAutomation automation, String macroName, RoleBasedRow roleBasedRow,ExtentTest test) throws AutomationException, InterruptedException, InvalidFormatException, IOException, AWTException, PatternNotFoundException {
//        logger.info("Check for submit has started");
        // if(roleBasedRow.getSubmit().equals("Yes")){
        try{
            runMacro(macroName, roleBasedRow.getRole_Workbook());
            test.log(Status.INFO, MarkupHelper.createLabel("Run Submit functionality", ExtentColor.BLUE));
//                logger.info("Check for submit has been completed");
            return launchMessages(automation, roleBasedRow,test);
        }
        catch (Exception e){
//                logger.info("Check for submit has been completed");
            return false;
        }

        /*}
        else {
            try{
                runMacro("RUN_FCST_STB_OB", roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Run Submit functionality", ExtentColor.BLUE));
//                logger.info("Check for submit has been completed");
                return !launchMessages(automation, roleBasedRow,test);
            }
            catch (Exception e){
//                logger.info("Check for submit has been completed");
                return true;
            }
        }*/
    }
    //
    public static boolean checkForSave(UIAutomation automation, String macroName, RoleBasedRow roleBasedRow,ExtentTest test){
        //if(roleBasedRow.getSubmit().equals("Yes")){
        try{
            runMacro(macroName, roleBasedRow.getRole_Workbook());
            test.log(Status.INFO, MarkupHelper.createLabel("Run Save functionality", ExtentColor.BLUE));
            return launchMessages(automation, roleBasedRow,test);
            //return true;
        }
        catch (Exception e){
            return false;
        }
/*
        }
        else {
            try{
                runMacro("Save", roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Run Save functionality", ExtentColor.BLUE));
                return !launchMessages(automation, roleBasedRow,test);
                //return true;
            }
            catch (Exception e){
                return true;
            }
        }*/
    }
//
//    public static boolean checkInputAction(RoleBasedRow roleBasedRow){
//
//    }

    public static void runMacro(String macroName, String workBookName) throws AutomationException, InterruptedException, IOException, AWTException, InvalidFormatException, PatternNotFoundException {
//        logger.info("Run macro has started");
        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, FilenameUtils.getBaseName(workBookName) + ".xlsm - Excel");
        ComThread.InitSTA();
        ActiveXComponent xl = ActiveXComponent.connectToActiveInstance("Excel.Application");
        //ActiveXComponent workbooks = xl.invokeGetComponent("workbooks");
        xl.invoke("Run",new Variant(macroName));
        //ActionExecutors.selectRegion(automation, row);
        xl.safeRelease();
        ComThread.Release();
//        logger.info("Run Macro has been completed");
    }

    public static void clickRefresh(String refreshCords,ExtentTest test) throws InterruptedException, AWTException, IOException, AutomationException {
//        logger.info("Click refresh has started");
//        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Image, "Refresh");
        Thread.sleep(5000);
        Robot robot = new Robot();
        robot.mouseMove(Integer.parseInt(refreshCords.split(",")[0]), Integer.parseInt(refreshCords.split(",")[1]));
//        robot.mouseMove(949, 365);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Refresh in Instructions Tab" , ExtentColor.BLUE));
//        logger.info("Click refresh has been completed");
    }

    public static void clickRefresh(ExtentTest test) throws InterruptedException, AWTException, IOException, AutomationException {
//        logger.info("Click refresh has started");
//        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Image, "Refresh");
        Thread.sleep(5000);
        Robot robot = new Robot();
//        robot.mouseMove(Integer.parseInt(refreshCords.split(",")[0]), Integer.parseInt(refreshCords.split(",")[1]));
        robot.mouseMove(949, 365);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
//        logger.info("Click refresh has been completed");
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Refresh in Instructions Tab" , ExtentColor.BLUE));
    }

    public static void writeToCell(String workBookName, int sheetIndex, String address, String value) throws IOException, InterruptedException, AutomationException {
//        logger.info("Write to cell has started");
        if (address != null && value != null){
            WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, FilenameUtils.getBaseName(workBookName) + " - Excel");
            ComThread.InitSTA();
            ActiveXComponent xl = ActiveXComponent.connectToActiveInstance("Excel.Application");
            ActiveXComponent workbooks = xl.invokeGetComponent("workbooks");
            ActiveXComponent workbook = workbooks.invokeGetComponent("open",new Variant(workBookName));
            ActiveXComponent sheets = workbook.invokeGetComponent("sheets",new Variant(sheetIndex));
            CellAddress cellAddress = new CellAddress(address);
            sheets.invokeGetComponent("cells",
                    new Variant(cellAddress.getRow()+1),
                    new Variant(cellAddress.getColumn()+1)).setProperty("value", value);
            xl.safeRelease();
            ComThread.Release();
//            logger.info("Write to cell has been completed");
        }
    }

    public static boolean launchMessages(UIAutomation automation, RoleBasedRow roleBasedRow, ExtentTest test) throws PatternNotFoundException, AutomationException, IOException, AWTException {
//        logger.info("Launch messages has started");
        AutomationWindow window = automation.getDesktopWindow(roleBasedRow.getRole_Workbook()+".xlsm - Excel");
        window.getTab("Ribbon Tabs").selectTabPage("Analysis");
        test.log(Status.INFO, MarkupHelper.createLabel("Click on Analysis Tab", ExtentColor.BLUE));
        List<AutomationListItem> listOfMessages;
        try{
            window.getButton("Messages").click();
            test.log(Status.INFO, MarkupHelper.createLabel("Click on Messages", ExtentColor.BLUE));
            AutomationWindow window1 = automation.getDesktopWindow("Messages");
            listOfMessages = window1.getListByAutomationId("mErrorsListView").getItems();
            Thread.sleep(5000);
//            try {
//                window.getTitleBar().
//            }
            //window1.getPanelByAutomationId("mPanelButtons").getButtonByAutomationId("mOkButton").click();
            window1.getButtonByAutomationId("mOkButton").click();
        } catch (Exception e){
            logger.info("Messages are disabled");
            test.log(Status.WARNING, MarkupHelper.createLabel("Messages are Disabled", ExtentColor.BLUE));
//            logger.info("Launch messages has been completed");
            return true;
        }
        int error = 0;

        for (AutomationListItem listOfMessage : listOfMessages) {
            String item = listOfMessage.getName().toLowerCase();
            String userName = System.getProperty("user.name");
            String filePath = "C:\\Users\\"+ userName +"\\Documents\\BTAF Framework\\Error Message\\";
            Table errorData = getTableFromSheet(filePath+"ErrorMessages.xlsx", "0");
            boolean hasMessages = automation.getDesktop().getChildren(true).stream().anyMatch(e -> {
                try {
                    return e.getName().equals("Further messages...");
                } catch (AutomationException automationException) {
                    automationException.printStackTrace();
                }
                return false;
            });
            for(int i=0; i< errorData.rowCount(); i++) {
                Row row = errorData.row(i);
                String errorMsg = row.getString("Message");
                if(item.startsWith(errorMsg) || item.matches(errorMsg)) {
                    String errorType = row.getString("Type");
                    if(errorType.equals("Warning")) {
                        test.log(Status.WARNING, "Warning Found in Messages Window",MediaEntityBuilder.createScreenCaptureFromPath
                                (CapturingScreenshot.capture("Warnings")).build());
                    } else if (errorType.equals("Failure")) {
                        logger.info("Error Message: "+errorMsg);
                        error = 1;
                    }
                }
            }

            /*//item = "You do not have the authorization!!!";
            if (item.matches(".*not have the authorization.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*not have sufficient authorization.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*ended with errors.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*not authorized.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*error when reading.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*errors occurred.*")) {
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*cannot be null.*")){
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }
            if (item.matches(".*could not access.*")){
                logger.info("Error Message: "+listOfMessage.getName());
                error = 1;
            }*/

        }
        if (error == 1){
            test.log(Status.FAIL, "Error Found in Messages Window",MediaEntityBuilder.createScreenCaptureFromPath
                    (CapturingScreenshot.capture("ErrorFailure")).build());
            return true;
        }
        test.log(Status.INFO, "No Error Found in Messages Window",MediaEntityBuilder.createScreenCaptureFromPath
                (CapturingScreenshot.capture("NoError")).build());
        return true;
    }

    public static List<String> getMacroType(int i, RoleBasedRow roleBasedRow) {
        List<String> macroData = new ArrayList<>();
        switch (i) {
            case 10:
                if(roleBasedRow.getMacro1() != null || roleBasedRow.getMacro1().equals("")) {
                    macroData.add(roleBasedRow.getMacro1().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro1().split(";")[1]);
                }
                break;
            case 11:
                if(roleBasedRow.getMacro2() != null || roleBasedRow.getMacro2().equals("")) {
                    macroData.add(roleBasedRow.getMacro2().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro2().split(";")[1]);
                }
                break;
            case 12:
                if(roleBasedRow.getMacro3() != null || roleBasedRow.getMacro3().equals("")) {
                    macroData.add(roleBasedRow.getMacro3().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro3().split(";")[1]);
                }
                break;
            case 13:
                if(roleBasedRow.getMacro4() != null || roleBasedRow.getMacro4().equals("")) {
                    macroData.add(roleBasedRow.getMacro4().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro4().split(";")[1]);
                }
                break;
            case 14:
                if(roleBasedRow.getMacro5() != null || roleBasedRow.getMacro5().equals("")) {
                    macroData.add(roleBasedRow.getMacro5().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro5().split(";")[1]);
                }
                break;
            case 15:
                if(roleBasedRow.getMacro6() != null || roleBasedRow.getMacro6().equals("")) {
                    macroData.add(roleBasedRow.getMacro6().split(";")[0]);
                    macroData.add(roleBasedRow.getMacro6().split(";")[1]);
                }
                break;
        }
        return macroData;
    }

    public static RBTReport validateInputActions(UIAutomation automation, RoleBasedRow roleBasedRow, int sheetIndex, ExtentTest test) throws InterruptedException, AWTException, AutomationException, IOException, PatternNotFoundException, InvalidFormatException {
//        logger.info("Validate input actions has started");
        boolean result = false;
        String refreshJudgement = roleBasedRow.getRefresh();
        if(!refreshJudgement.equals("")){
            logger.info("Checking for refresh in Workbook " + roleBasedRow.getRole_Workbook());
            test.log(Status.INFO, MarkupHelper.createLabel("Validating Refresh functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
            result = checkForRefresh(automation, roleBasedRow,test);
        }
        for(int i=10; i<17; i++) {
            List<String> macroData = getMacroType(i, roleBasedRow);
            if (macroData != null && macroData.size() > 0) {
                String macroType = macroData.get(0);
                String macroName = macroData.get(1);
                if ((macroType != null && !macroType.isEmpty()) && (macroName != null && macroType.isEmpty())) {
                    logger.info("Performing " + macroType + "in sheet ");
                    test.log(Status.INFO, MarkupHelper.createLabel("Performing " + macroType + "in sheet " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                    if (result != false) {
                        runMacro(macroName, roleBasedRow.getRole_Workbook());
                        result = launchMessages(automation, roleBasedRow, test);
                    }
                }
            }
        }
        /*String calculateJudgement = roleBasedRow.getCalculate().split(";")[0];
        String saveJudgement = roleBasedRow.getSave();
        String submitJudgement = roleBasedRow.getSubmit();


        while(!refreshJudgement.equals("") || !calculateJudgement.equals("") || !saveJudgement.equals("") || !submitJudgement.equals("")){
            if(!refreshJudgement.equals("")){
                logger.info("Checking for refresh in Workbook " + roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Validating Refresh functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                result = checkForRefresh(automation, roleBasedRow,test);
                refreshJudgement = "";
            }
            else if(!calculateJudgement.equals("")){
                logger.info("Checking for calculate in sheet " + roleBasedRow.getCalculate().split(";")[1]);
                test.log(Status.INFO, MarkupHelper.createLabel("Validating Calculate functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                if (result != false){
                    result = checkForCalculate(automation, roleBasedRow, sheetIndex,test);

                }
//                result = checkForCalculate(automation, roleBasedRow, sheetIndex);
                calculateJudgement = "";
            }
            else if(!saveJudgement.equals("")){
                logger.info("Clicking on save in Workbook " + roleBasedRow.getRole_Workbook());
                if (result != false){
                    result = checkForSave(automation, roleBasedRow,test);
                }
//                result = checkForSave(automation, roleBasedRow);
                saveJudgement = "";
            }
            else if(!submitJudgement.equals("")){
                logger.info("Clicking on Submit in Workbook " + roleBasedRow.getRole_Workbook());
                if (result != false){
                    result = checkForSubmit(automation, roleBasedRow,test);
                }
//                result = checkForSubmit(automation, roleBasedRow);
                submitJudgement = "";
            }
        }*/
//        logger.info("Validate input actions has been completed");
        return new RBTReport(result, null, null);
    }

   /* public static RBTReport validateRefreshActions(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws InterruptedException, AWTException, AutomationException, IOException, PatternNotFoundException, InvalidFormatException {
//        logger.info("Validate input actions has started");
        boolean result = false;
        String refreshJudgement = roleBasedRow.getRefresh();
        String saveJudgement = roleBasedRow.getSave();
        String submitJudgement = roleBasedRow.getSubmit();

        while(!refreshJudgement.equals("") || !saveJudgement.equals("") || !submitJudgement.equals("")){
            if(!refreshJudgement.equals("")){
                logger.info("Checking for refresh in Workbook " + roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Validating Refresh functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                result = checkForRefresh(automation, roleBasedRow,test);
                refreshJudgement = "";
            }
            else if(!saveJudgement.equals("")){
                logger.info("Clicking on save in Workbook " + roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Validating Save functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                if (result != false){
                    result = checkForSave(automation, roleBasedRow,test);

                }
//                result = checkForSave(automation, roleBasedRow);
                saveJudgement = "";
            }
            else if(!submitJudgement.equals("")){
                logger.info("Clicking on Submit in Workbook " + roleBasedRow.getRole_Workbook());
                test.log(Status.INFO, MarkupHelper.createLabel("Validating Submit functionality in " + roleBasedRow.getRole_Workbook(), ExtentColor.BLUE));
                if (result != false){
                    result = checkForSubmit(automation, roleBasedRow,test);

                }
                //result = checkForSubmit(automation, roleBasedRow);
                submitJudgement = "";
            }
        }
//        logger.info("Validate input actions has been completed");
        return new RBTReport(result, null, null);
    }*/
}
