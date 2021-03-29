package RolesTesting.Roles_Based.FolderHandlers;

import RolesTesting.Roles_Based.ApplicationStatus.WindowsSyncCheck;
import RolesTesting.Roles_Based.PoJos.RBTReport;
import RolesTesting.Roles_Based.PoJos.RoleBasedRow;
import RolesTesting.Roles_Based.PoJos.RoleBasedRowReport;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import mmarquee.automation.AutomationException;
import mmarquee.automation.ControlType;
import mmarquee.automation.UIAutomation;
import mmarquee.automation.pattern.PatternNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RoleFolderValidation {

    private static final Logger logger = LoggerFactory.getLogger(RoleFolderValidation.class);

    public static List<String> getRoleFolders(UIAutomation automation, String role_folder,ExtentTest test) throws AutomationException, InterruptedException, IOException, PatternNotFoundException {
//        logger.info("Waiting for Role Window to show up");
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
                .getItem(role_folder.split(",")[0]).click();
        int lengthOfRoleFolders = automation.getDesktopWindow("Open Document")
                .getPanelByAutomationId("mOpenBrowseControl")
                .getPanelByAutomationId("mPanelContainer")
                .getTabByAutomationId("mDatasourceTabControl")
                .getPanelByAutomationId("Role")
                .getPanelByAutomationId("CnOpenTreeControl")
                .getTreeViewByAutomationId("mMultiColumnTreeView")
                .getChildren(true)
                .size();

        List<String> roleFolders = new ArrayList<>();
        automation.getDesktopWindow("Open Document")
                .getPanelByAutomationId("mOpenBrowseControl")
                .getPanelByAutomationId("mPanelContainer")
                .getTabByAutomationId("mDatasourceTabControl")
                .getPanelByAutomationId("Role")
                .getPanelByAutomationId("CnOpenTreeControl")
                .getTreeViewByAutomationId("mMultiColumnTreeView")
                .getChildren(true)
                //.subList(6, lengthOfRoleFolders)
                .forEach(automationBase -> {
                    try {
                        if(automationBase.getName().contains("YBP") || automationBase.getName().contains("BPC")) {
                            roleFolders.add(automationBase.getName());
                        }
                    } catch (AutomationException e) {
                        e.printStackTrace();
                    }
                });
        logger.info("Actual Role Folders found are " + roleFolders);
        test.log(Status.INFO, MarkupHelper.createLabel("Actual Role Folders are " + roleFolders, ExtentColor.BLUE));
        return roleFolders;
    }

    public static RBTReport validateRoleFolders(UIAutomation automation, RoleBasedRow roleBasedRow, ExtentTest test) throws AutomationException, PatternNotFoundException, InterruptedException, IOException {
        List<String> expectedRoleFolders = Arrays.stream(roleBasedRow
                                            .getRole_Folder()
                                            .split(","))
                                            .sequential()
                                            .map(String::trim)
                                            .collect(Collectors.toList());
        logger.info("Expected Role Folders are " + expectedRoleFolders);
        test.log(Status.INFO, MarkupHelper.createLabel("Expected Role Folders are " + expectedRoleFolders, ExtentColor.BLUE));
        List<String> actual = getRoleFolders(automation, roleBasedRow.getRole_Folder(),test);
        if(expectedRoleFolders.equals(actual)){
            return new RBTReport(true,
                    expectedRoleFolders,
                    actual);
        }
        else return new RBTReport(false,
                expectedRoleFolders,
                actual);
    }
}
