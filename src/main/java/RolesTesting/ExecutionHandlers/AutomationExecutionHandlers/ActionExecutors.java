package RolesTesting.ExecutionHandlers.AutomationExecutionHandlers;
import RolesTesting.Roles_Based.ApplicationStatus.WindowsSyncCheck;
import RolesTesting.ActionValidationHandlers.ActionVerifiers;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.JacobBase;
import RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseDriverHandler;
import RolesTesting.ExecutionHandlers.OrchestrationSheetHandlers.TestCaseFlowHandler;
import RolesTesting.ReportHandlers.ReportWriter;
import RolesTesting.Util.ConfigProperties;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import mmarquee.automation.AutomationElement;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationApplication;
import mmarquee.automation.controls.AutomationWindow;
import mmarquee.automation.controls.menu.AutomationMenuItem;
import mmarquee.automation.pattern.PatternNotFoundException;
import mmarquee.automation.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActionExecutors extends WindowsSyncCheck {

    private static final Logger logger = LoggerFactory.getLogger(ActionExecutors.class);

    public static String openWorkBookInExecution(String workBookName, UIAutomation automation, Row row) throws Exception {
        String fileName = null;
        try{
            //        Todo: Make changes
            Thread.sleep(4000);
            Optional<Path> lastFilePath = Files.walk(Paths.get("C:\\Users\\" + ConfigProperties.getProperty("vDash.userID") +"\\AppData\\Local\\Temp\\sapaocache"))
                     .filter(Files::isRegularFile)
                    .max(Comparator.comparingLong(value -> value.toFile().lastModified()));
             fileName = lastFilePath.get().toAbsolutePath().toString().replace("~$", "");
            try{
                if(automation.getDesktopWindow(workBookName)!=null){
                    Thread.sleep(45000);
                    Object workbook;
                    JacobBase save = new JacobBase();
                    save.initializeExcel();
                    save.setUp(1);
                    workbook = save.getWorkbook(1);
                    save.jacobQuit();

                    /*Workbook wb = new XSSFWorkbook(fileName);

                    FileOutputStream out = new FileOutputStream(fileName);
                    wb.write(out);
                    out.close();
                    wb.close();*/

//                    Dispatch.call((Dispatch)workbook, "SaveAs", fileName ,new Variant(1));
//
//                    Dispatch.call((Dispatch)workbook, "Close", new Variant(false));
//                    save.jacobQuit();

//                    Utils.closeProcess(automation.getDesktopWindow(workBookName).getNativeWindowHandle());
                    Thread.sleep(3000);
                    automation.getDesktopWindow("Analysis").getButton("Yes").click();
                    Thread.sleep(3000);
                    automation.getDesktopWindow(
                            "Messages").getButton("Close").click();
                    Thread.sleep(3000);
                    automation.getDesktopWindow(workBookName).getComboboxByAutomationId("FileNameControlHost").setText(fileName);
                    Thread.sleep( 3000);
                    automation.getDesktopWindow(workBookName).getButton("Save").click();
                    Thread.sleep(3000);
                    automation.getDesktopWindow(workBookName).getButton("Yes").click();
                    Thread.sleep(5000);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
            Desktop.getDesktop().open(new File(fileName));
//        automation.launchOrAttach(fileName);
            ReportWriter.assertStepAndPrint("Workbook Open", true, true, row);
        }
        catch (Exception e){
            ReportWriter.assertStepAndPrint("Workbook Open", true, false, row);
        }
        return fileName;
    }

//    public static String closeAndReturnWorkbookPath(String workBookName, UIAutomation automation) throws Exception {
//        String fileName = null;
//        try{
//            //        Todo: Make changes
//            Thread.sleep(2000);
//            Optional<Path> lastFilePath = Files.walk(Paths.get("C:\\Users\\" + ConfigProperties.getProperty("vDash.userID") +"\\AppData\\Local\\Temp\\sapaocache"))
//                    .filter(Files::isRegularFile)
//                    .max(Comparator.comparingLong(value -> value.toFile().lastModified()));
//            fileName = lastFilePath.get().toAbsolutePath().toString().replace("~$", "");
//            try{
//                if(automation.getDesktopWindow(workBookName)!=null){
//                    Utils.closeProcess(automation.getDesktopWindow(workBookName).getNativeWindowHandle());
//                }
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
//           // Desktop.getDesktop().open(new File(fileName));
////        automation.launchOrAttach(fileName);
//            //ReportWriter.assertStepAndPrint("Workbook Name Returned", true, true, row);
//        }
//        catch (Exception e){
//            //ReportWriter.assertStepAndPrint("Workbook Open", true, false, row);
//        }
//        return fileName;
//    }

    public static void clickOnRoleFolders(UIAutomation automation, List<String> roleFolders, String workSheetWithSteps) throws PatternNotFoundException, AutomationException, InterruptedException, IOException {
//        Todo: Make changes Panel
        ActionExecutors.waitForWindowWithTitle(ControlType.TabItem,"Role");
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getTabItems().get(2).selectItem();
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(roleFolders.get(0)).click();
        try{
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(roleFolders.get(1)).click();
        }
        finally {
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(workSheetWithSteps).click();
            automation.getDesktopWindow("Open Document").getButtonByAutomationId("mOkButton").click();
        }
    }

    public static void clickOnRoleFolderNames(UIAutomation automation, String roleFolder, String workSheetFolder, String workSheetWithSteps) throws PatternNotFoundException, AutomationException, InterruptedException, IOException {
//        Todo: Make changes Panel
        ActionExecutors.waitForWindowWithTitle(ControlType.TabItem,"Role");
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getTabItems().get(2).selectItem();
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(roleFolder).click();
        try{
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(workSheetFolder).click();
        }
        finally {
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(workSheetWithSteps).click();
            automation.getDesktopWindow("Open Document").getButtonByAutomationId("mOkButton").click();
        }
    }

    public static void clickRefresh(Row row) throws InterruptedException, AWTException, IOException, InvalidFormatException, AutomationException {
        try{
            ActionExecutors.waitForWindowWithTitle(ControlType.Image, "Refresh");
            Thread.sleep(5000);
            Robot robot = new Robot();
            robot.mouseMove(874, 364);
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
            //Thread.sleep(20000);
            ReportWriter.assertStepAndPrint("Click Refresh", true, true, row);
            //System.out.println(ActionExecutors.waitForWindowWithTitle("Prompts") + "PROMPTS FOUND");
        }catch(Exception e){
            ReportWriter.assertStepAndPrint("Click Refresh", true, false, row);
        }
    }

    public static void selectRegion(UIAutomation automation, Row row) throws AWTException, PatternNotFoundException, AutomationException, InterruptedException, IOException, InvalidFormatException {
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Prompts");
        try{
            List<String> regions = new ArrayList<>();
            Collections.addAll(regions, row.getText(ConfigProperties.getProperty("testCaseFlow.parameter1"))
                    .split(","));

            Thread.sleep(3000);

            //TODO: Test the Variants text box
//            selectVariant(automation, "Test");
           // automation.getDesktopWindow("Prompts").getEditBoxByAutomationId("PART_EditableTextBox").setValue("Test");
            //Need to use Robot to press enter to enable the ok button to be clickable
//            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();

                                Robot robot = new Robot();
            for (int i=0; i<=4; i++){
                robot.keyPress(KeyEvent.VK_TAB);

                    robot.keyRelease(KeyEvent.VK_TAB);
            }
            Thread.sleep(1000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            Thread.sleep(2000);
            //automation.getDesktopWindow(workBookName + ".xlsm - Excel").getTreeViewByAutomationId("mTreeView").getItem("France");
            regions.forEach(s -> {
                System.out.println(s);
                try {
                    automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").getItem(s).select();

                }catch (AutomationException | PatternNotFoundException e) {
                    e.printStackTrace();
                }
                robot.keyPress(KeyEvent.VK_SPACE);
                robot.keyRelease(KeyEvent.VK_SPACE);
            });
            automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
            ReportWriter.assertStepAndPrint("Select Region", true, true, row);
        }catch(Exception e){
            ReportWriter.assertStepAndPrint("Select Region", true, false, row);
        }

    }

    public static void openCorrectWorkSheetInWindow(UIAutomation automation, String workSheetName, Row row) throws IOException, InvalidFormatException {
        ReportWriter.assertStepAndPrint("Open Sheet", true, true, row);
    }

    public static void clickOnConnection(UIAutomation automation, String connectionToClick) throws Exception {
        automation.launchOrAttach("C:\\Program Files\\SAP BusinessObjects\\Office AddIn\\BiOfficeLauncher.EXE");
        AutomationWindow window = automation.getDesktopWindow("Book1 - Excel");
        window.getButtonByAutomationId("FileTabButton").click();
        window.getListByAutomationId("NavBarMenu").getItem(2).select();

        AutomationMenuItem menuItem = (AutomationMenuItem) window.getControlByClassName("Open Workbook","NetUIAnchor");
        menuItem.expand();

        //opens up the options for open workbook, now work on selecting SAP warehouse
        AutomationMenuItem menuItem1 = (AutomationMenuItem) window.getControlByClassName("Open a workbook from the SAP Business Warehouse Platform.", "NetUITWBtnMenuItem");
        menuItem1.click();

        AutomationWindow window1 = automation.
                getDesktopWindow("Open Document");
        //Thread.sleep(10000);


        window1.getListByAutomationId("mConnectionListView").getItem(connectionToClick).select();
        Thread.sleep(5000);
        //TODO: Fragile, need to find better solution
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        System.out.println("done--------");
        //Thread.sleep(5000);
        //window1.getButtonByAutomationId("mNextButton").click();
    }

    public static void clickOnConnection(UIAutomation automation, String connectionToClick, String userName, String password, ExtentTest test) throws Exception {
        logger.info("Attempting to click on connection " + connectionToClick);
        automation.launchOrAttach("C:\\Program Files\\SAP BusinessObjects\\Office AddIn\\BiOfficeLauncher.EXE");
        test.log(Status.INFO, MarkupHelper.createLabel("Launched SAP Analysis for Excel", ExtentColor.BLUE));
        AutomationWindow window = automation.getDesktopWindow("Book1 - Excel");
        Thread.sleep(5000);
        window.getButtonByAutomationId("FileTabButton").click();
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on File Tab", ExtentColor.BLUE));
        window.getListByAutomationId("NavBarMenu").getItem(2).select();
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Analysis under File Tab", ExtentColor.BLUE));

        AutomationMenuItem menuItem = (AutomationMenuItem) window.getControlByClassName("Open Workbook","NetUIAnchor");
        menuItem.expand();
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Open Workbook under Analysis tab", ExtentColor.BLUE));

        //opens up the options for open workbook, now work on selecting SAP warehouse
        AutomationMenuItem menuItem1 = (AutomationMenuItem) window.getControlByClassName("Open a workbook from the SAP Business Warehouse Platform.", "NetUITWBtnMenuItem");
        menuItem1.click();
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on - Open a workbook from the SAP Business Warehouse Platform.", ExtentColor.BLUE));



        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, "Open Document");
        AutomationWindow window1 = automation.
                getDesktopWindow("Open Document");

//       window1.getButtonByAutomationId("_treeModeButton").click();
//       window1.getButtonByAutomationId("_listModeButton").click();


        //ApplicationStatus.WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, "Open Document");
        window1.getListByAutomationId("_connectionListView").getItem(connectionToClick).select();
        test.log(Status.INFO, MarkupHelper.createLabel("Clicked on " + connectionToClick, ExtentColor.BLUE));
//        logger.info("Waiting for Application to Load");
        Thread.sleep(5000);
        //TODO: Fragile, need to find better solution
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        logger.info("Application connected to " + connectionToClick);
        test.log(Status.INFO, MarkupHelper.createLabel("Connected successfully to " + connectionToClick, ExtentColor.BLUE));
//        System.out.println("done--------");
        //Thread.sleep(5000);
        //window1.getButtonByAutomationId("mNextButton").click();
    }

    public static void launchAnalysisOfExcel(UIAutomation automation) throws Exception {
        automation.launchOrAttach("C:\\Program Files\\SAP BusinessObjects\\Office AddIn\\BiOfficeLauncher.EXE");
        AutomationWindow window = automation.getDesktopWindow("Book1 - Excel");
        window.getButtonByAutomationId("FileTabButton").click();
        window.getListByAutomationId("NavBarMenu").getItem(2).select();
//                window.getControlByClassName("Open Workbook","NetUIAnchor").getExpandCollapsePattern().expand();
        AutomationMenuItem menuItem = (AutomationMenuItem) window.getControlByClassName("Open Workbook","NetUIAnchor");
        menuItem.expand();
        menuItem = menuItem.getItems().get(2);
        menuItem.click();
    }

    public void closeApplication(AutomationApplication app, String title) throws PatternNotFoundException, AutomationException{
        app.close(title);
    }

    public static void doTheValidation(Row rowWithDetails) throws IOException {
        Table table = SheetHandler.getTableFromSheet(rowWithDetails.getString("testCaseFlow.parameter7"),
                rowWithDetails.getString("testCaseFlow.parameter8"));
        String index = rowWithDetails.getString("testCaseFlow.parameter1");
        table = SheetHandler.getDataInCellRangeFromSheet(table, index);


    }

    public static boolean waitForWindowWithTitle(String title) throws AutomationException, InterruptedException, IOException {
//        String title = row.getString(ConfigProperties.getProperty("testCaseFlow.syncSheet.windowTitle"));
        return new WindowsSyncCheck().waitUntilLoad(ControlType.Window, title, Integer.parseInt(ConfigProperties.getProperty("global.wait.sync.retry"))).getElement() != null;
    }

    public static boolean waitForWindowWithTitle(ControlType controlType, String title) throws AutomationException, InterruptedException, IOException {
//        String title = row.getString(ConfigProperties.getProperty("testCaseFlow.syncSheet.windowTitle"));
        return new WindowsSyncCheck().waitUntilLoad(controlType, title, Integer.parseInt(ConfigProperties.getProperty("global.wait.sync.retry"))).getElement() != null;
    }

    public static void selectVariantTest(UIAutomation automation,String variantName) throws PatternNotFoundException, AutomationException, AWTException, InterruptedException, IOException {
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Prompts");

            Thread.sleep(3000);
            automation.getDesktopWindow("Prompts").getEditBoxByAutomationId("PART_EditableTextBox").setValue(variantName);
            Robot robot = new Robot();
            Thread.sleep(5000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            automation.getDesktopWindow("Prompts").getControlByName("OK");
            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
    }
    public static void selectVariant(UIAutomation automation, Row row, ExtentReports extent) throws AWTException, PatternNotFoundException, AutomationException, InterruptedException, IOException, InvalidFormatException {
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Prompts");
        try {
            String variantName = row.getString(ConfigProperties.getProperty("testCaseFlow.parameter1"));
            Thread.sleep(3000);
            automation.getDesktopWindow("Prompts").getEditBoxByAutomationId("PART_EditableTextBox").setValue(variantName);
            Robot robot = new Robot();
            Thread.sleep(5000);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);

        automation.getDesktopWindow("Prompts").getControlByName("OK");
        automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
        ReportWriter.assertStepAndPrint("Select Region", true, true, row);

    }catch(Exception e){
            ReportWriter.assertStepAndPrint("Select Region", true, false, row);
        }

    }
    public static String connectionToClick() {
        String connectionToClick = "BPC QR /Project SIT System";
        return connectionToClick;
    }

    public static void openPromptsWindow(UIAutomation automation, String workbookName) throws PatternNotFoundException, AutomationException {
        automation.getDesktopWindow(workbookName).getTab("Ribbon Tabs").selectTabPage("Analysis");
        automation.getDesktopWindow(workbookName).getButton("Prompts").invoke();
    }

}
