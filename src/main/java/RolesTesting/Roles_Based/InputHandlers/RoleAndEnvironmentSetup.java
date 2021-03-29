package RolesTesting.Roles_Based.InputHandlers;

import FunctionalTesting.ExecutionHandlers.ActionExecutors;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import RolesTesting.Roles_Based.ApplicationStatus.WindowsSyncCheck;
import RolesTesting.Roles_Based.ReportHandlers.CapturingScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import mmarquee.automation.AutomationElement;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationWindow;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class RoleAndEnvironmentSetup {
    private static Logger logger = LoggerFactory.getLogger(RoleAndEnvironmentSetup.class);
    public static void selectRegion(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws AWTException, PatternNotFoundException, AutomationException, InterruptedException, IOException, InvalidFormatException {
        //logger.info("Select region has started");
        //try {
        String region = roleBasedRow.getRefresh().split(";")[1];
        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, "Prompts");
        if (roleBasedRow.getRole_Workbook().contains("Report")) {
            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();

        } else {
            try {
                List<String> regions = new ArrayList<>();
                Collections.addAll(regions, region);

                Thread.sleep(3000);
                Robot robot = new Robot();
                for (int i = 0; i <= 6; i++) {
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
                        test.log(Status.INFO, MarkupHelper.createLabel("Selected " + s.toString() + " Region", ExtentColor.BLUE));
                    } catch (AutomationException | PatternNotFoundException e) {
                        e.printStackTrace();
                    }
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                });
                automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
                test.log(Status.INFO, MarkupHelper.createLabel("Clicked OK on Select Member window", ExtentColor.BLUE));
                automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
                test.log(Status.INFO, MarkupHelper.createLabel("Clicked OK on Prompts window", ExtentColor.BLUE));
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
        }
        logger.info("Select region has been completed");
    }
    public static void checkRegion(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws AWTException, PatternNotFoundException, AutomationException, InterruptedException, IOException, InvalidFormatException {
        //logger.info("Select region has started");
        //try {
        String region = roleBasedRow.getRefresh().split(";")[1];
        WindowsSyncCheck.waitForWindowWithTitle(ControlType.Window, "Prompts");
        if (roleBasedRow.getRole_Workbook().contains("Report")) {
            try{
                automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
            }
            catch (Exception e){
                logger.info("Prompts cannot be launched");
            }
            //automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();

        } else {
            try {
                List<String> regions = new ArrayList<>();
                Collections.addAll(regions, region);

                Thread.sleep(3000);
                Robot robot = new Robot();
                for (int i = 0; i <= 6; i++) {
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
                        if(!roleBasedRow.getListOfRegions().equals("")){
                            String sample = roleBasedRow.getListOfRegions();
                            List<String> regionsNew = Arrays.asList(sample.split(";"));
                            List<String> members = new ArrayList<>();
                            automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").getChildren(false).forEach(member->{
                                try {
                                    if (!member.getName().equals("Horizontal"))
                                    {
                                        members.add(member.getName());
                                    }
                                } catch (AutomationException e) {
                                    e.printStackTrace();
                                }
                            });
                            Collections.sort(regionsNew);
                            Collections.sort(members);
                            if(regionsNew.equals(members)) {
                                test.log(Status.INFO, "Regions are present as expected",MediaEntityBuilder.createScreenCaptureFromPath
                                        (CapturingScreenshot.capture("test")).build());
                            }else{
                                test.log(Status.FAIL, "Regions are not present as expected",MediaEntityBuilder.createScreenCaptureFromPath
                                        (CapturingScreenshot.capture("test")).build());
                            }
                        }
                        automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").getItem(s).select();
                        test.log(Status.INFO, MarkupHelper.createLabel("Selected " + s.toString() + " Region", ExtentColor.BLUE));

                    } catch (AutomationException | PatternNotFoundException | IOException | AWTException e) {
                        e.printStackTrace();
                    }
                    robot.keyPress(KeyEvent.VK_SPACE);
                    robot.keyRelease(KeyEvent.VK_SPACE);
                });
                try{
                    automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
                    test.log(Status.INFO, MarkupHelper.createLabel("Clicked OK on Select Member window", ExtentColor.BLUE));
                    automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
                    test.log(Status.INFO, MarkupHelper.createLabel("Clicked OK on Prompts window", ExtentColor.BLUE));
                }
                catch (Exception e){
                    logger.info("Prompts cannot be launched");
                    test.log(Status.FAIL, "Prompts window was not launched",MediaEntityBuilder.createScreenCaptureFromPath
                    (CapturingScreenshot.capture("PromptsFailure")).build());

                }
//                automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
//                automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
            } catch (Exception e) {
                e.printStackTrace();
                logger.info(e.getMessage());
            }
        }
        logger.info("Select region has been completed");
    }



       //}

        //catch (Exception e){


//                        catch (AutomationException | PatternNotFoundException i) {
//                            i.printStackTrace();
//                        }
//                        robot.keyPress(KeyEvent.VK_SPACE);
//                        robot.keyRelease(KeyEvent.VK_SPACE);
//                    });
//                    automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
//                    automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
//                }catch(Exception o){
//                    o.printStackTrace();
//                    logger.info(e.getMessage());
//                }
//                logger.info("Select region has been completed");
//            }
//
//        }
//        waitForWindowWithTitle(ControlType.Window, "Prompts");
//        String region = roleBasedRow.getRefresh().split(";")[1];
//        if(roleBasedRow.getRole_Workbook().contains("Report")){
//            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
//        }
//        else {
//            try{
//                List<String> regions = new ArrayList<>();
//                Collections.addAll(regions, region);
//
//                Thread.sleep(3000);
//                Robot robot = new Robot();
//                for (int i=0; i<=6; i++){
//                    robot.keyPress(KeyEvent.VK_TAB);
//                    robot.keyRelease(KeyEvent.VK_TAB);
//                }
//                Thread.sleep(1000);
//                robot.keyPress(KeyEvent.VK_ENTER);
//                robot.keyRelease(KeyEvent.VK_ENTER);
//                Thread.sleep(2000);
//                //automation.getDesktopWindow(workBookName + ".xlsm - Excel").getTreeViewByAutomationId("mTreeView").getItem("France");
//                regions.forEach(s -> {
//                    System.out.println(s);
//                    try {
//                        automation.getDesktopWindow("Select Member").getTreeViewByAutomationId("mTreeView").getItem(s).select();
//
//                    }catch (AutomationException | PatternNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                    robot.keyPress(KeyEvent.VK_SPACE);
//                    robot.keyRelease(KeyEvent.VK_SPACE);
//                });
//                automation.getDesktopWindow("Select Member").getButtonByAutomationId("mOKButton").click();
//                automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
//            }catch(Exception e){
//                e.printStackTrace();
//                logger.info(e.getMessage());
//            }
//            logger.info("Select region has been completed");
//        }


    public static void usernameLogin(UIAutomation automation, RoleBasedRow roleBasedRow, ExtentTest test) throws Exception {
        logger.info("Logging into the system as " + roleBasedRow.getUsername());
        test.log(Status.INFO, MarkupHelper.createLabel("Logging into the system as " + roleBasedRow.getUsername(), ExtentColor.BLUE));
        //handles the first text box for select unique user that pops up
        AutomationElement selectUserElement = WindowsSyncCheck.checkForWindows(ControlType.Window,"Select user",2);
        if(selectUserElement != null) {
            automation.getDesktopWindow("Select user").getButtonByAutomationId("CancelButton").click();
        }
        //handles the actual log in
        AutomationWindow window = automation.getDesktopWindow("Logon to System " + roleBasedRow.getConnection());
        window.getEditBoxByAutomationId("_userTextBox").setValue(roleBasedRow.getUsername());
        test.log(Status.INFO, MarkupHelper.createLabel("Entering User Name " + roleBasedRow.getUsername(), ExtentColor.BLUE));
        window.getEditBoxByAutomationId("_passwordTextBox").setValue(roleBasedRow.getPassword());
        test.log(Status.INFO, MarkupHelper.createLabel("Entered Password", ExtentColor.BLUE));
        //window.getButtonByAutomationId("mOkButton").click();
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        boolean hasError = ActionExecutors.checkForErrorMessage(automation, null);
        if(hasError) {
            test.log(Status.FAIL, MarkupHelper.createLabel("Login Failed", ExtentColor.RED));
            throw new Exception("Login Failure");
        } else {
            test.log(Status.INFO, MarkupHelper.createLabel("Login Successful", ExtentColor.BLUE));
            System.out.println("Login has been completed");
            logger.info("Login has been completed");
        }
    }

    public static void credentialsLogin(UIAutomation automation, String username, String password, String connection) throws PatternNotFoundException, AutomationException, InterruptedException {
        logger.info("Logging into the system as " + username);
       // test.log(Status.INFO, MarkupHelper.createLabel("Logging into the system as " + username, ExtentColor.BLUE));
        //handles the first text box for select unique user that pops up
            automation.getDesktopWindow("Select user").getButtonByAutomationId("CancelButton").click();
            //handles the actual log in
            AutomationWindow window = automation.getDesktopWindow("Logon to System " + connection);
            window.getEditBoxByAutomationId("_userTextBox").setValue(username);
            // test.log(Status.INFO, MarkupHelper.createLabel("Entering User Name " + username, ExtentColor.BLUE));
            window.getEditBoxByAutomationId("_passwordTextBox").setValue(password);
            // test.log(Status.INFO, MarkupHelper.createLabel("Entered Password", ExtentColor.BLUE));
            window.getButtonByAutomationId("mOkButton").click();
            System.out.println("Login Success");
            logger.info("Login has been completed");

    }

    public static void clickOnRoleFolders(UIAutomation automation, RoleBasedRow roleBasedRow,ExtentTest test) throws PatternNotFoundException, AutomationException, InterruptedException, IOException {
//        logger.info("Click on role folders has started");
//        Todo: Make changes Panel
        try{
            WindowsSyncCheck.waitForWindowWithTitle(ControlType.TabItem,"Role");
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").
                    getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").
                    getTabItems().get(2).selectItem();
            test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Role Tab", ExtentColor.BLUE));
            automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").
                    getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").
                    getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").
                    getTreeViewByAutomationId("mMultiColumnTreeView").
                    getItem(roleBasedRow.getRole_Folder().split(",")[0]).click();
            test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Folder "+ roleBasedRow.getRole_Folder().split(",")[0] + " in Role Tab", ExtentColor.BLUE));
            if(roleBasedRow.getRole_Folder().split(",").length > 1){
                automation.getDesktopWindow("Open Document").
                        getPanelByAutomationId("mOpenBrowseControl").
                        getPanelByAutomationId("mPanelContainer").
                        getTabByAutomationId("mDatasourceTabControl").
                        getPanelByAutomationId("Role").
                        getPanelByAutomationId("CnOpenTreeControl").
                        getTreeViewByAutomationId("mMultiColumnTreeView").
                        getItem(roleBasedRow.getRole_Folder().split(",")[1]).click();
                test.log(Status.INFO, MarkupHelper.createLabel("Clicked on Folder "+ roleBasedRow.getRole_Folder().split(",")[1] + " in Role Tab", ExtentColor.BLUE));
            }
            automation.getDesktopWindow("Open Document").
                    getPanelByAutomationId("mOpenBrowseControl").
                    getPanelByAutomationId("mPanelContainer").
                    getTabByAutomationId("mDatasourceTabControl").
                    getPanelByAutomationId("Role").
                    getPanelByAutomationId("CnOpenTreeControl").
                    getTreeViewByAutomationId("mMultiColumnTreeView").
                    getItem(roleBasedRow.getRole_Workbook()).click();
            test.log(Status.INFO, MarkupHelper.createLabel("Selected Workbook "+ roleBasedRow.getRole_Workbook() + " in Role Tab", ExtentColor.BLUE));
            automation.getDesktopWindow("Open Document").getButtonByAutomationId("mOkButton").click();
            test.log(Status.INFO, MarkupHelper.createLabel("Clicked OK on Role Tab", ExtentColor.BLUE));
            //            logger.info("Click on role folders has been completed");

        }
        catch (Exception e){
            logger.info(e.getMessage());
        }

    }



}
