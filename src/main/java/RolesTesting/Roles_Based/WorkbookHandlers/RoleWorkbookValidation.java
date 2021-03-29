package RolesTesting.Roles_Based.WorkbookHandlers;

import RolesTesting.Roles_Based.ApplicationStatus.WindowsSyncCheck;
import RolesTesting.Roles_Based.PoJos.RBTReport;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import RolesTesting.Roles_Based.ReportHandlers.CapturingScreenshot;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.controls.AutomationTreeView;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoleWorkbookValidation {
    private static Logger logger = LoggerFactory.getLogger(RoleWorkbookValidation.class);
    public static List<String> getWorkbooksForFolder(UIAutomation automation, String folder,ExtentTest test) throws AutomationException, InterruptedException, IOException, PatternNotFoundException, AWTException {
//        logger.info("Get workbooks for folder has started");
        WindowsSyncCheck.waitForWindowWithTitle(ControlType.TabItem,"Role");
        automation.getDesktopWindow("Open Document")
                .getPanelByAutomationId("mOpenBrowseControl")
                .getPanelByAutomationId("mPanelContainer")
                .getTabByAutomationId("mDatasourceTabControl")
                .getTabItems()
                .get(2)
                .selectItem();
        automation.getDesktopWindow("Open Document")
                .getPanelByAutomationId("mOpenBrowseControl")
                .getPanelByAutomationId("mPanelContainer")
                .getTabByAutomationId("mDatasourceTabControl")
                .getPanelByAutomationId("Role")
                .getPanelByAutomationId("CnOpenTreeControl")
                .getTreeViewByAutomationId("mMultiColumnTreeView")
                .getItem(folder.split(";")[0])
                .click();
        String folderBasedOnDepth = folder.split(";").length>1 ? folder.split(";")[1] : folder.split(";")[0];
        if(folder.split(";").length>1){
            AutomationTreeView treeView = automation.getDesktopWindow("Open Document")
                    .getPanelByAutomationId("mOpenBrowseControl")
                    .getPanelByAutomationId("mPanelContainer")
                    .getTabByAutomationId("mDatasourceTabControl")
                    .getPanelByAutomationId("Role")
                    .getPanelByAutomationId("CnOpenTreeControl")
                    .getTreeViewByAutomationId("mMultiColumnTreeView");
            try {
                treeView.getItem(folderBasedOnDepth).click();

            }
            catch (Exception e){
                logger.info("RoleFolder not found");
                //test.log(Status.FAIL, MarkupHelper.createLabel("Role Folder Not found",ExtentColor.RED));
                test.log(Status.WARNING,"Role Folder Not found",MediaEntityBuilder.createScreenCaptureFromPath
                        (CapturingScreenshot.capture("RoleFolderFail")).build());
                List<String> list = Collections.<String>emptyList();
                return list;
            }
        }
        List<String> workbooksInFolder = new ArrayList<>();
        automation.getDesktopWindow("Open Document")
                .getPanelByAutomationId("mOpenBrowseControl")
                .getPanelByAutomationId("mPanelContainer")
                .getTabByAutomationId("mDatasourceTabControl")
                .getPanelByAutomationId("Role")
                .getPanelByAutomationId("CnOpenTreeControl")
                .getTreeViewByAutomationId("mMultiColumnTreeView")
                .getItem(folderBasedOnDepth)
                .getChildren(true)
                .forEach(automationBase -> {
                    try {
                        workbooksInFolder.add(automationBase.getName());
                    } catch (AutomationException e) {
                        e.printStackTrace();
                    }
                });
        logger.info("Actual Workbooks are " + workbooksInFolder);
        test.log(Status.INFO, MarkupHelper.createLabel("Actual Workbooks present - " + workbooksInFolder,ExtentColor.BLUE));
        return workbooksInFolder;
    }

    public static RBTReport validateRoleWorkbooksForFolder(UIAutomation automation, RoleBasedRow roleBasedRow, ExtentTest test) throws AutomationException, PatternNotFoundException, InterruptedException, IOException, AWTException {
        List<String> expectedWorkbooks = Arrays.stream(roleBasedRow
                                            .getRole_Workbook()
                                            .split(","))
                                            .sequential()
                                            .map(String::trim)
                                            .collect(Collectors.toList());
        logger.info("Expected Workbooks are " + expectedWorkbooks);
        test.log(Status.INFO, MarkupHelper.createLabel("Expected Role Workbooks are " + expectedWorkbooks, ExtentColor.BLUE));
        List<String> actual = getWorkbooksForFolder(automation, roleBasedRow.getRole_Folder(),test);
        if(expectedWorkbooks.equals(actual)){
            return new RBTReport(true,
                    expectedWorkbooks,
                    actual);
        }
        else {
            return new RBTReport(false,
                    expectedWorkbooks,
                    actual);
        }

    }
}
