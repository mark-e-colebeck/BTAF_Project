package FunctionalTesting.ExecutionHandlers;

import FunctionalTesting.DataModel.TestCaseDetails;
import FunctionalTesting.Util.ConfigProperties;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationWindow;
import mmarquee.automation.pattern.PatternNotFoundException;
import tech.tablesaw.api.Table;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static RolesTesting.ExcelApplicationHandlers.ExcelHandlers.SheetHandler.getTableFromSheet;

public class ActionExecutors {

    //Waits for desktop window with title before continuing execution
    public static boolean waitForWindowWithTitle(ControlType controlType, String title) throws AutomationException, InterruptedException, IOException {
//        String title = row.getString(ConfigProperties.getProperty("testCaseFlow.syncSheet.windowTitle"));
        return new WindowsSyncCheck().waitUntilLoad(controlType, title, Integer.parseInt(ConfigProperties.getProperty("global.wait.sync.retry"))).getElement() != null;
    }
    //Opens the prompts window in the Analysis tab of Excel
    public static void openPromptsWindow(UIAutomation automation, String workbookName) throws PatternNotFoundException, AutomationException, IOException, InterruptedException {
        waitForWindowWithTitle(ControlType.Window, workbookName);
        automation.getDesktopWindow(workbookName).getTab("Ribbon Tabs").selectTabPage("Analysis");
        automation.getDesktopWindow(workbookName).getButton("Prompts").invoke();
    }

    //Selects the desired Variant and loads the data
    public static void selectVariantTest(UIAutomation automation,String variantName) throws PatternNotFoundException, AutomationException, AWTException, InterruptedException, IOException {
        ActionExecutors.waitForWindowWithTitle(ControlType.Window, "Prompts");
        automation.getDesktopWindow("Prompts").getEditBoxByAutomationId("PART_EditableTextBox").setValue(variantName);
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        Thread.sleep(7000);
        automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
        System.out.println("Completed");
        Thread.sleep(3000);
       /* if (automation.getDesktopWindow("Prompts").getControlByName("OK").isEnabled()) {
            automation.getDesktopWindow("Prompts").getButtonByAutomationId("OkButton").click();
        } else {
            Thread.sleep(5000);
        }*/
    }

    //Enters credentials to login if not using Single-Sign-On
    public static void credentialsLogin(UIAutomation automation, String executionEnvName, TestCaseDetails testCaseDetails) throws PatternNotFoundException, AutomationException, IOException, AWTException {
        AutomationWindow window1 = automation.getDesktopWindow("Logon to System " + executionEnvName);
        //Updated for AO 2.8
        window1.getEditBoxByAutomationId("_userTextBox").setValue(testCaseDetails.getUserName());
        //Updated for AO 2.8
        window1.getEditBoxByAutomationId("_passwordTextBox").setValue(testCaseDetails.getPassword());
        Robot robot = new Robot();
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    //Checks for error messages under the Analysis tab
    public static boolean checkForErrorMessage(UIAutomation automation, TestCaseDetails testCaseDetails) throws PatternNotFoundException, AutomationException, IOException {
        boolean hasError = false;
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

        if(hasMessages) {
            hasError = automation.getDesktop().getChildren(true).stream().anyMatch(e -> {
                try {
                    Table filteredErrorTable = errorData.select("Type").where(errorData.stringColumn("Message").isEqualTo(e.getElement().getName()));
                    if(e.getElement().getAutomationId().equals("mLabelErrorMsg") && filteredErrorTable.get(0,0).equals("Warning")) {
                        if(testCaseDetails != null && testCaseDetails.getLoginType().equals("SSO")) {
                            System.out.println("Failure: "+ filteredErrorTable.get(0,0));
                            return false;
                        }
                        System.out.println("Warning: "+ filteredErrorTable.get(0,0));
                        return false;
                    } else if (e.getElement().getAutomationId().equals("mLabelErrorMsg") && filteredErrorTable.get(0,0).equals("Failure")) {
                        System.out.println("Failure: "+ filteredErrorTable.get(0,0));
                        return true;
                    }
                } catch (AutomationException automationException) {
                    automationException.printStackTrace();
                }
                return false;
            });
        }
        return hasError;
    }

    //Selects the deired folder and workbook in the role tab
    public static void clickOnRoleFolderNames(UIAutomation automation, String roleFolder, String workSheetFolder, String workSheetWithSteps) throws PatternNotFoundException, AutomationException, InterruptedException, IOException {
        ActionExecutors.waitForWindowWithTitle(ControlType.TabItem,"Role");
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getTabItems().get(2).selectItem();
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(roleFolder).click();
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(workSheetFolder).click();
        automation.getDesktopWindow("Open Document").getPanelByAutomationId("mOpenBrowseControl").getPanelByAutomationId("mPanelContainer").getTabByAutomationId("mDatasourceTabControl").getPanelByAutomationId("Role").getPanelByAutomationId("CnOpenTreeControl").getTreeViewByAutomationId("mMultiColumnTreeView").getItem(workSheetWithSteps).click();
        automation.getDesktopWindow("Open Document").getButtonByAutomationId("mOkButton").click();
    }
}
